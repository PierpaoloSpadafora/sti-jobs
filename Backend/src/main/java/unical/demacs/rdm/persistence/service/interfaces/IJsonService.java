package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.dto.JsonDTO;

public interface IJsonService {
    void processImport(JsonDTO jsonDTO);
    //JsonDTO processExport(String userId);
}