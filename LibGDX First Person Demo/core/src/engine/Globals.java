package engine;

import com.badlogic.gdx.math.Matrix4;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.RandomXS128;


/**
 * Contains the definitions for the game engine. Globals is a collection list
 * the collection of global variables and constants that the game engine may
 * require. As consequence you don't have to touch that much code this time.
 * <b>You can not instantiate this class.</b>
 *
 * @author root
 */
public final class Globals implements Core {

    /**
     * You can not instantiate this class.
     */
    @Deprecated
    private Globals() {
    }

    //<editor-fold defaultstate="uncollapsed" desc="Constants">
    /**
     *
     */
    public static final RandomXS128 RAND = new RandomXS128(new SecureRandom().nextLong());

    static {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final SecureRandom secureRandom = SecureRandom.getInstanceStrong();

                    final long seed0 = secureRandom.nextLong();
                    final long seed1 = secureRandom.nextLong();

                    RAND.setState(seed0, seed1);
                } catch (NoSuchAlgorithmException ignore) {
                    // nsae.printStackTrace(System.err);
                }
            }
        }).start();
    }

    /**
     * Temporary vectors to be used by the engine. Pass them around like bong
     * (HIMYM).
     */
    public static final Vector3 TMP_VEC = new Vector3(0, 0, 0),
            TMP_VEC_B = new Vector3(0, 0, 0),
            TMP_VEC_C = new Vector3(0, 0, 0);

    /**
     *
     */
    public final static Matrix4 TMP_M = new Matrix4();
    //</editor-fold>

    /**
     * Message dispatcher stuff.
     */
    public interface Dispatcher {

        public static final int UPDATE = 0x0;
    }
}
