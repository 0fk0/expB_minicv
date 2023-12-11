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

public class CodeGenWhileTest {
    
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
    public void codeGenWhileTest1() throws FatalErrorException {
        inputStream.setInputString( "i_b = 1;" +
                                    "while (i_b == 1) { \r\n" + 
                                    "   input i_a; \r\n" +
                                    "   i_b = 4; \r\n" +
                                    "}");
        String expected = """
                . = 0x100
                JMP     __START ; ProgramNode: 最初の実行文へ
        __START:
                MOV     #0x1000, R6     ; ProgramNode: 計算用スタック初期化
                MOV     #i_b, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #1, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
        WHILE1: ; StatementWhile:
                MOV     #i_b, (R6)+     ; Ident: 変数アドレスを積む
                MOV     -(R6), R0       ; addressToValue: アドレスを取り出して、内容を参照して、積む
                MOV     (R0), (R6)+     ; addressToValue:
                MOV     #1, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; ConditionEQ: ２数を取り出して、比べる
                MOV     -(R6), R1       ; ConditionEQ:
                MOV     #0x0001, R2     ; ConditionEQ: set true
                CMP     R0, R1  ; ConditionEQ: R1-R0 = R1-R0=0
                BRZ     EQ2     ; ConditionEQ
                CLR     R2              ; ConditionEQ: set false
        EQ2:    MOV     R2, (R6)+       ; ConditionEQ:
                MOV     -(R6), R0       ; StatementWhile:真理値を取り出す
                BRZ     ENDWHILE1       ;
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #0xFFE0, (R6)+  ; statementInput: 入出力番地を右辺にセット
                MOV     -(R6), R0       ; statementInput: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementInput:
                MOV     (R0), (R1)        ; statementInput:
                MOV     #i_b, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #4, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                JMP     WHILE1  ;
        ENDWHILE1:      ; StatementWhile:
                HLT                     ; ProgramNode:
                .END                    ; ProgramNode:        
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // Please copy and paste the above code and add the specified test case to the following
    @Test
    public void codeGenWhileTest2() throws FatalErrorException {
        inputStream.setInputString("""
            while (true) {
                input i_a;
                while (false) {
                   output i_a;
                }
                i_a=4;
             }      
        """);
        String expected = """
                . = 0x100
                JMP     __START ; ProgramNode: 最初の実行文へ
        __START:
                MOV     #0x1000, R6     ; ProgramNode: 計算用スタック初期化
        WHILE1: ; StatementWhile:
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     -(R6), R0       ; StatementWhile:真理値を取り出す
                BRZ     ENDWHILE1       ;
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #0xFFE0, (R6)+  ; statementInput: 入出力番地を右辺にセット
                MOV     -(R6), R0       ; statementInput: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementInput:
                MOV     (R0), (R1)        ; statementInput:
        WHILE2: ; StatementWhile:
                MOV     #0x0000, (R6)+  ; Condition: false
                MOV     -(R6), R0       ; StatementWhile:真理値を取り出す
                BRZ     ENDWHILE2       ;
                MOV     #0xFFE0, (R6)+  ; statementOutput: 入出力番地を左辺にセット
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     -(R6), R0       ; addressToValue: アドレスを取り出して、内容を参照して、積む
                MOV     (R0), (R6)+     ; addressToValue:
                MOV     -(R6), R0       ; statementOutput: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementOutput:
                MOV     R0, (R1)        ; statementOutput:
                JMP     WHILE2  ;
        ENDWHILE2:      ; StatementWhile:
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #4, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                JMP     WHILE1  ;
        ENDWHILE1:      ; StatementWhile:
                HLT                     ; ProgramNode:
                .END                    ; ProgramNode:      
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenWhileTest3() throws FatalErrorException {
        inputStream.setInputString("""
            while(true) i_c=1;    
        """);
        String expected = """
                . = 0x100
                JMP     __START ; ProgramNode: 最初の実行文へ
        __START:
                MOV     #0x1000, R6     ; ProgramNode: 計算用スタック初期化
        WHILE1: ; StatementWhile:
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     -(R6), R0       ; StatementWhile:真理値を取り出す
                BRZ     ENDWHILE1       ;
                MOV     #i_c, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #1, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                JMP     WHILE1  ;
        ENDWHILE1:      ; StatementWhile:
                HLT                     ; ProgramNode:
                .END                    ; ProgramNode:    
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenWhileTest4() throws FatalErrorException {
        inputStream.setInputString("""
            while(false) while(true) while(true) input i_a;  
        """);
        String expected = """
                . = 0x100
                JMP     __START ; ProgramNode: 最初の実行文へ
        __START:
                MOV     #0x1000, R6     ; ProgramNode: 計算用スタック初期化
        WHILE1: ; StatementWhile:
                MOV     #0x0000, (R6)+  ; Condition: false
                MOV     -(R6), R0       ; StatementWhile:真理値を取り出す
                BRZ     ENDWHILE1       ;
        WHILE2: ; StatementWhile:
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     -(R6), R0       ; StatementWhile:真理値を取り出す
                BRZ     ENDWHILE2       ;
        WHILE3: ; StatementWhile:
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     -(R6), R0       ; StatementWhile:真理値を取り出す
                BRZ     ENDWHILE3       ;
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む<[1行目,44文字目の'i_a']>
                MOV     #0xFFE0, (R6)+  ; statementInput: 入出力番地を右辺にセット
                MOV     -(R6), R0       ; statementInput: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementInput:
                MOV     (R0), (R1)        ; statementInput:
                JMP     WHILE3  ;
        ENDWHILE3:      ; StatementWhile:
                JMP     WHILE2  ;
        ENDWHILE2:      ; StatementWhile:
                JMP     WHILE1  ;
        ENDWHILE1:      ; StatementWhile:
                HLT                     ; ProgramNode:
                .END                    ; ProgramNode:    
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}
