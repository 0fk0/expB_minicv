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

public class CodeGenStatementAssignTest {

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
    public void assign() throws FatalErrorException {
        inputStream.setInputString("i_A = i_B;");
        String expected[] = {
            "   MOV #i_A, (R6)+     ; Ident: 変数アドレスを積む",
            "   MOV #i_B, (R6)+     ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0       ; addressToValue: アドレスを取り出して、内容を参照して、積む",
            "   MOV (R0), (R6)+     ; addressToValue:",
            "   MOV -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入",
            "   MOV -(R6), R1       ; statementAssign:",
            "   MOV R0, (R1)        ; statementAssign:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // (1) 整数型の扱い
    @Test
    public void assignInt() throws FatalErrorException {
        inputStream.setInputString("i_a=0;");
        String expected[] = {
            "   MOV #i_a, (R6)+     ; Ident: 変数アドレスを積む",
            "   MOV #0, (R6)+       ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入",
            "   MOV -(R6), R1       ; statementAssign:",
            "   MOV R0, (R1)        ; statementAssign:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // (2) ポインタ型の扱い
    // Please copy and paste the above and add the specified test case to the following
    @Test
    public void assignPint1() throws FatalErrorException {
        inputStream.setInputString("ip_a=&1;");
        String expected[] = {
            "   MOV #ip_a, (R6)+     ; Ident: 変数アドレスを積む",
            "   MOV #1, (R6)+       ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入",
            "   MOV -(R6), R1       ; statementAssign:",
            "   MOV R0, (R1)        ; statementAssign:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void assignPint2() throws FatalErrorException {
        inputStream.setInputString("*ip_a=1;");
        String expected[] = {
            "	MOV	#ip_a, (R6)+    ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0       ; PrimaryMult: アドレスを取り出して、内容を参照して、積む",
			"   MOV (R0), (R6)+     ; PrimaryMult:",
            "   MOV #1, (R6)+       ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入",
            "   MOV -(R6), R1       ; statementAssign:",
            "   MOV R0, (R1)        ; statementAssign:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void assignPint3() throws FatalErrorException {
        inputStream.setInputString("ip_a=&1;*ip_a=1;");
        String expected[] = {
            "   . = 0x100",
            "   JMP __START            ; ProgramNode: 最初の実行文へ",
            "__START:",
		    "   MOV #0x1000, R6     ; ProgramNode: 計算用スタック初期化",
            "   MOV #ip_a, (R6)+    ; Ident: 変数アドレスを積む",
            "   MOV #1, (R6)+       ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入",
            "   MOV -(R6), R1       ; statementAssign:",
            "   MOV R0, (R1)        ; statementAssign:",
            "	MOV	#ip_a, (R6)+    ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0       ; PrimaryMult: アドレスを取り出して、内容を参照して、積む",
			"   MOV (R0), (R6)+     ; PrimaryMult:",
            "   MOV #1, (R6)+       ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入",
            "   MOV -(R6), R1       ; statementAssign:",
            "   MOV R0, (R1)        ; statementAssign:",
            "   HLT                 ; ProgramNode:",
		    "   .END                ; ProgramNode:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // (3) 配列型の扱い
    @Test
    public void assignArray() throws FatalErrorException {
        inputStream.setInputString("ia_a[3]=1;");
        String expected[] = {
            "	MOV	#ia_a, (R6)+    ; Ident: 変数アドレスを積む",
            "	MOV	#3, (R6)+       ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0       ; Variable: 配列名とindexを取り出して配列先頭アドレスとindex分を足し、内容を参照して、積む",
        	"   MOV -(R6), R1       ; Variable:",
			"   ADD R0, R1          ; Variable:",
			"   MOV R1, (R6)+       ; Variable",
            "   MOV #1, (R6)+       ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入",
            "   MOV -(R6), R1       ; statementAssign:",
            "   MOV R0, (R1)        ; statementAssign:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // (4) ポインタ配列型の扱い
    // Please copy and paste the above code and add the specified test case to the following
    @Test
    public void assignPintArray1() throws FatalErrorException {
        inputStream.setInputString("ipa_a[3]=&3;");
        String expected[] = {
            "	MOV	#ipa_a, (R6)+    ; Ident: 変数アドレスを積む",
            "	MOV	#3, (R6)+       ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0       ; Variable: 配列名とindexを取り出して配列先頭アドレスとindex分を足し、内容を参照して、積む",
        	"   MOV -(R6), R1       ; Variable:",
			"   ADD R0, R1          ; Variable:",
			"   MOV R1, (R6)+       ; Variable",
            "   MOV #3, (R6)+       ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入",
            "   MOV -(R6), R1       ; statementAssign:",
            "   MOV R0, (R1)        ; statementAssign:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void assignPintArray2() throws FatalErrorException {
        inputStream.setInputString("*ipa_a[3]=3;");
        String expected[] = {
            "	MOV	#ipa_a, (R6)+    ; Ident: 変数アドレスを積む",
            "	MOV	#3, (R6)+       ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0       ; Variable: 配列名とindexを取り出して配列先頭アドレスとindex分を足し、内容を参照して、積む",
        	"   MOV -(R6), R1       ; Variable:",
			"   ADD R0, R1          ; Variable:",
			"   MOV R1, (R6)+       ; Variable",
            "   MOV -(R6), R0       ; PrimaryMult: アドレスを取り出して、内容を参照して、積む",
			"   MOV (R0), (R6)+     ; PrimaryMult:",
            "   MOV #3, (R6)+       ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0       ; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入",
            "   MOV -(R6), R1       ; statementAssign:",
            "   MOV R0, (R1)        ; statementAssign:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}
