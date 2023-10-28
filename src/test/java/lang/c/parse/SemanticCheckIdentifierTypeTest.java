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

/**
 * Before Testing Semantic Check by using this testing class, All ParseTest must be passed.
 * Bacause this testing class uses parse method to create testing data.
 */
public class SemanticCheckIdentifierTypeTest {
    // Test for Ident Type and type conversion
    
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

    //  以下テストケースにおいて違うエラーメッセージのためテストが出来ない場合
    // 同じエラーメッセージのものだけでテストメソッドを分割してください．
    // (1) 整数型の扱い
    @Test
    public void semanticErrorForIntegerType()  {
        String[] testDataArr = { "*i_a", "i_a[3]", "&10 + ip_a", "10 - &i_a" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Expression.isFirst(firstToken), is(true));
            Expression cp = new Expression(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("Write down the output you have decided on here"));
            }
        } 
    }

    // (2) ポインタ型の扱い
    // ip_a[3] はCでは正当だが，この実験では不当にすること
    @Test
    public void semanticErrorForPointerType()  {
        String[] testDataArr = { "ip_a[3]", "&ip_a", "10 - ip_a", "*ip_a - &10" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Expression.isFirst(firstToken), is(true));
            Expression cp = new Expression(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("Write down the output you have decided on here"));
            }
        } 
    }

    @Test
    public void semanticErrorForIdentWithMinusSign() {
        String[] testDataArr = { "-ip_a" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Expression.isFirst(firstToken), is(true));
            Expression cp = new Expression(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("Write down the output you have decided on here"));
            }
        } 

    }
    // (3) 配列型の扱い
    // *ia_a はCでは正当だが，この実験では不当にすること
    @Test
    public void semanticErrorForIdentArrayType()  {
        String[] testDataArr = { "ia_a", "*ia_a", "ia_a[3] - &1", "1 - &ia_a[3]" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Expression.isFirst(firstToken), is(true));
            Expression cp = new Expression(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("Write down the output you have decided on here"));
            }
        } 
    }

    // (4) ポインタ配列型の扱い
    // *ipa_a はCでは正当だが，この実験では不当にすること
    @Test
    public void semanticErrorForPointerArrayType()  {
        String[] testDataArr = { "ipa_a", "*ipa_a", "ipa_a[3] + ipa_a[3]", "*ipa_a[3] - &100"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Expression.isFirst(firstToken), is(true));
            Expression cp = new Expression(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("Write down the output you have decided on here"));
            }
        } 
    }
}