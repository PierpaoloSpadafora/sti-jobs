package unical.demacs.rdm.persistence.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.repository.ScheduleRepository;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.service.interfaces.IScheduleService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleServiceImpl implements IScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final JobRepository jobRepository;

    @Autowired
    public ScheduleServiceImpl(ScheduleRepository scheduleRepository,
                               JobRepository jobRepository) {
        this.scheduleRepository = scheduleRepository;
        this.jobRepository = jobRepository;
    }

    @Override
    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {
        Job job = jobRepository.findById(scheduleDTO.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Job ID"));

        if (!isTimeSlotAvailable(scheduleDTO.getMachineType(), scheduleDTO.getStartTime(), scheduleDTO.getStartTime().plusMinutes(scheduleDTO.getDuration()))) {
            throw new IllegalArgumentException("Time slot is not available for the selected machine type");
        }
        Schedule schedule = new Schedule();
        schedule.setJob(job);
        schedule.setMachineType(scheduleDTO.getMachineType());
        schedule.setDueDate(scheduleDTO.getDueDate());
        schedule.setStartTime(scheduleDTO.getStartTime());
        schedule.setDuration(scheduleDTO.getDuration());
        schedule.setStatus(ScheduleStatus.valueOf(scheduleDTO.getStatus()));
        schedule = scheduleRepository.save(schedule);

        return convertToDTO(schedule);
    }

    @Override
    public Optional<ScheduleDTO> getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleDTO updateSchedule(Long id, ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + id));

        validateScheduleTime(scheduleDTO);
        updateScheduleFromDTO(schedule, scheduleDTO);

        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return convertToDTO(updatedSchedule);
    }

    @Override
    public boolean deleteSchedule(Long id) {
        if (scheduleRepository.existsById(id)) {
            scheduleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<ScheduleDTO> getSchedulesByStatus(ScheduleStatus status) {
        return scheduleRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDTO> getSchedulesByJobId(Long jobId) {
        return scheduleRepository.findByJob_Id(jobId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDTO> getSchedulesByMachineType(String machineType) {
        return scheduleRepository.findByMachineType(machineType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDTO> getSchedulesInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return scheduleRepository.findAll().stream()
                .filter(schedule -> {
                    LocalDateTime scheduleStartTime = schedule.getStartTime();
                    LocalDateTime scheduleEndTime = scheduleStartTime.plusMinutes(schedule.getDuration());
                    return !(scheduleEndTime.isBefore(startTime) || scheduleStartTime.isAfter(endTime));
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isTimeSlotAvailable(String machineType, LocalDateTime startTime, LocalDateTime endTime) {
        List<Schedule> conflictingSchedules = scheduleRepository.findByMachineType(machineType).stream()
                .filter(schedule -> {
                    LocalDateTime scheduleStartTime = schedule.getStartTime();
                    LocalDateTime scheduleEndTime = scheduleStartTime.plusMinutes(schedule.getDuration());
                    return !(scheduleEndTime.isBefore(startTime) || scheduleStartTime.isAfter(endTime));
                })
                .collect(Collectors.toList());

        return conflictingSchedules.isEmpty();
    }

    @Override
    public ScheduleDTO updateScheduleStatus(Long id, ScheduleStatus newStatus) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + id));

        schedule.setStatus(newStatus);
        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return convertToDTO(updatedSchedule);
    }

    @Override
    public List<ScheduleDTO> getUpcomingSchedules(LocalDateTime from) {
        return scheduleRepository.findByStartTimeAfter(from).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDTO> getPastSchedules(LocalDateTime until) {
        return scheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getStartTime().plusMinutes(schedule.getDuration()).isBefore(until))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ScheduleDTO convertToDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setJobId(schedule.getJob().getId());
        dto.setMachineType(schedule.getMachineType());
        dto.setDueDate(schedule.getDueDate());
        dto.setStartTime(schedule.getStartTime());
        dto.setDuration(schedule.getDuration());
        dto.setStatus(schedule.getStatus().toString());
        return dto;
    }

    private void updateScheduleFromDTO(Schedule schedule, ScheduleDTO dto) {
        if (dto.getJobId() == null || dto.getJobId() <= 0) {
            throw new IllegalArgumentException("Invalid Job ID: " + dto.getJobId());
        }
        Job job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + dto.getJobId()));

        schedule.setJob(job);
        schedule.setMachineType(dto.getMachineType());
        schedule.setDueDate(dto.getDueDate());
        schedule.setStartTime(dto.getStartTime());
        schedule.setDuration(dto.getDuration());
        schedule.setStatus(ScheduleStatus.valueOf(dto.getStatus()));
    }

    private void validateScheduleTime(ScheduleDTO dto) {
        LocalDateTime endTime = dto.getStartTime().plusMinutes(dto.getDuration());

        if (dto.getStartTime().isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        if (dto.getDueDate() != null && endTime.isAfter(dto.getDueDate())) {
            throw new IllegalArgumentException("End time must be before or equal to due date");
        }
        if (!isTimeSlotAvailable(dto.getMachineType(), dto.getStartTime(), endTime)) {
            throw new IllegalArgumentException("Time slot is not available for the selected machine type");
        }
    }
}
