package unical.demacs.rdm.persistence.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unical.demacs.rdm.persistence.dto.MachineDTO;
import unical.demacs.rdm.persistence.entities.Machine;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.enums.MachineStatus;
import unical.demacs.rdm.persistence.repository.MachineRepository;
import unical.demacs.rdm.persistence.service.interfaces.IMachineService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MachineServiceImpl implements IMachineService {

    private final MachineRepository machineRepository;

    public MachineServiceImpl(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @Override
    public MachineDTO createMachine(MachineDTO machineDTO) {
        Machine machine = convertToEntity(machineDTO);
        machine.setCreatedAt(LocalDateTime.now());
        machine.setStatus(MachineStatus.AVAILABLE);
        Machine savedMachine = machineRepository.save(machine);
        return convertToDTO(savedMachine);
    }

    @Override
    public MachineDTO getMachineById(Long id) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine not found with id: " + id));
        return convertToDTO(machine);
    }

    @Override
    public MachineDTO updateMachine(Long id, MachineDTO machineDTO) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine not found with id: " + id));

        machine.setName(machineDTO.getName());
        machine.setDescription(machineDTO.getDescription());
        machine.setStatus(machineDTO.getStatus());
        machine.setUpdatedAt(LocalDateTime.now());

        if (machineDTO.getTypeId() != null) {
            MachineType machineType = new MachineType();
            machineType.setId(machineDTO.getTypeId());
            machine.setType(machineType);
        }

        Machine updatedMachine = machineRepository.save(machine);
        return convertToDTO(updatedMachine);
    }

    @Override
    public void deleteMachine(Long id) {
        if (!machineRepository.existsById(id)) {
            throw new RuntimeException("Machine not found with id: " + id);
        }
        machineRepository.deleteById(id);
    }

    @Override
    public List<MachineDTO> getAllMachines() {
        return machineRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MachineDTO> findById(Long id) {
        return machineRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public Optional<MachineDTO> findByIdOrName(Long id, String name) {
        Optional<Machine> machine = machineRepository.findByIdOrName(id, name);
        return machine.map(this::convertToDTO);
    }

    @Override
    public MachineDTO saveMachine(MachineDTO machineDTO) {
        Machine machine = convertToEntity(machineDTO);
        Machine savedMachine = machineRepository.save(machine);
        return convertToDTO(savedMachine);
    }

    private MachineDTO convertToDTO(Machine machine) {
        MachineDTO dto = new MachineDTO();
        dto.setId(machine.getId());
        dto.setName(machine.getName());
        dto.setDescription(machine.getDescription());
        dto.setStatus(machine.getStatus());
        dto.setTypeId(machine.getType() != null ? machine.getType().getId() : null);
        dto.setTypeName(machine.getType() != null ? machine.getType().getName() : null);
        dto.setCreatedAt(machine.getCreatedAt());
        dto.setUpdatedAt(machine.getUpdatedAt());
        return dto;
    }

    private Machine convertToEntity(MachineDTO machineDTO) {
        Machine machine = new Machine();
        machine.setId(machineDTO.getId());
        machine.setName(machineDTO.getName());
        machine.setDescription(machineDTO.getDescription());
        machine.setStatus(machineDTO.getStatus());
        machine.setCreatedAt(machineDTO.getCreatedAt());

        if (machineDTO.getTypeId() != null) {
            MachineType machineType = new MachineType();
            machineType.setId(machineDTO.getTypeId());
            machine.setType(machineType);
        }

        return machine;
    }

    @Override
    @Transactional
    public void deleteMachineByType(Long machineTypeId) {
        machineRepository.deleteByTypeId(machineTypeId);
    }
}
