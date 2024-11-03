// MachineTypeServiceImpl.java

package unical.demacs.rdm.persistence.service.implementation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import unical.demacs.rdm.config.exception.MachineException;
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.repository.MachineTypeRepository;
import unical.demacs.rdm.persistence.service.interfaces.IMachineTypeService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MachineTypeServiceImpl implements IMachineTypeService {

    private final MachineTypeRepository machineTypeRepository;

    @Override
    public MachineTypeDTO createMachineType(MachineTypeDTO machineTypeDTO) {
        MachineType existingMachineType = machineTypeRepository.findByName(machineTypeDTO.getName()).orElse(null);
        if (existingMachineType != null) {
            return convertToDTO(existingMachineType);
        }

        MachineType machineType = new MachineType();
        machineType.setName(machineTypeDTO.getName());
        machineType.setDescription(machineTypeDTO.getDescription());
        machineType = machineTypeRepository.save(machineType);
        return convertToDTO(machineType);
    }

    @Override
    public Optional<MachineTypeDTO> getMachineTypeById(Long id) {
        return machineTypeRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public List<MachineTypeDTO> getAllMachineTypes() {
        return machineTypeRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMachineType(Long id) {
        if (!machineTypeRepository.existsById(id)) {
            throw new MachineException("No machine type found");
        }
        machineTypeRepository.deleteById(id);
    }

    @Override
    public List<MachineTypeDTO> parseMachineTypesFromJson(MultipartFile file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<MachineTypeDTO> machineTypeDTOList = objectMapper.readValue(file.getInputStream(), new TypeReference<List<MachineTypeDTO>>() {});
        return machineTypeDTOList;
    }

    @Override
    public ByteArrayResource exportMachineTypes() {
        List<MachineTypeDTO> machineTypeDTOs = getAllMachineTypes();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            byte[] data = objectMapper.writeValueAsBytes(machineTypeDTOs);
            return new ByteArrayResource(data);
        } catch (IOException e) {
            throw new RuntimeException("Error exporting machine types to JSON", e);
        }
    }

    private MachineTypeDTO convertToDTO(MachineType machineType) {
        return new MachineTypeDTO(machineType.getId(), machineType.getName(), machineType.getDescription());
    }
}
