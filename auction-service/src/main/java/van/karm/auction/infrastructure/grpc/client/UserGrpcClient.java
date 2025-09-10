package van.karm.auction.infrastructure.grpc.client;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import van.karm.auction.infrastructure.grpc.GrpcExceptionHandler;
import van.karm.user.UserId;
import van.karm.user.UserServiceGrpc;

import java.util.UUID;

@Component
public class UserGrpcClient implements UserClient {
    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

    @Override
    public String getUsername(UUID userId) {
        try{
            UserId userIdRequest = UserId.newBuilder().setUserId(userId.toString()).build();
            return userServiceBlockingStub.getUsernameByUserId(userIdRequest).getUsername();
        }catch (StatusRuntimeException e){
            GrpcExceptionHandler.handleException(e);
            return null;
        }
    }
}
