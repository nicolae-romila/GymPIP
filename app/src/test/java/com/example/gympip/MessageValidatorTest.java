package com.example.gympip;
import org.junit.Test;
import static org.junit.Assert.*;


public class MessageValidatorTest {
    private final MessageValidator validator = new MessageValidator();

    /**
     * Testeaza un mesaj valid cu text normal.
     */
    @Test
    public void validMessage_shouldReturnTrue() {
        String msg = "Salut! Ne vedem la sala diseara?";
        assertTrue(validator.isValid(msg));
    }

    /**
     * Testeaza un mesaj complet gol.
     */
    @Test
    public void emptyMessage_shouldReturnFalse() {
        assertFalse(validator.isValid(""));
    }

    /**
     * Testeaza un mesaj care contine doar spatii.
     */
    @Test
    public void whitespaceOnlyMessage_shouldReturnFalse() {
        assertFalse(validator.isValid("     "));
    }

    /**
     * Testeaza un mesaj null.
     */
    @Test
    public void nullMessage_shouldReturnFalse() {
        assertFalse(validator.isValid(null));
    }

    /**
     * Testeaza un mesaj care depaseste limita de 1000 de caractere.
     */
    @Test
    public void longMessage_shouldReturnFalse() {
        String longMessage = "a".repeat(1001); // 1001 caractere
        assertFalse(validator.isValid(longMessage));
    }

    /**
     * Testeaza un mesaj care are exact 1000 de caractere.
     */
    @Test
    public void boundaryLengthMessage_shouldReturnTrue() {
        String boundaryMessage = "a".repeat(1000); // exact 1000
        assertTrue(validator.isValid(boundaryMessage));
    }
}
