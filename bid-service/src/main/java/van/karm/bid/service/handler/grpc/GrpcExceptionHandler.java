package van.karm.bid.service.handler.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.persistence.EntityNotFoundException;
import van.karm.bid.exception.InvalidArgumentException;
import van.karm.bid.exception.ServiceUnavailableException;

public class GrpcExceptionHandler {

    public static void handleException(final StatusRuntimeException e) {
        Status.Code code = Status.fromThrowable(e).getCode();
        switch (code) {
            case NOT_FOUND -> throw new EntityNotFoundException(e.getMessage());
            case INVALID_ARGUMENT -> throw new InvalidArgumentException(e.getMessage());
            case UNAVAILABLE -> throw new ServiceUnavailableException(e.getMessage());
            default -> throw new RuntimeException("gRPC error: " + e.getMessage(), e);
        }
    }
}
