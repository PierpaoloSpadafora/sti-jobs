package unical.demacs.rdm.persistence.service.implementation;

import com.google.common.util.concurrent.RateLimiter;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.repository.MachineTypeRepository;
import unical.demacs.rdm.persistence.repository.ScheduleRepository;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.service.interfaces.IScheduleService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ScheduleServiceImpl implements IScheduleService {


    private static final Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);
    private final RateLimiter rateLimiter;
    private final ScheduleRepository scheduleRepository;
    private final JobRepository jobRepository;
    private final MachineTypeRepository machineTypeRepository;

    @Override
    public Schedule createSchedule(ScheduleDTO scheduleDTO) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Creating schedule for job with id: " + scheduleDTO.getJobId());
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for createSchedule");
                throw new RuntimeException("Rate limit exceeded");
            }
            if(jobRepository.findById(scheduleDTO.getJobId()).isEmpty()){
                logger.error("Job with id {} not found", scheduleDTO.getJobId());
                throw new RuntimeException("Job not found");
            }
            Schedule schedule = Schedule.scheduleBuilder()
                    .job(jobRepository.findById(scheduleDTO.getJobId()).orElse(null))
                    .machineType(machineTypeRepository.findById(scheduleDTO.getMachineTypeId()).orElse(null))
                    .dueDate(scheduleDTO.getDueDate())
                    .startTime(scheduleDTO.getStartTime())
                    .duration(scheduleDTO.getDuration())
                    .build();
            scheduleRepository.save(schedule);
            logger.info("Schedule for job with id {} created successfully", scheduleDTO.getJobId());
            return schedule;
        } catch (Exception e) {
            logger.error("Error creating schedule for job with id: {}", scheduleDTO.getJobId(), e);
            throw new RuntimeException("Error creating schedule");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Optional<Schedule> getScheduleById(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting schedule by id: " + id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getScheduleById");
                throw new RuntimeException("Rate limit exceeded");
            }
            Optional<Schedule> schedule = scheduleRepository.findById(id);
            if (schedule.isEmpty()) {
                logger.error("Schedule with id {} not found", id);
                throw new RuntimeException("Schedule not found");
            }
            logger.info("Schedule with id {} found successfully", id);
            return schedule;
        } catch (Exception e) {
            logger.error("Error getting schedule with id: {}", id, e);
            throw new RuntimeException("Error getting schedule");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public List<Schedule> getAllSchedules() {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting all schedules");
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getAllSchedules");
                throw new RuntimeException("Rate limit exceeded");
            }
            List<Schedule> schedules = scheduleRepository.findAll();
            logger.info("All schedules found successfully");
            return schedules;
        } catch (Exception e) {
            logger.error("Error getting all schedules", e);
            throw new RuntimeException("Error getting all schedules");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Schedule updateSchedule(Long id, ScheduleDTO scheduleDTO) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Updating schedule with id: " + id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for updateSchedule");
                throw new RuntimeException("Rate limit exceeded");
            }
            Optional<Schedule> schedule = scheduleRepository.findById(id);
            if (schedule.isEmpty()) {
                logger.error("Schedule with id {} not found", id);
                throw new RuntimeException("Schedule not found");
            }
            if(jobRepository.findById(scheduleDTO.getJobId()).isEmpty()){
                logger.error("Job with id {} not found", scheduleDTO.getJobId());
                throw new RuntimeException("Job not found");
            }
            Schedule updatedSchedule = schedule.get();
            updatedSchedule.setJob(jobRepository.findById(scheduleDTO.getJobId()).orElse(null));
            updatedSchedule.setMachineType(machineTypeRepository.findById(scheduleDTO.getMachineTypeId()).orElse(null));
            updatedSchedule.setDueDate(scheduleDTO.getDueDate());
            updatedSchedule.setStartTime(scheduleDTO.getStartTime());
            updatedSchedule.setDuration(scheduleDTO.getDuration());
            scheduleRepository.save(updatedSchedule);
            logger.info("Schedule with id {} updated successfully", id);
            return updatedSchedule;
        } catch (Exception e) {
            logger.error("Error updating schedule with id: {}", id, e);
            throw new RuntimeException("Error updating schedule");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public boolean deleteSchedule(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Deleting schedule with id: " + id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for deleteSchedule");
                throw new RuntimeException("Rate limit exceeded");
            }
            Optional<Schedule> schedule = scheduleRepository.findById(id);
            if (schedule.isEmpty()) {
                logger.error("Schedule with id {} not found", id);
                throw new RuntimeException("Schedule not found");
            }
            scheduleRepository.delete(schedule.get());
            logger.info("Schedule with id {} deleted successfully", id);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting schedule with id: {}", id, e);
            throw new RuntimeException("Error deleting schedule");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public List<Schedule> getSchedulesByStatus(ScheduleStatus status) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting schedules by status: " + status);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getSchedulesByStatus");
                throw new RuntimeException("Rate limit exceeded");
            }
            List<Schedule> schedules = scheduleRepository.findAll().stream()
                    .filter(schedule -> schedule.getStatus().equals(status))
                    .collect(Collectors.toList());
            logger.info("Schedules with status {} found successfully", status);
            return schedules;
        } catch (Exception e) {
            logger.error("Error getting schedules by status: {}", status, e);
            throw new RuntimeException("Error getting schedules by status");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public List<Schedule> getSchedulesByJobId(Long jobId) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting schedules by job id: " + jobId);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getSchedulesByJobId");
                throw new RuntimeException("Rate limit exceeded");
            }
            List<Schedule> schedules = scheduleRepository.findAll().stream()
                    .filter(schedule -> schedule.getJob().getId().equals(jobId))
                    .collect(Collectors.toList());
            logger.info("Schedules with job id {} found successfully", jobId);
            return schedules;
        } catch (Exception e) {
            logger.error("Error getting schedules by job id: {}", jobId, e);
            throw new RuntimeException("Error getting schedules by job id");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public List<Schedule> getSchedulesByMachineType(String machineType) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting schedules by machine type: " + machineType);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getSchedulesByMachineType");
                throw new RuntimeException("Rate limit exceeded");
            }
            List<Schedule> schedules = scheduleRepository.findAll().stream()
                    .filter(schedule -> schedule.getMachineType().equals(machineType))
                    .collect(Collectors.toList());
            logger.info("Schedules with machine type {} found successfully", machineType);
            return schedules;
        } catch (Exception e) {
            logger.error("Error getting schedules by machine type: {}", machineType, e);
            throw new RuntimeException("Error getting schedules by machine type");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public List<Schedule> getSchedulesInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting schedules in time range: " + startTime + " - " + endTime);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getSchedulesInTimeRange");
                throw new RuntimeException("Rate limit exceeded");
            }
            List<Schedule> schedules = scheduleRepository.findAll().stream()
                    .filter(schedule -> schedule.getStartTime().isAfter(startTime) && schedule.getStartTime().isBefore(endTime))
                    .collect(Collectors.toList());
            logger.info("Schedules in time range {} - {} found successfully", startTime, endTime);
            return schedules;
        } catch (Exception e) {
            logger.error("Error getting schedules in time range: {} - {}", startTime, endTime, e);
            throw new RuntimeException("Error getting schedules in time range");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public boolean isTimeSlotAvailable(String machineType, LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Checking if time slot is available for machine type: " + machineType);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for isTimeSlotAvailable");
                throw new RuntimeException("Rate limit exceeded");
            }
            List<Schedule> schedules = scheduleRepository.findAll().stream()
                    .filter(schedule -> schedule.getMachineType().equals(machineType))
                    .collect(Collectors.toList());
            for (Schedule schedule : schedules) {
                if (schedule.getStartTime().isBefore(endTime) && schedule.getStartTime().plusMinutes(schedule.getDuration()).isAfter(startTime)) {
                    logger.info("Time slot not available for machine type: {}", machineType);
                    return false;
                }
            }
            logger.info("Time slot available for machine type: {}", machineType);
            return true;
        } catch (Exception e) {
            logger.error("Error checking if time slot is available for machine type: {}", machineType, e);
            throw new RuntimeException("Error checking if time slot is available");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Schedule updateScheduleStatus(Long id, ScheduleStatus newStatus) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Updating schedule status with id: " + id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for updateScheduleStatus");
                throw new RuntimeException("Rate limit exceeded");
            }
            Optional<Schedule> schedule = scheduleRepository.findById(id);
            if (schedule.isEmpty()) {
                logger.error("Schedule with id {} not found", id);
                throw new RuntimeException("Schedule not found");
            }
            Schedule updatedSchedule = schedule.get();
            updatedSchedule.setStatus(newStatus);
            scheduleRepository.save(updatedSchedule);
            logger.info("Schedule status with id {} updated successfully", id);
            return updatedSchedule;
        } catch (Exception e) {
            logger.error("Error updating schedule status with id: {}", id, e);
            throw new RuntimeException("Error updating schedule status");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public List<Schedule> getUpcomingSchedules(LocalDateTime from) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting upcoming schedules from: " + from);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getUpcomingSchedules");
                throw new RuntimeException("Rate limit exceeded");
            }
            List<Schedule> schedules = scheduleRepository.findAll().stream()
                    .filter(schedule -> schedule.getStartTime().isAfter(from))
                    .collect(Collectors.toList());
            logger.info("Upcoming schedules from {} found successfully", from);
            return schedules;
        } catch (Exception e) {
            logger.error("Error getting upcoming schedules from: {}", from, e);
            throw new RuntimeException("Error getting upcoming schedules");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public List<Schedule> getPastSchedules(LocalDateTime until) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting past schedules until: " + until);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getPastSchedules");
                throw new RuntimeException("Rate limit exceeded");
            }
            List<Schedule> schedules = scheduleRepository.findAll().stream()
                    .filter(schedule -> schedule.getStartTime().isBefore(until))
                    .collect(Collectors.toList());
            logger.info("Past schedules until {} found successfully", until);
            return schedules;
        } catch (Exception e) {
            logger.error("Error getting past schedules until: {}", until, e);
            throw new RuntimeException("Error getting past schedules");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public List<Schedule> getSchedulesDueBefore(LocalDateTime date) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting schedules with due-date before: {}", date);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getSchedulesDueBefore");
                throw new RuntimeException("Rate limit exceeded");
            }
            List<Schedule> schedules = scheduleRepository.findByDueDateBefore(date);
            logger.info("Found {} schedules with due-date before {}", schedules.size(), date);
            return schedules;
        } catch (Exception e) {
            logger.error("Error getting schedules with due-date before: {}", date, e);
            throw new RuntimeException("Error getting schedules due before the specified date");
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public List<Schedule> getSchedulesDueAfter(LocalDateTime date) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Getting schedules with due-date after: {}", date);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getSchedulesDueAfter");
                throw new RuntimeException("Rate limit exceeded");
            }
            List<Schedule> schedules = scheduleRepository.findByDueDateAfter(date);
            logger.info("Found {} schedules with due-date after {}", schedules.size(), date);
            return schedules;
        } catch (Exception e) {
            logger.error("Error getting schedules with due-date after: {}", date, e);
            throw new RuntimeException("Error getting schedules due after the specified date");
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }


}
