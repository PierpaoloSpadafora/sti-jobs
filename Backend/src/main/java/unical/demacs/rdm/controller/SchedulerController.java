package unical.demacs.rdm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.dto.ScheduleViewDTO;
import unical.demacs.rdm.persistence.service.implementation.JobServiceImpl;
import unical.demacs.rdm.persistence.service.implementation.ScheduleServiceImpl;
import unical.demacs.rdm.utils.Scheduler;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scheduler")
@Tag(name = "Scheduler", description = "Scheduler management APIs")
@AllArgsConstructor
public class SchedulerController {

    private final Scheduler scheduler;

    @PostMapping("/schedule/{machineType}")
    @Operation(summary = "Schedule jobs by priority for a machine type")
    @ApiResponse(responseCode = "200", description = "Successfully scheduled jobs")
    public ResponseEntity<List<ScheduleViewDTO>> scheduleByPriority(
            @PathVariable Long machineType) {
        return ResponseEntity.ok(scheduler.scheduleByPriority(machineType));
    }




}