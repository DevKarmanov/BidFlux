package van.karm.auction.domain.exception;

public class InvalidArgumentException extends RuntimeException {

    public InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
