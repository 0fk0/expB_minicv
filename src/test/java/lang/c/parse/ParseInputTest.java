package lang.c.parse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;

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

public class ParseInputTest {

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

    @Test
    public void parseValid() {
        String[] testDataArr = {"input i_a;"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementInput.isFirst(firstToken), is(true));
            StatementInput cp = new StatementInput(cpContext);

            try {
                cp.parse(cpContext);
            } catch ( FatalErrorException e ) {
                fail("Failed with " + testData + ". Please modify this Testcase to pass.");
            }
        }
    }

    @Test
    public void parseInvalid1() {
        String[] testDataArr = {"input i_b"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementInput.isFirst(firstToken), is(true));
            StatementInput cp = new StatementInput(cpContext);

            try {
                cp.parse(cpContext);
                fail("Error should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("入力文の最後には;が必要です"));
            }
        }
    }

    @Test
    public void parseInvalid2() {
        String[] testDataArr = {"input 3;", "input;"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementInput.isFirst(firstToken), is(true));
            StatementInput cp = new StatementInput(cpContext);

            try {
                cp.parse(cpContext);
                fail("Error should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("INPUTの後ろにはprimaryが必要です"));
            }
        }
    }
}
