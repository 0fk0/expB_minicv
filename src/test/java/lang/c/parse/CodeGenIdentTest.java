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

public class CodeGenIdentTest {
    // Test for UnsignedFactor node of "cv04".
    // i_a &i_a ip_a *ip_a ia_a[3] &ia_a[3] ipa_a[3] *ipa_a[3]
    
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
    public void codeGenInt() throws FatalErrorException {
        inputStream.setInputString("i_a");
        String expected[] = {
            "	MOV	#i_a, (R6)+ ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0; addressToValue: アドレスを取り出して、内容を参照して、積む",
			"   MOV (R0), (R6)+; addressToValue:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new UnsignedFactor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenAMPInt() throws FatalErrorException {
        inputStream.setInputString("&i_a");
        String expected[] = {
            "	MOV	#i_a, (R6)+ ; Ident: 変数アドレスを積む"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new UnsignedFactor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenPint() throws FatalErrorException {
        inputStream.setInputString("ip_a");
        String expected[] = {
            "	MOV	#ip_a, (R6)+ ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0; addressToValue: アドレスを取り出して、内容を参照して、積む",
			"   MOV (R0), (R6)+; addressToValue:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new UnsignedFactor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenMultPint() throws FatalErrorException {
        inputStream.setInputString("*ip_a");
        String expected[] = {
            "	MOV	#ip_a, (R6)+ ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0; addressToValue: アドレスを取り出して、内容を参照して、積む",
			"   MOV (R0), (R6)+; addressToValue:",
            "   MOV -(R6), R0 ; PrimaryMult: アドレスを取り出して、内容を参照して、積む",
			"   MOV (R0), (R6)+ ; PrimaryMult:"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new UnsignedFactor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenArray() throws FatalErrorException {
        inputStream.setInputString("ia_a[3]");
        String expected[] = {
            "	MOV	#ia_a, (R6)+ ; Ident: 変数アドレスを積む",
            "	MOV	#3, (R6)+ ; Ident: 変数アドレスを積む",
            "   MOV -(R6), R0 ; Variable: 配列名とindexを取り出して配列先頭アドレスとindex分を足し、内容を参照して、積む",
        	"   MOV -(R6), R1 ; Variable:",
			"   ADD R0, R1 ; Variable:",
			"   MOV R1, (R6)+ ; Variable",
            "   MOV -(R6), R0; addressToValue: アドレスを取り出して、内容を参照して、積む",
			"   MOV (R0), (R6)+; addressToValue:",
        };

        // Check only code portion, not validate comments
        CParseRule rule = new UnsignedFactor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // Please copy and paste the above code and add the specified test case to the following

}