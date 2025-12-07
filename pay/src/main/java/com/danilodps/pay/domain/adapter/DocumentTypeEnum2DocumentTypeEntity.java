package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.model.DocumentTypeEntity;
import com.danilodps.pay.domain.model.enums.DocumentTypeEnum;

public class DocumentTypeEnum2DocumentTypeEntity {

    private DocumentTypeEnum2DocumentTypeEntity(){}

    public static DocumentTypeEntity convert(DocumentTypeEnum documentTypeEnum){
        return DocumentTypeEntity.builder()
                .numSequenceId(documentTypeEnum.getId())
                .shortName(documentTypeEnum.getShortName())
                .description(documentTypeEnum.getDescription())
                .build();
    }
}
