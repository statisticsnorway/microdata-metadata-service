package no.microdata.datastore.repository;

import com.google.gson.Gson;
import no.microdata.datastore.model.EventQuery;
import no.microdata.datastore.model.FixedQuery;
import no.microdata.datastore.model.StatusQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Repository
public class DataServiceClientRepository implements DataServiceRepository {

    private final static Logger log = LoggerFactory.getLogger(DataServiceClientRepository.class);

    @Value("${dataservice.url.status}")
    private String statusURL;

    @Value("${dataservice.url.event}")
    private String eventURL;

    @Value("${dataservice.url.fixed}")
    private String fixedURL;

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public Map<String, String> findByTime(StatusQuery query) {
        throw new RuntimeException("Not implemented!");
    }

    @Override
    public Map<String, String> findByFixed(FixedQuery query) {
        throw new RuntimeException("Not implemented!");
    }

    @Override
    public Map<String, String> findByTimePeriod(EventQuery query) {
        String json = new Gson().toJson(
                Map.of(
                        "version", query.getDatasetRevision().getVersion(),
                        "dataStructureName", query.getDatasetRevision().getDatasetName(),
                        "startDate", query.getStartDate(),
                        "stopDate", query.getEndDate()
                )
        );
        HttpResponse<String> response = getHttpResponse(json, eventURL);

        System.out.println(response.body());

        return new Gson().fromJson(response.body(), Map.class);
    }

    private HttpResponse<String> getHttpResponse(String json, String url) {

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(url))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = null;
        try {
            log.info("Trying to send request {} to {}", json, url);
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.error("httpClient throwed IOException: {}", e.getMessage());
            throw new RuntimeException("httpClient throwed IOException: " + e.getMessage());
        } catch (InterruptedException e) {
            log.error("httpClient throwed InterruptedException: {}", e.getMessage());
            throw new RuntimeException("httpClient throwed InterruptedException: " + e.getMessage());
        }
        if (response.statusCode() != 200) {
            log.error("DataService returned HTTP status code: {}", response.statusCode());
            throw new RuntimeException("DataService returned HTTP status code " + response.statusCode());
        }
        return response;
    }
}