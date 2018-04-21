package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntIntMap;
import static engine.Globals.TMP_VEC;
import static engine.Globals.TMP_VEC_B;
import engine.physics.Entity;
import static engine.Globals.Dispatcher.UPDATE;

/**
 * @author root
 */
public class CharacterController extends Entity implements Disposable, Telegraph {

    private float degreesPerPixel = 0.5f;
    private final IntIntMap keys = new IntIntMap();

    private Camera camera;

    @SuppressWarnings("LeakingThisInConstructor")
    public CharacterController(final Camera camera_, Model model, btRigidBody body,
            float xPos, float yPos, float zPos) {
        super(model, body, xPos, yPos, zPos);
        this.camera = camera_;

        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                keys.put(keycode, keycode);
                return super.keyDown(keycode);
            }

            @Override
            public boolean keyUp(int keycode) {
                keys.remove(keycode, 0);
                return super.keyUp(keycode);
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                final float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
                final float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
                camera.direction.rotate(camera.up, deltaX);
                TMP_VEC.set(camera.direction).crs(camera.up).nor();
                camera.direction.rotate(TMP_VEC, deltaY);
                TMP_VEC.setZero();
                return super.mouseMoved(screenX, screenY);
            }
        });

        MessageManager.getInstance().addListener(this, UPDATE); // LeakingThisInConstructor
    }

    @Override
    public void dispose() {
        MessageManager.getInstance().removeListener(this, UPDATE);
        super.dispose();
    }

    @Override
    public boolean handleMessage(Telegram tlgrm) {
        switch (tlgrm.message) {
            case UPDATE: {
                return update();
            }
        }
        return false;
    }

    private boolean update() {
        int speed = 20 * 2;

        float tmpY = camera.direction.y;
        camera.direction.y = 0;

        if (Gdx.input.isKeyPressed(Keys.W)) {
            TMP_VEC.add(TMP_VEC_B.set(camera.direction).nor().scl(speed));
        }
        if (Gdx.input.isKeyPressed(Keys.S)) {
            TMP_VEC.add(TMP_VEC_B.set(camera.direction).nor().scl(-speed));
        }
        if (Gdx.input.isKeyPressed(Keys.A)) {
            TMP_VEC.add(TMP_VEC_B.set(camera.direction).crs(camera.up).nor().scl(-speed));
        }
        if (Gdx.input.isKeyPressed(Keys.D)) {
            TMP_VEC.add(TMP_VEC_B.set(camera.direction).crs(camera.up).nor().scl(speed));
        }
        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            if (body.getLinearVelocity().y < 1
                    && body.getLinearVelocity().y > 0) {
                body.applyCentralImpulse(TMP_VEC.set(0, 12.5f, tmpY));
            }
        }

        body.applyCentralForce(TMP_VEC);

        body.getWorldTransform(transform);
        camera.position.set(transform.getTranslation(TMP_VEC_B));

        camera.direction.y = tmpY;
        TMP_VEC.setZero();

        return true;
    }
}
