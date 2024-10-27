package unical.demacs.rdm.persistence.service.implementation;

import com.google.common.util.concurrent.RateLimiter;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.config.exception.TooManyRequestsException;
import unical.demacs.rdm.config.exception.MachineNotFoundException;
import unical.demacs.rdm.config.exception.MachineException;
import unical.demacs.rdm.persistence.entities.Machine;
import unical.demacs.rdm.persistence.repository.MachineRepository;
import unical.demacs.rdm.persistence.service.interfaces.IMachineService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MachineServiceImpl implements IMachineService {
    private static final Logger logger = LoggerFactory.getLogger(MachineServiceImpl.class);
    private MachineRepository machineRepository;
    private final RateLimiter rateLimiter;

    @Override
    public Machine createMachine(Machine machine) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Creating new machine: {}", machine.getName());
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for createMachine");
                throw new TooManyRequestsException();
            }
            try {
                return machineRepository.save(machine);
            } catch (Exception e) {
                logger.error("Error creating machine", e);
                throw new MachineException("Error creating machine");
            }
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Optional<Machine> getMachineById(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Fetching machine with id: {}", id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getMachineById");
                throw new TooManyRequestsException();
            }
            return Optional.ofNullable(machineRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Machine not found for id: {}", id);
                        return new MachineNotFoundException("Machine not found with id: " + id);
                    }));
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public List<Machine> getAllMachines() {
        logger.info("++++++START REQUEST++++++");
        logger.info("Fetching all machines");
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getAllMachines");
                throw new TooManyRequestsException();
            }
            List<Machine> machines = machineRepository.findAll();
            if (machines.isEmpty()) {
                logger.warn("No machines found");
                throw new MachineNotFoundException("No machines found in the system");
            }
            return machines;
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Machine updateMachine(Machine machine) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Updating machine with id: {}", machine.getId());
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for updateMachine");
                throw new TooManyRequestsException();
            }
            if (!machineRepository.existsById(machine.getId())) {
                logger.warn("Machine not found for update with id: {}", machine.getId());
                throw new MachineNotFoundException("Machine not found with id: " + machine.getId());
            }
            try {
                return machineRepository.save(machine);
            } catch (Exception e) {
                logger.error("Error updating machine", e);
                throw new MachineException("Error updating machine");
            }
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public void deleteMachine(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Deleting machine with id: {}", id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for deleteMachine");
                throw new TooManyRequestsException();
            }
            if (!machineRepository.existsById(id)) {
                logger.warn("Machine not found for deletion with id: {}", id);
                throw new MachineNotFoundException("Machine not found with id: " + id);
            }
            try {
                machineRepository.deleteById(id);
            } catch (Exception e) {
                logger.error("Error deleting machine", e);
                throw new MachineException("Error deleting machine");
            }
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }
}