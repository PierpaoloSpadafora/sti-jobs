package unical.demacs.rdm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unical.demacs.rdm.persistence.dto.JobDTO;
import unical.demacs.rdm.persistence.dto.MachineDTO;
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.service.interfaces.IJobService;
import unical.demacs.rdm.persistence.service.interfaces.IMachineService;
import unical.demacs.rdm.persistence.service.interfaces.IMachineTypeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/json", produces = "application/json")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "json-controller", description = "Operations related to JSON import and export.")
public class JsonController {

    private final IJobService jobService;
    private final IMachineService machineService;
    private final IMachineTypeService machineTypeService;

    @Operation(summary = "Import Job data from JSON", description = "Import Job data into the system from JSON content.",
            tags = {"json-controller"})
    @PostMapping(value = "/importJob", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> importJob(@RequestBody List<JobDTO> jobs) {
        try {
            jobs.forEach(job -> {
                Optional<JobDTO> existingJob = jobService.findById(job.getId());
                if (existingJob.isEmpty()) {
                    jobService.saveJob(job);
                }
            });
            return ResponseEntity.ok("Jobs imported successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error importing jobs: " + e.getMessage());
        }
    }

    @Operation(summary = "Export Job data to JSON", description = "Export all Job data to JSON.",
            tags = {"json-controller"})
    @GetMapping(value = "/exportJob", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JobDTO>> exportJob() {
        List<JobDTO> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @Operation(summary = "Import MachineType data from JSON", description = "Import MachineType data into the system from JSON content.",
            tags = {"json-controller"})
    @PostMapping(value = "/importMachineType", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> importMachineType(@RequestBody List<MachineTypeDTO> machineTypes) {
        try {
            machineTypes.forEach(machineType -> {
                Optional<MachineTypeDTO> existingType = machineTypeService.getMachineTypeById(machineType.getId());
                if (existingType.isEmpty()) {
                    machineTypeService.createMachineType(machineType);
                }
            });
            return ResponseEntity.ok("Machine Types imported successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error importing machine types: " + e.getMessage());
        }
    }

    @Operation(summary = "Export MachineType data to JSON", description = "Export all MachineType data to JSON.",
            tags = {"json-controller"})
    @GetMapping(value = "/exportMachineType", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MachineTypeDTO>> exportMachineType() {
        List<MachineTypeDTO> machineTypes = machineTypeService.getAllMachineTypes();
        return ResponseEntity.ok(machineTypes);
    }

    @Operation(summary = "Import Machine data from JSON", description = "Import Machine data into the system from JSON content.",
            tags = {"json-controller"})
    @PostMapping(value = "/importMachine", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> importMachine(@RequestBody List<MachineDTO> machines) {
        try {
            machines.forEach(machine -> {
                Optional<MachineDTO> existingMachine = machineService.findByIdOrName(machine.getId(), machine.getName());
                if (existingMachine.isEmpty()) {
                    machineService.saveMachine(machine);
                }
            });
            return ResponseEntity.ok("Machines imported successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error importing machines: " + e.getMessage());
        }
    }

    @Operation(summary = "Export Machine data to JSON", description = "Export all Machine data to JSON.",
            tags = {"json-controller"})
    @GetMapping(value = "/exportMachine", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MachineDTO>> exportMachine() {
        List<MachineDTO> machines = machineService.getAllMachines();
        return ResponseEntity.ok(machines);
    }
}
