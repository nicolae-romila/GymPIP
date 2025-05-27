package com.example.gympip;
import org.junit.Test;
import static org.junit.Assert.*;


public class LocationValidatorTest {
    private final LocationValidator validator = new LocationValidator();

    @Test
    public void validCityName_shouldReturnTrue() {
        assertTrue(validator.isValidLocationName("Cluj-Napoca"));
    }

    @Test
    public void cityWithNumbers_shouldReturnFalse() {
        assertFalse(validator.isValidLocationName("Bucuresti123"));
    }

    @Test
    public void emptyLocation_shouldReturnFalse() {
        assertFalse(validator.isValidLocationName(""));
    }

    @Test
    public void nullLocation_shouldReturnFalse() {
        assertFalse(validator.isValidLocationName(null));
    }

    @Test
    public void shortLocationName_shouldReturnFalse() {
        assertFalse(validator.isValidLocationName("A"));
    }

    @Test
    public void validCountryNameWithAccents_shouldReturnTrue() {
        assertTrue(validator.isValidLocationName("Romania"));
    }
}
