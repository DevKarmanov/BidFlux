package van.karm.auth.presentation.controller.jwt;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwksControllerImpl implements JwksController {

    @Override
    public ResponseEntity<Resource> getJwks() {
        ClassPathResource resource = new ClassPathResource("certs/jwt/jwks.json");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resource);
    }
}