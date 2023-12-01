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

public class CodeGenIfTest {
    
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
    public void codeGenIfTest1() throws FatalErrorException {
        inputStream.setInputString( "if (false) {\n" +
                                        "i_a=3;\n" +
                                    "}");
        String expected = """
                . = 0x100
                JMP     __START ; ProgramNode: 最初の実行文へ
        __START:
                MOV     #0x1000, R6     ; ProgramNode: 計算用スタック初期化
                MOV     #0x0000, (R6)+  ; Condition: false
                MOV     -(R6), R0       ; StatementIF:真理値を取り出す
                BRZ     ENDIF1  ;
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #3, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                JMP ENDIF1:     ; StatementIF:
        ENDIF1: ; StatementIF:
                HLT                     ; ProgramNode:
                .END                    ; ProgramNode:
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenIfTest2() throws FatalErrorException {
        inputStream.setInputString("""
                    if (true) {
                        i_a=1;
                    } else {
                        i_a=2;
                    }
                """);
        String expected = """
                . = 0x100
                JMP     __START ; ProgramNode: 最初の実行文へ
        __START:
                MOV     #0x1000, R6     ; ProgramNode: 計算用スタック初期化
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     -(R6), R0       ; StatementIF:真理値を取り出す
                BRZ     ELSEIF1 ;
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #1, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                JMP ENDIF1:     ; StatementIF:
        ELSEIF1:        ; StatementIF:
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #2, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                JMP ENDIF1:     ; StatementIF:
        ENDIF1: ; StatementIF:
                HLT                     ; ProgramNode:
                .END                    ; ProgramNode:
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenIfTest3() throws FatalErrorException {
        inputStream.setInputString("""
                    if (i_a == 3) {
                        i_a=0;
                    } else if (i_a == 4){	// キーワードelseifを導入した人は、そのように直すこと
                        i_a=1;
                    } else {
                        i_a=2;
                    }
                """);
        String expected = """
                . = 0x100
                JMP     __START ; ProgramNode: 最初の実行文へ
        __START:
                MOV     #0x1000, R6     ; ProgramNode: 計算用スタック初期化
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む<[1行目,5文字目の'i_a']>
                MOV     -(R6), R0       ; addressToValue: アドレスを取り出して、内容を参照して、積む
                MOV     (R0), (R6)+     ; addressToValue:
                MOV     #3, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; ConditionEQ: ２数を取り出して、比べる
                MOV     -(R6), R1       ; ConditionEQ:
                MOV     #0x0001, R2     ; ConditionEQ: set true
                CMP     R0, R1  ; ConditionEQ: R1-R0 = R1-R0=0
                BRZ     EQ2     ; ConditionEQ
                CLR     R2              ; ConditionEQ: set false
        EQ2:    MOV     R2, (R6)+       ; ConditionEQ:
                MOV     -(R6), R0       ; StatementIF:真理値を取り出す
                BRZ     ELSEIF1 ;
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #0, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                JMP ENDIF1:     ; StatementIF:
        ELSEIF1:        ; StatementIF:
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     -(R6), R0       ; addressToValue: アドレスを取り出して、内容を参照して、積む
                MOV     (R0), (R6)+     ; addressToValue:
                MOV     #4, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; ConditionEQ: ２数を取り出して、比べる
                MOV     -(R6), R1       ; ConditionEQ:
                MOV     #0x0001, R2     ; ConditionEQ: set true
                CMP     R0, R1  ; ConditionEQ: R1-R0 = R1-R0=0
                BRZ     EQ4     ; ConditionEQ
                CLR     R2              ; ConditionEQ: set false
        EQ4:    MOV     R2, (R6)+       ; ConditionEQ:
                MOV     -(R6), R0       ; StatementIF:真理値を取り出す
                BRZ     ELSEIF3 ;
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #1, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                JMP ENDIF3:     ; StatementIF:
        ELSEIF3:        ; StatementIF:
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #2, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                JMP ENDIF3:     ; StatementIF:
        ENDIF3: ; StatementIF:
                JMP ENDIF1:     ; StatementIF:
        ENDIF1: ; StatementIF:
                HLT                     ; ProgramNode:
                .END                    ; ProgramNode:   
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenIfTest4() throws FatalErrorException {
        inputStream.setInputString("""
                    i_a = 54;
                    if (i_a == 3) {
                        i_a=0;
                    } else if (i_a == 4){	// キーワードelseifを導入した人は、そのように直すこと
                        i_a=1;
                    } else if (i_a ==54){	// キーワードelseifを導入した人は、そのように直すこと
                        i_a=2;
                    } else {
                        i_a=3;
                    }
                """);
        String expected = """
                . = 0x100
                JMP     __START ; ProgramNode: 最初の実行文へ
        __START:
                MOV     #0x1000, R6     ; ProgramNode: 計算用スタック初期化
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む<[1行目,1文字目の'i_a']>
                MOV     #54, (R6)+      ; Number: 数を積む<[1行目,7文字目の'54']>
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     -(R6), R0       ; addressToValue: アドレスを取り出して、内容を参照して、積む
                MOV     (R0), (R6)+     ; addressToValue:
                MOV     #3, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; ConditionEQ: ２数を取り出して、比べる
                MOV     -(R6), R1       ; ConditionEQ:
                MOV     #0x0001, R2     ; ConditionEQ: set true
                CMP     R0, R1  ; ConditionEQ: R1-R0 = R1-R0=0
                BRZ     EQ2     ; ConditionEQ
                CLR     R2              ; ConditionEQ: set false
        EQ2:    MOV     R2, (R6)+       ; ConditionEQ:
                MOV     -(R6), R0       ; StatementIF:真理値を取り出す
                BRZ     ELSEIF1 ;
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #0, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                JMP ENDIF1:     ; StatementIF:
        ELSEIF1:        ; StatementIF:
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     -(R6), R0       ; addressToValue: アドレスを取り出して、内容を参照して、積む
                MOV     (R0), (R6)+     ; addressToValue:
                MOV     #4, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; ConditionEQ: ２数を取り出して、比べる
                MOV     -(R6), R1       ; ConditionEQ:
                MOV     #0x0001, R2     ; ConditionEQ: set true
                CMP     R0, R1  ; ConditionEQ: R1-R0 = R1-R0=0
                BRZ     EQ4     ; ConditionEQ
                CLR     R2              ; ConditionEQ: set false
        EQ4:    MOV     R2, (R6)+       ; ConditionEQ:
                MOV     -(R6), R0       ; StatementIF:真理値を取り出す
                BRZ     ELSEIF3 ;
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #1, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                JMP ENDIF3:     ; StatementIF:
        ELSEIF3:        ; StatementIF:
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     -(R6), R0       ; addressToValue: アドレスを取り出して、内容を参照して、積む
                MOV     (R0), (R6)+     ; addressToValue:
                MOV     #54, (R6)+      ; Number: 数を積む
                MOV     -(R6), R0       ; ConditionEQ: ２数を取り出して、比べる
                MOV     -(R6), R1       ; ConditionEQ:
                MOV     #0x0001, R2     ; ConditionEQ: set true
                CMP     R0, R1  ; ConditionEQ: R1-R0 = R1-R0=0
                BRZ     EQ6     ; ConditionEQ
                CLR     R2              ; ConditionEQ: set false
        EQ6:    MOV     R2, (R6)+       ; ConditionEQ:
                MOV     -(R6), R0       ; StatementIF:真理値を取り出す
                BRZ     ELSEIF5 ;
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #2, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                JMP ENDIF5:     ; StatementIF:
        ELSEIF5:        ; StatementIF:
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #3, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                JMP ENDIF5:     ; StatementIF:
        ENDIF5: ; StatementIF:
                JMP ENDIF3:     ; StatementIF:
        ENDIF3: ; StatementIF:
                JMP ENDIF1:     ; StatementIF:
        ENDIF1: ; StatementIF:
                HLT                     ; ProgramNode:
                .END                    ; ProgramNode:
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenIfTest5() throws FatalErrorException {
        inputStream.setInputString("""
                    if (true) i_a=1;
                    else i_b=1;
                """);
        String expected = """
                . = 0x100
                JMP     __START ; ProgramNode: 最初の実行文へ
        __START:
                MOV     #0x1000, R6     ; ProgramNode: 計算用スタック初期化
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     -(R6), R0       ; StatementIF:真理値を取り出す
                BRZ     ELSEIF1 ;
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #1, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                JMP ENDIF1:     ; StatementIF:
        ELSEIF1:        ; StatementIF:
                MOV     #i_b, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #1, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                JMP ENDIF1:     ; StatementIF:
        ENDIF1: ; StatementIF:
                HLT                     ; ProgramNode:
                .END                    ; ProgramNode:
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenIfTest6() throws FatalErrorException {
        inputStream.setInputString("""
                    if (true) if (false) if (true) i_a=1;
                """);
        String expected = """
                . = 0x100
                JMP     __START ; ProgramNode: 最初の実行文へ
        __START:
                MOV     #0x1000, R6     ; ProgramNode: 計算用スタック初期化
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     -(R6), R0       ; StatementIF:真理値を取り出す
                BRZ     ENDIF1  ;
                MOV     #0x0000, (R6)+  ; Condition: false
                MOV     -(R6), R0       ; StatementIF:真理値を取り出す
                BRZ     ENDIF2  ;
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     -(R6), R0       ; StatementIF:真理値を取り出す
                BRZ     ENDIF3  ;
                MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                MOV     #1, (R6)+       ; Number: 数を積む
                MOV     -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入
                MOV     -(R6), R1       ; statementAssign:
                MOV     R0, (R1)        ; statementAssign:
                JMP ENDIF3:     ; StatementIF:
        ENDIF3: ; StatementIF:
                JMP ENDIF2:     ; StatementIF:
        ENDIF2: ; StatementIF:
                JMP ENDIF1:     ; StatementIF:
        ENDIF1: ; StatementIF:
                HLT                     ; ProgramNode:
                .END                    ; ProgramNode:
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}