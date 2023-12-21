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

public class CodeGenJudgeTest {
    // NT , judge

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
    public void conditionNTTest() throws FatalErrorException {
        inputStream.setInputString("""
                ! 2 >= i_a
        """);
        String expected = """
                    MOV     #2, (R6)+       ; Number: 数を積む
                    MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                    MOV     -(R6), R0       ; addressToValue: アドレスを取り出して、内容を参照して、積む
                    MOV     (R0), (R6)+     ; addressToValue:
                    MOV     -(R6), R0       ; ConditionGE: ２数を取り出して、比べる
                    MOV     -(R6), R1       ; ConditionGE:
                    MOV     #0x0001, R2     ; ConditionGE: set true
                    CMP     R1, R0  ; ConditionGE: R1>=R0 = R0-R1<=0
                    BRN     GE1     ; ConditionGE
                    BRZ     GE1     ; ConditionGE
                    CLR     R2              ; ConditionGE: set false
            GE1:    MOV     R2, (R6)+       ; ConditionGE:
                    MOV     -(R6), R0       ; ConditionNT: 真理値を取り出して反転
                    XOR     #0x0001, R0     ; ConditionNT: 反転
                    MOV     R0, (R6)+       ; ConditionNT:
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Judge(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void JudgeAndTest() throws FatalErrorException {
        inputStream.setInputString("""
                2 >= i_a && i_a >= 1
        """);
        String expected = """
                    MOV     #2, (R6)+       ; Number: 数を積む
                    MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                    MOV     -(R6), R0       ; addressToValue: アドレスを取り出して、内容を参照して、積む
                    MOV     (R0), (R6)+     ; addressToValue:
                    MOV     -(R6), R0       ; ConditionGE: ２数を取り出して、比べる
                    MOV     -(R6), R1       ; ConditionGE:
                    MOV     #0x0001, R2     ; ConditionGE: set true
                    CMP     R1, R0  ; ConditionGE: R1>=R0 = R0-R1<=0
                    BRN     GE1     ; ConditionGE
                    BRZ     GE1     ; ConditionGE
                    CLR     R2              ; ConditionGE: set false
            GE1:    MOV     R2, (R6)+       ; ConditionGE:
                    MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                    MOV     -(R6), R0       ; addressToValue: アドレスを取り出して、内容を参照して、積む
                    MOV     (R0), (R6)+     ; addressToValue:
                    MOV     #1, (R6)+       ; Number: 数を積む
                    MOV     -(R6), R0       ; ConditionGE: ２数を取り出して、比べる
                    MOV     -(R6), R1       ; ConditionGE:
                    MOV     #0x0001, R2     ; ConditionGE: set true
                    CMP     R1, R0  ; ConditionGE: R1>=R0 = R0-R1<=0
                    BRN     GE2     ; ConditionGE
                    BRZ     GE2     ; ConditionGE
                    CLR     R2              ; ConditionGE: set false
            GE2:    MOV     R2, (R6)+       ; ConditionGE:
                    MOV     -(R6), R0       ; JudgeAnd: 二つの真理値を取り出して論理積を取る
                    MOV     -(R6), R1       ; JudgeAnd:
                    AND     R0, R1  ; JudgeAnd:
                    MOV     R1, (R6)+       ; JudgeAnd:
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Judge(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void JudgeOrTest() throws FatalErrorException {
        inputStream.setInputString("""
                2 >= i_a || i_a >= 1
        """);
        String expected = """
                    MOV     #2, (R6)+       ; Number: 数を積む
                    MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                    MOV     -(R6), R0       ; addressToValue: アドレスを取り出して、内容を参照して、積む
                    MOV     (R0), (R6)+     ; addressToValue:
                    MOV     -(R6), R0       ; ConditionGE: ２数を取り出して、比べる
                    MOV     -(R6), R1       ; ConditionGE:
                    MOV     #0x0001, R2     ; ConditionGE: set true
                    CMP     R1, R0  ; ConditionGE: R1>=R0 = R0-R1<=0
                    BRN     GE1     ; ConditionGE
                    BRZ     GE1     ; ConditionGE
                    CLR     R2              ; ConditionGE: set false
            GE1:    MOV     R2, (R6)+       ; ConditionGE:
                    MOV     #i_a, (R6)+     ; Ident: 変数アドレスを積む
                    MOV     -(R6), R0       ; addressToValue: アドレスを取り出して、内容を参照して、積む
                    MOV     (R0), (R6)+     ; addressToValue:
                    MOV     #1, (R6)+       ; Number: 数を積む
                    MOV     -(R6), R0       ; ConditionGE: ２数を取り出して、比べる
                    MOV     -(R6), R1       ; ConditionGE:
                    MOV     #0x0001, R2     ; ConditionGE: set true
                    CMP     R1, R0  ; ConditionGE: R1>=R0 = R0-R1<=0
                    BRN     GE2     ; ConditionGE
                    BRZ     GE2     ; ConditionGE
                    CLR     R2              ; ConditionGE: set false
            GE2:    MOV     R2, (R6)+       ; ConditionGE:
                    MOV     -(R6), R0       ; JudgeOr: 二つの真理値を取り出して論理和を取る
                    MOV     -(R6), R1       ; JudgeOr:
                    OR      R0, R1  ; JudgeOr:
                    MOV     R1, (R6)+       ; JudgeOr:
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Judge(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void JudgeAndOrTest() throws FatalErrorException {
        inputStream.setInputString("""
                true || true && true
        """);
        String expected = """
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     -(R6), R0       ; JudgeAnd: 二つの真理値を取り出し て論理積を取る
                MOV     -(R6), R1       ; JudgeAnd:
                AND     R0, R1  ; JudgeAnd:
                MOV     R1, (R6)+       ; JudgeAnd:
                MOV     -(R6), R0       ; JudgeOr: 二つの真理値を取り出して論理和を取る
                MOV     -(R6), R1       ; JudgeOr:
                OR      R0, R1  ; JudgeOr:
                MOV     R1, (R6)+       ; JudgeOr:
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Judge(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void JudgePARTest() throws FatalErrorException {
        inputStream.setInputString("""
                !(true || true && !true)
        """);
        String expected = """
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     -(R6), R0       ; ConditionNT: 真理値を取り出して反転
                XOR     #0x0001, R0     ; ConditionNT: 反転
                MOV     R0, (R6)+       ; ConditionNT:
                MOV     -(R6), R0       ; JudgeAnd: 二つの真理値を取り出して論理積を取る
                MOV     -(R6), R1       ; JudgeAnd:
                AND     R0, R1  ; JudgeAnd:
                MOV     R1, (R6)+       ; JudgeAnd:
                MOV     -(R6), R0       ; JudgeOr: 二つの真理値を取り出して論理和を取る
                MOV     -(R6), R1       ; JudgeOr:
                OR      R0, R1  ; JudgeOr:
                MOV     R1, (R6)+       ; JudgeOr:
                MOV     -(R6), R0       ; ConditionNT: 真理値を取り出して反転
                XOR     #0x0001, R0     ; ConditionNT: 反転
                MOV     R0, (R6)+       ; ConditionNT:
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Judge(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void JudgeBARTest() throws FatalErrorException {
        inputStream.setInputString("""
                ![true || true && !true]
        """);
        String expected = """
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     #0x0001, (R6)+  ; Condition: true
                MOV     -(R6), R0       ; ConditionNT: 真理値を取り出して反転
                XOR     #0x0001, R0     ; ConditionNT: 反転
                MOV     R0, (R6)+       ; ConditionNT:
                MOV     -(R6), R0       ; JudgeAnd: 二つの真理値を取り出して論理積を取る
                MOV     -(R6), R1       ; JudgeAnd:
                AND     R0, R1  ; JudgeAnd:
                MOV     R1, (R6)+       ; JudgeAnd:
                MOV     -(R6), R0       ; JudgeOr: 二つの真理値を取り出して論理和を取る
                MOV     -(R6), R1       ; JudgeOr:
                OR      R0, R1  ; JudgeOr:
                MOV     R1, (R6)+       ; JudgeOr:
                MOV     -(R6), R0       ; ConditionNT: 真理値を取り出して反転
                XOR     #0x0001, R0     ; ConditionNT: 反転
                MOV     R0, (R6)+       ; ConditionNT:
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new Judge(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}
