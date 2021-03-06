package yachosan.api;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Optional;

public class ResponseEntites {

    public static <T> ResponseEntity<T> ok(T body) {
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    public static <T> ResponseEntity<T> created(T body, URI location) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        return new ResponseEntity<>(body, headers, HttpStatus.CREATED);
    }


    public static <T> ResponseEntity<T> okIfPresent(Optional<T> body) {
        return body.map(ResponseEntites::ok)
                .orElse(notFound());
    }

    public static <T> ResponseEntity<T> notFound() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<Void> noContent() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public static ResponseEntity<Void> noContentIfPresent(Optional<?> body) {
        return body.map(x -> ResponseEntites.noContent())
                .orElse(notFound());
    }
}
