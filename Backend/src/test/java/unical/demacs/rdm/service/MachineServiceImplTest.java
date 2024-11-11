package unical.demacs.rdm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unical.demacs.rdm.persistence.dto.MachineDTO;
import unical.demacs.rdm.persistence.entities.Machine;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.enums.MachineStatus;
import unical.demacs.rdm.persistence.repository.MachineRepository;
import unical.demacs.rdm.persistence.service.implementation.MachineServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MachineServiceImplTest {

    @Mock
    private MachineRepository machineRepository;

    @InjectMocks
    private MachineServiceImpl machineService;

    private MachineDTO machineDTO;
    private Machine machine;

    @BeforeEach
    void setUp() {
        machineDTO = new MachineDTO();
        machineDTO.setId(1L);
        machineDTO.setName("Test Machine");
        machineDTO.setDescription("Description");
        machineDTO.setStatus(MachineStatus.AVAILABLE);
        machineDTO.setTypeId(1L);

        MachineType machineType = new MachineType();
        machineType.setId(1L);

        machine = new Machine();
        machine.setId(1L);
        machine.setName("Test Machine");
        machine.setDescription("Description");
        machine.setStatus(MachineStatus.AVAILABLE);
        machine.setType(machineType);
    }

    @Test
    void testCreateMachine() {
        when(machineRepository.save(any(Machine.class))).thenReturn(machine);
        MachineDTO createdMachine = machineService.createMachine(machineDTO);
        assertNotNull(createdMachine);
        assertEquals(machineDTO.getName(), createdMachine.getName());
        verify(machineRepository, times(1)).save(any(Machine.class));
    }

    @Test
    void testGetMachineById_Found() {
        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
        MachineDTO foundMachine = machineService.getMachineById(1L);
        assertNotNull(foundMachine);
        assertEquals(machine.getId(), foundMachine.getId());
    }

    @Test
    void testGetMachineById_NotFound() {
        when(machineRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> machineService.getMachineById(1L));
        assertEquals("Machine not found with id: 1", exception.getMessage());
    }

    @Test
    void testUpdateMachine_Success() {
        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
        when(machineRepository.save(any(Machine.class))).thenReturn(machine);
        MachineDTO updatedMachine = machineService.updateMachine(1L, machineDTO);
        assertNotNull(updatedMachine);
        assertEquals(machineDTO.getName(), updatedMachine.getName());
        verify(machineRepository, times(1)).save(any(Machine.class));
    }

    @Test
    void testUpdateMachine_NotFound() {
        when(machineRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> machineService.updateMachine(1L, machineDTO));
    }

    @Test
    void testDeleteMachine_Success() {
        when(machineRepository.existsById(1L)).thenReturn(true);
        machineService.deleteMachine(1L);
        verify(machineRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteMachine_NotFound() {
        when(machineRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> machineService.deleteMachine(1L));
    }

    @Test
    void testGetAllMachines() {
        when(machineRepository.findAll()).thenReturn(List.of(machine));
        List<MachineDTO> machines = machineService.getAllMachines();
        assertNotNull(machines);
        assertEquals(1, machines.size());
    }

    @Test
    void testFindById_Found() {
        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
        Optional<MachineDTO> foundMachine = machineService.findById(1L);
        assertTrue(foundMachine.isPresent());
        assertEquals(machine.getId(), foundMachine.get().getId());
    }

    @Test
    void testFindById_NotFound() {
        when(machineRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<MachineDTO> foundMachine = machineService.findById(1L);
        assertFalse(foundMachine.isPresent());
    }

    @Test
    void testFindByIdOrName_FoundById() {
        when(machineRepository.findByIdOrName(1L, "Test Machine")).thenReturn(Optional.of(machine));
        Optional<MachineDTO> foundMachine = machineService.findByIdOrName(1L, "Test Machine");
        assertTrue(foundMachine.isPresent());
        assertEquals(machine.getId(), foundMachine.get().getId());
    }

    @Test
    void testFindByIdOrName_NotFound() {
        when(machineRepository.findByIdOrName(1L, "Test Machine")).thenReturn(Optional.empty());
        Optional<MachineDTO> foundMachine = machineService.findByIdOrName(1L, "Test Machine");
        assertFalse(foundMachine.isPresent());
    }

    @Test
    void testSaveMachine() {
        when(machineRepository.save(any(Machine.class))).thenReturn(machine);
        MachineDTO savedMachine = machineService.saveMachine(machineDTO);
        assertNotNull(savedMachine);
        assertEquals(machineDTO.getName(), savedMachine.getName());
        verify(machineRepository, times(1)).save(any(Machine.class));
    }
}
