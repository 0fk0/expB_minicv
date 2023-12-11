package lang.c.parse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.FatalErrorException;
import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CTokenRule;
import lang.c.CTokenizer;
import lang.c.TestHelper;

public class CodeGenInputTest {
    
    InputStreamForTest inputStream;
    PrintStreamForTest outputStream;
    PrintStreamForTest errorOutputStream;
    CTokenizer tokenizer;
    IOContext context;
    CParseContext cpContext;
    TestHelper helper = new TestHelper();

    @Before
    public void setUp() {
        inputStream = new InputStreamForTest();
        outputStream = new PrintStreamForTest(System.out);
        errorOutputStream = new PrintStreamForTest(System.err);
        context = new IOContext(inputStream, outputStream, errorOutputStream);
        tokenizer = new CTokenizer(new CTokenRule());
        cpContext = new CParseContext(context, tokenizer);
    }

    @After
    public void tearDown() {
        inputStream = null;
        outputStream = null;
        errorOutputStream = null;
        tokenizer = null;
        context = null;
        cpContext = null;
    }

    @Test
    public void codeGenInputTest1() throws FatalErrorException {
        inputStream.setInputString("input ip_a;");
        String expected = """
                . = 0x100
                JMP     __START ; ProgramNode: 最初の実行文へ
        __START:
                MOV     #0x1000, R6     ; ProgramNode: 計算用スタック初期化
                MOV     #ip_a, (R6)+    ; Ident: 変数アドレスを積む
                MOV     #0xFFE0, (R6)+  ; statementInput: 入出力番地を右辺にセット
                MOV     -(R6), R0       ; statementInput: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementInput:
                MOV     (R0), (R1)        ; statementInput:
                HLT                     ; ProgramNode:
                .END                    ; ProgramNode:        
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // Please copy and paste the above code and add the specified test case to the following
    @Test
    public void codeGenInputTest2() throws FatalErrorException {
        inputStream.setInputString("input ia_a[2];");
        String expected = """
                . = 0x100
                JMP     __START ; ProgramNode: 最初の実行文へ
        __START:
                MOV     #0x1000, R6     ; ProgramNode: 計算用スタック初期化
                MOV     #ia_a, (R6)+    ; Ident: 変数アドレスを積む
                MOV     #2, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; Variable: 配列名とindexを取り出して配列先頭アドレスとindex分を足し、内容を参照して、積む
                MOV     -(R6), R1       ; Variable:
                ADD     R0, R1  ; Variable:
                MOV     R1, (R6)+       ; Variable:
                MOV     #0xFFE0, (R6)+  ; statementInput: 入出力番地を右辺にセット
                MOV     -(R6), R0       ; statementInput: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementInput:
                MOV     (R0), (R1)        ; statementInput:
                HLT                     ; ProgramNode:
                .END                    ; ProgramNode:     
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenInputTest3() throws FatalErrorException {
        inputStream.setInputString("input *ip_b;");
        String expected = """
                . = 0x100
                JMP     __START ; ProgramNode: 最初の実行文へ
        __START:
                MOV     #0x1000, R6     ; ProgramNode: 計算用スタック初期化
                MOV     #ip_b, (R6)+    ; Ident: 変数アドレスを積む
                MOV     -(R6), R0       ; PrimaryMult: アドレスを取り出して、内容を参照して、積む
                MOV     (R0), (R6)+     ; PrimaryMult:
                MOV     #0xFFE0, (R6)+  ; statementInput: 入出力番地を右辺にセット
                MOV     -(R6), R0       ; statementInput: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementInput:
                MOV     (R0), (R1)        ; statementInput:
                HLT                     ; ProgramNode:
                .END                    ; ProgramNode:   
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenInputTest4() throws FatalErrorException {
        inputStream.setInputString("input *ipa_b[6];");
        String expected = """
                . = 0x100
                JMP     __START ; ProgramNode: 最初の実行文へ
        __START:
                MOV     #0x1000, R6     ; ProgramNode: 計算用スタック初期化
                MOV     #ipa_b, (R6)+   ; Ident: 変数アドレスを積む
                MOV     #6, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; Variable: 配列名とindexを取り出して配列先頭アドレスとindex分を足し、内容を参照して、積む
                MOV     -(R6), R1       ; Variable:
                ADD     R0, R1  ; Variable:
                MOV     R1, (R6)+       ; Variable:
                MOV     -(R6), R0       ; PrimaryMult: アドレスを取り出して、内容を参照して、積む<[1行目,7文字目の'*']>
                MOV     (R0), (R6)+     ; PrimaryMult:
                MOV     #0xFFE0, (R6)+  ; statementInput: 入出力番地を右辺にセット
                MOV     -(R6), R0       ; statementInput: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementInput:
                MOV     (R0), (R1)        ; statementInput:
                HLT                     ; ProgramNode:
                .END                    ; ProgramNode:
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}
