package unical.demacs.rdm.persistence.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.persistence.dto.*;
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

    public List<ScheduleWithMachineDTO> readScheduleFile(String fileName) {
        File file = new File(fileName);
        try {
            return objectMapper.readValue(file,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ScheduleWithMachineDTO.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}