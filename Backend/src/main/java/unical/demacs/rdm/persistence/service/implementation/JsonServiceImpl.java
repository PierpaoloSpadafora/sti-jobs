package unical.demacs.rdm.persistence.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.persistence.dto.*;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.repository.ScheduleRepository;
import unical.demacs.rdm.persistence.service.interfaces.IJsonService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JsonServiceImpl implements IJsonService {

    private final ObjectMapper objectMapper;
    private final ScheduleRepository scheduleRepository;

    public List<ScheduleWithMachineDTO> readScheduleFile(String fileName) {
        File file = new File(fileName);
        try {
            return objectMapper.readValue(file,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ScheduleWithMachineDTO.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();
        return schedules.stream()
                .map(schedule -> new ScheduleDTO(
                        schedule.getId(),
                        schedule.getJob().getId(),
                        schedule.getMachineType().getId(),
                        schedule.getDueDate(),
                        schedule.getStartTime(),
                        schedule.getDuration(),
                        schedule.getStatus(),
                        schedule.getMachine() != null ? schedule.getMachine().getId() : null,
                        schedule.getMachine() != null ? schedule.getMachine().getName() : null
                ))
                .collect(Collectors.toList());
    }

    public byte[] exportSchedulesToJson() {
        try {
            List<ScheduleDTO> schedules = getAllSchedules();
            return objectMapper.writeValueAsBytes(schedules);
        } catch (IOException e) {
            throw new RuntimeException("Errore durante l'esportazione degli schedule in JSON", e);
        }
    }

}