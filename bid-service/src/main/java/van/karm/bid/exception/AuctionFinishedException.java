package van.karm.bid.exception;

public class AuctionFinishedException extends RuntimeException {
    public AuctionFinishedException(String message) {
        super(message);
    }
}
