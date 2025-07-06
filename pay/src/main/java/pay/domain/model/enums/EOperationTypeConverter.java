package pay.domain.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EOperationTypeConverter implements AttributeConverter<EOperationType, String> {

    @Override
    public String convertToDatabaseColumn(EOperationType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getShortName();
    }

    @Override
    public EOperationType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return EOperationType.fromShortName(dbData);
    }
}