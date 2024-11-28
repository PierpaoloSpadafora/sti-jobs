package unical.demacs.rdm.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unical.demacs.rdm.config.ExtendedModelMapper;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.service.interfaces.IScheduleService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/schedules", produces = "application/json")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "schedule-controller", description = "Operations related to schedule management, include schedule create, update and delete.")
public class ScheduleController {

    private final IScheduleService scheduleService;
    private final ModelMapper modelMapper;
    private final ExtendedModelMapper modelMapperExtended;

    @PostMapping("/create-schedule")
    public ResponseEntity<ScheduleDTO> createSchedule(@Valid @RequestBody ScheduleDTO scheduleDTO) {
        Schedule createdSchedule = scheduleService.createSchedule(scheduleDTO);
        return new ResponseEntity<>(modelMapper.map(createdSchedule, ScheduleDTO.class), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ScheduleDTO> updateScheduleStatus(@PathVariable Long id, @RequestParam String status) {
        ScheduleStatus scheduleStatus = ScheduleStatus.valueOf(status.toUpperCase());
        Schedule updatedSchedule = scheduleService.updateScheduleStatus(id, scheduleStatus);
        return new ResponseEntity<>(modelMapper.map(updatedSchedule, ScheduleDTO.class), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, @Valid @RequestBody ScheduleDTO scheduleDTO) {
        return new ResponseEntity<>(modelMapper.map(scheduleService.updateSchedule(id, scheduleDTO), ScheduleDTO.class), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Long id) {
        Optional<Schedule> schedule = scheduleService.getScheduleById(id);
        return schedule.map(value -> new ResponseEntity<>(modelMapper.map(value, ScheduleDTO.class), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/get-all-schedules")
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return new ResponseEntity<>(modelMapperExtended.mapList(schedules, ScheduleDTO.class), HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByStatus(@PathVariable String status) {
        List<Schedule> schedules = scheduleService.getSchedulesByStatus(ScheduleStatus.valueOf(status.toUpperCase()));
        return new ResponseEntity<>(modelMapperExtended.mapList(schedules, ScheduleDTO.class), HttpStatus.OK);
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByJobId(@PathVariable Long jobId) {
        List<Schedule> schedules = scheduleService.getSchedulesByJobId(jobId);
        return new ResponseEntity<>(modelMapperExtended.mapList(schedules, ScheduleDTO.class), HttpStatus.OK);
    }

    @GetMapping("/machine/{machineType}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByMachineType(@PathVariable String machineType) {
        List<Schedule> schedules = scheduleService.getSchedulesByMachineType(machineType);
        return new ResponseEntity<>(modelMapperExtended.mapList(schedules, ScheduleDTO.class), HttpStatus.OK);
    }

    @GetMapping("/timeRange")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesInTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<Schedule> schedules = scheduleService.getSchedulesInTimeRange(startTime, endTime);
        return new ResponseEntity<>(modelMapperExtended.mapList(schedules, ScheduleDTO.class), HttpStatus.OK);
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
        List<Schedule> schedules = scheduleService.getUpcomingSchedules(startTime);
        return new ResponseEntity<>(modelMapperExtended.mapList(schedules, ScheduleDTO.class), HttpStatus.OK);
    }

    @GetMapping("/past")
    public ResponseEntity<List<ScheduleDTO>> getPastSchedules(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime until) {
        LocalDateTime endTime = until != null ? until : LocalDateTime.now();
        List<Schedule> schedules = scheduleService.getPastSchedules(endTime);
        return new ResponseEntity<>(modelMapperExtended.mapList(schedules, ScheduleDTO.class), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        boolean deleted = scheduleService.deleteSchedule(id);
        return new ResponseEntity<>(deleted ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
    }

}
