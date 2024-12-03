package unical.demacs.rdm.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unical.demacs.rdm.utils.Scheduler;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/scheduler")
@Tag(name = "scheduler-engine", description = "Scheduler management APIs")
@AllArgsConstructor
public class SchedulerController {

    private final Scheduler scheduler;

    @GetMapping("/schedule-all")
    public ResponseEntity<Map<String,String>> ScheduleAll() {
        scheduler.scheduleByEveryType();
        return new ResponseEntity<>(Map.of("message", "Scheduling completed"), HttpStatus.OK);
    }

    @GetMapping("/schedule-priority")
    public ResponseEntity<Map<String,String>> SchedulePriority() {
        scheduler.scheduleByPriority();
        return new ResponseEntity<>(Map.of("message", "Scheduling completed"), HttpStatus.OK);
    }

    @GetMapping("/schedule-due-date")
    public ResponseEntity<Map<String,String>> ScheduleDueDate() {
        scheduler.scheduleByDueDate();
        return new ResponseEntity<>(Map.of("message", "Scheduling completed"), HttpStatus.OK);
    }

    @GetMapping("/schedule-duration")
    public ResponseEntity<Map<String,String>> ScheduleDuration() {
        scheduler.scheduleByDuration();
        return new ResponseEntity<>(Map.of("message", "Scheduling completed"), HttpStatus.OK);
    }


}