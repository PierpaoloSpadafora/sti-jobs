package unical.demacs.rdm.persistence.service.implementation;

import com.google.common.util.concurrent.RateLimiter;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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

    @Override
    public Machine createMachine(MachineDTO machineDTO) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Creating machine with name: " + machineDTO.getName());
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for createMachine");
                throw new TooManyRequestsException();
            }
            if(machineTypeRepository.findById(machineDTO.getTypeId()).isEmpty()){
                logger.error("Machine type with id {} not found", machineDTO.getTypeId());
                throw new RuntimeException("Machine type not found");
            }
            Machine machine = Machine.machineBuilder()
                    .name(machineDTO.getName())
                    .description(machineDTO.getDescription())
                    .type(machineTypeRepository.findById(machineDTO.getTypeId()).orElse(null))
                    .build();
            machineRepository.save(machine);
            logger.info("Machine with name {} created successfully", machineDTO.getName());
            return machine;
        } catch (Exception e) {
            logger.error("Error creating machine with name: {}", machineDTO.getName(), e);
            throw new RuntimeException("Error creating machine");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Machine getMachineById(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting machine by id: " + id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getMachineById");
                throw new TooManyRequestsException();
            }
            Optional<Machine> machine = machineRepository.findById(id);
            if (machine.isEmpty()) {
                logger.error("Machine with id {} not found", id);
                throw new RuntimeException("Machine not found");
            }
            logger.info("Machine with id {} found", id);
            return machine.get();
        } catch (Exception e) {
            logger.error("Error getting machine by id: {}", id, e);
            throw new RuntimeException("Error getting machine");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Machine updateMachine(Long id, MachineDTO machineDTO) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Updating machine with id: " + id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getUserByEmail");
                throw new TooManyRequestsException();
            }
            Optional<Machine> machine = machineRepository.findById(id);
            if (machine.isEmpty()) {
                logger.error("Machine with id {} not found", id);
                throw new RuntimeException("Machine not found");
            }
            if(machineTypeRepository.findById(machineDTO.getTypeId()).isEmpty()){
                logger.error("Machine type with id {} not found", machineDTO.getTypeId());
                throw new RuntimeException("Machine type not found");
            }
            machine.get().setName(machineDTO.getName());
            machine.get().setDescription(machineDTO.getDescription());
            machine.get().setType(machineTypeRepository.findById(machineDTO.getTypeId()).orElse(null));
            machineRepository.save(machine.get());
            logger.info("Machine with id {} updated successfully", id);
            return machine.get();
        } catch (Exception e) {
            logger.error("Error updating machine with id: {}", id, e);
            throw new RuntimeException("Error updating machine");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Boolean deleteMachine(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Deleting machine with id: " + id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for deleteMachine");
                throw new TooManyRequestsException();
            }
            Optional<Machine> machine = machineRepository.findById(id);
            if (machine.isEmpty()) {
                logger.error("Machine with id {} not found", id);
                throw new RuntimeException("Machine not found");
            }
            machineRepository.delete(machine.get());
            logger.info("Machine with id {} deleted successfully", id);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting machine with id: {}", id, e);
            throw new RuntimeException("Error deleting machine");
        }
        finally {
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
            logger.info("All machines found");
            return machines;
        } catch (Exception e) {
            logger.error("Error getting all machines", e);
            throw new RuntimeException("Error getting all machines");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Optional<Machine> findById(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting machine by id: " + id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for findById");
                throw new TooManyRequestsException();
            }
            Optional<Machine> machine = machineRepository.findById(id);
            if (machine.isEmpty()) {
                logger.error("Machine with id {} not found", id);
                throw new RuntimeException("Machine not found");
            }
            logger.info("Machine with id {} found", id);
            return machine;
        } catch (Exception e) {
            logger.error("Error getting machine by id: {}", id, e);
            throw new RuntimeException("Error getting machine");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Optional<Machine> findByIdOrName(Long id, String name) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting machine by id: " + id + " or name: " + name);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for findByIdOrName");
                throw new TooManyRequestsException();
            }
            Optional<Machine> machine = machineRepository.findByIdOrName(id, name);
            if (machine.isEmpty()) {
                logger.error("Machine with id {} or name {} not found", id, name);
                throw new RuntimeException("Machine not found");
            }
            logger.info("Machine with id {} or name {} found", id, name);
            return machine;
        } catch (Exception e) {
            logger.error("Error getting machine by id: {} or name: {}", id, name, e);
            throw new RuntimeException("Error getting machine");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }


}
