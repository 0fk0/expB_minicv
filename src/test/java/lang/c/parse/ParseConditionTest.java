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

public class ParseConditionTest {
    
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

    // == の打ち間違い	
    @Test
    public void parseConditionComparisonOpTest1() throws FatalErrorException {
        inputStream.setInputString("1 = 2");
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(Condition.isFirst(firstToken), is(true));
        Condition ruleNumber = new Condition(cpContext);
        CParseRule rule = ruleNumber;
        try {
            rule.parse(cpContext);
        } catch ( FatalErrorException e ) {
            assertThat(e.getMessage(), containsString("比較演算子がありません"));
        }
    }

    @Test
    public void parseConditionComparisonOpTest2()  {
        String[] testDataArr = {"i_a ==", "i_a >=", "i_a >", "i_a <=", "i_a <", "i_a !="};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Condition.isFirst(firstToken), is(true));
            Condition cp = new Condition(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("比較演算子の後ろはexpressionです"));
            }
        }
    }
    
    // 条件になってない
    @Test
    public void notHaveComparisonOperater() throws FatalErrorException {
        inputStream.setInputString("2");
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(Condition.isFirst(firstToken), is(true));
        Condition ruleNumber = new Condition(cpContext);
        CParseRule rule = ruleNumber;
        try {
            rule.parse(cpContext);
        } catch ( FatalErrorException e ) {
            assertThat(e.getMessage(), containsString("比較演算子がありません"));
        }
    }
}
