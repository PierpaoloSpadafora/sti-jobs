package unical.demacs.rdm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unical.demacs.rdm.persistence.dto.JobDTO;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.service.implementation.JobServiceImpl;

@RestController
@RequestMapping(value = "/api/v1/job", produces = "application/json")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "job-controller", description = "Operations related to job management, include job create, update and delete.")
public class JobController {

    private final JobServiceImpl jobServiceImpl;
    private final ModelMapper modelMapper;

    @Operation(summary = "Get all job", description = "Return all the jobs in the database.",
            tags = {"job-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All jobs retrieved successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JobDTO.class))),
            @ApiResponse(responseCode = "404", description = "No jobs found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @GetMapping(path="/get-all-jobs/")
    public ResponseEntity<JobDTO> getAllJobs() {
        return ResponseEntity.ok(modelMapper.map(jobServiceImpl.getAllJobs(), JobDTO.class));
    }

    @Operation(summary = "Get job by id", description = "Retrieve a job using their id.",
            tags = {"job-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Job retrieved successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Job.class))),
            @ApiResponse(responseCode = "404", description = "Job not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @GetMapping(path="/jobs-by-id/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(modelMapper.map(jobServiceImpl.getJobById(id), JobDTO.class));
    }


    @Operation(summary = "Create a job", description = "Create a job using the provided job object.",
            tags = {"job-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Job created successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JobDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @PostMapping(value = "/create-job", produces = "application/json")
    public ResponseEntity<JobDTO> createJob(@RequestBody Job job) {
        return ResponseEntity.ok(modelMapper.map(jobServiceImpl.createJob(job), JobDTO.class));
    }

    @Operation(summary = "Update a job", description = "Update a job using the provided job object.",
            tags = {"job-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Job updated successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JobDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @PostMapping(value = "/update-job", produces = "application/json")
    public ResponseEntity<JobDTO> updateJob(@PathVariable("id") Long id, @RequestBody Job job) {
        return ResponseEntity.ok(modelMapper.map(jobServiceImpl.updateJob(id, job), JobDTO.class));
    }

    @Operation(summary = "Delete a job", description = "Delete a job using their id.",
            tags = {"job-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Job deleted successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JobDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @PostMapping(value = "/delete-job", produces = "application/json")
    public ResponseEntity<JobDTO> deleteJob(@PathVariable("id") Long id) {
        jobServiceImpl.deleteJob(id);
        return ResponseEntity.ok().build();
    }
}
