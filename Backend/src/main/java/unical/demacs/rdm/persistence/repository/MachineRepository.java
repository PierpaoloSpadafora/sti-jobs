package unical.demacs.rdm.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unical.demacs.rdm.persistence.entities.Machine;
import unical.demacs.rdm.persistence.enums.MachineStatus;

import java.util.List;
import java.util.Optional;

public interface MachineRepository extends JpaRepository<Machine, Long> {
    List<Machine> findByStatus(MachineStatus status);
    List<Machine> findByTypeId(Long typeId);
    Optional<Machine> findById(Long id);
    Optional<Machine> findByIdOrName(Long id, String name);

    Optional<Machine> findByName(String name);
    void deleteByTypeId(Long typeId);
}
