package unical.demacs.rdm.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@Data

@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "scheduleBuilder")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne
    @JoinColumn(name = "machine_type_id", nullable = false)
    private MachineType machineType;

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

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", job=" + job +
                ", machineType=" + machineType +
                ", dueDate=" + dueDate +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", status=" + status +
                '}';
    }
}