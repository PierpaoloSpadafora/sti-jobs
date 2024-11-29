package unical.demacs.rdm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import unical.demacs.rdm.persistence.dto.MachineDTO;
import unical.demacs.rdm.persistence.entities.Machine;
import unical.demacs.rdm.persistence.service.implementation.MachineServiceImpl;
import unical.demacs.rdm.persistence.enums.MachineStatus;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MachineControllerTest {

    @Mock
    private MachineServiceImpl machineService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MachineController machineController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private MachineDTO machineDTO;
    private Machine machine;

    private static final Long TEST_ID = 1L;
    private static final String TEST_NAME = "Test Machine";
    private static final String TEST_DESCRIPTION = "Test Description";
    private static final Long TEST_TYPE_ID = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(machineController).build();
        objectMapper = new ObjectMapper();

        machineDTO = new MachineDTO();
        machineDTO.setId(TEST_ID);
        machineDTO.setName(TEST_NAME);
        machineDTO.setDescription(TEST_DESCRIPTION);
        machineDTO.setTypeId(TEST_TYPE_ID);
        machineDTO.setStatus(MachineStatus.AVAILABLE); // Aggiunto campo obbligatorio

        machine = Machine.machineBuilder()
                .id(TEST_ID)
                .name(TEST_NAME)
                .description(TEST_DESCRIPTION)
                .status(MachineStatus.AVAILABLE) // Aggiunto campo obbligatorio
                .build();
    }

    @Test
    void testCreateMachine() throws Exception {
        when(machineService.createMachine(any(MachineDTO.class))).thenReturn(machine);
        when(modelMapper.map(any(), eq(MachineDTO.class))).thenReturn(machineDTO);

        mockMvc.perform(post("/api/v1/machine")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machineDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.description").value(TEST_DESCRIPTION))
                .andExpect(jsonPath("$.status").value(MachineStatus.AVAILABLE.toString()));

        verify(machineService, times(1)).createMachine(any(MachineDTO.class));
    }

    @Test
    void testUpdateMachine() throws Exception {
        when(machineService.updateMachine(eq(TEST_ID), any(MachineDTO.class))).thenReturn(machine);
        when(modelMapper.map(any(), eq(MachineDTO.class))).thenReturn(machineDTO);

        mockMvc.perform(put("/api/v1/machine/" + TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machineDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.description").value(TEST_DESCRIPTION))
                .andExpect(jsonPath("$.status").value(MachineStatus.AVAILABLE.toString()));

        verify(machineService, times(1)).updateMachine(eq(TEST_ID), any(MachineDTO.class));
    }

    @Test
    void testGetAllMachines() throws Exception {
        List<Machine> machines = Arrays.asList(machine);
        when(machineService.getAllMachines()).thenReturn(machines);
        when(modelMapper.map(any(), eq(MachineDTO.class))).thenReturn(machineDTO);

        mockMvc.perform(get("/api/v1/machine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_ID))
                .andExpect(jsonPath("$[0].name").value(TEST_NAME))
                .andExpect(jsonPath("$[0].description").value(TEST_DESCRIPTION))
                .andExpect(jsonPath("$[0].status").value(MachineStatus.AVAILABLE.toString()));

        verify(machineService, times(1)).getAllMachines();
    }

    @Test
    void testGetMachineById() throws Exception {
        when(machineService.getMachineById(TEST_ID)).thenReturn(machine);
        when(modelMapper.map(any(), eq(MachineDTO.class))).thenReturn(machineDTO);

        mockMvc.perform(get("/api/v1/machine/" + TEST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.description").value(TEST_DESCRIPTION))
                .andExpect(jsonPath("$.status").value(MachineStatus.AVAILABLE.toString()));

        verify(machineService, times(1)).getMachineById(TEST_ID);
    }

    @Test
    void testDeleteMachine() throws Exception {
        when(machineService.deleteMachine(TEST_ID)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/machine/" + TEST_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(machineService, times(1)).deleteMachine(TEST_ID);
    }
}