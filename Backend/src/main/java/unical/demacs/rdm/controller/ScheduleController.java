package unical.demacs.rdm.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final IScheduleService scheduleService;

    @Autowired
    public ScheduleController(IScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public ResponseEntity<ScheduleDTO> createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        try {
            ScheduleDTO createdSchedule = scheduleService.createSchedule(scheduleDTO);
            return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")

    @PatchMapping("/{id}/status")
    public ResponseEntity<ScheduleDTO> updateScheduleStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            ScheduleStatus newStatus = ScheduleStatus.valueOf(status.toUpperCase());
            ScheduleDTO updatedSchedule = scheduleService.updateScheduleStatus(id, newStatus);
            return new ResponseEntity<>(updatedSchedule, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDTO> updateSchedule(@PathVariable Long id, @RequestBody ScheduleDTO scheduleDTO) {
        try {
            ScheduleDTO updatedSchedule = scheduleService.updateSchedule(id, scheduleDTO);
            return new ResponseEntity<>(updatedSchedule, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
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

    @GetMapping("/machine/{machineId}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByMachineId(@PathVariable Long machineId) {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByMachineId(machineId);
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
            @RequestParam Long machineId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        boolean isAvailable = scheduleService.isTimeSlotAvailable(machineId, startTime, endTime);
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