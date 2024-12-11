package unical.demacs.rdm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import unical.demacs.rdm.config.ModelMapperExtended;
import unical.demacs.rdm.persistence.dto.*;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.entities.Machine;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.service.interfaces.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<List<ScheduleWithMachineDTO>> exportJobScheduledPriority() {
        List<ScheduleWithMachineDTO> schedules = jsonService.readScheduleFile("./data/job-scheduled-by-priority.json");
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "Export Job data to JSON", description = "Export all Job data to JSON.",
            tags = {"json-controller"})
    @GetMapping(value = "/export-job-scheduled-by-due-date", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ScheduleWithMachineDTO>> exportJobScheduledDueDate() {
        List<ScheduleWithMachineDTO> schedules = jsonService.readScheduleFile("./data/job-scheduled-by-due-date.json");
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "Export Job data to JSON", description = "Export all Job data to JSON.",
            tags = {"json-controller"})
    @GetMapping(value = "/export-job-scheduled-by-duration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ScheduleWithMachineDTO>> exportJobScheduledDuration() {
        List<ScheduleWithMachineDTO> schedules = jsonService.readScheduleFile("./data/job-scheduled-by-duration.json");
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "Export RO scheduled jobs", description = "Export all RO scheduled jobs to JSON.")
    @GetMapping(value = "/export-job-scheduled-ro", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ScheduleWithMachineDTO>> exportJobScheduledRO() {
        List<ScheduleWithMachineDTO> schedules = jsonService.readScheduleFile("./data/job-scheduled-ro.json");
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "Download all Schedules as JSON", description = "Download all Schedules as a JSON file.",
            tags = {"json-controller"})
    @GetMapping(value = "/download-schedules", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ScheduleDTO>> downloadSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(modelMapperExtended.mapList(schedules, ScheduleDTO.class));
    }

}