package no.microdata.datastore;

import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
public class MockConfig {

    @Bean
    public DataStoreService dataStoreService(){return Mockito.mock(DataStoreService.class);}

//    @Bean
//    public DataStructureService dataStructureService(){return Mockito.mock(DataStructureService.class);}

    @Bean
    public MetadataService metadataService(){return Mockito.mock(MetadataService.class);}

    @Bean
    public GenericService genericService(){return Mockito.mock(GenericService.class);}

    @Bean
    public AllMetadataService allMetadataService(){return Mockito.mock(AllMetadataService.class);}

    @Bean
    public DataService dataService(){return Mockito.mock(DataService.class);}
}