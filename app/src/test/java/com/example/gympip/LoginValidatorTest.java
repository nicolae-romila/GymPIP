package com.example.gympip;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Clasa LoginValidatorTest contine teste unitare pentru clasa LoginValidator.
 * Testele verifica comportamentul metodelor de validare pentru email si parola.
 */
public class LoginValidatorTest {

    private final LoginValidator validator = new LoginValidator();

    /**
     * Testeaza daca un email valid returneaza true.
     */
    @Test
    public void validEmail_shouldReturnTrue() {
        boolean result = validator.isValidEmail("user@example.com");
        assertTrue(result);
    }

    /**
     * Testeaza daca un email invalid returneaza false.
     */
    @Test
    public void invalidEmail_shouldReturnFalse() {
        boolean result = validator.isValidEmail("user@");
        assertFalse(result);
    }

    /**
     * Testeaza daca o parola prea scurta returneaza false.
     */
    @Test
    public void shortPassword_shouldReturnFalse() {
        boolean result = validator.isValidPassword("123");
        assertFalse(result);
    }

    /**
     * Testeaza daca o parola suficient de lunga returneaza true.
     */
    @Test
    public void strongPassword_shouldReturnTrue() {
        boolean result = validator.isValidPassword("mypassword123");
        assertTrue(result);
    }
}
