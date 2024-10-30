package unical.demacs.rdm.persistence.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import unical.demacs.rdm.persistence.dto.JsonDTO;

public interface IJsonService {
    void processImport(JsonDTO jsonDTO);
    public JsonDTO parseJsonFile(MultipartFile multipartFile);
}
