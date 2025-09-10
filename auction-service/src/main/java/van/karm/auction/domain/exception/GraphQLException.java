package van.karm.auction.domain.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class GraphQLException extends RuntimeException {
    private final String code;
    private final Map<String, Object> details;

    public GraphQLException(String message, String code, Map<String, Object> details) {
        super(message);
        this.code = code;
        this.details = details;
    }
}