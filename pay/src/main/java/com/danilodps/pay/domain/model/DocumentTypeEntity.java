package com.danilodps.pay.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "TB_DOCUMENT")
public class DocumentTypeEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "DOCUMENT_ID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID documentId;

    @Column(name = "NUM_SEQUENCE_ID")
    private Long numSequenceId;

    @Column(name = "DOCUMENT_NAME", length = 10)
    private String shortName;

    @Column(name = "DOCUMENT_DESCRIPTION", length = 25)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROFILE_ID", nullable = false)
    private ProfileEntity profileEntity;

}