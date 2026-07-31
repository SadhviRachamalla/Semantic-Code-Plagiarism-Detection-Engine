package com.plagiarism.engine.parser.impl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;
import com.plagiarism.engine.parser.CodeParser;
import com.plagiarism.engine.parser.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JavaCodeParser implements CodeParser {
    private static final Logger log = LoggerFactory.getLogger(JavaCodeParser.class);

    @Override
    public List<TokenType> parse(String sourceCode) {
        List<TokenType> tokens = new ArrayList<>();
        try {
            CompilationUnit cu;
            try {
                cu = StaticJavaParser.parse(sourceCode);
            } catch (Exception e) {
                // Try wrapping in a class if it's a code block or method list
                String wrapped = "public class DummyWrapper {\n" + sourceCode + "\n}";
                cu = StaticJavaParser.parse(wrapped);
            }
            traverse(cu, tokens);
        } catch (Exception e) {
            log.warn("Failed to parse Java code via AST: {}. Falling back to lexical analysis.", e.getMessage());
            fallbackTokenize(sourceCode, tokens);
        }
        return tokens;
    }

    private void traverse(Node node, List<TokenType> tokens) {
        if (node instanceof MethodDeclaration) {
            tokens.add(TokenType.FUNC_DECL);
        } else if (node instanceof VariableDeclarator) {
            tokens.add(TokenType.VAR_DECL);
        } else if (node instanceof IfStmt) {
            tokens.add(TokenType.IF);
        } else if (node instanceof ForStmt || node instanceof ForEachStmt) {
            tokens.add(TokenType.FOR);
        } else if (node instanceof WhileStmt || node instanceof DoStmt) {
            tokens.add(TokenType.WHILE);
        } else if (node instanceof TryStmt) {
            tokens.add(TokenType.TRY);
        } else if (node instanceof CatchClause) {
            tokens.add(TokenType.CATCH);
        } else if (node instanceof AssignExpr) {
            tokens.add(TokenType.ASSIGN);
        } else if (node instanceof MethodCallExpr) {
            tokens.add(TokenType.CALL);
        } else if (node instanceof ReturnStmt) {
            tokens.add(TokenType.RETURN);
        }

        for (Node child : node.getChildNodes()) {
            traverse(child, tokens);
        }
    }

    private void fallbackTokenize(String code, List<TokenType> tokens) {
        String cleanCode = code.replaceAll("//.*|/\\*(?s:.*?)\\*/", "");
        String[] words = cleanCode.split("\\s+|(?=[\\(\\)\\{\\};=])|(?<=[\\(\\)\\{\\};=])");
        for (String word : words) {
            String w = word.trim();
            if (w.isEmpty()) continue;
            switch (w) {
                case "if" -> tokens.add(TokenType.IF);
                case "else" -> tokens.add(TokenType.ELSE);
                case "for" -> tokens.add(TokenType.FOR);
                case "while" -> tokens.add(TokenType.WHILE);
                case "try" -> tokens.add(TokenType.TRY);
                case "catch" -> tokens.add(TokenType.CATCH);
                case "return" -> tokens.add(TokenType.RETURN);
                case "=" -> tokens.add(TokenType.ASSIGN);
            }
        }
    }
}
