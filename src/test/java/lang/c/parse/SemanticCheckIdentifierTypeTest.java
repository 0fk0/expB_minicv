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
    public void semanticErrorForIntegerType1()  {
        String[] testDataArr = { "*i_a" };
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
                assertThat(e.getMessage(), containsString("*の後に型[int]は許可されません"));
            }
        } 
    }

    @Test
    public void semanticErrorForIntegerType2()  {
        String[] testDataArr = {"i_a[3]"};
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
                assertThat(e.getMessage(), containsString("型[int]の識別子の後にarrayは続きません"));
            }
        } 
    }

    @Test
    public void semanticErrorForIntegerType3()  {
        String[] testDataArr = { "&10 + ip_a" };
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
                assertThat(e.getMessage(), containsString("左辺の型[int*]と右辺の型[int*]は足せません"));
            }
        } 
    }

    @Test
    public void semanticErrorForIntegerType4()  {
        String[] testDataArr = { "10 - &i_a", "10 - ip_a", "*ip_a - &10" };
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
                assertThat(e.getMessage(), containsString("左辺の型[int]と右辺の型[int*]は引けません"));
            }
        } 
    }

    // (2) ポインタ型の扱い
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
                assertThat(e.getMessage(), containsString("-の後に型[int*]は許可されません"));
            }
        } 
    }

    @Test
    public void semanticErrorForPointerType1()  {
        String[] testDataArr = { "ip_a[3]"};
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
                assertThat(e.getMessage(), containsString("型[int*]の識別子の後にarrayは続きません"));
            }
        } 
    }

    @Test
    public void semanticErrorForPointerType2()  {
        String[] testDataArr = { "&ip_a" };
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
                assertThat(e.getMessage(), containsString("&の後の参照型(ポインタのポインタ)は許可されません"));
            }
        } 
    }

    @Test
    public void semanticErrorForPointerType3()  {
        String[] testDataArr = { "10 - ip_a", "*ip_a - &10" };
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
                assertThat(e.getMessage(), containsString("左辺の型[int]と右辺の型[int*]は引けません"));
            }
        } 
    }

    // (3) 配列型の扱い
    // *ia_a はCでは正当だが，この実験では不当にすること
    @Test
    public void semanticErrorForIdentArrayType1()  {
        String[] testDataArr = { "ia_a", "*ia_a" };
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
                assertThat(e.getMessage(), containsString("配列型[int[]]の識別子の後にarrayが必要です"));
            }
        } 
    }

    @Test
    public void semanticErrorForIdentArrayType2()  {
        String[] testDataArr = { "ia_a[3] - &1" };
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
                assertThat(e.getMessage(), containsString("左辺の型[int[]]と右辺の型[int*]は引けません"));
            }
        } 
    }

    @Test
    public void semanticErrorForIdentArrayType3()  {
        String[] testDataArr = { "1 - &ia_a[3]" };
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
                assertThat(e.getMessage(), containsString("左辺の型[int]と右辺の型[int*]は引けません"));
            }
        } 
    }



    // (4) ポインタ配列型の扱い
    // *ipa_a はCでは正当だが，この実験では不当にすること
    @Test
    public void semanticErrorForPointerArrayType1()  {
        String[] testDataArr = { "ipa_a", "*ipa_a" };
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
                assertThat(e.getMessage(), containsString("配列型[int*[]]の識別子の後にarrayが必要です"));
            }
        } 
    }

    @Test
    public void semanticErrorForPointerArrayType2()  {
        String[] testDataArr = { "ipa_a[3] + ipa_a[3]" };
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
                assertThat(e.getMessage(), containsString("左辺の型[int*[]]と右辺の型[int*[]]は足せません"));
            }
        } 
    }

    @Test
    public void semanticErrorForPointerArrayType3()  {
        String[] testDataArr = { "*ipa_a[3] - &100"};
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
                assertThat(e.getMessage(), containsString("左辺の型[int]と右辺の型[int*]は引けません"));
            }
        } 
    }
}
