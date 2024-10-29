package unical.demacs.rdm.persistence.service.implementation;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.config.exception.MachineException;
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.repository.MachineTypeRepository;
import unical.demacs.rdm.persistence.service.interfaces.IMachineTypeService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MachineTypeServiceImpl implements IMachineTypeService {

    public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private MachineTypeRepository machineTypeRepository;

    @Override
    public MachineTypeDTO createMachineType(MachineTypeDTO machineTypeDTO) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Attempting to create machine type with name: {}", machineTypeDTO.getName());
        try {
            MachineType existingMachineType = machineTypeRepository.findByName(machineTypeDTO.getName()).orElse(null);
            if (existingMachineType != null) {
                logger.info("Machine type with name {} already exists", machineTypeDTO.getName());
                return convertToDTO(existingMachineType);
            }

            MachineType machineType = new MachineType();
            machineType.setName(machineTypeDTO.getName());
            machineType.setDescription(machineTypeDTO.getDescription());
            machineType = machineTypeRepository.save(machineType);
            logger.info("Machine type with name {} created successfully", machineTypeDTO.getName());
            return convertToDTO(machineType);
        } catch (Exception e) {
            logger.error("Error creating machine type with name: {}", machineTypeDTO.getName(), e);
            throw new MachineException("Error creating machine type");
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Optional<MachineTypeDTO> getMachineTypeById(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Attempting to get machine type by id: {}", id);
        try {
            return Optional.ofNullable(machineTypeRepository.findById(id)
                    .map(this::convertToDTO)
                    .orElseThrow(() -> {
                        logger.warn("Machine type not found for id: {}", id);
                        return new MachineException("No machine type found");
                    }));
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public List<MachineTypeDTO> getAllMachineTypes() {
        logger.info("++++++START REQUEST++++++");
        try {
            return machineTypeRepository.findAll()
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public void deleteMachineType(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Attempting to delete machine type by id: {}", id);
        try {
            if (!machineTypeRepository.existsById(id)) {
                logger.warn("Machine type not found for id: {}", id);
                throw new MachineException("No machine type found");
            }
            machineTypeRepository.deleteById(id);
            logger.info("Machine type with id {} deleted successfully", id);
        } catch (Exception e) {
            logger.error("Error deleting machine type with id: {}", id, e);
            throw new MachineException("Error deleting machine type");
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    private MachineTypeDTO convertToDTO(MachineType machineType) {
        return new MachineTypeDTO(machineType.getId(), machineType.getName(), machineType.getDescription());
    }
}
