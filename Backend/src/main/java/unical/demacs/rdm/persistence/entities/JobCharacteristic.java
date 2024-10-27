package unical.demacs.rdm.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "job-characteristics")
@Data
public class JobCharacteristic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @Column(nullable = false)
    private String characteristicName;

    @Column
    private String characteristicValue;
}