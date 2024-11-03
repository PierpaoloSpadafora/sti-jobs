// IMachineTypeService.java

package unical.demacs.rdm.persistence.service.interfaces;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IMachineTypeService {
    MachineTypeDTO createMachineType(MachineTypeDTO machineTypeDTO);
    Optional<MachineTypeDTO> getMachineTypeById(Long id);
    List<MachineTypeDTO> getAllMachineTypes();
    void deleteMachineType(Long id);

    List<MachineTypeDTO> parseMachineTypesFromJson(MultipartFile file) throws IOException;
    ByteArrayResource exportMachineTypes();
}
