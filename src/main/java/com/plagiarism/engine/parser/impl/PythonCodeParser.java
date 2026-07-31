package com.plagiarism.engine.parser.impl;

import com.plagiarism.engine.parser.CodeParser;
import com.plagiarism.engine.parser.TokenType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonCodeParser implements CodeParser {
    @Override
    public List<TokenType> parse(String sourceCode) {
        List<TokenType> tokens = new ArrayList<>();
        String cleanCode = stripPythonCommentsAndStrings(sourceCode);
        
        String[] lines = cleanCode.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            if (trimmed.startsWith("def ")) {
                tokens.add(TokenType.FUNC_DECL);
            } else if (trimmed.startsWith("if ") || trimmed.startsWith("elif ")) {
                tokens.add(TokenType.IF);
            } else if (trimmed.startsWith("else:")) {
                tokens.add(TokenType.ELSE);
            } else if (trimmed.startsWith("for ")) {
                tokens.add(TokenType.FOR);
            } else if (trimmed.startsWith("while ")) {
                tokens.add(TokenType.WHILE);
            } else if (trimmed.startsWith("try:")) {
                tokens.add(TokenType.TRY);
            } else if (trimmed.startsWith("except ") || trimmed.startsWith("except:")) {
                tokens.add(TokenType.CATCH);
            } else if (trimmed.startsWith("return ") || trimmed.equals("return")) {
                tokens.add(TokenType.RETURN);
            }

            // Detect function calls: name(
            Pattern callPattern = Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\s*\\(");
            Matcher callMatcher = callPattern.matcher(trimmed);
            while (callMatcher.find()) {
                String match = callMatcher.group().trim();
                String name = match.substring(0, match.indexOf('(')).trim();
                if (!isKeyword(name)) {
                    tokens.add(TokenType.CALL);
                }
            }

            // Detect assignments (excluding comparison operators)
            if (trimmed.contains("=") 
                    && !trimmed.contains("==") 
                    && !trimmed.contains("!=") 
                    && !trimmed.contains(">=") 
                    && !trimmed.contains("<=")) {
                tokens.add(TokenType.ASSIGN);
            }
        }
        return tokens;
    }

    private boolean isKeyword(String name) {
        return name.equals("if") || name.equals("elif") || name.equals("while") 
                || name.equals("for") || name.equals("print") || name.equals("range")
                || name.equals("len") || name.equals("list") || name.equals("dict")
                || name.equals("set") || name.equals("super");
    }

    private String stripPythonCommentsAndStrings(String code) {
        // Strip triple quoted strings (both """ and ''')
        String noDocstrings = code.replaceAll("\"\"\"(?s:.*?)\"\"\"|'''(?s:.*?)\"'''", "");
        // Strip single line comments
        String noComments = noDocstrings.replaceAll("#.*", "");
        // Strip string literals to avoid parsing keywords within strings
        return noComments.replaceAll("\"[^\"]*\"|'[^']*'", "");
    }
}
