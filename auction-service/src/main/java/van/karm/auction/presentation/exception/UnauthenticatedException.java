package van.karm.auction.presentation.exception;

public class UnauthenticatedException extends RuntimeException {

    public UnauthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
