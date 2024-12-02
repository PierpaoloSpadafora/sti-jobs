package unical.demacs.rdm.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unical.demacs.rdm.utils.Scheduler;

@RestController
@RequestMapping("/api/v1/scheduler")
@Tag(name = "scheduler-engine", description = "Scheduler management APIs")
@AllArgsConstructor
public class SchedulerController {

    private final Scheduler scheduler;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        scheduler.scheduleByEveryType();
        return new ResponseEntity<>("Test success", HttpStatus.OK);
    }


}