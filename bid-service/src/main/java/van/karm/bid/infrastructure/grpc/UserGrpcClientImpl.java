package van.karm.bid.infrastructure.grpc;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import van.karm.bid.application.grpc.UserGrpcClient;
import van.karm.bid.infrastructure.grpc.exception.handler.GrpcExceptionHandler;
import van.karm.user.UserId;
import van.karm.user.UserServiceGrpc;

@Component
public class UserGrpcClientImpl implements UserGrpcClient {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

    @Override
    public String getUsername(String userId) {
        var request = UserId.newBuilder().setUserId(userId).build();

        try {
            var response = userBlockingStub.getUsernameByUserId(request);
            return response.getUsername();
        } catch (StatusRuntimeException e) {
            GrpcExceptionHandler.handleException(e);
            return null;
        }
    }
}
