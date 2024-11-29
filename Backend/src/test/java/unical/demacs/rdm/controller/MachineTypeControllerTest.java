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
import unical.demacs.rdm.config.ExtendedModelMapper;
import unical.demacs.rdm.config.exception.handler.ExceptionsHandler;
import unical.demacs.rdm.config.exception.MachineNotFoundException;
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.service.implementation.MachineTypeServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MachineTypeControllerTest {

    @Mock
    private MachineTypeServiceImpl machineTypeService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ExtendedModelMapper extendedModelMapper;

    @InjectMocks
    private MachineTypeController machineTypeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private MachineTypeDTO machineTypeDTO;
    private MachineType machineType;

    private static final Long TEST_ID = 1L;
    private static final String TEST_NAME = "Test Machine Type";
    private static final String TEST_DESCRIPTION = "Test Description";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(machineTypeController)
                .setControllerAdvice(new ExceptionsHandler(objectMapper))
                .build();

        machineTypeDTO = new MachineTypeDTO();
        machineTypeDTO.setId(TEST_ID);
        machineTypeDTO.setName(TEST_NAME);
        machineTypeDTO.setDescription(TEST_DESCRIPTION);

        machineType = MachineType.buildMachineType()
                .id(TEST_ID)
                .name(TEST_NAME)
                .description(TEST_DESCRIPTION)
                .build();
    }

    @Test
    void testCreateMachineType() throws Exception {
        when(machineTypeService.createMachineType(any(MachineTypeDTO.class))).thenReturn(machineType);
        when(modelMapper.map(eq(machineType), eq(MachineTypeDTO.class))).thenReturn(machineTypeDTO);

        mockMvc.perform(post("/api/v1/machine-type/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machineTypeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.description").value(TEST_DESCRIPTION));

        verify(machineTypeService).createMachineType(any(MachineTypeDTO.class));
    }

    @Test
    void testGetMachineTypeById() throws Exception {
        when(machineTypeService.getMachineTypeById(TEST_ID)).thenReturn(Optional.of(machineType));
        when(modelMapper.map(eq(machineType), eq(MachineTypeDTO.class))).thenReturn(machineTypeDTO);

        mockMvc.perform(get("/api/v1/machine-type/by-id/" + TEST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.description").value(TEST_DESCRIPTION));

        verify(machineTypeService).getMachineTypeById(TEST_ID);
    }

    @Test
    void testGetAllMachineTypes() throws Exception {
        List<MachineType> machineTypes = Arrays.asList(machineType);
        List<MachineTypeDTO> machineTypeDTOs = Arrays.asList(machineTypeDTO);

        when(machineTypeService.getAllMachineTypes()).thenReturn(machineTypes);
        when(extendedModelMapper.mapList(eq(machineTypes), eq(MachineTypeDTO.class))).thenReturn(machineTypeDTOs);

        mockMvc.perform(get("/api/v1/machine-type/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_ID))
                .andExpect(jsonPath("$[0].name").value(TEST_NAME))
                .andExpect(jsonPath("$[0].description").value(TEST_DESCRIPTION));

        verify(machineTypeService).getAllMachineTypes();
    }

    @Test
    void testUpdateMachineType() throws Exception {
        when(machineTypeService.updateMachineType(eq(TEST_ID), any(MachineTypeDTO.class))).thenReturn(machineType);
        when(modelMapper.map(eq(machineType), eq(MachineTypeDTO.class))).thenReturn(machineTypeDTO);

        mockMvc.perform(put("/api/v1/machine-type/" + TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machineTypeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.description").value(TEST_DESCRIPTION));

        verify(machineTypeService).updateMachineType(eq(TEST_ID), any(MachineTypeDTO.class));
    }

    @Test
    void testDeleteMachineType() throws Exception {
        doNothing().when(machineTypeService).deleteMachineType(TEST_ID);

        mockMvc.perform(delete("/api/v1/machine-type/" + TEST_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(machineTypeService).deleteMachineType(TEST_ID);
    }

    @Test
    void testGetMachineTypeById_NotFound() throws Exception {
        when(machineTypeService.getMachineTypeById(TEST_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/machine-type/by-id/" + TEST_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Machine type not found"));
    }
}