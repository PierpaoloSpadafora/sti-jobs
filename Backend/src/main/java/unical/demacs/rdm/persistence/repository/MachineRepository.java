package unical.demacs.rdm.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unical.demacs.rdm.persistence.entities.Machine;
import java.util.Optional;

public interface MachineRepository extends JpaRepository<Machine, Long> {
    Optional<Machine> findById(Long id);
    Optional<Machine> findByName(String name);
}
