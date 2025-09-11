package van.karm.auth.infrastructure.service.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import van.karm.auth.domain.repo.UserRepo;
import van.karm.user.UserId;
import van.karm.user.UserServiceGrpc;
import van.karm.user.Username;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@GrpcService
public class UserGrpcServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private final UserRepo userRepo;

    @Override
    public void getUsernameByUserId(UserId request, StreamObserver<Username> responseObserver) {
        UUID userId = UUID.fromString(request.getUserId());

        Optional<String> usernameOpt = userRepo.findUsernameById(userId);
        if (usernameOpt.isPresent()) {
            var response = Username.newBuilder().setUsername(usernameOpt.get()).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }else {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("User with id " + userId + " not found")
                            .asRuntimeException()
            );
        }
    }
}
