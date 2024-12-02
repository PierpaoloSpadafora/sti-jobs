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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import unical.demacs.rdm.config.ModelMapperExtended;
import unical.demacs.rdm.config.exception.MachineNotFoundException;
import unical.demacs.rdm.config.exception.handler.ExceptionsHandler;
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.service.implementation.MachineTypeServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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
    private ModelMapperExtended modelMapperExtended;

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
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(machineTypeController)
                .setControllerAdvice(new ExceptionsHandler(objectMapper))
                .setMessageConverters(converter)
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
        given(machineTypeService.createMachineType(any(MachineTypeDTO.class))).willReturn(machineType);


        mockMvc.perform(post("/api/v1/machine-type/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machineTypeDTO)))
                .andExpect(status().isOk());


        verify(machineTypeService).createMachineType(any(MachineTypeDTO.class));
    }

    @Test
    void testGetMachineTypeById() throws Exception {
        given(machineTypeService.getMachineTypeById(TEST_ID)).willReturn(Optional.ofNullable(machineType));

        mockMvc.perform(get("/api/v1/machine-type/by-id/{id}", TEST_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(machineTypeService).getMachineTypeById(TEST_ID);
    }

    @Test
    void testGetMachineTypeById_NotFound() throws Exception {
        given(machineTypeService.getMachineTypeById(TEST_ID))
                .willThrow(new MachineNotFoundException("Machine type not found."));

        mockMvc.perform(get("/api/v1/machine-type/by-id/{id}", TEST_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(machineTypeService).getMachineTypeById(TEST_ID);
    }

    @Test
    void testGetAllMachineTypes() throws Exception {
        List<MachineType> machineTypes = Collections.singletonList(machineType);
        List<MachineTypeDTO> machineTypeDTOs = Collections.singletonList(machineTypeDTO);

        given(machineTypeService.getAllMachineTypes()).willReturn(machineTypes);
        given(modelMapperExtended.mapList(eq(machineTypes), eq(MachineTypeDTO.class))).willReturn(machineTypeDTOs);

        mockMvc.perform(get("/api/v1/machine-type/get-all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(TEST_ID))
                .andExpect(jsonPath("$[0].name").value(TEST_NAME))
                .andExpect(jsonPath("$[0].description").value(TEST_DESCRIPTION));

        verify(machineTypeService).getAllMachineTypes();
    }

    @Test
    void testUpdateMachineType() throws Exception {
        given(machineTypeService.updateMachineType(eq(TEST_ID), any(MachineTypeDTO.class))).willReturn(machineType);
        given(modelMapper.map(any(MachineType.class), eq(MachineTypeDTO.class))).willReturn(machineTypeDTO);

        mockMvc.perform(put("/api/v1/machine-type/{id}", TEST_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machineTypeDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.description").value(TEST_DESCRIPTION));

        verify(machineTypeService).updateMachineType(eq(TEST_ID), any(MachineTypeDTO.class));
    }

    @Test
    void testDeleteMachineType() throws Exception {
        doNothing().when(machineTypeService).deleteMachineType(TEST_ID);

        mockMvc.perform(delete("/api/v1/machine-type/{id}", TEST_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(machineTypeService).deleteMachineType(TEST_ID);
    }
}