package unical.demacs.rdm.persistence.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import unical.demacs.rdm.config.exception.JsonException;
import unical.demacs.rdm.config.exception.UserException;
import unical.demacs.rdm.persistence.dto.*;
import unical.demacs.rdm.persistence.entities.*;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.repository.*;
import unical.demacs.rdm.persistence.service.interfaces.IJsonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class JsonServiceImpl implements IJsonService {

    private static final Logger logger = LoggerFactory.getLogger(JsonServiceImpl.class);

    private final JobRepository jobRepository;
    private final MachineRepository machineRepository;
    private final MachineTypeRepository machineTypeRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    public byte[] convertToJson(JsonDTO jsonDTO) {
        try {
            return objectMapper.writeValueAsBytes(jsonDTO);
        } catch (JsonProcessingException e) {
            logger.error("Errore durante la creazione del file JSON", e);
            throw new JsonException("Errore durante la conversione del file JSON");
        }
    }

    @Override
    @Transactional
    public void processImport(JsonDTO jsonDTO) {
        logger.info("++++++ START IMPORT PROCESS ++++++");
        logger.info(String.valueOf(jsonDTO));
        logger.debug("Received JsonDTO: {}", jsonDTO);
        try {
            if (jsonDTO.getMachineTypes() != null) {
                logger.debug("Importing MachineTypes: {}", jsonDTO.getMachineTypes());
                importMachineTypes(jsonDTO.getMachineTypes());
            }

            if (jsonDTO.getMachines() != null) {
                logger.debug("Importing Machines: {}", jsonDTO.getMachines());
                importMachines(jsonDTO.getMachines());
            }

            if (jsonDTO.getJobs() != null) {
                logger.debug("Importing Jobs: {}", jsonDTO.getJobs());
                importJobs(jsonDTO.getJobs());
            }

            if (jsonDTO.getSchedules() != null) {
                logger.debug("Importing Schedules: {}", jsonDTO.getSchedules());
                importSchedules(jsonDTO.getSchedules());
            }

        } catch (Exception e) {
            logger.error("Errore durante il processo di importazione", e);
            throw new JsonException("Errore durante l'importazione dei dati: " + e.getMessage());
        }

        logger.info("++++++ END IMPORT PROCESS ++++++");
    }

    private void importMachineTypes(List<MachineTypeDTO> machineTypeDTOs) {
        for (MachineTypeDTO dto : machineTypeDTOs) {
            logger.debug("Processing MachineTypeDTO: {}", dto);
            MachineType machineType = new MachineType();
            machineType.setName(dto.getName());
            machineType.setDescription(dto.getDescription());
            machineTypeRepository.save(machineType);
            logger.debug("MachineType saved: {}", machineType);
        }
    }

    private void importMachines(List<MachineDTO> machineDTOs) {
        for (MachineDTO dto : machineDTOs) {
            logger.debug("Processing MachineDTO: {}", dto);
            MachineType machineType = machineTypeRepository.findById(dto.getTypeId())
                    .orElseThrow(() -> new JsonException("Tipo di macchina non trovato per ID: " + dto.getTypeId()));

            Machine machine = new Machine();
            machine.setName(dto.getName());
            machine.setDescription(dto.getDescription());
            machine.setType(machineType);
            machine.setStatus(dto.getStatus());
            machine.setCreatedAt(dto.getCreatedAt());
            machine.setUpdatedAt(dto.getUpdatedAt());
            machineRepository.save(machine);
            logger.debug("Machine saved: {}", machine);
        }
    }

    private void importJobs(List<JobDTO> jobDTOs) {
        for (JobDTO dto : jobDTOs) {
            logger.debug("Processing JobDTO: {}", dto);

            Job job = new Job();
            job.setTitle(dto.getTitle());
            job.setDescription(dto.getDescription());
            job.setStatus(dto.getStatus());
            job.setPriority(dto.getPriority());
            job.setDuration(dto.getDuration());

            UserDTO assigneeDTO = dto.getAssignee();
            User assignee = userRepository.findById(assigneeDTO.getId())
                    .orElseGet(() -> {
                        logger.debug("Assignee not found, creating new User: {}", assigneeDTO);
                        User newUser = new User();
                        newUser.setId(assigneeDTO.getId());
                        newUser.setEmail(assigneeDTO.getEmail());
                        return userRepository.save(newUser);
                    });
            job.setAssignee(assignee);

            MachineTypeDTO machineTypeDTO = dto.getRequiredMachineType();
            MachineType machineType = machineTypeRepository.findById(machineTypeDTO.getId())
                    .orElseGet(() -> {
                        logger.debug("MachineType not found, creating new MachineType: {}", machineTypeDTO);
                        MachineType newMachineType = new MachineType();
                        newMachineType.setId(machineTypeDTO.getId());
                        newMachineType.setName(machineTypeDTO.getName());
                        newMachineType.setDescription(machineTypeDTO.getDescription());
                        return machineTypeRepository.save(newMachineType);
                    });
            job.setRequiredMachineType(machineType);

            jobRepository.save(job);
            logger.debug("Job saved: {}", job);
        }
    }

    private void importSchedules(List<ScheduleDTO> scheduleDTOs) {
        for (ScheduleDTO dto : scheduleDTOs) {
            logger.debug("Processing ScheduleDTO: {}", dto);
            Schedule schedule = new Schedule();

            schedule.setJob(jobRepository.findById(dto.getJobId())
                    .orElseThrow(() -> new JsonException("Lavoro non trovato per ID: " + dto.getJobId())));

            schedule.setMachine(machineRepository.findById(dto.getMachineId())
                    .orElseThrow(() -> new JsonException("Macchina non trovata per ID: " + dto.getMachineId())));

            schedule.setDueDate(dto.getDueDate());
            schedule.setStartTime(dto.getStartTime());
            schedule.setEndTime(dto.getEndTime());
            schedule.setStatus(ScheduleStatus.valueOf(dto.getStatus()));
            schedule.setCreatedAt(dto.getCreatedAt());
            schedule.setUpdatedAt(dto.getUpdatedAt());

            scheduleRepository.save(schedule);
            logger.debug("Schedule saved: {}", schedule);
        }
    }

    @Transactional
    public JsonDTO processExport(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found with email: " + email));

        String userId = user.getId();
        JsonDTO jsonDTO = new JsonDTO();

        List<Job> jobs = jobRepository.findByAssignee_Id(userId);
        jsonDTO.setJobs(jobs.stream()
                .map(job -> modelMapper.map(job, JobDTO.class))
                .collect(Collectors.toList()));

        List<Schedule> schedules = new ArrayList<>();
        for (Job job : jobs) {
            List<Schedule> jobSchedules = scheduleRepository.findByJob_Id(job.getId());
            schedules.addAll(jobSchedules);
        }
        jsonDTO.setSchedules(schedules.stream()
                .map(schedule -> modelMapper.map(schedule, ScheduleDTO.class))
                .collect(Collectors.toList()));

        List<Long> machineIds = schedules.stream()
                .map(schedule -> schedule.getMachine().getId())
                .distinct()
                .toList();

        List<Machine> machines = new ArrayList<>();
        for (Long machineId : machineIds) {
            machineRepository.findById(machineId).ifPresent(machines::add);
        }
        jsonDTO.setMachines(machines.stream()
                .map(machine -> modelMapper.map(machine, MachineDTO.class))
                .collect(Collectors.toList()));

        List<Long> machineTypeIds = new ArrayList<>(machines.stream()
                .map(machine -> machine.getType().getId())
                .distinct()
                .toList());

        List<MachineType> machineTypes = new ArrayList<>();
        for (Long machineTypeId : machineTypeIds) {
            machineTypeRepository.findById(machineTypeId).ifPresent(machineTypes::add);
        }
        jsonDTO.setMachineTypes(machineTypes.stream()
                .map(machineType -> modelMapper.map(machineType, MachineTypeDTO.class))
                .collect(Collectors.toList()));

        return jsonDTO;
    }
}