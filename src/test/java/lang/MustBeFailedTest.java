package lang;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class MustBeFailedTest {
    
    @Test
    public void testMustNotBeFailed() {
        String expected = "MUST BE FAILED THIS TEST.";
        String actual = "MUST BE FAILED THIS TEST.";
        assertThat(actual, is(expected));
    }

}
