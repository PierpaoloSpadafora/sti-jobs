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
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.service.implementation.MachineTypeServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MachineTypeControllerTest {

    @Mock
    private MachineTypeServiceImpl machineTypeServiceImpl;

    @InjectMocks
    private MachineTypeController machineTypeController;

    private MockMvc mockMvc;
    private MachineTypeDTO machineTypeDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(machineTypeController).build();

        machineTypeDTO = new MachineTypeDTO();
        machineTypeDTO.setId(1L);
        machineTypeDTO.setName("Test Machine");
        machineTypeDTO.setDescription("Test Machine Description");
    }

    @Test
    void testCreateMachineType() throws Exception {
        when(machineTypeServiceImpl.createMachineType(any(MachineTypeDTO.class))).thenReturn(machineTypeDTO);

        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(post("/api/v1/machine-type/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(machineTypeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(machineTypeDTO.getId()))
                .andExpect(jsonPath("$.name").value(machineTypeDTO.getName()));
    }

    @Test
    void testGetMachineTypeById_Success() throws Exception {
        when(machineTypeServiceImpl.getMachineTypeById(1L)).thenReturn(Optional.of(machineTypeDTO));

        mockMvc.perform(get("/api/v1/machine-type/by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(machineTypeDTO.getId()))
                .andExpect(jsonPath("$.name").value(machineTypeDTO.getName()));
    }


    @Test
    void testGetAllMachineTypes() throws Exception {
        List<MachineTypeDTO> machineTypes = List.of(machineTypeDTO);

        when(machineTypeServiceImpl.getAllMachineTypes()).thenReturn(machineTypes);

        mockMvc.perform(get("/api/v1/machine-type/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(machineTypeDTO.getId()))
                .andExpect(jsonPath("$[0].name").value(machineTypeDTO.getName()));
    }

    @Test
    void testDeleteMachineType_Success() throws Exception {
        doNothing().when(machineTypeServiceImpl).deleteMachineType(1L);

        mockMvc.perform(delete("/api/v1/machine-type/1"))
                .andExpect(status().isNoContent());
    }

}
