package unical.demacs.rdm.persistence.service.implementation;

import com.google.common.util.concurrent.RateLimiter;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unical.demacs.rdm.config.exception.DuplicateMachineNameException;
import unical.demacs.rdm.config.exception.MachineException;
import unical.demacs.rdm.config.exception.TooManyRequestsException;
import unical.demacs.rdm.persistence.dto.MachineDTO;
import unical.demacs.rdm.persistence.entities.Machine;
import unical.demacs.rdm.persistence.repository.MachineRepository;
import unical.demacs.rdm.persistence.repository.MachineTypeRepository;
import unical.demacs.rdm.persistence.service.interfaces.IMachineService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MachineServiceImpl implements IMachineService {

    private static final Logger logger = LoggerFactory.getLogger(MachineServiceImpl.class);
    private final RateLimiter rateLimiter;
    private final MachineRepository machineRepository;
    private final MachineTypeRepository machineTypeRepository;

    @Transactional
    @Override
    public Machine createMachine(MachineDTO machineDTO) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Creating machine with name: {}", machineDTO.getName());
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for createMachine");
                throw new MachineException("Rate limit exceeded for createMachine");
            }
            Optional<Machine> existingMachine = machineRepository.findByName(machineDTO.getName());
            if (existingMachine.isPresent()) {
                logger.error("Duplicate machine name: {}", machineDTO.getName());
                throw new DuplicateMachineNameException("Esiste già una Machine con questo nome.");
            }

            var machineType = machineTypeRepository.findById(machineDTO.getTypeId())
                    .orElseThrow(() -> new MachineException("Machine type not found"));

            Machine machine = Machine.machineBuilder()
                    .name(machineDTO.getName())
                    .description(machineDTO.getDescription() != null ? machineDTO.getDescription() : "")
                    .type(machineType)
                    .build();

            machine = machineRepository.save(machine);
            logger.info("Machine with name {} created successfully", machineDTO.getName());
            return machine;
        } catch (DuplicateMachineNameException e) {
            throw e;
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            logger.error("Data integrity violation while creating machine: {}", machineDTO.getName(), e);
            throw new DuplicateMachineNameException("Esiste già una Machine con questo nome.");
        } catch (MachineException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating machine with name: {}", machineDTO.getName(), e);
            throw new MachineException("Errore nella creazione della Machine.");
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Machine getMachineById(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting machine by id: {}", id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getMachineById");
                throw new TooManyRequestsException();
            }

            return machineRepository.findById(id)
                    .orElseThrow(() -> new MachineException("Machine not found"));
        } catch (TooManyRequestsException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting machine by id: {}", id, e);
            throw new MachineException("Error getting machine");
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Machine updateMachine(Long id, MachineDTO machineDTO) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Updating machine with id: {}", id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for updateMachine");
                throw new TooManyRequestsException();
            }

            Machine machine = machineRepository.findById(id)
                    .orElseThrow(() -> new MachineException("Machine not found"));

            var machineType = machineTypeRepository.findById(machineDTO.getTypeId())
                    .orElseThrow(() -> new MachineException("Machine type not found"));

            machine.setName(machineDTO.getName());
            machine.setDescription(machineDTO.getDescription());
            machine.setType(machineType);

            machine = machineRepository.save(machine);
            logger.info("Machine with id {} updated successfully", id);
            return machine;
        } catch (TooManyRequestsException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating machine with id: {}", id, e);
            throw new MachineException("Error updating machine");
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Boolean deleteMachine(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Deleting machine with id: {}", id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for deleteMachine");
                throw new TooManyRequestsException();
            }

            Machine machine = machineRepository.findById(id)
                    .orElseThrow(() -> new MachineException("Machine not found"));

            machineRepository.delete(machine);
            logger.info("Machine with id {} deleted successfully", id);
            return true;
        } catch (TooManyRequestsException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting machine with id: {}", id, e);
            throw new MachineException("Error deleting machine");
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public List<Machine> getAllMachines() {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting all machines");
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getAllMachines");
                throw new TooManyRequestsException();
            }

            List<Machine> machines = machineRepository.findAll();
            logger.info("Found {} machines", machines.size());
            return machines;
        } catch (TooManyRequestsException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting all machines", e);
            throw new MachineException("Error getting all machines");
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

}