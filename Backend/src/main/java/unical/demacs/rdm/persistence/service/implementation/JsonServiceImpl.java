package unical.demacs.rdm.persistence.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import unical.demacs.rdm.persistence.dto.*;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.repository.MachineRepository;
import unical.demacs.rdm.persistence.repository.MachineTypeRepository;
import unical.demacs.rdm.persistence.repository.ScheduleRepository;
import unical.demacs.rdm.persistence.service.interfaces.IJsonService;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class JsonServiceImpl implements IJsonService {

    private final ObjectMapper objectMapper;
    private final ScheduleRepository scheduleRepository;
    private final JobRepository jobRepository;
    private final MachineRepository machineRepository;
    private final MachineTypeRepository machineTypeRepository;

    public List<ScheduleWithMachineDTO> readScheduleFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new RuntimeException(new IOException("File not found: " + fileName));
        }
        try {
            return objectMapper.readValue(file,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ScheduleWithMachineDTO.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void importSchedules(MultipartFile file) {
        try {
            String jsonContent = new String(file.getBytes());
            List<ScheduleWithMachineDTO> schedules = objectMapper.readValue(
                    jsonContent,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ScheduleWithMachineDTO.class)
            );
            for (ScheduleWithMachineDTO dto : schedules) {
                Schedule schedule = mapDtoToEntity(dto);
                scheduleRepository.save(schedule);
            }
        } catch (IOException e) {
            throw new RuntimeException("Errore durante la lettura del file JSON: " + e.getMessage(), e);
        }
    }

    private Schedule mapDtoToEntity(ScheduleWithMachineDTO dto) {
        return Schedule.scheduleBuilder()
                .job(jobRepository.findById(dto.getJobId())
                        .orElseThrow(() -> new RuntimeException("Job non trovato")))
                .machineType(machineTypeRepository.findById(dto.getMachineTypeId())
                        .orElseThrow(() -> new RuntimeException("MachineType non trovato")))
                .machine(dto.getMachineId() != null ?
                        machineRepository.findById(dto.getMachineId())
                                .orElseThrow(() -> new RuntimeException("Machine non trovata"))
                        : null)
                .dueDate(dto.getDueDate())
                .startTime(dto.getStartTime())
                .duration(dto.getDuration())
                .status(dto.getStatus())
                .build();
    }

}