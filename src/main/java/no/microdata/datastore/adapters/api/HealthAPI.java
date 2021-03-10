package no.microdata.datastore.adapters.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/health", produces = {"application/json;charset=UTF-8"})
class HealthAPI {

    @GetMapping(value = "/alive")
    ResponseEntity<String> alive() {
        return new ResponseEntity<>("I'm alive!", HttpStatus.OK);
    }

    @GetMapping(value = "/ready")
    ResponseEntity<String> ready() {
        return new ResponseEntity<>("I'm ready!", HttpStatus.OK);
    }
}