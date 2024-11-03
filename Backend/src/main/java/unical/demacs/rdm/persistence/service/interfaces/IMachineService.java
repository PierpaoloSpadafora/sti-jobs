// IMachineService.java

package unical.demacs.rdm.persistence.service.interfaces;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;
import unical.demacs.rdm.persistence.dto.MachineDTO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IMachineService {
    MachineDTO createMachine(MachineDTO machineDTO);
    MachineDTO getMachineById(Long id);
    MachineDTO updateMachine(Long id, MachineDTO machineDTO);
    void deleteMachine(Long id);
    List<MachineDTO> getAllMachines();

    List<MachineDTO> parseMachinesFromJson(MultipartFile file) throws IOException;
    ByteArrayResource exportMachines();

    Optional<MachineDTO> findById(Long id);
    Optional<MachineDTO> findByIdOrName(Long id, String name);
    MachineDTO saveMachine(MachineDTO machineDTO);
}
