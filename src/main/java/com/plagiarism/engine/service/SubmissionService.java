package com.plagiarism.engine.service;

import com.plagiarism.engine.dto.*;
import com.plagiarism.engine.entity.*;
import com.plagiarism.engine.exception.*;
import com.plagiarism.engine.mapper.*;
import com.plagiarism.engine.parser.*;
import com.plagiarism.engine.repository.*;
import com.plagiarism.engine.similarity.WinnowingFingerprinter;
import com.plagiarism.engine.similarity.WinnowingFingerprinter.Fingerprint;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Transactional
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionSetRepository submissionSetRepository;
    private final ParserFactory parserFactory;
    private final WinnowingFingerprinter fingerprinter;
    private final SubmissionMapper submissionMapper;
    private final SubmissionSetMapper submissionSetMapper;
    private final AuditService auditService;
    private final RedisTemplate<String, List<Fingerprint>> redisTemplate;

    public SubmissionService(SubmissionRepository submissionRepository,
                             SubmissionSetRepository submissionSetRepository,
                             ParserFactory parserFactory,
                             WinnowingFingerprinter fingerprinter,
                             SubmissionMapper submissionMapper,
                             SubmissionSetMapper submissionSetMapper,
                             AuditService auditService,
                             RedisTemplate<String, List<Fingerprint>> redisTemplate) {
        this.submissionRepository = submissionRepository;
        this.submissionSetRepository = submissionSetRepository;
        this.parserFactory = parserFactory;
        this.fingerprinter = fingerprinter;
        this.submissionMapper = submissionMapper;
        this.submissionSetMapper = submissionSetMapper;
        this.auditService = auditService;
        this.redisTemplate = redisTemplate;
    }

    public SubmissionSetResponse createSubmissionSet(SubmissionSetRequest request) {
        SubmissionSet set = new SubmissionSet();
        set.setName(request.getName());
        SubmissionSet saved = submissionSetRepository.save(set);
        
        auditService.logAction("CREATE_SUBMISSION_SET", "Created submission set '" + saved.getName() + "' with ID: " + saved.getId());
        return submissionSetMapper.toResponse(saved);
    }

    public SubmissionResponse uploadSubmission(SubmissionRequest request) {
        SubmissionSet set = null;
        if (request.getSubmissionSetId() != null) {
            set = submissionSetRepository.findById(request.getSubmissionSetId())
                    .orElseThrow(() -> new EntityNotFoundException("Submission set not found with ID: " + request.getSubmissionSetId()));
        }

        String fileHash = calculateSHA256(request.getSourceCode());
        
        // Parse and tokenize
        CodeParser parser = parserFactory.getParser(request.getLanguage());
        List<TokenType> tokens = parser.parse(request.getSourceCode());
        String tokenStr = tokens.stream().map(TokenType::getSymbol).collect(Collectors.joining());

        // Generate fingerprints (attempt Redis fetch first)
        List<Fingerprint> domainFingerprints = getOrGenerateFingerprints(fileHash, tokens);

        Submission submission = new Submission();
        submission.setName(request.getName());
        submission.setLanguage(request.getLanguage().toLowerCase());
        submission.setSourceCode(request.getSourceCode());
        submission.setNormalizedTokens(tokenStr);
        submission.setFileHash(fileHash);
        submission.setSubmissionSet(set);

        // Convert domain fingerprints to DB fingerprints
        List<DbFingerprint> dbFingerprints = domainFingerprints.stream()
                .map(df -> new DbFingerprint(df.hash(), df.position()))
                .collect(Collectors.toList());
        submission.setFingerprints(dbFingerprints);

        Submission saved = submissionRepository.save(submission);
        auditService.logAction("UPLOAD_SUBMISSION", "Uploaded file '" + saved.getName() + "' for language " + saved.getLanguage());

        return submissionMapper.toResponse(saved);
    }

    public List<SubmissionResponse> uploadZip(UUID submissionSetId, MultipartFile file) {
        SubmissionSet set = submissionSetRepository.findById(submissionSetId)
                .orElseThrow(() -> new EntityNotFoundException("Submission set not found with ID: " + submissionSetId));

        List<SubmissionResponse> responses = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }

                String name = entry.getName();
                String ext = getFileExtension(name);
                String language = detectLanguageByExtension(ext);
                if (language == null) {
                    continue; // Skip unsupported extensions silently
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    bos.write(buffer, 0, len);
                }
                String content = bos.toString(StandardCharsets.UTF_8);

                SubmissionRequest req = new SubmissionRequest();
                req.setName(name);
                req.setLanguage(language);
                req.setSourceCode(content);
                req.setSubmissionSetId(submissionSetId);

                responses.add(uploadSubmission(req));
            }
        } catch (IOException e) {
            throw new InvalidSubmissionException("Failed to unpack zip archive: " + e.getMessage());
        }

        auditService.logAction("UPLOAD_ZIP", "Uploaded zip archive containing " + responses.size() + " files to set " + submissionSetId);
        return responses;
    }

    public List<Fingerprint> getOrGenerateFingerprints(String fileHash, List<TokenType> tokens) {
        String cacheKey = "fingerprints:" + fileHash;
        try {
            List<Fingerprint> cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return cached;
            }
        } catch (Exception e) {
            // Log warning but continue if Redis goes down
        }

        List<Fingerprint> fresh = fingerprinter.generateFingerprints(tokens);
        
        try {
            redisTemplate.opsForValue().set(cacheKey, fresh);
        } catch (Exception e) {
            // Log warning but continue
        }

        return fresh;
    }

    @Transactional(readOnly = true)
    public Submission getSubmissionEntity(UUID id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public SubmissionResponse getSubmission(UUID id) {
        return submissionMapper.toResponse(getSubmissionEntity(id));
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsInSet(UUID setId) {
        return submissionRepository.findBySubmissionSetId(setId).stream()
                .map(submissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SubmissionSetResponse> getAllSubmissionSets() {
        return submissionSetRepository.findAll().stream()
                .map(submissionSetMapper::toResponse)
                .collect(Collectors.toList());
    }

    private String calculateSHA256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm missing", e);
        }
    }

    private String getFileExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return idx == -1 ? "" : filename.substring(idx + 1).toLowerCase();
    }

    private String detectLanguageByExtension(String ext) {
        return switch (ext) {
            case "java" -> "java";
            case "py" -> "python";
            case "cpp", "cc", "cxx", "hpp", "h" -> "cpp";
            default -> null;
        };
    }
}
