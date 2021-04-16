package no.microdata.datastore;

import no.microdata.datastore.repository.MetadataRepository;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
public class MockConfig {

    @Bean
    public DataService dataService(){return Mockito.mock(DataService.class);}

    @Bean
    public MetadataService metadataService(){return Mockito.mock(MetadataService.class);}

    @Bean
    public MetadataRepository metadataRepository(){return Mockito.mock(MetadataRepository.class);}

}