package manager_system.exception;

public class UserEndpointsException extends RuntimeException {
    public UserEndpointsException(String errMsg) {
        super(errMsg);
    }
}
