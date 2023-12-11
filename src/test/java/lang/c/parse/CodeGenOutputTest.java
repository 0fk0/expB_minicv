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

public class CodeGenOutputTest {
    
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
    public void codeGenOutputTest1() throws FatalErrorException {
        inputStream.setInputString("output &i_a;");
        String expected = """
                . = 0x100
                JMP     __START ; ProgramNode: 最初の実行文へ
        __START:
                MOV     #0x1000, R6     ; ProgramNode: 計算用スタック初期化
                MOV     #0xFFE0, (R6)+  ; statementOutput: 入出力番地を左辺にセット
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む<[1行目,9文字目の'i_a']>
                MOV     -(R6), R0       ; statementOutput: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementOutput:
                MOV     R0, (R1)        ; statementOutput:
                HLT                     ; ProgramNode:
                .END                    ; ProgramNode:
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // Please copy and paste the above code and add the specified test case to the following
    @Test
    public void codeGenOutputTest2() throws FatalErrorException {
        inputStream.setInputString("output i_a+3;");
        String expected = """
                . = 0x100
                JMP     __START ; ProgramNode: 最初の実行文へ
        __START:
                MOV     #0x1000, R6     ; ProgramNode: 計算用スタック初期化
                MOV     #0xFFE0, (R6)+  ; statementOutput: 入出力番地を左辺にセット
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     -(R6), R0       ; addressToValue: アドレスを取り出して、内容を参照して、積む
                MOV     (R0), (R6)+     ; addressToValue:
                MOV     #3, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; ExpressionAdd: ２数を取り出して、足し、積む
                MOV     -(R6), R1       ; ExpressionAdd:
                ADD     R1, R0          ; ExpressionAdd:
                MOV     R0, (R6)+       ; ExpressionAdd:
                MOV     -(R6), R0       ; statementOutput: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementOutput:
                MOV     R0, (R1)        ; statementOutput:
                HLT                     ; ProgramNode:
                .END                    ; ProgramNode:
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}
