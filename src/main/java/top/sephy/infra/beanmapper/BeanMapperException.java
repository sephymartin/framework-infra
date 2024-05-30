package top.sephy.infra.beanmapper;

public class BeanMapperException extends RuntimeException {

    private static final long serialVersionUID = 6950382237265823637L;

    public BeanMapperException() {}

    public BeanMapperException(String message) {
        super(message);
    }

    public BeanMapperException(String message, Throwable cause) {
        super(message, cause);
    }
}
