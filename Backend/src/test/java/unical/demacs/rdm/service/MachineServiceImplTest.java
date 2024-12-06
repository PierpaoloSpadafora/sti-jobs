package unical.demacs.rdm.service;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unical.demacs.rdm.config.exception.MachineException;
import unical.demacs.rdm.config.exception.TooManyRequestsException;
import unical.demacs.rdm.persistence.dto.MachineDTO;
import unical.demacs.rdm.persistence.entities.Machine;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.repository.MachineRepository;
import unical.demacs.rdm.persistence.repository.MachineTypeRepository;
import unical.demacs.rdm.persistence.service.implementation.MachineServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MachineServiceImplTest {

    @Mock
    private MachineRepository machineRepository;

    @Mock
    private MachineTypeRepository machineTypeRepository;

    @Mock
    private RateLimiter rateLimiter;

    private MachineServiceImpl machineService;
    private Machine testMachine;
    private MachineType testMachineType;
    private MachineDTO testMachineDTO;

    private static final Long TEST_ID = 1L;
    private static final String TEST_NAME = "Test Machine";
    private static final String TEST_DESCRIPTION = "Test Description";
    private static final Long TEST_TYPE_ID = 1L;

    @BeforeEach
    void setUp() {
        machineService = new MachineServiceImpl(rateLimiter, machineRepository, machineTypeRepository);

        testMachineType = new MachineType();
        testMachineType.setId(TEST_TYPE_ID);

        testMachine = Machine.machineBuilder()
                .id(TEST_ID)
                .name(TEST_NAME)
                .description(TEST_DESCRIPTION)
                .machine_type_id(testMachineType)
                .build();

        testMachineDTO = new MachineDTO();
        testMachineDTO.setName(TEST_NAME);
        testMachineDTO.setDescription(TEST_DESCRIPTION);
        testMachineDTO.setTypeId(TEST_TYPE_ID);

        when(rateLimiter.tryAcquire()).thenReturn(true);
    }

    @Test
    void testGetAllMachines_Success() {
        when(machineRepository.findAll()).thenReturn(Arrays.asList(testMachine));

        List<Machine> machines = machineService.getAllMachines();

        assertNotNull(machines);
        assertEquals(1, machines.size());
        assertEquals(TEST_NAME, machines.get(0).getName());
        verify(machineRepository, times(1)).findAll();
    }

    @Test
    void testGetAllMachines_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(TooManyRequestsException.class, () -> machineService.getAllMachines());
        verify(machineRepository, never()).findAll();
    }

    @Test
    void testGetMachineById_Found() {
        when(machineRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(testMachine));

        Machine machine = machineService.getMachineById(TEST_ID);

        assertNotNull(machine);
        assertEquals(TEST_NAME, machine.getName());
        verify(machineRepository, times(1)).findById(TEST_ID);
    }

    @Test
    void testGetMachineById_NotFound() {
        when(machineRepository.findById(eq(TEST_ID))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> machineService.getMachineById(TEST_ID));
    }

    @Test
    void testGetMachineById_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(TooManyRequestsException.class, () -> machineService.getMachineById(TEST_ID));
        verify(machineRepository, never()).findById(any());
    }

    @Test
    void testCreateMachine_Success() {
        when(machineTypeRepository.findById(eq(TEST_TYPE_ID))).thenReturn(Optional.of(testMachineType));
        when(machineRepository.save(any(Machine.class))).thenReturn(testMachine);

        Machine createdMachine = machineService.createMachine(testMachineDTO);

        assertNotNull(createdMachine);
        assertEquals(TEST_NAME, createdMachine.getName());
        assertEquals(TEST_DESCRIPTION, createdMachine.getDescription());
        verify(machineRepository, times(1)).save(any(Machine.class));
    }

    @Test
    void testCreateMachine_MachineTypeNotFound() {
        when(machineTypeRepository.findById(eq(TEST_TYPE_ID))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> machineService.createMachine(testMachineDTO));
        verify(machineRepository, never()).save(any(Machine.class));
    }

    @Test
    void testCreateMachine_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(MachineException.class, () -> machineService.createMachine(testMachineDTO));
        verify(machineRepository, never()).save(any());
        verify(machineTypeRepository, never()).findById(any());
    }

    @Test
    void testUpdateMachine_Success() {
        when(machineRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(testMachine));
        when(machineTypeRepository.findById(eq(TEST_TYPE_ID))).thenReturn(Optional.of(testMachineType));
        when(machineRepository.save(any(Machine.class))).thenReturn(testMachine);

        Machine updatedMachine = machineService.updateMachine(TEST_ID, testMachineDTO);

        assertNotNull(updatedMachine);
        assertEquals(TEST_NAME, updatedMachine.getName());
        assertEquals(TEST_DESCRIPTION, updatedMachine.getDescription());
        verify(machineRepository, times(1)).save(any(Machine.class));
    }

    @Test
    void testUpdateMachine_NotFound() {
        when(machineRepository.findById(eq(TEST_ID))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> machineService.updateMachine(TEST_ID, testMachineDTO));
        verify(machineRepository, never()).save(any());
    }

    @Test
    void testUpdateMachine_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(TooManyRequestsException.class, () -> machineService.updateMachine(TEST_ID, testMachineDTO));
        verify(machineRepository, never()).findById(any());
        verify(machineRepository, never()).save(any());
    }

    @Test
    void testDeleteMachine_Success() {
        when(machineRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(testMachine));
        doNothing().when(machineRepository).delete(any(Machine.class));

        assertTrue(machineService.deleteMachine(TEST_ID));
        verify(machineRepository, times(1)).delete(any(Machine.class));
    }

    @Test
    void testDeleteMachine_NotFound() {
        when(machineRepository.findById(eq(TEST_ID))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> machineService.deleteMachine(TEST_ID));
        verify(machineRepository, never()).delete(any());
    }

    @Test
    void testDeleteMachine_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(TooManyRequestsException.class, () -> machineService.deleteMachine(TEST_ID));
        verify(machineRepository, never()).findById(any());
        verify(machineRepository, never()).delete(any());
    }
}