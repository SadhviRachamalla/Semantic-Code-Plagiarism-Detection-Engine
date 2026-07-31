package com.plagiarism.engine.parser.impl;

import com.plagiarism.engine.parser.CodeParser;
import com.plagiarism.engine.parser.TokenType;
import java.util.ArrayList;
import java.util.List;

public class CppCodeParser implements CodeParser {
    @Override
    public List<TokenType> parse(String sourceCode) {
        List<TokenType> tokens = new ArrayList<>();
        String cleanCode = stripCppCommentsAndStrings(sourceCode);

        String[] words = cleanCode.split("\\s+|(?=[\\(\\)\\{\\};=])|(?<=[\\(\\)\\{\\};=])");
        for (int i = 0; i < words.length; i++) {
            String word = words[i].trim();
            if (word.isEmpty()) continue;

            switch (word) {
                case "if" -> tokens.add(TokenType.IF);
                case "else" -> tokens.add(TokenType.ELSE);
                case "for" -> tokens.add(TokenType.FOR);
                case "while" -> tokens.add(TokenType.WHILE);
                case "try" -> tokens.add(TokenType.TRY);
                case "catch" -> tokens.add(TokenType.CATCH);
                case "return" -> tokens.add(TokenType.RETURN);
                case "=" -> {
                    if (i > 0 && i < words.length - 1) {
                        String prev = words[i - 1].trim();
                        String next = words[i + 1].trim();
                        if (!prev.equals("=") && !prev.equals("!") && !prev.equals("<") && !prev.equals(">")
                                && !next.equals("=")) {
                            tokens.add(TokenType.ASSIGN);
                        }
                    }
                }
            }

            if (isCppType(word)) {
                if (i < words.length - 1) {
                    String nextWord = words[i + 1].trim();
                    if (nextWord.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                        boolean isFunc = false;
                        for (int j = i + 2; j < Math.min(words.length, i + 5); j++) {
                            if (words[j].trim().equals("(")) {
                                isFunc = true;
                                break;
                            }
                        }
                        if (isFunc) {
                            tokens.add(TokenType.FUNC_DECL);
                        } else {
                            tokens.add(TokenType.VAR_DECL);
                        }
                    }
                }
            }

            if (word.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                if (i < words.length - 1 && words[i + 1].trim().equals("(")) {
                    if (!isKeyword(word) && !isCppType(word)) {
                        tokens.add(TokenType.CALL);
                    }
                }
            }
        }
        return tokens;
    }

    private boolean isCppType(String word) {
        return word.equals("int") || word.equals("float") || word.equals("double") 
                || word.equals("char") || word.equals("bool") || word.equals("void")
                || word.equals("auto") || word.equals("string");
    }

    private boolean isKeyword(String word) {
        return word.equals("if") || word.equals("else") || word.equals("for") 
                || word.equals("while") || word.equals("try") || word.equals("catch")
                || word.equals("return") || word.equals("switch") || word.equals("case")
                || word.equals("break") || word.equals("continue");
    }

    private String stripCppCommentsAndStrings(String code) {
        String noBlockComments = code.replaceAll("/\\*(?s:.*?)\\*/", "");
        String noLineComments = noBlockComments.replaceAll("//.*", "");
        return noLineComments.replaceAll("\"[^\"]*\"|'[^']*'", "");
    }
}
