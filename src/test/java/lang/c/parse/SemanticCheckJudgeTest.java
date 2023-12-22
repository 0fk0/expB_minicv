package lang.c.parse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.FatalErrorException;
import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;
import lang.c.CParseContext;
import lang.c.CToken;
import lang.c.CTokenRule;
import lang.c.CTokenizer;
import lang.c.CType;

/**
 * Before Testing Semantic Check by using this testing class, All ParseTest must be passed.
 * Bacause this testing class uses parse method to create testing data.
 */
public class SemanticCheckJudgeTest {
    // Test that Condition node's semanticCheck is valid.
    
    InputStreamForTest inputStream;
    PrintStreamForTest outputStream;
    PrintStreamForTest errorOutputStream;
    CTokenizer tokenizer;
    IOContext context;
    CParseContext cpContext;

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

    void resetEnvironment() {
        tearDown();
        setUp();
    }

    // 正当のテストコード例(Judge)
    @Test
    public void semanticCheckTrueExample() throws FatalErrorException {
        String[] testDataArr = {"true && false || i_b == 1", "![true && true]"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Judge.isFirst(firstToken), is(true));
            Judge cp = new Judge(cpContext);

            cp.parse(cpContext);
            cp.semanticCheck(cpContext);
            assertThat(cp.getCType(), is(CType.getCType(CType.T_bool)));
            String errorOutput = errorOutputStream.getPrintBufferString();
            assertThat(errorOutput, is(""));
        }
    }

    // 正当のテストコード例(conditionAll)
    @Test
    public void semanticCheckTrueExample2() throws FatalErrorException {
        String[] testDataArr = {"!true", "[-1 <= i_a && i_a < 1]"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, ConditionAll.isFirst(firstToken), is(true));
            ConditionAll cp = new ConditionAll(cpContext);

            cp.parse(cpContext);
            cp.semanticCheck(cpContext);
            assertThat(cp.getCType(), is(CType.getCType(CType.T_bool)));
            String errorOutput = errorOutputStream.getPrintBufferString();
            assertThat(errorOutput, is(""));
        }
    }
}
