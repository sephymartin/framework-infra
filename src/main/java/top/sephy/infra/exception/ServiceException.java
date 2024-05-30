package top.sephy.infra.exception;

/**
 * @author sephy
 * @date 2020-06-14 00:52
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 3007273321183731025L;

    public ServiceException() {}

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
