package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.entities.Machine;

import java.util.List;
import java.util.Optional;

public interface IMachineService {
    Machine createMachine(Machine machine);
    Optional<Machine> getMachineById(Long id);
    List<Machine> getAllMachines();
    Machine updateMachine(Machine machine);
    void deleteMachine(Long id);
}
