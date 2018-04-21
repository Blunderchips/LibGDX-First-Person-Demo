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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import java.util.HashMap;
import java.util.HashSet;

import engine.Renderable;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Disposable;

/**
 * Bullet physics world that holds all bullet entities and constructors.
 *
 * @author xoppa
 */
public class BulletWorld implements Disposable {

    public DebugDrawer debugDrawer = null;
    public boolean renderMeshes = true;

    public final btCollisionConfiguration collisionConfiguration;
    public final btCollisionDispatcher dispatcher;
    public final btBroadphaseInterface broadphase;
    public final btConstraintSolver solver;
    public final btDynamicsWorld dynamicsWorld;

    protected final HashSet<Renderable> entities;
    private final HashMap<String, EntityBlueprint> blueprints;

    public BulletWorld() {
        this.collisionConfiguration = new btDefaultCollisionConfiguration();
        this.dispatcher = new btCollisionDispatcher(collisionConfiguration);
        this.broadphase = new btDbvtBroadphase();
        this.solver = new btSequentialImpulseConstraintSolver();
        this.dynamicsWorld = new btDiscreteDynamicsWorld(
                dispatcher, broadphase, solver, collisionConfiguration
        );

        this.entities = new HashSet<Renderable>();
        this.blueprints = new HashMap<String, EntityBlueprint>();
    }

    public BulletWorld add(final Object obj) throws IllegalArgumentException {
        if (obj instanceof Entity) {
            Entity e = (Entity) obj;

            this.entities.add(e);
            this.dynamicsWorld.addRigidBody(e.body);

            e.body.setUserValue(entities.size() - 1); // Store the index of the entity in the collision object.
        } else if (obj instanceof Renderable) {
            this.entities.add((Renderable) obj);
        } else {
            throw new IllegalArgumentException();
        }
        return this;
    }

    /**
     * Callback function used to update the state of the world every frame.
     *
     * @param dt Delta Time is the time it takes for the computer to go through
     * all the processing/rendering for a single frame. It is dynamically
     * updated, so it can fluctuate depending on what level of processing the
     * last frame required.
     */
    public void update(float dt) {
        this.dynamicsWorld.stepSimulation(dt);
    }

    public void render(ModelBatch batch, Environment lights, HashSet<Renderable> entities) {
        if (renderMeshes) {
            for (Renderable obj : entities) {
                obj.render(batch, lights);
            }
        }
        if (debugDrawer != null && debugDrawer.getDebugMode() > 0) {
            batch.flush();
            this.debugDrawer.begin(batch.getCamera());
            this.dynamicsWorld.debugDrawWorld();
            this.debugDrawer.end();
        }
    }

    @Override
    public void dispose() {
        for (Object obj : entities) {
            if (obj instanceof Entity) {
                Entity e = (Entity) obj;
                this.dynamicsWorld.removeRigidBody(e.getRigidBody());  // 1
                e.dispose();
            } else if (obj instanceof Disposable) {
                ((Disposable) obj).dispose();
            }
        }
        this.entities.clear();

        for (EntityBlueprint constructor : blueprints.values()) {
            constructor.dispose();
        }
        this.blueprints.clear();

        this.dynamicsWorld.dispose();
        this.solver.dispose();
        this.broadphase.dispose();
        this.dispatcher.dispose();
        this.collisionConfiguration.dispose();
    }

    public void setDebugMode(final int mode) {
        if (mode == btIDebugDraw.DebugDrawModes.DBG_NoDebug && debugDrawer == null) {
            return;
        }

        if (debugDrawer == null) {
            dynamicsWorld.setDebugDrawer(debugDrawer = new DebugDrawer());
        }
        this.debugDrawer.setDebugMode(mode);
    }

    public int getDebugMode() {
        return (debugDrawer == null) ? 0 : debugDrawer.getDebugMode();
    }

    public void addConstructor(final String name, final EntityBlueprint constructor) {
        this.blueprints.put(name, constructor);
    }

    public EntityBlueprint getConstructor(final String name) {
        return this.blueprints.get(name);
    }

    public Entity add(final String type, float xPos, float yPos, float zPos) {
        final Entity entity = blueprints.get(type).construct(xPos, yPos, zPos);
        add(entity);
        return entity;
    }

    public Entity add(final String type, final Matrix4 transform) {
        final Entity entity = blueprints.get(type).construct(transform);
        this.add(entity);
        return entity;
    }

    public void render(final ModelBatch batch, final Environment lights) {
        render(batch, lights, entities);
    }

    public void render(final ModelBatch batch, final Environment lights,
            final Entity entity) {
        batch.render(entity.getModelInstance(), lights);
    }
}
