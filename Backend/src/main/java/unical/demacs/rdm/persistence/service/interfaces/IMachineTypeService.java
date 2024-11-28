package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.entities.MachineType;

import java.util.List;
import java.util.Optional;

public interface IMachineTypeService {
    MachineType createMachineType(MachineTypeDTO machineTypeDTO);
    Optional<MachineType> getMachineTypeById(Long id);
    List<MachineType> getAllMachineTypes();
    void deleteMachineType(Long id);
    MachineType updateMachineType(Long id, MachineTypeDTO machineTypeDTO);
}
