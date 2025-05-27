package com.example.gympip;
import org.junit.Test;
import static org.junit.Assert.*;


public class MessageValidatorTest {
    private final MessageValidator validator = new MessageValidator();

    @Test
    public void validMessage_shouldReturnTrue() {
        String msg = "Salut! Ne vedem la sala diseara?";
        assertTrue(validator.isValid(msg));
    }

    @Test
    public void emptyMessage_shouldReturnFalse() {
        assertFalse(validator.isValid(""));
    }

    @Test
    public void whitespaceOnlyMessage_shouldReturnFalse() {
        assertFalse(validator.isValid("     "));
    }

    @Test
    public void nullMessage_shouldReturnFalse() {
        assertFalse(validator.isValid(null));
    }

    @Test
    public void longMessage_shouldReturnFalse() {
        String longMessage = "a".repeat(1001); // 1001 caractere
        assertFalse(validator.isValid(longMessage));
    }

    @Test
    public void boundaryLengthMessage_shouldReturnTrue() {
        String boundaryMessage = "a".repeat(1000); // exact 1000
        assertTrue(validator.isValid(boundaryMessage));
    }
}
