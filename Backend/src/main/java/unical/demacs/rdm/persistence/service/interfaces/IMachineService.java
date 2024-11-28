package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.dto.MachineDTO;
import unical.demacs.rdm.persistence.entities.Machine;

import java.util.List;
import java.util.Optional;

public interface IMachineService {
    Machine createMachine(MachineDTO machineDTO);
    Machine getMachineById(Long id);
    Machine updateMachine(Long id, MachineDTO machineDTO);
    Boolean deleteMachine(Long id);
    List<Machine> getAllMachines();

    Optional<Machine> findById(Long id);
    Optional<Machine> findByIdOrName(Long id, String name);
}
