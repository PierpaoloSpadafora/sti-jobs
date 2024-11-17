package unical.demacs.rdm.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unical.demacs.rdm.config.exception.MachineException;
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.repository.MachineTypeRepository;
import unical.demacs.rdm.persistence.service.implementation.MachineTypeServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MachineTypeServiceImplTest {

    @Mock
    private MachineTypeRepository machineTypeRepository;

    private MachineTypeServiceImpl machineTypeService;

    private MachineTypeDTO machineTypeDTO;
    private MachineType machineType;

    @BeforeEach
    void setUp() {
        machineTypeService = new MachineTypeServiceImpl(machineTypeRepository);

        machineTypeDTO = new MachineTypeDTO();
        machineTypeDTO.setId(1L);
        machineTypeDTO.setName("Type A");
        machineTypeDTO.setDescription("Description for Type A");

        machineType = new MachineType();
        machineType.setId(1L);
        machineType.setName("Type A");
        machineType.setDescription("Description for Type A");
    }

    @Test
    void testCreateMachineType_NewType() {
        when(machineTypeRepository.findByName(eq(machineTypeDTO.getName()))).thenReturn(Optional.empty());
        when(machineTypeRepository.save(any(MachineType.class))).thenReturn(machineType);

        MachineTypeDTO createdMachineType = machineTypeService.createMachineType(machineTypeDTO);

        assertNotNull(createdMachineType);
        assertEquals(machineTypeDTO.getName(), createdMachineType.getName());
        verify(machineTypeRepository, times(1)).save(any(MachineType.class));
    }

    @Test
    void testCreateMachineType_ExistingType() {
        when(machineTypeRepository.findByName(eq(machineTypeDTO.getName()))).thenReturn(Optional.of(machineType));

        MachineTypeDTO existingMachineType = machineTypeService.createMachineType(machineTypeDTO);

        assertNotNull(existingMachineType);
        assertEquals(machineTypeDTO.getName(), existingMachineType.getName());
        verify(machineTypeRepository, never()).save(any(MachineType.class));
    }

    @Test
    void testGetMachineTypeById_Found() {
        when(machineTypeRepository.findById(eq(machineType.getId()))).thenReturn(Optional.of(machineType));

        Optional<MachineTypeDTO> foundMachineType = machineTypeService.getMachineTypeById(machineType.getId());

        assertTrue(foundMachineType.isPresent());
        assertEquals(machineType.getId(), foundMachineType.get().getId());
    }

    @Test
    void testGetMachineTypeById_NotFound() {
        when(machineTypeRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<MachineTypeDTO> foundMachineType = machineTypeService.getMachineTypeById(1L);

        assertFalse(foundMachineType.isPresent());
    }

    @Test
    void testGetAllMachineTypes() {
        when(machineTypeRepository.findAll()).thenReturn(List.of(machineType));

        List<MachineTypeDTO> machineTypes = machineTypeService.getAllMachineTypes();

        assertNotNull(machineTypes);
        assertEquals(1, machineTypes.size());
        assertEquals(machineType.getName(), machineTypes.get(0).getName());
    }

    @Test
    void testDeleteMachineType_Success() {
        when(machineTypeRepository.existsById(eq(machineType.getId()))).thenReturn(true);

        machineTypeService.deleteMachineType(machineType.getId());

        verify(machineTypeRepository, times(1)).deleteById(machineType.getId());
    }

    @Test
    void testDeleteMachineType_NotFound() {
        when(machineTypeRepository.existsById(anyLong())).thenReturn(false);

        Exception exception = assertThrows(MachineException.class, () -> machineTypeService.deleteMachineType(1L));

        assertEquals("No machine type found", exception.getMessage());
        verify(machineTypeRepository, never()).deleteById(anyLong());
    }
}

