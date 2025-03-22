
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringLengthTest {
    @Test
    public void testStringLength(){
        String hello = "Hello, world!!!!";
        assertTrue(hello.length() > 15);
    }
}
