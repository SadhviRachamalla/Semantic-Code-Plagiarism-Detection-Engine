package com.plagiarism.engine.parser;

import com.plagiarism.engine.parser.impl.CppCodeParser;
import com.plagiarism.engine.parser.impl.JavaCodeParser;
import com.plagiarism.engine.parser.impl.PythonCodeParser;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ParserFactory {
    private final Map<String, CodeParser> parsers = new HashMap<>();

    public ParserFactory() {
        parsers.put("java", new JavaCodeParser());
        parsers.put("python", new PythonCodeParser());
        parsers.put("cpp", new CppCodeParser());
        parsers.put("c++", new CppCodeParser());
    }

    public CodeParser getParser(String language) {
        String normalizedLang = Optional.ofNullable(language)
                .map(String::toLowerCase)
                .map(String::trim)
                .orElse("java");
        
        CodeParser parser = parsers.get(normalizedLang);
        if (parser == null) {
            throw new IllegalArgumentException("Unsupported language: " + language);
        }
        return parser;
    }
}
