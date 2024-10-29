package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.dto.MachineDTO;
import java.util.List;

public interface IMachineService {
    MachineDTO createMachine(MachineDTO machineDTO);
    MachineDTO getMachineById(Long id);
    MachineDTO updateMachine(Long id, MachineDTO machineDTO);
    void deleteMachine(Long id);
    List<MachineDTO> getAllMachines();
}