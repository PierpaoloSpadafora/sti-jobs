package unical.demacs.rdm.service;

import com.google.common.util.concurrent.RateLimiter;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MachineTypeServiceImplTest {

    @Mock
    private MachineTypeRepository machineTypeRepository;

    @Mock
    private RateLimiter rateLimiter;

    private MachineTypeServiceImpl machineTypeService;
    private MachineType testMachineType;
    private MachineTypeDTO testMachineTypeDTO;

    private static final Long TEST_ID = 1L;
    private static final String TEST_NAME = "Test Machine Type";
    private static final String TEST_DESCRIPTION = "Test Description";

    @BeforeEach
    void setUp() {
        machineTypeService = new MachineTypeServiceImpl(rateLimiter, machineTypeRepository);

        testMachineType = MachineType.buildMachineType()
                .id(TEST_ID)
                .name(TEST_NAME)
                .description(TEST_DESCRIPTION)
                .build();

        testMachineTypeDTO = new MachineTypeDTO();
        testMachineTypeDTO.setName(TEST_NAME);
        testMachineTypeDTO.setDescription(TEST_DESCRIPTION);

        when(rateLimiter.tryAcquire()).thenReturn(true);
    }

    @Test
    void testGetAllMachineTypes_Success() {
        when(machineTypeRepository.findAll()).thenReturn(Arrays.asList(testMachineType));

        List<MachineType> machineTypes = machineTypeService.getAllMachineTypes();

        assertNotNull(machineTypes);
        assertEquals(1, machineTypes.size());
        assertEquals(TEST_NAME, machineTypes.get(0).getName());
        verify(machineTypeRepository, times(1)).findAll();
    }

    @Test
    void testGetAllMachineTypes_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(MachineException.class, () -> machineTypeService.getAllMachineTypes());
        verify(machineTypeRepository, never()).findAll();
    }

    @Test
    void testGetMachineTypeById_Found() {
        when(machineTypeRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(testMachineType));

        Optional<MachineType> machineType = machineTypeService.getMachineTypeById(TEST_ID);

        assertTrue(machineType.isPresent());
        assertEquals(TEST_NAME, machineType.get().getName());
        verify(machineTypeRepository, times(1)).findById(TEST_ID);
    }

    @Test
    void testGetMachineTypeById_NotFound() {
        when(machineTypeRepository.findById(eq(TEST_ID))).thenReturn(Optional.empty());

        assertThrows(MachineException.class, () -> machineTypeService.getMachineTypeById(TEST_ID));
    }

    @Test
    void testGetMachineTypeById_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(MachineException.class, () -> machineTypeService.getMachineTypeById(TEST_ID));
        verify(machineTypeRepository, never()).findById(any());
    }

    @Test
    void testCreateMachineType_Success() {
        when(machineTypeRepository.save(any(MachineType.class))).thenReturn(testMachineType);

        MachineType createdMachineType = machineTypeService.createMachineType(testMachineTypeDTO);

        assertNotNull(createdMachineType);
        assertEquals(TEST_NAME, createdMachineType.getName());
        assertEquals(TEST_DESCRIPTION, createdMachineType.getDescription());
        verify(machineTypeRepository, times(1)).save(any(MachineType.class));
    }

    @Test
    void testCreateMachineType_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(MachineException.class, () -> machineTypeService.createMachineType(testMachineTypeDTO));
        verify(machineTypeRepository, never()).save(any());
    }

    @Test
    void testUpdateMachineType_Success() {
        when(machineTypeRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(testMachineType));
        when(machineTypeRepository.save(any(MachineType.class))).thenReturn(testMachineType);

        MachineType updatedMachineType = machineTypeService.updateMachineType(TEST_ID, testMachineTypeDTO);

        assertNotNull(updatedMachineType);
        assertEquals(TEST_NAME, updatedMachineType.getName());
        assertEquals(TEST_DESCRIPTION, updatedMachineType.getDescription());
        verify(machineTypeRepository, times(1)).save(any(MachineType.class));
    }

    @Test
    void testUpdateMachineType_NotFound() {
        when(machineTypeRepository.findById(eq(TEST_ID))).thenReturn(Optional.empty());

        assertThrows(MachineException.class, () -> machineTypeService.updateMachineType(TEST_ID, testMachineTypeDTO));
        verify(machineTypeRepository, never()).save(any());
    }

    @Test
    void testUpdateMachineType_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(MachineException.class, () -> machineTypeService.updateMachineType(TEST_ID, testMachineTypeDTO));
        verify(machineTypeRepository, never()).findById(any());
        verify(machineTypeRepository, never()).save(any());
    }

    @Test
    void testDeleteMachineType_Success() {
        when(machineTypeRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(testMachineType));
        doNothing().when(machineTypeRepository).deleteById(TEST_ID);

        machineTypeService.deleteMachineType(TEST_ID);

        verify(machineTypeRepository, times(1)).deleteById(TEST_ID);
    }

    @Test
    void testDeleteMachineType_NotFound() {
        when(machineTypeRepository.findById(eq(TEST_ID))).thenReturn(Optional.empty());

        assertThrows(MachineException.class, () -> machineTypeService.deleteMachineType(TEST_ID));
        verify(machineTypeRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteMachineType_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(MachineException.class, () -> machineTypeService.deleteMachineType(TEST_ID));
        verify(machineTypeRepository, never()).findById(any());
        verify(machineTypeRepository, never()).deleteById(any());
    }
}