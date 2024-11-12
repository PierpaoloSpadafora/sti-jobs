package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.dto.MachineTypeDTO;

import java.util.List;
import java.util.Optional;

public interface IMachineTypeService {
    MachineTypeDTO createMachineType(MachineTypeDTO machineTypeDTO);
    Optional<MachineTypeDTO> getMachineTypeById(Long id);
    List<MachineTypeDTO> getAllMachineTypes();
    void deleteMachineType(Long id);
    MachineTypeDTO updateMachineType(Long id, MachineTypeDTO machineTypeDTO);
}
