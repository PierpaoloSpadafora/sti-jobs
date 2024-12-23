package unical.demacs.rdm.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unical.demacs.rdm.persistence.enums.MachineStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "machines", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name", name = "uq_machine_name")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "machineBuilder")
public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MachineStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "machine_type_id", nullable = false)
    private MachineType machine_type_id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = MachineStatus.AVAILABLE;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Machine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", machine_type_id=" + machine_type_id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
