package com.plagiarism.engine.parser;

public enum TokenType {
    FUNC_DECL("F"),
    VAR_DECL("V"),
    IF("I"),
    ELSE("E"),
    FOR("O"),
    WHILE("W"),
    TRY("T"),
    CATCH("C"),
    ASSIGN("A"),
    CALL("K"),
    RETURN("R");

    private final String symbol;

    TokenType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
