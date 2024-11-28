package unical.demacs.rdm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;
import java.util.List;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ExtendedModelMapper modelMapper() {
        return new ExtendedModelMapper();
    }
}

