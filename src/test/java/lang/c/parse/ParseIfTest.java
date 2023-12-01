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
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenRule;
import lang.c.CTokenizer;

public class ParseIfTest {

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
    public void parseValid1() {
        String[] testDataArr = {"if (true) { i_a=1; i_b=2; }",
                                "if (true) { i_a=1; i_b=2; } else { i_a=2; i_b=3;}",
                                "if (true) { i_a=1; i_b=2; } else if ( true ) { i_a=2; } else { i_a=3; }",
                                "if (true) { if (true) { if (true) { i_a=1; i_b=2; }}}"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementIf.isFirst(firstToken), is(true));
            StatementIf cp = new StatementIf(cpContext);

            try {
                cp.parse(cpContext);
            } catch ( FatalErrorException e ) {
                fail("Failed with " + testData + ". Please modify this Testcase to pass.");
            }
        }
    }

    @Test
    public void parseValid2() {
        String[] testDataArr = {"if (true) i_a=1;",
                                "if (true) i_a=1; else i_a=2;",
                                "if (true) if (true) if (true) i_a=3;"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementIf.isFirst(firstToken), is(true));
            StatementIf cp = new StatementIf(cpContext);

            try {
                cp.parse(cpContext);
            } catch ( FatalErrorException e ) {
                fail("Failed with " + testData + ". Please modify this Testcase to pass.");
            }
        }
    }

    @Test
    public void parseInvalid1() {
        String[] testDataArr = {"if"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementIf.isFirst(firstToken), is(true));
            StatementIf cp = new StatementIf(cpContext);

            try {
                cp.parse(cpContext);
                fail("Error should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("ifの後には(条件式)が必要です"));
            }
        }
    }

    @Test
    public void parseInvalid2() {
        String[] testDataArr = {"if (true);"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementIf.isFirst(firstToken), is(true));
            StatementIf cp = new StatementIf(cpContext);

            try {
                cp.parse(cpContext);
                fail("Error should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("if(条件式)の後にはstatementが必要です"));
            }
        }
    }

    @Test
    public void parseInvalid3() {
        String[] testDataArr = {"if (true) i_a=1; else"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementIf.isFirst(firstToken), is(true));
            StatementIf cp = new StatementIf(cpContext);

            try {
                cp.parse(cpContext);
                fail("Error should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("elseの後にはstatementが必要です"));
            }
        }
    }
}
