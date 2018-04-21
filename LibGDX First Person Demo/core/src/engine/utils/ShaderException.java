package engine.utils;

/**
 * Thrown to indicate failure to find, load in or compile and run a GLSL Shader
 * program. created: 05/06/02015
 *
 * @author Matthew 'siD' Van der Bijl
 * @see engine.utils.ResourceException
 */
public final class ShaderException extends ResourceException {

    /**
     * Constructs an instance of <code>ShaderException</code> with the specified
     * detail message (msg).
     *
     * @param msg the detail message.
     */
    public ShaderException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>ShaderException</code> with the specified
     * cause.
     *
     * @param cause the cause of the exception
     */
    public ShaderException(Throwable cause) {
        super(cause);
    }
}
