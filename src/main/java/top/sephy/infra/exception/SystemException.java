package top.sephy.infra.exception;

/**
 * @author sephy
 * @date 2020-06-27 18:14
 */
public class SystemException extends RuntimeException {

    public SystemException() {}

    public SystemException(String message) {
        super(message);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    private static final long serialVersionUID = -4634153819023050939L;
}
