package unical.demacs.rdm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unical.demacs.rdm.config.ExtendedModelMapper;
import unical.demacs.rdm.config.ModelMapperConfig;
import unical.demacs.rdm.persistence.dto.JobDTO;
import unical.demacs.rdm.persistence.dto.MachineDTO;
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.dto.UserDTO;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.entities.User;
import unical.demacs.rdm.persistence.service.interfaces.IJobService;
import unical.demacs.rdm.persistence.service.interfaces.IMachineService;
import unical.demacs.rdm.persistence.service.interfaces.IMachineTypeService;
import unical.demacs.rdm.persistence.service.interfaces.IUserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/json", produces = "application/json")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "json-controller", description = "Operations related to JSON import and export.")
public class JsonController {

    private final IJobService jobService;
    private final ExtendedModelMapper extendedModelMapper;

    @Operation(summary = "Import Job data from JSON", description = "Import Job data into the system from JSON content.",
            tags = {"json-controller"})
    @PostMapping(value = "/importJob", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> importJob(@RequestBody List<JobDTO> jobs, @RequestParam("assigneeEmail") String assigneeEmail) {
        try {
            jobs.forEach(job -> {
                Optional<Job> existingJob = jobService.getJobById(job.getId());
                if (existingJob.isEmpty()) {
                    jobService.createJob(assigneeEmail, job);
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
        List<Job> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(extendedModelMapper.mapList(jobs, JobDTO.class));
    }

    /*
    @Operation(summary = "Import MachineType data from JSON", description = "Import MachineType data into the system from JSON content.",
            tags = {"json-controller"})
    @PostMapping(value = "/importMachineType", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> importMachineType(@RequestBody List<MachineTypeDTO> machineTypes) {

    }

    @Operation(summary = "Export MachineType data to JSON", description = "Export all MachineType data to JSON.",
            tags = {"json-controller"})
    @GetMapping(value = "/exportMachineType", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MachineTypeDTO>> exportMachineType() {

    }

    @Operation(summary = "Import Machine data from JSON", description = "Import Machine data into the system from JSON content.",
            tags = {"json-controller"})
    @PostMapping(value = "/importMachine", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> importMachine(@RequestBody List<MachineDTO> machines) {

    }

    @Operation(summary = "Export Machine data to JSON", description = "Export all Machine data to JSON.",
            tags = {"json-controller"})
    @GetMapping(value = "/exportMachine", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MachineDTO>> exportMachine() {

    }

     */
}
