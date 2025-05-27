package com.example.gympip;
import org.junit.Test;
import static org.junit.Assert.*;

public class LoginValidatorTest {
    private final LoginValidator validator = new LoginValidator();

    @Test
    public void validEmail_shouldReturnTrue() {
        boolean result = validator.isValidEmail("user@example.com");
        assertTrue(result);
    }

    @Test
    public void invalidEmail_shouldReturnFalse() {
        boolean result = validator.isValidEmail("user@");
        assertFalse(result);
    }

    @Test
    public void shortPassword_shouldReturnFalse() {
        boolean result = validator.isValidPassword("123");
        assertFalse(result);
    }

    @Test
    public void strongPassword_shouldReturnTrue() {
        boolean result = validator.isValidPassword("mypassword123");
        assertTrue(result);
    }
}
