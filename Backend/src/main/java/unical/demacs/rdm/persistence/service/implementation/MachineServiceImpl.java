package unical.demacs.rdm.persistence.service.implementation;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.persistence.entities.Machine;
import unical.demacs.rdm.persistence.repository.MachineRepository;
import unical.demacs.rdm.persistence.service.interfaces.IMachineService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MachineServiceImpl implements IMachineService {
    private static final Logger logger = LoggerFactory.getLogger(MachineServiceImpl.class);
    private final MachineRepository machineRepository;

    @Override
    public Machine createMachine(Machine machine) {
        logger.info("Creating new machine: {}", machine.getName());
        return machineRepository.save(machine);
    }

    @Override
    public Optional<Machine> getMachineById(Long id) {
        logger.info("Fetching machine with id: {}", id);
        return machineRepository.findById(id);
    }

    @Override
    public List<Machine> getAllMachines() {
        logger.info("Fetching all machines");
        return machineRepository.findAll();
    }

    @Override
    public Machine updateMachine(Machine machine) {
        logger.info("Updating machine with id: {}", machine.getMachineId());
        return machineRepository.save(machine);
    }

    @Override
    public void deleteMachine(Long id) {
        logger.info("Deleting machine with id: {}", id);
        machineRepository.deleteById(id);
    }
}