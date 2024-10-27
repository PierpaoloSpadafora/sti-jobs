package unical.demacs.rdm.persistence.service.implementation;

import com.google.common.util.concurrent.RateLimiter;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.config.exception.TooManyRequestsException;
import unical.demacs.rdm.config.exception.ScheduleNotFoundException;
import unical.demacs.rdm.config.exception.ScheduleException;
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
    private ScheduleRepository scheduleRepository;
    private final RateLimiter rateLimiter;

    @Override
    public Schedule createSchedule(Schedule schedule) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Creating new schedule for job: {} on machine: {}",
                schedule.getJob().getId(), schedule.getMachine().getId());
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for createSchedule");
                throw new TooManyRequestsException();
            }
            try {
                return scheduleRepository.save(schedule);
            } catch (Exception e) {
                logger.error("Error creating schedule", e);
                throw new ScheduleException("Error creating schedule");
            }
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Optional<Schedule> getScheduleById(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Fetching schedule with id: {}", id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getScheduleById");
                throw new TooManyRequestsException();
            }
            return Optional.ofNullable(scheduleRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Schedule not found for id: {}", id);
                        return new ScheduleNotFoundException("Schedule not found with id: " + id);
                    }));
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public List<Schedule> getSchedulesByMachine(Long machineId) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Fetching schedules for machine: {}", machineId);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getSchedulesByMachine");
                throw new TooManyRequestsException();
            }
            List<Schedule> schedules = scheduleRepository.findByMachineId(machineId);
            if (schedules.isEmpty()) {
                logger.warn("No schedules found for machine id: {}", machineId);
                throw new ScheduleNotFoundException("No schedules found for machine id: " + machineId);
            }
            return schedules;
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public List<Schedule> getSchedulesByJob(Long jobId) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Fetching schedules for job: {}", jobId);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getSchedulesByJob");
                throw new TooManyRequestsException();
            }
            List<Schedule> schedules = scheduleRepository.findByJobId(jobId);
            if (schedules.isEmpty()) {
                logger.warn("No schedules found for job id: {}", jobId);
                throw new ScheduleNotFoundException("No schedules found for job id: " + jobId);
            }
            return schedules;
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public List<Schedule> getSchedulesByDateRange(LocalDateTime start, LocalDateTime end) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Fetching schedules between {} and {}", start, end);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getSchedulesByDateRange");
                throw new TooManyRequestsException();
            }
            List<Schedule> schedules = scheduleRepository.findByStartTimeBetween(start, end);
            if (schedules.isEmpty()) {
                logger.warn("No schedules found between {} and {}", start, end);
                throw new ScheduleNotFoundException("No schedules found for the specified date range");
            }
            return schedules;
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Schedule updateSchedule(Schedule schedule) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Updating schedule with id: {}", schedule.getId());
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for updateSchedule");
                throw new TooManyRequestsException();
            }
            if (!scheduleRepository.existsById(schedule.getId())) {
                logger.warn("Schedule not found for update with id: {}", schedule.getId());
                throw new ScheduleNotFoundException("Schedule not found with id: " + schedule.getId());
            }
            try {
                return scheduleRepository.save(schedule);
            } catch (Exception e) {
                logger.error("Error updating schedule", e);
                throw new ScheduleException("Error updating schedule");
            }
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public void deleteSchedule(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Deleting schedule with id: {}", id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for deleteSchedule");
                throw new TooManyRequestsException();
            }
            if (!scheduleRepository.existsById(id)) {
                logger.warn("Schedule not found for deletion with id: {}", id);
                throw new ScheduleNotFoundException("Schedule not found with id: " + id);
            }
            try {
                scheduleRepository.deleteById(id);
            } catch (Exception e) {
                logger.error("Error deleting schedule", e);
                throw new ScheduleException("Error deleting schedule");
            }
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }
}