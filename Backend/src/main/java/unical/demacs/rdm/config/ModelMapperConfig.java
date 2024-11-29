package unical.demacs.rdm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapperExtended modelMapper() {
        return new ModelMapperExtended();
    }
}

