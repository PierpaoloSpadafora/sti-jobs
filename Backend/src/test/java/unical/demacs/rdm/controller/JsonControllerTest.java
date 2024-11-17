package unical.demacs.rdm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import unical.demacs.rdm.persistence.dto.JobDTO;
import unical.demacs.rdm.persistence.dto.MachineDTO;
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.service.interfaces.IJobService;
import unical.demacs.rdm.persistence.service.interfaces.IMachineService;
import unical.demacs.rdm.persistence.service.interfaces.IMachineTypeService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class JsonControllerTest {

    @Mock
    private IJobService jobService;

    @Mock
    private IMachineService machineService;

    @Mock
    private IMachineTypeService machineTypeService;

    @InjectMocks
    private JsonController jsonController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private JobDTO jobDTO;
    private MachineDTO machineDTO;
    private MachineTypeDTO machineTypeDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(jsonController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        jobDTO = new JobDTO();
        jobDTO.setId(1L);
        jobDTO.setTitle("Test Job");

        machineDTO = new MachineDTO();
        machineDTO.setId(1L);
        machineDTO.setName("Test Machine");

        machineTypeDTO = new MachineTypeDTO();
        machineTypeDTO.setId(1L);
        machineTypeDTO.setName("Test MachineType");
    }

    @Test
    void testImportJob_Success() throws Exception {
        List<JobDTO> jobs = List.of(jobDTO);
        when(jobService.findById(jobDTO.getId())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/json/importJob")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobs)))
                .andExpect(status().isOk())
                .andExpect(content().string("Jobs imported successfully."));
    }

    @Test
    void testImportJob_Error() throws Exception {
        List<JobDTO> jobs = List.of(jobDTO);
        when(jobService.findById(jobDTO.getId())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/v1/json/importJob")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobs)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error importing jobs: Error"));
    }

    @Test
    void testExportJob() throws Exception {
        List<JobDTO> jobs = List.of(jobDTO);
        when(jobService.getAllJobs()).thenReturn(jobs);

        mockMvc.perform(get("/api/v1/json/exportJob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(jobDTO.getId()));
    }

    @Test
    void testImportMachineType_Success() throws Exception {
        List<MachineTypeDTO> machineTypes = List.of(machineTypeDTO);
        when(machineTypeService.getMachineTypeById(machineTypeDTO.getId())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/json/importMachineType")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machineTypes)))
                .andExpect(status().isOk())
                .andExpect(content().string("Machine Types imported successfully."));
    }

    @Test
    void testImportMachineType_Error() throws Exception {
        List<MachineTypeDTO> machineTypes = List.of(machineTypeDTO);
        when(machineTypeService.getMachineTypeById(machineTypeDTO.getId())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/v1/json/importMachineType")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machineTypes)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error importing machine types: Error"));
    }

    @Test
    void testExportMachineType() throws Exception {
        List<MachineTypeDTO> machineTypes = List.of(machineTypeDTO);
        when(machineTypeService.getAllMachineTypes()).thenReturn(machineTypes);

        mockMvc.perform(get("/api/v1/json/exportMachineType"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(machineTypeDTO.getId()));
    }

    @Test
    void testImportMachine_Success() throws Exception {
        List<MachineDTO> machines = List.of(machineDTO);
        when(machineService.findByIdOrName(machineDTO.getId(), machineDTO.getName())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/json/importMachine")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machines)))
                .andExpect(status().isOk())
                .andExpect(content().string("Machines imported successfully."));
    }

    @Test
    void testImportMachine_Error() throws Exception {
        List<MachineDTO> machines = List.of(machineDTO);
        when(machineService.findByIdOrName(machineDTO.getId(), machineDTO.getName())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/v1/json/importMachine")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machines)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error importing machines: Error"));
    }

    @Test
    void testExportMachine() throws Exception {
        List<MachineDTO> machines = List.of(machineDTO);
        when(machineService.getAllMachines()).thenReturn(machines);

        mockMvc.perform(get("/api/v1/json/exportMachine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(machineDTO.getId()));
    }
}

