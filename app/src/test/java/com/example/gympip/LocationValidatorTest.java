package com.example.gympip;
import org.junit.Test;
import static org.junit.Assert.*;


public class LocationValidatorTest {
    private final LocationValidator validator = new LocationValidator();

    /**
     * Testeaza un nume valid de oras care contine cratima.
     */
    @Test
    public void validCityName_shouldReturnTrue() {
        assertTrue(validator.isValidLocationName("Cluj-Napoca"));
    }

    /**
     * Testeaza un nume care contine cifre si ar trebui sa fie invalid.
     */
    @Test
    public void cityWithNumbers_shouldReturnFalse() {
        assertFalse(validator.isValidLocationName("Bucuresti123"));
    }

    /**
     * Testeaza un sir gol care ar trebui sa fie invalid.
     */
    @Test
    public void emptyLocation_shouldReturnFalse() {
        assertFalse(validator.isValidLocationName(""));
    }

    /**
     * Testeaza un sir null care ar trebui sa fie invalid.
     */
    @Test
    public void nullLocation_shouldReturnFalse() {
        assertFalse(validator.isValidLocationName(null));
    }

    /**
     * Testeaza un nume prea scurt (sub 4 caractere).
     */
    @Test
    public void shortLocationName_shouldReturnFalse() {
        assertFalse(validator.isValidLocationName("A"));
    }

    /**
     * Testeaza un nume valid de tara fara caractere speciale.
     */
    @Test
    public void validCountryNameWithAccents_shouldReturnTrue() {
        assertTrue(validator.isValidLocationName("Romania"));
    }
}
