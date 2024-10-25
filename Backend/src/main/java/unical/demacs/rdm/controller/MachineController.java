package unical.demacs.rdm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unical.demacs.rdm.persistence.dto.MachineDTO;
import unical.demacs.rdm.persistence.entities.Machine;
import unical.demacs.rdm.persistence.service.implementation.MachineServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/machines")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "machine-controller", description = "Operations related to machine management")
public class MachineController {
    private final MachineServiceImpl machineService;
    private final ModelMapper modelMapper;

    @PostMapping
    @Operation(summary = "Create a new machine")
    public ResponseEntity<MachineDTO> createMachine(@RequestBody MachineDTO machineDTO) {
        return ResponseEntity.ok(modelMapper.map(
                machineService.createMachine(modelMapper.map(machineDTO, Machine.class)),
                MachineDTO.class));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get machine by ID")
    public ResponseEntity<MachineDTO> getMachineById(@PathVariable Long id) {
        return machineService.getMachineById(id)
                .map(machine -> ResponseEntity.ok(modelMapper.map(machine, MachineDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all machines")
    public ResponseEntity<List<MachineDTO>> getAllMachines() {
        List<MachineDTO> machines = machineService.getAllMachines().stream()
                .map(machine -> modelMapper.map(machine, MachineDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(machines);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a machine")
    public ResponseEntity<MachineDTO> updateMachine(@PathVariable Long id, @RequestBody MachineDTO machineDTO) {
        machineDTO.setMachineId(id);
        return ResponseEntity.ok(modelMapper.map(
                machineService.updateMachine(modelMapper.map(machineDTO, Machine.class)),
                MachineDTO.class));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a machine")
    public ResponseEntity<Void> deleteMachine(@PathVariable Long id) {
        machineService.deleteMachine(id);
        return ResponseEntity.ok().build();
    }
}