package van.karm.auth.presentation.controller.jwt;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public interface JwksController {

    @GetMapping("/.well-known/jwks.json")
    ResponseEntity<Resource> getJwks();
}
