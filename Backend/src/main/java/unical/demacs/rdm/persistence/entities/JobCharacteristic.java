package unical.demacs.rdm.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "JobCharacteristics")
@Data
public class JobCharacteristic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long characteristicId;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @Column(nullable = false)
    private String characteristicName;

    private String characteristicValue;
}