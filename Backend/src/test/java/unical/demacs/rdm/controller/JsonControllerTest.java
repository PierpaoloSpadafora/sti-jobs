package unical.demacs.rdm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import unical.demacs.rdm.config.ModelMapperExtended;
import unical.demacs.rdm.persistence.dto.*;
import unical.demacs.rdm.persistence.entities.*;
import unical.demacs.rdm.persistence.enums.JobPriority;
import unical.demacs.rdm.persistence.enums.JobStatus;
import unical.demacs.rdm.persistence.enums.MachineStatus;
import unical.demacs.rdm.persistence.service.interfaces.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class JsonControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IJobService jobService;

    @Mock
    private IMachineService machineService;

    @Mock
    private IMachineTypeService machineTypeService;

    @Mock
    private IJsonService jsonService;

    @Mock
    private ModelMapperExtended modelMapperExtended;

    @InjectMocks
    private JsonController jsonController;

    private ObjectMapper objectMapper;

    private JobDTO jobDTO;
    private MachineTypeDTO machineTypeDTO;
    private MachineDTO machineDTO;
    private ScheduleDTO scheduleDTO;

    private Job job;
    private MachineType machineType;
    private Machine machine;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(jsonController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        jobDTO = new JobDTO();
        jobDTO.setId(1L);
        jobDTO.setTitle("Test Job");
        jobDTO.setDescription("Test Job Description");
        jobDTO.setStatus(JobStatus.PENDING);
        jobDTO.setPriority(JobPriority.LOW);
        jobDTO.setDuration(100L);
        jobDTO.setIdMachineType(1L);
        jobDTO.setAssigneeEmail("test@example.com");

        machineTypeDTO = new MachineTypeDTO();
        machineTypeDTO.setId(1L);
        machineTypeDTO.setName("Test Machine Type");
        machineTypeDTO.setDescription("Test Description");

        machineDTO = new MachineDTO();
        machineDTO.setId(1L);
        machineDTO.setName("Test Machine");
        machineDTO.setDescription("Test Machine Description");
        machineDTO.setTypeId(1L);
        machineDTO.setStatus(MachineStatus.AVAILABLE);

        scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(1L);
        scheduleDTO.setJobId(1L);
        scheduleDTO.setMachineTypeId(1L);
        scheduleDTO.setDuration(100L);
        scheduleDTO.setStatus(null);

        job = Job.buildJob()
                .id(1L)
                .title("Test Job")
                .description("Test Job Description")
                .status(JobStatus.PENDING)
                .priority(JobPriority.LOW)
                .duration(100L)
                .requiredMachineType(null)
                .assignee(null)
                .build();

        machineType = MachineType.buildMachineType()
                .id(1L)
                .name("Test Machine Type")
                .description("Test Description")
                .build();

        machine = Machine.machineBuilder()
                .id(1L)
                .name("Test Machine")
                .description("Test Machine Description")
                .machine_type_id(machineType) // Corretto da .type() a .machine_type_id()
                .status(MachineStatus.AVAILABLE)
                .build();
    }

    @Test
    void testImportJob_Success() throws Exception {
        List<JobDTO> jobs = Collections.singletonList(jobDTO);
        String assigneeEmail = "test@example.com";

        when(jobService.createJob(eq(assigneeEmail), any(JobDTO.class))).thenReturn(job);

        mockMvc.perform(post("/api/v1/json/importJob")
                        .param("assigneeEmail", assigneeEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobs)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Jobs imported successfully."));

        verify(jobService, times(1)).createJob(eq(assigneeEmail), any(JobDTO.class));
    }

    @Test
    void testImportJob_Failure() throws Exception {
        List<JobDTO> jobs = Collections.singletonList(jobDTO);
        String assigneeEmail = "test@example.com";

        when(jobService.createJob(eq(assigneeEmail), any(JobDTO.class)))
                .thenThrow(new RuntimeException("Error importing jobs"));

        mockMvc.perform(post("/api/v1/json/importJob")
                        .param("assigneeEmail", assigneeEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobs)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error importing jobs: Error importing jobs"));

        verify(jobService, times(1)).createJob(eq(assigneeEmail), any(JobDTO.class));
    }

    @Test
    void testExportJob() throws Exception {
        List<Job> jobs = Arrays.asList(job);
        List<JobDTO> jobDTOs = Arrays.asList(jobDTO);

        when(jobService.getAllJobs()).thenReturn(jobs);
        when(modelMapperExtended.mapList(eq(jobs), eq(JobDTO.class))).thenReturn(jobDTOs);

        mockMvc.perform(get("/api/v1/json/exportJob")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(jobDTOs)));

        verify(jobService, times(1)).getAllJobs();
        verify(modelMapperExtended, times(1)).mapList(eq(jobs), eq(JobDTO.class));
    }

    @Test
    void testImportMachineType_Success() throws Exception {
        List<MachineTypeDTO> machineTypes = Collections.singletonList(machineTypeDTO);

        when(machineTypeService.createMachineType(any(MachineTypeDTO.class))).thenReturn(machineType);

        mockMvc.perform(post("/api/v1/json/importMachineType")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machineTypes)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("MachineTypes imported successfully."));

        verify(machineTypeService, times(1)).createMachineType(any(MachineTypeDTO.class));
    }

    @Test
    void testImportMachineType_Failure() throws Exception {
        List<MachineTypeDTO> machineTypes = Collections.singletonList(machineTypeDTO);

        when(machineTypeService.createMachineType(any(MachineTypeDTO.class)))
                .thenThrow(new RuntimeException("Error importing MachineTypes"));

        mockMvc.perform(post("/api/v1/json/importMachineType")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machineTypes)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error importing MachineTypes: Error importing MachineTypes"));
    }

    @Test
    void testExportMachineType() throws Exception {
        List<MachineType> machineTypes = Arrays.asList(machineType);
        List<MachineTypeDTO> machineTypeDTOs = Arrays.asList(machineTypeDTO);

        when(machineTypeService.getAllMachineTypes()).thenReturn(machineTypes);
        when(modelMapperExtended.mapList(eq(machineTypes), eq(MachineTypeDTO.class))).thenReturn(machineTypeDTOs);

        mockMvc.perform(get("/api/v1/json/exportMachineType")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(machineTypeDTOs)));

        verify(machineTypeService, times(1)).getAllMachineTypes();
        verify(modelMapperExtended, times(1)).mapList(eq(machineTypes), eq(MachineTypeDTO.class));
    }

    @Test
    void testImportMachine_Success() throws Exception {
        List<MachineDTO> machines = Collections.singletonList(machineDTO);

        when(machineService.createMachine(any(MachineDTO.class))).thenReturn(machine);

        mockMvc.perform(post("/api/v1/json/importMachine")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machines)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Machines imported successfully."));

        verify(machineService, times(1)).createMachine(any(MachineDTO.class));
    }

    @Test
    void testImportMachine_Failure() throws Exception {
        List<MachineDTO> machines = Collections.singletonList(machineDTO);

        when(machineService.createMachine(any(MachineDTO.class)))
                .thenThrow(new RuntimeException("Error importing Machines"));

        mockMvc.perform(post("/api/v1/json/importMachine")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machines)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Errore durante l'importazione delle Machines: Error importing Machines"));

        verify(machineService, times(1)).createMachine(any(MachineDTO.class));
    }

    @Test
    void testExportMachine() throws Exception {
        List<Machine> machines = Arrays.asList(machine);
        List<MachineDTO> machineDTOs = Arrays.asList(machineDTO);

        when(machineService.getAllMachines()).thenReturn(machines);
        when(modelMapperExtended.mapList(eq(machines), eq(MachineDTO.class))).thenReturn(machineDTOs);

        mockMvc.perform(get("/api/v1/json/exportMachine")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(machineDTOs)));

        verify(machineService, times(1)).getAllMachines();
        verify(modelMapperExtended, times(1)).mapList(eq(machines), eq(MachineDTO.class));
    }

    @Test
    void testExportJobScheduledPriority() throws Exception {
        List<ScheduleWithMachineDTO> schedules = Collections.singletonList(new ScheduleWithMachineDTO());

        when(jsonService.readScheduleFile(anyString())).thenReturn(schedules);

        mockMvc.perform(get("/api/v1/json/export-job-scheduled-by-priority")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(schedules)));

        verify(jsonService, times(1)).readScheduleFile("./data/job-scheduled-by-priority.json");
    }

    @Test
    void testExportJobScheduledDueDate() throws Exception {
        List<ScheduleWithMachineDTO> schedules = Collections.singletonList(new ScheduleWithMachineDTO());

        when(jsonService.readScheduleFile(anyString())).thenReturn(schedules);

        mockMvc.perform(get("/api/v1/json/export-job-scheduled-by-due-date")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(schedules)));

        verify(jsonService, times(1)).readScheduleFile("./data/job-scheduled-by-due-date.json");
    }

    @Test
    void testExportJobScheduledDuration() throws Exception {
        List<ScheduleWithMachineDTO> schedules = Collections.singletonList(new ScheduleWithMachineDTO());

        when(jsonService.readScheduleFile(anyString())).thenReturn(schedules);

        mockMvc.perform(get("/api/v1/json/export-job-scheduled-by-duration")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(schedules)));

        verify(jsonService, times(1)).readScheduleFile("./data/job-scheduled-by-duration.json");
    }
}
