package engine;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import engine.core.BaseEngine;
import engine.physics.Entity;
import java.util.List;

public class TestGame extends BaseEngine {

    Entity ground;
    CharacterController player;

    @Override
    public void create() {
        super.create();

        btCollisionShape shape = new btBoxShape(new Vector3(1.5f, 2, 1.5f));

        final Vector3 inertia = new Vector3(0, 0, 0);
        shape.calculateLocalInertia(1.0f, inertia);
//
        btDefaultMotionState motionState = new btDefaultMotionState();
        btRigidBody body = new btRigidBody(2, motionState, shape, inertia);
        body.setDamping(0.8f, 0.8f);
        body.setAngularFactor(new Vector3(0, 0, 0)); // prevent the player from falling over

        getWorld().add(player = new CharacterController(camera, getWorld().getConstructor("box").model, body, 5, 5, 5));

        // Add the ground
        (ground = getWorld().add("ground", 0f, 0f, 0f))
                .setColour(
                        0.25f + 0.5f * (float) Math.random(),
                        0.25f + 0.5f * (float) Math.random(),
                        0.25f + 0.5f * (float) Math.random(),
                        1f
                );

        (ground = getWorld().add("ground", 25f, 0f, 0f))
                .setColour(
                        0.25f + 0.5f * (float) Math.random(),
                        0.25f + 0.5f * (float) Math.random(),
                        0.25f + 0.5f * (float) Math.random(),
                        1f
                );

        // Create some boxes to play with
        final int BOXCOUNT_X = 5;
        final int BOXCOUNT_Y = 5;
        final int BOXCOUNT_Z = 1;

        final float BOXOFFSET_X = -2.5f;
        final float BOXOFFSET_Y = 0.5f;
        final float BOXOFFSET_Z = 0f;

        for (int x = 0; x < BOXCOUNT_X; x++) {
            for (int y = 0; y < BOXCOUNT_Y; y++) {
                for (int z = 0; z < BOXCOUNT_Z; z++) {
                    getWorld().add("box", BOXOFFSET_X + x, BOXOFFSET_Y + y, BOXOFFSET_Z + z).setColour(
                            0.5f + 0.5f * (float) Math.random(),
                            0.5f + 0.5f * (float) Math.random(),
                            0.5f + 0.5f * (float) Math.random(),
                            1f
                    );
                }
            }
        }

//        String data = readFileAsString("data/newfile.json");
//
//        Wrapper obj = new Gson().fromJson(data, Wrapper.class);
//        System.out.println(obj.getLessons());
    }

    @Override
    public void dispose() {
        super.dispose();
        this.ground = null;
    }

    class Wrapper {

        List<Lesson> lessons;

        //Getters & Setters
        public List<Lesson> getLessons() {
            return lessons;
        }
    }

    class Lesson {

        private String id;
        private String discipline;
        private String type;
        private String comment;

        @Override
        public String toString() {
            return "Lesson{" + "id=" + id
                    + ", discipline=" + discipline
                    + ", type=" + type
                    + ", comment=" + comment + '}';
        }
    }
}
