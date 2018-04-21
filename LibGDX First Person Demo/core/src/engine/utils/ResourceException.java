package engine.utils;

/**
 * Thrown to indicate failure to find or load a resource.
 *
 * @author Matthew 'siD' Van der Bijl
 */
public class ResourceException extends RuntimeException {

    /**
     * Constructs an instance of <code>ResourceException</code> with the
     * specified detail message (msg).
     *
     * @param msg the detail message.
     */
    public ResourceException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>ResourceException</code> with the
     * specified cause.
     *
     * @param cause the cause of the exception
     */
    public ResourceException(Throwable cause) {
        super(cause);
    }

    @Override
    public final String toString() {
        return String.format("%s: %s", getClass().getSimpleName(), getMessage());
    }
}
