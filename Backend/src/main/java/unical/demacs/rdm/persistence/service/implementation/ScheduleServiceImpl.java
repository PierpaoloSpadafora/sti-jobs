package unical.demacs.rdm.persistence.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.entities.Machine;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.repository.ScheduleRepository;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.repository.MachineRepository;
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
    private final MachineRepository machineRepository;

    @Autowired
    public ScheduleServiceImpl(ScheduleRepository scheduleRepository,
                               JobRepository jobRepository,
                               MachineRepository machineRepository) {
        this.scheduleRepository = scheduleRepository;
        this.jobRepository = jobRepository;
        this.machineRepository = machineRepository;
    }

    @Override
    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {
        Job job = jobRepository.findById(scheduleDTO.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Job ID"));
        Machine machine = machineRepository.findById(scheduleDTO.getMachineId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Machine ID"));

        if (!isTimeSlotAvailable(scheduleDTO.getMachineId(), scheduleDTO.getStartTime(), scheduleDTO.getEndTime())) {
            throw new IllegalArgumentException("Time slot is not available for the selected machine");
        }
        Schedule schedule = new Schedule();
        schedule.setJob(job);
        schedule.setMachine(machine);
        schedule.setDueDate(scheduleDTO.getDueDate());
        schedule.setStartTime(scheduleDTO.getStartTime());
        schedule.setEndTime(scheduleDTO.getEndTime());
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
    public List<ScheduleDTO> getSchedulesByMachineId(Long machineId) {
        return scheduleRepository.findByMachine_Id(machineId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDTO> getSchedulesInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return scheduleRepository.findByStartTimeBetween(startTime, endTime).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isTimeSlotAvailable(Long machineId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Schedule> conflictingSchedules = scheduleRepository.findByMachine_Id(machineId).stream()
                .filter(schedule ->
                        !(schedule.getEndTime().isBefore(startTime) || schedule.getStartTime().isAfter(endTime)))
                .toList();

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
        return scheduleRepository.findByEndTimeBefore(until).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ScheduleDTO convertToDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setJobId(schedule.getJob().getId());
        dto.setMachineId(schedule.getMachine().getId());
        dto.setDueDate(schedule.getDueDate());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setStatus(schedule.getStatus().toString());
        dto.setCreatedAt(schedule.getCreatedAt());
        dto.setUpdatedAt(schedule.getUpdatedAt());
        return dto;
    }

    private void updateScheduleFromDTO(Schedule schedule, ScheduleDTO dto) {
        if (dto.getJobId() == null || dto.getJobId() <= 0) {
            throw new IllegalArgumentException("Invalid Job ID: " + dto.getJobId());
        }
        Job job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + dto.getJobId()));
        Machine machine = machineRepository.findById(dto.getMachineId())
                .orElseThrow(() -> new RuntimeException("Machine not found with id: " + dto.getMachineId()));

        schedule.setJob(job);
        schedule.setMachine(machine);
        schedule.setDueDate(dto.getDueDate());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setStatus(ScheduleStatus.valueOf(dto.getStatus()));
    }

    private void validateScheduleTime(ScheduleDTO dto) {
        if (dto.getStartTime().isAfter(dto.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        if (dto.getDueDate() != null && dto.getEndTime().isAfter(dto.getDueDate())) {
            throw new IllegalArgumentException("End time must be before or equal to due date");
        }
        if (!isTimeSlotAvailable(dto.getMachineId(), dto.getStartTime(), dto.getEndTime())) {
            throw new IllegalArgumentException("Time slot is not available for the selected machine");
        }
    }
}