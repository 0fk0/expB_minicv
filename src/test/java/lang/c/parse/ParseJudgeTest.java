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

public class ParseJudgeTest {
    
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

    // !condition
    @Test
    public void parseConditionNTTest1() throws FatalErrorException {
        inputStream.setInputString("!");
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(ConditionNT.isFirst(firstToken), is(true));
        ConditionNT ruleNumber = new ConditionNT(cpContext);
        CParseRule rule = ruleNumber;
        try {
            rule.parse(cpContext);
        } catch ( FatalErrorException e ) {
            assertThat(e.getMessage(), containsString("NTの後ろはconditionAll又は[judge]です"));
        }
    }

    @Test
    public void parseConditionNTTest2() throws FatalErrorException {
        inputStream.setInputString("!(");
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(ConditionNT.isFirst(firstToken), is(true));
        ConditionNT ruleNumber = new ConditionNT(cpContext);
        CParseRule rule = ruleNumber;
        try {
            rule.parse(cpContext);
        } catch ( FatalErrorException e ) {
            assertThat(e.getMessage(), containsString("NT(の後ろはjudgeです"));
        }
    }

    @Test
    public void parseConditionNTTest3() throws FatalErrorException {
        inputStream.setInputString("!(true");
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(ConditionNT.isFirst(firstToken), is(true));
        ConditionNT ruleNumber = new ConditionNT(cpContext);
        CParseRule rule = ruleNumber;
        try {
            rule.parse(cpContext);
        } catch ( FatalErrorException e ) {
            assertThat(e.getMessage(), containsString("NT(judgeの後ろは ) です"));
        }
    }
    
    @Test
    public void parseJudgeAndTest()  {
        String[] testDataArr = {"i_a == 1 &&"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Judge.isFirst(firstToken), is(true));
            Judge cp = new Judge(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("論理演算子&&の後ろはconditionAllです"));
            }
        }
    }

    @Test
    public void parseJudgeOrTest()  {
        String[] testDataArr = {"i_a == 1 ||"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Judge.isFirst(firstToken), is(true));
            Judge cp = new Judge(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("論理演算子||の後ろはconditionAllPriorityです"));
            }
        }
    }

    // conditionBrackets
    @Test
    public void parseConditionBracketsTest1()  {
        String[] testDataArr = {"["};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Judge.isFirst(firstToken), is(true));
            Judge cp = new Judge(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("[ の後ろはconditioinAllです"));
            }
        }
    }

    @Test
    public void parseConditionBracketsTest2()  {
        String[] testDataArr = {"[true && true"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Judge.isFirst(firstToken), is(true));
            Judge cp = new Judge(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("[conditionAll の後ろは ] です"));
            }
        }
    }
}
