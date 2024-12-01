package unical.demacs.rdm.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor

public class JsonFileService {
    private final ObjectMapper objectMapper;

    public List<ScheduleDTO> readScheduleFile(String fileName) throws IOException {
        File file = new File(fileName);
        return objectMapper.readValue(file,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ScheduleDTO.class));
    }
}