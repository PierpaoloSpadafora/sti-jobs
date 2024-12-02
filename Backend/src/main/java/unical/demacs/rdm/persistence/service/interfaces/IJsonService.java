package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.dto.ScheduleDTO;

import java.util.List;

public interface IJsonService {
    public List<ScheduleDTO> readScheduleFile(String fileName);
}