package unical.demacs.rdm.persistence.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "Jobs")
@Data
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(columnDefinition = "TEXT DEFAULT 'DA COMPLETARE'")
    private String status;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    private String priority;

    private LocalDateTime dueDate;

    @ManyToOne
    @JoinColumn(name = "machine_id")
    private Machine machine;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}