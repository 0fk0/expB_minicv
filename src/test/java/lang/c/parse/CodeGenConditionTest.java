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

public class CodeGenConditionTest {
    // Test for Conditions of "true, false, LT, LE, GT, GE, EQ, NE".

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
    public void conditionTRUE() throws FatalErrorException {
        inputStream.setInputString("true");
        String expected[] = {
            "   MOV #0x0001, (R6)+; Condition: true",
            "   MOV R2, (R6)+; Condition: true/false"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionFALSE() throws FatalErrorException {
        inputStream.setInputString("false");
        String expected[] = {
            "   MOV #0x0000, (R6)+; Condition: false",
            "   MOV R2, (R6)+; Condition: true/false"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionLT() throws FatalErrorException {
        inputStream.setInputString("1<2");
        String expected[] = {
            "   MOV #1, (R6)+   ;",
            "   MOV #2, (R6)+   ;",
            "   MOV -(R6), R0   ; ConditionLT: ２数を取り出して、比べる",
			"   MOV -(R6), R1   ; ConditionLT:",
			"   MOV #0x0001, R2 ; ConditionLT: set true",
			"   CMP R0, R1      ; ConditionLT: R1<R0 = R1-R0<0",
			"   BRN LT1          ; ConditionLT",
			"   CLR R2          ; ConditionLT: set false",
			"LT1:MOV R2, (R6)+   ; ConditionLT:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // Please copy and paste the above code and add the specified test case to the following
    @Test
    public void conditionLE() throws FatalErrorException {
        inputStream.setInputString("1<=2");
        String expected[] = {
            "   MOV #1, (R6)+   ;",
            "   MOV #2, (R6)+   ;",
            "   MOV -(R6), R0   ; ConditionLE: ２数を取り出して、比べる",
			"   MOV -(R6), R1   ; ConditionLE:",
			"   MOV #0x0001, R2 ; ConditionLE: set true",
			"   CMP R0, R1      ; ConditionLE: R1<=R0 = R1-R0<=0",
			"   BRN LE1         ; ConditionLE",
			"   BRZ LE1         ; ConditionLE",
			"   CLR R2          ; ConditionLE: set false",
			"LE1:MOV R2, (R6)+  ; ConditionLE:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionGT() throws FatalErrorException {
        inputStream.setInputString("1>2");
        String expected[] = {
            "   MOV #1, (R6)+   ;",
            "   MOV #2, (R6)+   ;",
            "   MOV -(R6), R0   ; ConditionGT: ２数を取り出して、比べる",
			"   MOV -(R6), R1   ; ConditionGT:",
			"   MOV #0x0001, R2 ; ConditionGT: set true",
			"   CMP R1, R0      ; ConditionGT: R1>R0 = R0-R1>0",
			"   BRN GT1         ; ConditionGT",
			"   CLR R2          ; ConditionGT: set false",
			"GT1: MOV R2, (R6)+ ; ConditionGT:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionGE() throws FatalErrorException {
        inputStream.setInputString("1>=2");
        String expected[] = {
            "   MOV #1, (R6)+   ;",
            "   MOV #2, (R6)+   ;",
            "   MOV -(R6), R0   ; ConditionGE: ２数を取り出して、比べる",
			"   MOV -(R6), R1   ; ConditionGE:",
			"   MOV #0x0001, R2 ; ConditionGE: set true",
			"   CMP R1, R0      ; ConditionGE: R1>=R0 = R0-R1<=0",
			"   BRN GE1         ; ConditionGE",
			"   BRZ GE1         ; ConditionGE",
			"   CLR R2          ; ConditionGE: set false",
			"GE1:MOV R2, (R6)+  ; ConditionGE:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionEQ() throws FatalErrorException {
        inputStream.setInputString("1==2");
        String expected[] = {
            "   MOV #1, (R6)+   ;",
            "   MOV #2, (R6)+   ;",
            "   MOV -(R6), R0   ; ConditionEQ: ２数を取り出して、比べる",
			"   MOV -(R6), R1   ; ConditionEQ:",
			"   MOV #0x0001, R2 ; ConditionEQ: set true",
			"   CMP R0, R1      ; ConditionEQ: R1-R0 = R1-R0=0",
			"   BRZ EQ1         ; ConditionEQ",
			"   CLR R2          ; ConditionEQ: set false",
			"EQ1:MOV R2, (R6)+  ; ConditionEQ:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionNE() throws FatalErrorException {
        inputStream.setInputString("1!=2");
        String expected[] = {
            "   MOV #1, (R6)+   ;",
            "   MOV #2, (R6)+   ;",
            "   MOV -(R6), R0   ; ConditionNE: ２数を取り出して、比べる",
			"   MOV -(R6), R1   ; ConditionNE:",
			"   MOV #0x0001, R2 ; ConditionNE: set true",
			"   CMP R0, R1      ; ConditionNE: R1!=R0 = R1-R0!=0",
			"   BNZ NE1         ; ConditionNE",
			"   CLR R2          ; ConditionNE: set false",
			"NE1:MOV R2, (R6)+  ; ConditionNE:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}
