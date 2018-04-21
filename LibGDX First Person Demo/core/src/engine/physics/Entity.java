/**
 * *****************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * ****************************************************************************
 */
package engine.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Disposable;

import static engine.Globals.TMP_M;
import engine.Renderable;

/**
 * Renderable BaseEntity with a bullet physics body.
 *
 * @author xoppa
 */
public class Entity implements Disposable, Renderable {

    public Entity.MotionState motionState;
    public btRigidBody body;

    public Matrix4 transform;
    public ModelInstance modelInstance;
    private final Color colour;

    public final BoundingBox boundingBox;
    public final float boundingBoxRadius;

    public Entity(final Model model, final btRigidBodyConstructionInfo bodyInfo,
            final float xPos, final float yPos, final float zPos) {
        this(model, new btRigidBody(bodyInfo), TMP_M.setToTranslation(xPos, yPos, zPos));
    }

    public Entity(final Model model, final btRigidBodyConstructionInfo bodyInfo,
            final Matrix4 transform) {
        this(model, new btRigidBody(bodyInfo), transform);
    }

    public Entity(final Model model, final btRigidBody body,
            final float xPos, final float yPos, final float zPos) {
        this(model, body, TMP_M.setToTranslation(xPos, yPos, zPos));
    }

    public Entity(final Model model, final btRigidBody body, final Matrix4 transform) {
        this(new ModelInstance(model, transform.cpy()), body);
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public Entity(final ModelInstance modelInstance, final btRigidBody body) {
        this.modelInstance = modelInstance;
        this.transform = this.modelInstance.transform;
        this.body = body;

        this.boundingBox = new BoundingBox();
        modelInstance.calculateBoundingBox(boundingBox);
        boundingBoxRadius = boundingBox.getDimensions(new Vector3(0, 0, 0)).len() * 0.5f;

        this.body.userData = this; // LeakingThisInConstructor
        this.motionState = new MotionState(this.modelInstance.transform);
        this.body.setMotionState(motionState);

        this.colour = new Color(1f, 1f, 1f, 1f);
    }

    @Override
    public void dispose() {
        // Don't rely on the GC
        if (motionState != null) {
            motionState.dispose();
        }
        if (body != null) {
            body.dispose();
        }
        // And remove the reference
        motionState = null;
        body = null;
    }

    @Override
    public void render(ModelBatch batch, Environment lights) {
        batch.render(getModelInstance(), lights);
    }

    public ModelInstance getModelInstance() {
        return this.modelInstance;
    }

    public btRigidBody getRigidBody() {
        return this.body;
    }

    public static class MotionState extends btMotionState {

        private final Matrix4 transform;

        public MotionState(final Matrix4 transform) {
            this.transform = transform;
        }

        /**
         * For dynamic and static bodies this method is called by bullet once to
         * get the initial state of the body. For kinematic bodies this method
         * is called on every update, unless the body is deactivated.
         *
         * @param worldTrans
         */
        @Override
        public void getWorldTransform(final Matrix4 worldTrans) {
            worldTrans.set(transform);
        }

        /**
         * For dynamic bodies this method is called by bullet every update to
         * inform about the new position and rotation.
         *
         * @param worldTrans
         */
        @Override
        public void setWorldTransform(final Matrix4 worldTrans) {
            this.transform.set(worldTrans);
        }
    }

    public Entity.MotionState getMotionState() {
        return this.motionState;
    }

    public btRigidBody getBody() {
        return this.body;
    }

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public float getBoundingBoxRadius() {
        return this.boundingBoxRadius;
    }

    public void setBody(btRigidBody body) {
        this.body = body;
        this.body.userData = this;
        this.motionState = new MotionState(this.modelInstance.transform);
        this.body.setMotionState(motionState);
    }

    public Color getColour() {
        return this.colour;
    }

    public void setColour(Color colour) {
        setColour(colour.r, colour.g, colour.b, colour.a);
    }

    public void setColour(float r, float g, float b, float a) {
        this.colour.set(r, g, b, a);
        if (modelInstance != null) {
            for (Material m : modelInstance.materials) {
                ColorAttribute ca = (ColorAttribute) m.get(ColorAttribute.Diffuse);
                if (ca != null) {
                    ca.color.set(r, g, b, a);
                }
            }
        }
    }
}
