package unical.demacs.rdm.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.repository.ScheduleRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleStatusUpdaterService {

    private final ScheduleRepository scheduleRepository;

    @Scheduled(fixedRate = 60000) // every minute todo: ragionare su frequenza
    @Transactional
    public void updateScheduleStatuses() {
        LocalDateTime now = LocalDateTime.now();

        List<Schedule> activeSchedules = scheduleRepository.findByStatusNotInAndStartTimeBefore(
                List.of(ScheduleStatus.COMPLETED, ScheduleStatus.CANCELLED),
                now
        );

        for (Schedule schedule : activeSchedules) {
            updateScheduleStatus(schedule, now);
        }

        scheduleRepository.saveAll(activeSchedules);
    }



    private void updateScheduleStatus(Schedule schedule, LocalDateTime now) {
        LocalDateTime endTime = schedule.getStartTime().plusSeconds(schedule.getDuration());

        if (now.isBefore(schedule.getStartTime())) {
            schedule.setStatus(ScheduleStatus.SCHEDULED);
        } else if (now.isAfter(schedule.getStartTime()) && now.isBefore(endTime)) {
            schedule.setStatus(ScheduleStatus.IN_PROGRESS);
        } else if (now.isAfter(endTime)) {
            schedule.setStatus(ScheduleStatus.COMPLETED);
        }
    }
}