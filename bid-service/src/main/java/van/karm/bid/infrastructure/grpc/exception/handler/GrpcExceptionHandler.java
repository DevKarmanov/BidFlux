package van.karm.bid.infrastructure.grpc.exception.handler;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.persistence.EntityNotFoundException;
import van.karm.bid.domain.exception.InvalidArgumentException;
import van.karm.bid.domain.exception.ServiceUnavailableException;
import van.karm.bid.domain.exception.UnauthenticatedException;

public class GrpcExceptionHandler {

    public static void handleException(final StatusRuntimeException e) {
        Status.Code code = Status.fromThrowable(e).getCode();
        switch (code) {
            case UNKNOWN -> throw new RuntimeException(e.getMessage());
            case NOT_FOUND -> throw new EntityNotFoundException(e.getMessage());
            case UNAUTHENTICATED -> throw new UnauthenticatedException(e.getMessage());
            case INVALID_ARGUMENT -> throw new InvalidArgumentException(e.getMessage());
            case UNAVAILABLE -> throw new ServiceUnavailableException(e.getMessage());
            default -> throw new RuntimeException("gRPC error: " + e.getMessage(), e);
        }
    }
}
