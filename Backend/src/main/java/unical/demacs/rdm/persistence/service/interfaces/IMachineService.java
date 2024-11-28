package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.dto.MachineDTO;

import java.util.List;
import java.util.Optional;

public interface IMachineService {
    MachineDTO createMachine(MachineDTO machineDTO);
    MachineDTO getMachineById(Long id);
    MachineDTO updateMachine(Long id, MachineDTO machineDTO);
    void deleteMachine(Long id);
    void deleteMachineByType(Long machineTypeId);
    List<MachineDTO> getAllMachines();

    Optional<MachineDTO> findById(Long id);
    Optional<MachineDTO> findByIdOrName(Long id, String name);
    MachineDTO saveMachine(MachineDTO machineDTO);
}
