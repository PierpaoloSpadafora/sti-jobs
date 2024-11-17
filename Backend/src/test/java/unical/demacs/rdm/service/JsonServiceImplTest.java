package unical.demacs.rdm.service;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import unical.demacs.rdm.config.exception.JsonException;
import unical.demacs.rdm.config.exception.UserException;
import unical.demacs.rdm.persistence.dto.JobDTO;
import unical.demacs.rdm.persistence.dto.JsonDTO;
import unical.demacs.rdm.persistence.dto.MachineDTO;
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.dto.UserDTO;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.entities.Machine;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.entities.User;
import unical.demacs.rdm.persistence.enums.JobPriority;
import unical.demacs.rdm.persistence.enums.JobStatus;
import unical.demacs.rdm.persistence.enums.MachineStatus;
import unical.demacs.rdm.persistence.repository.*;
import unical.demacs.rdm.persistence.service.implementation.JsonServiceImpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class JsonServiceImplTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private MachineRepository machineRepository;
    @Mock
    private MachineTypeRepository machineTypeRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ModelMapper modelMapper;

    private JsonServiceImpl jsonService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jsonService = new JsonServiceImpl(
                jobRepository,
                machineRepository,
                machineTypeRepository,
                scheduleRepository,
                userRepository,
                objectMapper,
                modelMapper
        );
    }

    @Test
    void testConvertToJson() throws JsonProcessingException {
        JsonDTO jsonDTO = new JsonDTO();
        when(objectMapper.writeValueAsBytes(jsonDTO)).thenReturn(new byte[]{});

        byte[] result = jsonService.convertToJson(jsonDTO);

        assertNotNull(result);
        verify(objectMapper).writeValueAsBytes(jsonDTO);
    }

    @Test
    void testConvertToJsonThrowsException() throws JsonProcessingException {
        JsonDTO jsonDTO = new JsonDTO();
        when(objectMapper.writeValueAsBytes(jsonDTO)).thenThrow(new JsonProcessingException("Test exception") {});

        JsonException exception = assertThrows(JsonException.class, () -> jsonService.convertToJson(jsonDTO));
        assertEquals("Errore durante la conversione del file JSON", exception.getMessage());
    }

    @Test
    void testProcessImport_withMachineTypes() {
        JsonDTO jsonDTO = new JsonDTO();
        jsonDTO.setMachineTypes(Collections.singletonList(new MachineTypeDTO(1L,"Type1", "Description")));

        jsonService.processImport(jsonDTO);

        verify(machineTypeRepository, times(1)).save(any(MachineType.class));
    }

    @Test
    void testProcessImport_withMachines() {
        JsonDTO jsonDTO = new JsonDTO();
        LocalDateTime date = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        List<MachineDTO> machines = Collections.singletonList(new MachineDTO(1L, "Machine1", "Description", MachineStatus.AVAILABLE,1L, "active", date, date));
        jsonDTO.setMachines(machines);

        MachineType machineType = new MachineType();
        when(machineTypeRepository.findById(1L)).thenReturn(Optional.of(machineType));

        jsonService.processImport(jsonDTO);

        verify(machineRepository, times(1)).save(any(Machine.class));
    }

    @Test
    void testProcessImport_withJobs() {
        JsonDTO jsonDTO = new JsonDTO();
        List<JobDTO> jobs = Collections.singletonList(
                new JobDTO(
                        1L,
                        "Job1",
                        "Job description",
                        JobStatus.PENDING,
                        new UserDTO("user@example.com", "userId"),
                        JobPriority.HIGH,
                        60,
                        new MachineTypeDTO(1L, "Machine Type", "Description")  // Aggiungi il tipo di macchina
                )
        );

        jsonDTO.setJobs(jobs);

        when(jobRepository.save(any(Job.class))).thenReturn(new Job());
        jsonService.processImport(jsonDTO);

        verify(jobRepository, times(1)).save(any(Job.class));
    }


    /*@Test
    void testProcessExport() {
        String email = "user@example.com";
        User user = new User();
        user.setId("userId");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        List<Job> jobs = Collections.singletonList(new Job());
        when(jobRepository.findByAssignee_Id(user.getId())).thenReturn(jobs);

        JsonDTO jsonDTO = jsonService.processExport(email);

        assertNotNull(jsonDTO);
        assertEquals(1, jsonDTO.getJobs().size());
    }

    @Test
    void testProcessExport_UserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, () -> jsonService.processExport(email));
        assertEquals("User not found with email: nonexistent@example.com", exception.getMessage());
    }*/
}

