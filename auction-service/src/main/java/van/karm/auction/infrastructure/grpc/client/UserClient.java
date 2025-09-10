package van.karm.auction.infrastructure.grpc.client;

import java.util.UUID;

public interface UserClient {
    String getUsername(UUID userId);
}
