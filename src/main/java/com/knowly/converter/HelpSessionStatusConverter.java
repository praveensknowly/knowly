package com.knowly.converter;

import com.knowly.enums.HelpSessionStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class HelpSessionStatusConverter implements AttributeConverter<HelpSessionStatus, String> {

    @Override
    public String convertToDatabaseColumn(HelpSessionStatus status) {
        if (status == null) {
            return null;
        }
        // Store as legacy name for backward compatibility
        return status.getLegacyName();
    }

    @Override
    public HelpSessionStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return HelpSessionStatus.fromString(dbData);
    }
}
