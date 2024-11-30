package unical.demacs.rdm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unical.demacs.rdm.persistence.dto.MachineDTO;
import unical.demacs.rdm.persistence.service.implementation.MachineServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/machine", produces = "application/json")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "machine-controller", description = "Operations related to machine management.")
public class MachineController {

    private final MachineServiceImpl machineServiceImpl;
    private final ModelMapper modelMapper;

    @Operation(summary = "Create a new machine", description = "Create a new machine with specified details.",
            tags = {"machine-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Machine created successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MachineDTO.class))),
            @ApiResponse(responseCode = "409", description = "Machine with the given name already exists.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @PostMapping
    public ResponseEntity<MachineDTO> createMachine(@Valid @RequestBody MachineDTO machineDTO) {
        MachineDTO createdMachine = modelMapper.map(machineServiceImpl.createMachine(machineDTO), MachineDTO.class);
        return new ResponseEntity<>(createdMachine, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a machine", description = "Update a machine's details by ID.",
            tags = {"machine-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Machine updated successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MachineDTO.class))),
            @ApiResponse(responseCode = "404", description = "Machine not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "409", description = "Machine with the given name already exists.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @PutMapping(path = "/{id}")
    public ResponseEntity<MachineDTO> updateMachine(@PathVariable("id") Long id, @Valid @RequestBody MachineDTO machineDTO) {
        MachineDTO updatedMachine = modelMapper.map(machineServiceImpl.updateMachine(id, machineDTO), MachineDTO.class);
        return ResponseEntity.ok(updatedMachine);
    }

    @Operation(summary = "Get machine by ID", description = "Retrieve a machine using its ID.",
            tags = {"machine-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Machine retrieved successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MachineDTO.class))),
            @ApiResponse(responseCode = "404", description = "Machine not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<MachineDTO> getMachineById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(modelMapper.map(machineServiceImpl.getMachineById(id), MachineDTO.class));
    }

    @Operation(summary = "Get all machines", description = "Retrieve all machines.",
            tags = {"machine-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Machines retrieved successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MachineDTO.class))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @GetMapping
    public ResponseEntity<List<MachineDTO>> getAllMachines() {
        List<MachineDTO> machines = machineServiceImpl.getAllMachines()
                .stream()
                .map(machine -> modelMapper.map(machine, MachineDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(machines);
    }

    @Operation(summary = "Delete a machine", description = "Delete a machine by ID.",
            tags = {"machine-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Machine deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Machine not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteMachine(@PathVariable("id") Long id) {
        machineServiceImpl.deleteMachine(id);
        return ResponseEntity.noContent().build();
    }
}
