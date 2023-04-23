package com.amigoscode.testing.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PhoneNumberValidatorTest {

    private PhoneNumberValidator underTest;

    @BeforeEach
    void setUp() {
        underTest = new PhoneNumberValidator();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "+4470000000000, true",
            "4470000000000,  false",
            "3170000000000,  false",
            "447000000000,   false",
            "44700000000000, false",
            ",               false",
            " ,              false"
    }, emptyValue = " ")
    void itShouldValidatePhoneNumber(String phoneNumber, boolean expected) {
        // When
        boolean isValid = underTest.test(phoneNumber);
        // Then
        assertThat(isValid).isEqualTo(expected);
    }

}
