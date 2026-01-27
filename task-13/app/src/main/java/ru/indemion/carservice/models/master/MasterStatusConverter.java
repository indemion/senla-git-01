package ru.indemion.carservice.models.master;

import jakarta.persistence.AttributeConverter;

public class MasterStatusConverter implements AttributeConverter<MasterStatus, String> {
    @Override
    public String convertToDatabaseColumn(MasterStatus masterStatus) {
        return masterStatus.toString();
    }

    @Override
    public MasterStatus convertToEntityAttribute(String s) {
        return MasterStatus.parse(s);
    }
}
