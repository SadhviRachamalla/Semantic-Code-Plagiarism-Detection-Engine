package com.plagiarism.engine.parser;

import com.plagiarism.engine.parser.impl.CppCodeParser;
import com.plagiarism.engine.parser.impl.JavaCodeParser;
import com.plagiarism.engine.parser.impl.PythonCodeParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    public void testJavaParser() {
        JavaCodeParser parser = new JavaCodeParser();
        String code = "public class Calc {\n" +
                "  public int add(int x, int y) {\n" +
                "    int z = x + y;\n" +
                "    if (z > 0) return z;\n" +
                "    return 0;\n" +
                "  }\n" +
                "}";
        List<TokenType> tokens = parser.parse(code);
        
        assertFalse(tokens.isEmpty());
        assertTrue(tokens.contains(TokenType.FUNC_DECL));
        assertTrue(tokens.contains(TokenType.VAR_DECL));
        assertTrue(tokens.contains(TokenType.IF));
        assertTrue(tokens.contains(TokenType.RETURN));
    }

    @Test
    public void testPythonParser() {
        PythonCodeParser parser = new PythonCodeParser();
        String code = "def compute(a, b):\n" +
                "    # sum variables\n" +
                "    val = a + b\n" +
                "    if val > 0:\n" +
                "        return val\n" +
                "    return 0";
        List<TokenType> tokens = parser.parse(code);

        assertFalse(tokens.isEmpty());
        assertTrue(tokens.contains(TokenType.FUNC_DECL));
        assertTrue(tokens.contains(TokenType.ASSIGN));
        assertTrue(tokens.contains(TokenType.IF));
        assertTrue(tokens.contains(TokenType.RETURN));
    }

    @Test
    public void testCppParser() {
        CppCodeParser parser = new CppCodeParser();
        String code = "#include <iostream>\n" +
                "int compute(int a, int b) {\n" +
                "    int val = a + b;\n" +
                "    if (val > 0) {\n" +
                "        return val;\n" +
                "    }\n" +
                "    return 0;\n" +
                "}";
        List<TokenType> tokens = parser.parse(code);

        assertFalse(tokens.isEmpty());
        assertTrue(tokens.contains(TokenType.FUNC_DECL));
        assertTrue(tokens.contains(TokenType.VAR_DECL));
        assertTrue(tokens.contains(TokenType.ASSIGN));
        assertTrue(tokens.contains(TokenType.IF));
        assertTrue(tokens.contains(TokenType.RETURN));
    }
}
