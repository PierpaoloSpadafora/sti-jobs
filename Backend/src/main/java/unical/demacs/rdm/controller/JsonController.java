package unical.demacs.rdm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import unical.demacs.rdm.config.ModelMapperExtended;
import unical.demacs.rdm.persistence.dto.*;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.entities.Machine;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.service.interfaces.*;

import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping(value = "/api/v1/json", produces = "application/json")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "json-controller", description = "Operations related to JSON import and export.")
public class JsonController {

    private final IJobService jobService;
    private final IMachineService machineService;
    private final IMachineTypeService machineTypeService;
    private final IJsonService jsonService;
    private final IScheduleService scheduleService;
    private final ModelMapperExtended modelMapperExtended;

    @Operation(summary = "Import Job data from JSON", description = "Import Job data into the system from JSON content.",
            tags = {"json-controller"})
    @PostMapping(value = "/importJob", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> importJob(@RequestBody List<JobDTO> jobs, @RequestParam("assigneeEmail") String assigneeEmail) {
        try {
            jobs.forEach(job -> {
                if (job.getId() != null && jobService.getJobById(job.getId()).isEmpty()) {
                    jobService.createJob(assigneeEmail, job);
                } else if (job.getId() == null) {
                    jobService.createJob(assigneeEmail, job);
                }
            });
            return ResponseEntity.ok(Map.of("message", "Jobs imported successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error importing jobs: " + e.getMessage()));
        }
    }


    @Operation(summary = "Export Job data to JSON", description = "Export all Job data to JSON.",
            tags = {"json-controller"})
    @GetMapping(value = "/exportJob", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JobDTO>> exportJob() {
        List<Job> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(modelMapperExtended.mapList(jobs, JobDTO.class));
    }

    @Operation(summary = "Import MachineType data from JSON", description = "Import MachineType data into the system from JSON content.",
            tags = {"json-controller"})
    @PostMapping(value = "/importMachineType", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> importMachineType(@RequestBody List<MachineTypeDTO> machineTypes) {
        try {
            for (MachineTypeDTO machineTypeDTO : machineTypes) {
                machineTypeService.createMachineType(machineTypeDTO);
            }
            Map<String, String> response = Map.of("message", "MachineTypes importati con successo.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", "Errore durante l'importazione dei MachineTypes: " + e.getMessage()));
        }
    }

    @Operation(summary = "Export MachineType data to JSON", description = "Export all MachineType data to JSON.",
            tags = {"json-controller"})
    @GetMapping(value = "/exportMachineType", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MachineTypeDTO>> exportMachineType() {
        List<MachineType> machineTypes = machineTypeService.getAllMachineTypes();
        return ResponseEntity.ok(modelMapperExtended.mapList(machineTypes, MachineTypeDTO.class));
    }

    @PostMapping(value = "/importMachine", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> importMachine(@RequestBody List<MachineDTO> machines) {
        try {
            for (MachineDTO machine : machines) {
                machineService.createMachine(machine);
            }
            return ResponseEntity.ok(Map.of("message", "Machines imported successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Errore durante l'importazione delle Machines: " + e.getMessage()));
        }
    }

    @Operation(summary = "Export Machine data to JSON", description = "Export all Machine data to JSON.",
            tags = {"json-controller"})
    @GetMapping(value = "/exportMachine", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MachineDTO>> exportMachine() {
        List<Machine> machines = machineService.getAllMachines();
        List <MachineDTO> list =  modelMapperExtended.mapList(machines, MachineDTO.class);
        for(int i = 0; i< list.size(); i++){
            list.get(i).setTypeId(machines.get(i).getMachine_type_id().getId());
        }
        return ResponseEntity.ok(list);
    }


    @Operation(summary = "Export Job data to JSON", description = "Export all Job data to JSON.",
            tags = {"json-controller"})
    @GetMapping(value = "/export-job-scheduled-by-priority", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exportJobScheduledPriority() {
        try {
            List<ScheduleWithMachineDTO> schedules = jsonService.readScheduleFile("./data/job-scheduled-by-priority.json");
            return ResponseEntity.ok(schedules);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message.startsWith("FILE_NOT_FOUND:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Schedule file not found", "details", message));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error reading schedule file", "details", message));
        }
    }

    @Operation(summary = "Export Job data to JSON", description = "Export all Job data to JSON.",
            tags = {"json-controller"})
    @GetMapping(value = "/export-job-scheduled-by-due-date", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exportJobScheduledDueDate() {
        try {
            List<ScheduleWithMachineDTO> schedules = jsonService.readScheduleFile("./data/job-scheduled-by-due-date.json");
            return ResponseEntity.ok(schedules);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message.startsWith("FILE_NOT_FOUND:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Schedule file not found", "details", message));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error reading schedule file", "details", message));
        }
    }

    @Operation(summary = "Export Job data to JSON", description = "Export all Job data to JSON.",
            tags = {"json-controller"})
    @GetMapping(value = "/export-job-scheduled-by-duration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exportJobScheduledDuration() {
        try {
            List<ScheduleWithMachineDTO> schedules = jsonService.readScheduleFile("./data/job-scheduled-by-duration.json");
            return ResponseEntity.ok(schedules);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message.startsWith("FILE_NOT_FOUND:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Schedule file not found", "details", message));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error reading schedule file", "details", message));
        }
    }

    @Operation(summary = "Export RO scheduled jobs", description = "Export all RO scheduled jobs to JSON.")
    @GetMapping(value = "/export-job-scheduled-external", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exportJobScheduledExternal() {
        try {
            List<ScheduleWithMachineDTO> schedules = jsonService.readScheduleFile("./data/job-scheduled-imported.json");
            return ResponseEntity.ok(schedules);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message.startsWith("FILE_NOT_FOUND:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Schedule file not found", "details", message));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error reading schedule file", "details", message));
        }
    }

    @Operation(summary = "Download all Schedules as JSON", description = "Download all Schedules as a JSON file.",
            tags = {"json-controller"})
    @GetMapping(value = "/download-schedules", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ScheduleDTO>> downloadSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(modelMapperExtended.mapList(schedules, ScheduleDTO.class));
    }

    @Operation(summary = "Import Schedules from JSON", description = "Upload and import Schedules from a JSON file.",
            tags = {"json-controller"})
    @PostMapping(value = "/upload-schedules-scheduled-externally", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> importSchedules(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "File is empty."));
            }
            File directory = new File("./data");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            Path filePath = Paths.get("./data/job-scheduled-imported.json");
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok(Map.of("message", "Schedules imported successfully."));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error importing schedules: " + e.getMessage()));
        }
    }

    @GetMapping("/export-job-scheduled-ro")
    public ResponseEntity<List<ScheduleWithMachineDTO>> exportJobScheduledRO() {
        List<ScheduleWithMachineDTO> schedules = jsonService.readScheduleFile("./data/job-scheduled-ro.json");
        return ResponseEntity.ok(schedules);
    }

}