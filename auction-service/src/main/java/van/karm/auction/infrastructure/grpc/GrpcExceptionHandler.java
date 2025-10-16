package van.karm.auction.infrastructure.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.persistence.EntityNotFoundException;
import van.karm.auction.domain.exception.InvalidArgumentException;
import van.karm.auction.domain.exception.ServiceUnavailableException;
import van.karm.auction.presentation.exception.UnauthenticatedException;

public class GrpcExceptionHandler {

    public static void handleException(final StatusRuntimeException e) {
        Status.Code code = Status.fromThrowable(e).getCode();
        switch (code) {
            case UNKNOWN -> throw new RuntimeException(e.getMessage(),e);
            case NOT_FOUND -> throw new EntityNotFoundException(e.getMessage(),e);
            case UNAUTHENTICATED -> throw new UnauthenticatedException(e.getMessage(),e);
            case INVALID_ARGUMENT -> throw new InvalidArgumentException(e.getMessage(),e);
            case UNAVAILABLE -> throw new ServiceUnavailableException(e.getMessage(),e);
            default -> throw new RuntimeException("gRPC error: " + e.getMessage(), e);
        }
    }
}
