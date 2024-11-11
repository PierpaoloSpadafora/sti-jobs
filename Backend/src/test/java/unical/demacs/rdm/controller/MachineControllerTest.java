package unical.demacs.rdm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import unical.demacs.rdm.persistence.dto.MachineDTO;
import unical.demacs.rdm.persistence.enums.MachineStatus;
import unical.demacs.rdm.persistence.service.implementation.MachineServiceImpl;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(machineController).build();
        objectMapper = new ObjectMapper();

        machineDTO = new MachineDTO();
        machineDTO.setId(1L);
        machineDTO.setName("Test Machine");
        machineDTO.setDescription("Test Description");
        machineDTO.setStatus(MachineStatus.AVAILABLE);
        machineDTO.setTypeId(1L);

        // Campi opzionali
        machineDTO.setTypeName("Test Type");
        //machineDTO.setCreatedAt(LocalDateTime.now());
        //machineDTO.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateMachine() throws Exception {
        when(machineService.createMachine(any(MachineDTO.class))).thenReturn(machineDTO);
        when(modelMapper.map(any(), eq(MachineDTO.class))).thenReturn(machineDTO);

        mockMvc.perform(post("/api/v1/machine")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machineDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(machineDTO.getId()));
    }

    @Test
    void testUpdateMachine() throws Exception {
        when(machineService.updateMachine(eq(1L), any(MachineDTO.class))).thenReturn(machineDTO);
        when(modelMapper.map(any(), eq(MachineDTO.class))).thenReturn(machineDTO);

        mockMvc.perform(put("/api/v1/machine/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machineDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(machineDTO.getId()));
    }

    @Test
    void testGetMachineById() throws Exception {
        when(machineService.getMachineById(1L)).thenReturn(machineDTO);
        when(modelMapper.map(any(), eq(MachineDTO.class))).thenReturn(machineDTO);

        mockMvc.perform(get("/api/v1/machine/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(machineDTO.getId()));
    }

    @Test
    void testGetAllMachines() throws Exception {
        List<MachineDTO> machines = new ArrayList<>();
        machines.add(machineDTO);
        when(machineService.getAllMachines()).thenReturn(machines);
        when(modelMapper.map(any(), eq(MachineDTO.class))).thenReturn(machineDTO);

        mockMvc.perform(get("/api/v1/machine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(machineDTO.getId()));
    }

    @Test
    void testDeleteMachine() throws Exception {
        mockMvc.perform(delete("/api/v1/machine/1"))
                .andExpect(status().isNoContent());
    }
}