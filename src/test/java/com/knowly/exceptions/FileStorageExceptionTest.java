package com.knowly.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

class FileStorageExceptionTest {

    @Test
    void constructorsPreserveMessageAndCause() {
        Throwable cause = new IllegalStateException("root cause");

        FileStorageException exception = new FileStorageException("storage failed", cause);

        assertEquals("storage failed", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
