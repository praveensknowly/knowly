package com.knowly.dto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import org.springframework.web.multipart.MultipartFile;

class EditDtoTest {

    @Test
    void setLanguagesCsvParsesAndDeduplicatesValues() {
        EditDto dto = new EditDto();

        dto.setLanguages("Java, Spring, Spring, Boot");

        assertEquals(List.of("Java", "Spring", "Boot"), dto.getLanguages());
    }

    @Test
    void setLanguagesWithNullReturnsEmptyList() {
        EditDto dto = new EditDto();

        dto.setLanguages((String) null);

        assertTrue(dto.getLanguages().isEmpty());
    }

    @Test
    void setProfilePictureStoresMultipartFile() {
        EditDto dto = new EditDto();
        MultipartFile file = mock(MultipartFile.class);

        dto.setProfilePicture(file);

        assertEquals(file, dto.getProfilePicture());
    }
}
