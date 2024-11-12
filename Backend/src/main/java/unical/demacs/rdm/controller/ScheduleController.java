package unical.demacs.rdm.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.service.interfaces.IScheduleService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/schedules", produces = "application/json")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "schedule-controller", description = "Operations related to schedule management, include schedule create, update and delete.")
public class ScheduleController {

    private final IScheduleService scheduleService;

    @PostMapping("/create-schedule")
    public ResponseEntity<ScheduleDTO> createSchedule(@Valid @RequestBody ScheduleDTO scheduleDTO) {
        try {
            ScheduleDTO createdSchedule = scheduleService.createSchedule(scheduleDTO);
            return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ScheduleDTO> updateScheduleStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            ScheduleStatus scheduleStatus = ScheduleStatus.valueOf(status.toUpperCase());
            ScheduleDTO updatedSchedule = scheduleService.updateScheduleStatus(id, scheduleStatus);
            return ResponseEntity.ok(updatedSchedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDTO> updateSchedule(@PathVariable Long id, @Valid @RequestBody ScheduleDTO scheduleDTO) {
        try {
            ScheduleDTO updatedSchedule = scheduleService.updateSchedule(id, scheduleDTO);
            return new ResponseEntity<>(updatedSchedule, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Long id) {
        return scheduleService.getScheduleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/get-all-schedules")
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules() {
        List<ScheduleDTO> schedules = scheduleService.getAllSchedules();
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByStatus(@PathVariable String status) {
        try {
            ScheduleStatus scheduleStatus = ScheduleStatus.valueOf(status.toUpperCase());
            List<ScheduleDTO> schedules = scheduleService.getSchedulesByStatus(scheduleStatus);
            return new ResponseEntity<>(schedules, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByJobId(@PathVariable Long jobId) {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByJobId(jobId);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @GetMapping("/machine/{machineType}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByMachineType(@PathVariable String machineType) {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByMachineType(machineType);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @GetMapping("/timeRange")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesInTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesInTimeRange(startTime, endTime);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @GetMapping("/availability")
    public ResponseEntity<Boolean> checkTimeSlotAvailability(
            @RequestParam String machineType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        boolean isAvailable = scheduleService.isTimeSlotAvailable(machineType, startTime, endTime);
        return new ResponseEntity<>(isAvailable, HttpStatus.OK);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<ScheduleDTO>> getUpcomingSchedules(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from) {
        LocalDateTime startTime = from != null ? from : LocalDateTime.now();
        List<ScheduleDTO> schedules = scheduleService.getUpcomingSchedules(startTime);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @GetMapping("/past")
    public ResponseEntity<List<ScheduleDTO>> getPastSchedules(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime until) {
        LocalDateTime endTime = until != null ? until : LocalDateTime.now();
        List<ScheduleDTO> schedules = scheduleService.getPastSchedules(endTime);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        boolean deleted = scheduleService.deleteSchedule(id);
        return new ResponseEntity<>(deleted ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
    }

}
