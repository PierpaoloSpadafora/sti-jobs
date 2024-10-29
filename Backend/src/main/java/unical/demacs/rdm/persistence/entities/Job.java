package unical.demacs.rdm.persistence.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.Duration;

import lombok.Builder;
import lombok.Data;
import unical.demacs.rdm.persistence.enums.JobPriority;
import unical.demacs.rdm.persistence.enums.JobStatus;

@Entity
@Table(name = "jobs")
@Builder(builderMethodName = "buildJob")
@Data
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User assignee;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobPriority priority;

    @Column(nullable = false)
    private Duration duration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "machine_type_id", nullable = false)
    private MachineType requiredMachineType;

}