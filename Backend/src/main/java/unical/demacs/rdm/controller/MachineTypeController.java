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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unical.demacs.rdm.config.exception.MachineException;
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.service.implementation.MachineTypeServiceImpl;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/machine-type", produces = "application/json")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "machine-type-controller", description = "Operations related to machine type management.")
public class MachineTypeController {

    private final MachineTypeServiceImpl machineTypeServiceImpl;

    @Operation(summary = "Create machine type", description = "Create a new machine type.",
            tags = {"machine-type-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Machine type created successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MachineTypeDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @PostMapping(path="create")
    public ResponseEntity<MachineTypeDTO> createMachineType(@Valid @RequestBody MachineTypeDTO machineTypeDTO) {
        return ResponseEntity.ok(machineTypeServiceImpl.createMachineType(machineTypeDTO));
    }

    @Operation(summary = "Get machine type by id", description = "Retrieve a machine type using its id.",
            tags = {"machine-type-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Machine type retrieved successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MachineTypeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Machine type not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @GetMapping(path="/by-id/{id}")
    public ResponseEntity<MachineTypeDTO> getMachineTypeById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(machineTypeServiceImpl.getMachineTypeById(id)
                .orElseThrow(() -> new MachineException("Machine type not found")));
    }

    @Operation(summary = "Get all machine types", description = "Retrieve all machine types.",
            tags = {"machine-type-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Machine types retrieved successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MachineTypeDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @GetMapping(path="/get-all")
    public ResponseEntity<List<MachineTypeDTO>> getAllMachineTypes() {
        List<MachineTypeDTO> machineTypes = machineTypeServiceImpl.getAllMachineTypes();
        return ResponseEntity.ok(machineTypes);
    }

    @Operation(summary = "Delete machine type", description = "Delete a machine type using its id.",
            tags = {"machine-type-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Machine type deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Machine type not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping(path="/by-id/{id}")
    public ResponseEntity<Void> deleteMachineType(@PathVariable("id") Long id) {
        machineTypeServiceImpl.deleteMachineType(id);
        return ResponseEntity.noContent().build();
    }


}