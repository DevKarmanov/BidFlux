package van.karm.auth.controller.auth;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwksController {

    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Resource> getJwks() {
        ClassPathResource resource = new ClassPathResource("certs/jwt/jwks.json");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resource);
    }
}