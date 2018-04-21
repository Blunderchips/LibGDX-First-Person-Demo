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

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.utils.Disposable;

import static engine.Globals.TMP_VEC;

/**
 * Holds the information necessary to create a bullet btRigidBody. This class
 * should outlive the btRigidBody (entity) itself.
 *
 * @author xoppa
 */
public class EntityBlueprint implements Disposable {

    public btRigidBodyConstructionInfo bodyInfo = null;
    public btCollisionShape shape = null;
    public Model model;

    /**
     * Specify null for the shape to use only the renderable part of this entity
     * and not the physics part.
     *
     * @param model
     * @param mass
     * @param shape
     */
    public EntityBlueprint(final Model model, final float mass, final btCollisionShape shape) {
        this.model = model;
        create(model, mass, shape);
    }

    /**
     * Specify null for the shape to use only the renderable part of this entity
     * and not the physics part.
     *
     * @param model
     * @param shape
     */
    public EntityBlueprint(final Model model, final btCollisionShape shape) {
        this(model, -1f, shape);
    }

    /**
     * Creates a btBoxShape with the specified dimensions.
     *
     * @param model
     * @param mass
     * @param width
     * @param height
     * @param depth
     */
    public EntityBlueprint(final Model model, final float mass, final float width,
            final float height, final float depth) {
        this.model = model;
        create(model, mass, width, height, depth);
    }

    /**
     * Creates a btBoxShape with the specified dimensions and NO rigidbody.
     *
     * @param model
     * @param width
     * @param height
     * @param depth
     */
    public EntityBlueprint(final Model model, final float width, final float height, final float depth) {
        this(model, -1f, width, height, depth);
    }

    /**
     * Creates a btBoxShape with the same dimensions as the shape.
     *
     * @param model
     * @param mass
     */
    public EntityBlueprint(final Model model, final float mass) {
        this.model = model;
        final BoundingBox boundingBox = new BoundingBox();
        model.calculateBoundingBox(boundingBox);
        create(model, mass, boundingBox.getWidth(), boundingBox.getHeight(), boundingBox.getDepth());
    }

    /**
     * Creates a btBoxShape with the same dimensions as the shape and NO
     * rigidbody.
     *
     * @param model
     */
    public EntityBlueprint(final Model model) {
        this(model, -1f);
    }

    private void create(final Model model, final float mass, final float width,
            final float height, final float depth) {
        create(model, mass, new btBoxShape(
                TMP_VEC.set(width * 0.5f, height * 0.5f, depth * 0.5f)
        )); // Create a simple boxshape
    }

    private void create(final Model model, final float mass, final btCollisionShape shape) {
        this.shape = shape;

        if (shape != null && mass >= 0) {
            // Calculate the local inertia, bodies with no mass are static
            Vector3 localInertia;
            if (mass == 0) {
                localInertia = Vector3.Zero;
            } else {
                shape.calculateLocalInertia(mass, TMP_VEC);
                localInertia = TMP_VEC;
            }

            // For now just pass null as the motionstate, we'll add that to the body in the entity itself
            this.bodyInfo = new btRigidBodyConstructionInfo(mass, null, shape, localInertia);
        }
    }

    @Override
    public void dispose() {
        // Don't rely on the GC
        if (bodyInfo != null) {
            this.bodyInfo.dispose();
        }
        if (shape != null) {
            this.shape.dispose();
        }
        // Remove references so the GC can do it's work
        this.bodyInfo = null;
        this.shape = null;
        try {
            this.model.dispose();
        } catch (IllegalArgumentException iae) {
            if (!iae.getMessage().equals("buffer not allocated with newUnsafeByteBuffer or already disposed")) {
                throw iae;
            }
        }
    }

    public Entity construct(float xPos, float yPos, float zPos) {
        return new Entity(model, bodyInfo, xPos, yPos, zPos);
    }

    public Entity construct(final Matrix4 transform) {
        return new Entity(model, bodyInfo, transform);
    }
}
