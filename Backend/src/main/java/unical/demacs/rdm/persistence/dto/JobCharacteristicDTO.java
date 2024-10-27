package unical.demacs.rdm.persistence.dto;

import lombok.Data;

@Data
public class JobCharacteristicDTO {
    private Long characteristicId;
    private Long jobId;
    private String characteristicName;
    private String characteristicValue;
}
