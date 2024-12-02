package unical.demacs.rdm.persistence.service.implementation;

import com.google.common.util.concurrent.RateLimiter;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.config.exception.MachineException;
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.repository.MachineTypeRepository;
import unical.demacs.rdm.persistence.service.interfaces.IMachineTypeService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MachineTypeServiceImpl implements IMachineTypeService {

    private static final Logger logger = LoggerFactory.getLogger(MachineTypeServiceImpl.class);
    private final RateLimiter rateLimiter;
    private final MachineTypeRepository machineTypeRepository;

    @Override
    public MachineType createMachineType(MachineTypeDTO machineTypeDTO) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Creating machine type with name: {}", machineTypeDTO.getName());
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for createMachineType");
                throw new MachineException("Rate limit exceeded for createMachineType");
            }
            Optional<MachineType> existingMachineType = machineTypeRepository.findByName(machineTypeDTO.getName());
            if (existingMachineType.isPresent()) {
                logger.error("Duplicate machine type name: {}", machineTypeDTO.getName());
                throw new MachineException("Esiste già un Machine Type con questo nome.");
            }
            MachineType machineType = MachineType.buildMachineType()
                    .name(machineTypeDTO.getName())
                    .description(machineTypeDTO.getDescription())
                    .build();
            machineTypeRepository.save(machineType);
            logger.info("Machine type with name {} created successfully", machineTypeDTO.getName());
            return machineType;
        } catch (MachineException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation while creating machine type: {}", machineTypeDTO.getName(), e);
            throw new MachineException("Esiste già un Machine Type con questo nome.");
        } catch (Exception e) {
            logger.error("Error creating machine type with name: {}", machineTypeDTO.getName(), e);
            throw new MachineException("Errore nella creazione del Machine Type.");
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Optional<MachineType> getMachineTypeById(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting machine type with id: " + id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getMachineTypeById");
                throw new MachineException("Rate limit exceeded for getMachineTypeById");
            }
            Optional<MachineType> machineType = machineTypeRepository.findById(id);
            if(machineType.isEmpty()){
                logger.error("Machine type with id {} not found", id);
                throw new MachineException("Machine type not found");
            }
            logger.info("Machine type with id {} found successfully", id);
            return machineType;
        } catch (Exception e) {
            logger.error("Error getting machine type with id: {}", id, e);
            throw new MachineException("Error getting machine type");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public List<MachineType> getAllMachineTypes() {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting all machine types");
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getAllMachineTypes");
                throw new MachineException("Rate limit exceeded for getAllMachineTypes");
            }
            List<MachineType> machineTypes = machineTypeRepository.findAll();
            logger.info("All machine types found successfully");
            return machineTypes;
        } catch (Exception e) {
            logger.error("Error getting all machine types", e);
            throw new MachineException("Error getting all machine types");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public void deleteMachineType(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Deleting machine type with id: " + id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for deleteMachineType");
                throw new MachineException("Rate limit exceeded for deleteMachineType");
            }
            Optional<MachineType> machineType = machineTypeRepository.findById(id);
            if(machineType.isEmpty()){
                logger.error("Machine type with id {} not found", id);
                throw new MachineException("Machine type not found");
            }
            machineTypeRepository.deleteById(id);
            logger.info("Machine type with id {} deleted successfully", id);
        } catch (Exception e) {
            logger.error("Error deleting machine type with id: {}", id, e);
            throw new MachineException("Error deleting machine type");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public MachineType updateMachineType(Long id, MachineTypeDTO machineTypeDTO) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Updating machine type with id: " + id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for updateMachineType");
                throw new MachineException("Rate limit exceeded for updateMachineType");
            }
            Optional<MachineType> machineType = machineTypeRepository.findById(id);
            if(machineType.isEmpty()){
                logger.error("Machine type with id {} not found", id);
                throw new MachineException("Machine type not found");
            }
            machineType.get().setName(machineTypeDTO.getName());
            machineType.get().setDescription(machineTypeDTO.getDescription());
            machineTypeRepository.save(machineType.get());
            logger.info("Machine type with id {} updated successfully", id);
            return machineType.get();
        } catch (Exception e) {
            logger.error("Error updating machine type with id: {}", id, e);
            throw new MachineException("Error updating machine type");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }
}
