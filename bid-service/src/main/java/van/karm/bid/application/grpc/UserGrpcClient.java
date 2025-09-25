package van.karm.bid.application.grpc;

public interface UserGrpcClient {
    String getUsername(String userId);
}
