package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.dto.MachineDTO;
import unical.demacs.rdm.persistence.entities.Machine;

import java.util.List;

public interface IMachineService {
    Machine createMachine(MachineDTO machineDTO);
    Machine getMachineById(Long id);
    Machine updateMachine(Long id, MachineDTO machineDTO);
    Boolean deleteMachine(Long id);
    List<Machine> getAllMachines();
}
