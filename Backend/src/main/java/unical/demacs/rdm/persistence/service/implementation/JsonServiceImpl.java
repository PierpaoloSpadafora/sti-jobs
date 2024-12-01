package unical.demacs.rdm.persistence.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.config.exception.JsonException;
import unical.demacs.rdm.persistence.dto.*;
import unical.demacs.rdm.persistence.repository.*;
import unical.demacs.rdm.persistence.service.interfaces.IJsonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@AllArgsConstructor
public class JsonServiceImpl implements IJsonService {

    private static final Logger logger = LoggerFactory.getLogger(JsonServiceImpl.class);

    private final ObjectMapper objectMapper;

    public byte[] convertToJson(JsonDTO jsonDTO) {
        try {
            return objectMapper.writeValueAsBytes(jsonDTO);
        } catch (JsonProcessingException e) {
            logger.error("Errore durante la creazione del file JSON", e);
            throw new JsonException("Errore durante la conversione del file JSON");
        }
    }

    @Override
    public void processImport(JsonDTO jsonDTO) {

    }

}