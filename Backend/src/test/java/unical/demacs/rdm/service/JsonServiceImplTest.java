package unical.demacs.rdm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.dto.ScheduleWithMachineDTO;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.repository.MachineRepository;
import unical.demacs.rdm.persistence.repository.MachineTypeRepository;
import unical.demacs.rdm.persistence.repository.ScheduleRepository;
import unical.demacs.rdm.persistence.service.implementation.JsonServiceImpl;
import unical.demacs.rdm.persistence.service.interfaces.IJsonService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JsonServiceImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private IJsonService jsonService;

    @Mock
    private MachineRepository machineRepository;

    @Mock
    private MachineTypeRepository machineTypeRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        jsonService = new JsonServiceImpl(objectMapper, scheduleRepository, jobRepository, machineRepository, machineTypeRepository);
    }

    @Test
    void testReadScheduleFile_Success() throws Exception {
        File tempFile = File.createTempFile("testSchedule", ".json");
        tempFile.deleteOnExit();

        String jsonContent = "[{\"id\":1,\"jobId\":1,\"machineTypeId\":2,\"duration\":100,\"status\":\"SCHEDULED\"}]";
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(jsonContent);
        }

        List<ScheduleWithMachineDTO> schedules = jsonService.readScheduleFile(tempFile.getAbsolutePath());

        assertNotNull(schedules);
        assertEquals(1, schedules.size());
        ScheduleWithMachineDTO schedule = schedules.get(0);
        assertEquals(1L, schedule.getId());
        assertEquals(1L, schedule.getJobId());
        assertEquals(2L, schedule.getMachineTypeId());
        assertEquals(100L, schedule.getDuration());
        assertEquals(ScheduleStatus.SCHEDULED, schedule.getStatus());
    }

    @Test
    void testReadScheduleFile_FileNotFound() {
        String fileName = "nonexistent.json";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> jsonService.readScheduleFile(fileName));

        assertTrue(exception.getCause() instanceof IOException);
    }
}
