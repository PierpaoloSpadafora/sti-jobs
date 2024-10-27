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
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column
    private String status;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @Column
    private String priority;

    @Column
    private LocalDateTime dueDate;

    @ManyToOne
    @JoinColumn(name = "machine_id")
    private Machine machine;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;
}