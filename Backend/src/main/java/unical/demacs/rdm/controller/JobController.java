package unical.demacs.rdm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unical.demacs.rdm.persistence.dto.JobDTO;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.service.implementation.JobServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/jobs")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "job-controller", description = "Operations related to job management")
public class JobController {
    private final JobServiceImpl jobService;
    private final ModelMapper modelMapper;

    @PostMapping
    @Operation(summary = "Create a new job")
    public ResponseEntity<JobDTO> createJob(@RequestBody JobDTO jobDTO) {
        return ResponseEntity.ok(modelMapper.map(
                jobService.createJob(modelMapper.map(jobDTO, Job.class)),
                JobDTO.class));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job by ID")
    public ResponseEntity<JobDTO> getJobById(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(job -> ResponseEntity.ok(modelMapper.map(job, JobDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all jobs")
    public ResponseEntity<List<JobDTO>> getAllJobs() {
        List<JobDTO> jobs = jobService.getAllJobs().stream()
                .map(job -> modelMapper.map(job, JobDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/assignee/{assigneeId}")
    @Operation(summary = "Get jobs by assignee")
    public ResponseEntity<List<JobDTO>> getJobsByAssignee(@PathVariable String assigneeId) {
        List<JobDTO> jobs = jobService.getJobsByAssignee(assigneeId).stream()
                .map(job -> modelMapper.map(job, JobDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/machine/{machineId}")
    @Operation(summary = "Get jobs by machine")
    public ResponseEntity<List<JobDTO>> getJobsByMachine(@PathVariable Long machineId) {
        List<JobDTO> jobs = jobService.getJobsByMachine(machineId).stream()
                .map(job -> modelMapper.map(job, JobDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(jobs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a job")
    public ResponseEntity<JobDTO> updateJob(@PathVariable Long id, @RequestBody JobDTO jobDTO) {
        jobDTO.setJobId(id);
        return ResponseEntity.ok(modelMapper.map(
                jobService.updateJob(modelMapper.map(jobDTO, Job.class)),
                JobDTO.class));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok().build();
    }
}
