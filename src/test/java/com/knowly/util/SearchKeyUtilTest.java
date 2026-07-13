package com.knowly.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class SearchKeyUtilTest {

    @Test
    void generateReturnsNullForBlankInput() {
        assertNull(SearchKeyUtil.generate(null));
        assertNull(SearchKeyUtil.generate("   "));
    }

    @Test
    void generateNormalizesTextAndRemovesSpecialCharacters() {
        String result = SearchKeyUtil.generate("  Java & Spring Boot 2025! ");

        assertEquals("javaspringboot2025", result);
    }
}
