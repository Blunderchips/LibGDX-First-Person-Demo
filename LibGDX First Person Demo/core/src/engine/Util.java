package engine;

import java.io.FileNotFoundException;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import engine.utils.ResourceException;
import engine.utils.ShaderException;

/**
 * Utilities to support the Engine. So basically anything that didn't fit
 * anywhere else. <b>You can not instantiate this class.</b>
 *
 * @author Matthew 'siD' Van der Bijl
 */
public final class Util {

    /**
     * You can not instantiate this class.
     */
    @Deprecated
    private Util() {
    }

    /**
     * @param path the internal path to the file
     * @return the file as a <code>String</code>
     * @throws ResourceException if the file is not found
     */
    public static String readFileAsString(String path) throws ResourceException {
        try {
            final Scanner sc = new Scanner(Gdx.files.internal(path).file());
            final StringBuilder shader = new StringBuilder();

            while (sc.hasNextLine()) {
                shader.append(sc.nextLine()).append("\n");
            }
            sc.close();
            return shader.toString();
        } catch (FileNotFoundException fnfe) {
            throw new ResourceException(fnfe);
        }
    }

    /**
     * Compiles and links shader.
     *
     * @param vertexShader path to vertex shader
     * @param fragmentShader path to fragment shader
     * @param classpath true if shader in classpath false if shader in assets
     * folder
     *
     * @return compiled shader program
     */
    public static ShaderProgram compile(String vertexShader, String fragmentShader, boolean classpath) {
        String vert;
        String frag;
        if (classpath) {
            vert = Gdx.files.classpath(vertexShader).readString();
            frag = Gdx.files.classpath(fragmentShader).readString();
        } else {
            vert = Gdx.files.internal(vertexShader).readString();
            frag = Gdx.files.internal(fragmentShader).readString();
        }

        ShaderProgram program = new ShaderProgram(vert, frag);
        if (!program.isCompiled()) {
            throw new ShaderException(program.getLog());
        }

        return program;
    }

}
