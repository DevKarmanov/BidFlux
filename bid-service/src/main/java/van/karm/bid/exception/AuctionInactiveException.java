package van.karm.bid.exception;

public class AuctionInactiveException extends RuntimeException {
    public AuctionInactiveException(String message) {
        super(message);
    }
}
