package unical.demacs.rdm.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unical.demacs.rdm.persistence.entities.MachineType;

import java.util.Optional;

@Repository
public interface MachineTypeRepository extends JpaRepository<MachineType, Long> {
    Optional<MachineType> findById(Long id);
}
