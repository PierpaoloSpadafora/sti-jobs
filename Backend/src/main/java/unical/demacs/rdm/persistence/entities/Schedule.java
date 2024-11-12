package unical.demacs.rdm.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@Data

@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @JoinColumn(name = "machine_id", nullable = false)
    private String machineType;

    @Column
    private LocalDateTime dueDate;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private Long duration;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    @PrePersist
    protected void onCreate() {
        status = ScheduleStatus.SCHEDULED;
    }
}