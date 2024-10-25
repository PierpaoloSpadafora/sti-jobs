package unical.demacs.rdm.persistence.service.implementation;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.repository.ScheduleRepository;
import unical.demacs.rdm.persistence.service.interfaces.IScheduleService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ScheduleServiceImpl implements IScheduleService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);
    private final ScheduleRepository scheduleRepository;

    @Override
    public Schedule createSchedule(Schedule schedule) {
        logger.info("Creating new schedule for job: {} on machine: {}",
                schedule.getJob().getJobId(), schedule.getMachine().getMachineId());
        return scheduleRepository.save(schedule);
    }

    @Override
    public Optional<Schedule> getScheduleById(Long id) {
        logger.info("Fetching schedule with id: {}", id);
        return scheduleRepository.findById(id);
    }

    @Override
    public List<Schedule> getSchedulesByMachine(Long machineId) {
        logger.info("Fetching schedules for machine: {}", machineId);
        return scheduleRepository.findByMachineId(machineId);
    }

    @Override
    public List<Schedule> getSchedulesByJob(Long jobId) {
        logger.info("Fetching schedules for job: {}", jobId);
        return scheduleRepository.findByJobId(jobId);
    }

    @Override
    public List<Schedule> getSchedulesByDateRange(LocalDateTime start, LocalDateTime end) {
        logger.info("Fetching schedules between {} and {}", start, end);
        return scheduleRepository.findByStartTimeBetween(start, end);
    }

    @Override
    public Schedule updateSchedule(Schedule schedule) {
        logger.info("Updating schedule with id: {}", schedule.getScheduleId());
        return scheduleRepository.save(schedule);
    }

    @Override
    public void deleteSchedule(Long id) {
        logger.info("Deleting schedule with id: {}", id);
        scheduleRepository.deleteById(id);
    }
}