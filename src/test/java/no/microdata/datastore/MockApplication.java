package no.microdata.datastore;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.microdata.datastore.services.AllMetadataServiceImpl;
import no.microdata.datastore.services.DataStoreServiceImpl;
import no.microdata.datastore.services.DataStructureServiceImpl;
import no.microdata.datastore.services.GenericServiceImpl;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

/**
 * This application class is used for spring wiring when testing the rest api. We need the component scan for bootstrapping
 * the application, but we also need to exclude scan of some classes. DataSetService.class must be excluded, because the sub class
 * DefaultDataSetService has autowire annotations we don't want work with and Application.class must be excluded, because it will
 * start scanning all the packages once again.
 */
@Configuration
@ComponentScan(basePackages = {"no.microdata.datastore"}, excludeFilters =
        {
                @ComponentScan.Filter(classes = DataStoreServiceImpl.class, type = FilterType.ASSIGNABLE_TYPE),
                @ComponentScan.Filter(classes = AllMetadataServiceImpl.class, type = FilterType.ASSIGNABLE_TYPE),
                @ComponentScan.Filter(classes = DataStructureServiceImpl.class, type = FilterType.ASSIGNABLE_TYPE),
                @ComponentScan.Filter(classes = GenericServiceImpl.class, type = FilterType.ASSIGNABLE_TYPE),
                @ComponentScan.Filter(classes = Application.class, type = FilterType.ASSIGNABLE_TYPE)
        })

@EnableAutoConfiguration()
public class MockApplication {

    @Bean
    HttpMessageConverter messagePackMessageConverter() {
        return new AbstractJackson2HttpMessageConverter(
                new ObjectMapper(new MessagePackFactory()),
                new MediaType("application", "x-msgpack")) {
        };
    }

    static void main(String[] args) {
        Class[] sources = new Class[] {MockApplication.class};
        SpringApplication.run(sources, args);
    }
}

