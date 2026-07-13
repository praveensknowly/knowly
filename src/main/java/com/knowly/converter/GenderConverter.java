package com.knowly.converter;

import com.knowly.enums.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender gender) {
        if (gender == null) {
            return null;
        }
        return gender.name();
    }

    @Override
    public Gender convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            return Gender.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle case where database has different case (e.g., "Male" instead of "MALE")
            for (Gender gender : Gender.values()) {
                if (gender.name().equalsIgnoreCase(dbData)) {
                    return gender;
                }
            }
            throw new IllegalArgumentException("No enum constant com.knowly.enums.Gender." + dbData);
        }
    }
}
