package unical.demacs.rdm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.service.implementation.ScheduleServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/schedules")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "schedule-controller", description = "Operations related to schedule management")
public class ScheduleController {
    private final ScheduleServiceImpl scheduleService;
    private final ModelMapper modelMapper;

    @PostMapping
    @Operation(summary = "Create a new schedule")
    public ResponseEntity<ScheduleDTO> createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        return ResponseEntity.ok(modelMapper.map(
                scheduleService.createSchedule(modelMapper.map(scheduleDTO, Schedule.class)),
                ScheduleDTO.class));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get schedule by ID")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Long id) {
        return scheduleService.getScheduleById(id)
                .map(schedule -> ResponseEntity.ok(modelMapper.map(schedule, ScheduleDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/machine/{machineId}")
    @Operation(summary = "Get schedules by machine")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByMachine(@PathVariable Long machineId) {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByMachine(machineId).stream()
                .map(schedule -> modelMapper.map(schedule, ScheduleDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/job/{jobId}")
    @Operation(summary = "Get schedules by job")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByJob(@PathVariable Long jobId) {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByJob(jobId).stream()
                .map(schedule -> modelMapper.map(schedule, ScheduleDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get schedules by date range")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByDateRange(start, end).stream()
                .map(schedule -> modelMapper.map(schedule, ScheduleDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(schedules);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a schedule")
    public ResponseEntity<ScheduleDTO> updateSchedule(@PathVariable Long id, @RequestBody ScheduleDTO scheduleDTO) {
        scheduleDTO.setScheduleId(id);
        return ResponseEntity.ok(modelMapper.map(
                scheduleService.updateSchedule(modelMapper.map(scheduleDTO, Schedule.class)),
                ScheduleDTO.class));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a schedule")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }
}
