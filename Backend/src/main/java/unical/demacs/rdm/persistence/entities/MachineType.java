package unical.demacs.rdm.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "machine_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
    private List<Machine> machines;

    @OneToMany(mappedBy = "requiredMachineType", fetch = FetchType.LAZY)
    private List<Job> jobs;
}
