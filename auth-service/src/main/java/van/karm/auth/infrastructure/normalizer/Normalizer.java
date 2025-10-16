package van.karm.auth.infrastructure.normalizer;

import java.util.Set;

public interface Normalizer {
    Set<String> normalize(Set<String> roles);
}
