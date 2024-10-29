package unical.demacs.rdm.persistence.service.implementation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.persistence.dto.MachineDTO;
import unical.demacs.rdm.persistence.entities.Machine;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.enums.MachineStatus;
import unical.demacs.rdm.persistence.repository.MachineRepository;
import unical.demacs.rdm.persistence.service.interfaces.IMachineService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MachineServiceImpl implements IMachineService {

    private final MachineRepository machineRepository;

    @Autowired
    public MachineServiceImpl(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @Override
    public MachineDTO createMachine(MachineDTO machineDTO) {
        Machine machine = convertToEntity(machineDTO);
        machine.setCreatedAt(LocalDateTime.now());
        machine.setStatus(MachineStatus.AVAILABLE); // Impostazione predefinita dello stato
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

        // Usa typeId direttamente senza verificare con MachineTypeRepository
        machine.getType().setId(machineDTO.getTypeId());

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

    // Metodo per convertire Machine a MachineDTO
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

    // Metodo per convertire MachineDTO a Machine
    private Machine convertToEntity(MachineDTO machineDTO) {
        Machine machine = new Machine();
        machine.setName(machineDTO.getName());
        machine.setDescription(machineDTO.getDescription());
        machine.setStatus(machineDTO.getStatus());
        machine.setCreatedAt(machineDTO.getCreatedAt());

        // Imposta solo l'ID di MachineType se fornito (senza caricare un oggetto completo)
        if (machineDTO.getTypeId() != null) {
            MachineType machineType = new MachineType();
            machineType.setId(machineDTO.getTypeId());
            machine.setType(machineType);
        }

        return machine;
    }
}
