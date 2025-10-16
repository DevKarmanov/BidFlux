package van.karm.auction.infrastructure.grpc.client.user;

import java.util.UUID;

public interface UserClient {
    String getUsername(UUID userId);
}
