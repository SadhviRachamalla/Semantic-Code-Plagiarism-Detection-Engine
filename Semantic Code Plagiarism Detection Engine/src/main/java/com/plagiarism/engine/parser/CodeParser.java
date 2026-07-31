package com.plagiarism.engine.parser;

import java.util.List;

public interface CodeParser {
    List<TokenType> parse(String sourceCode);
}
