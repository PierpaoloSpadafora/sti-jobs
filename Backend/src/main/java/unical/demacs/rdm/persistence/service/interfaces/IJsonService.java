package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.dto.ScheduleWithMachineDTO;

import java.util.List;

public interface IJsonService {
    public List<ScheduleWithMachineDTO> readScheduleFile(String fileName);
}