package unical.demacs.rdm.persistence.dto;

import unical.demacs.rdm.persistence.enums.MachineStatus;
import java.time.LocalDateTime;

public class MachineDTO {
    private Long id;
    private String name;
    private String description;
    private MachineStatus status;
    private Long typeId; // ID per riferire il MachineType
    private String typeName; // Nome per riferire il MachineType (opzionale)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Costruttori, getter e setter
    public MachineDTO() {
    }

    public MachineDTO(Long id, String name, String description, MachineStatus status, Long typeId, String typeName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.typeId = typeId;
        this.typeName = typeName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getter e Setter per tutti i campi
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MachineStatus getStatus() {
        return status;
    }

    public void setStatus(MachineStatus status) {
        this.status = status;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
