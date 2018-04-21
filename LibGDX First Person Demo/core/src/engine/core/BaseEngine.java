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
package engine.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.linearmath.LinearMath;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw.DebugDrawModes;
import com.badlogic.gdx.utils.Disposable;
import engine.physics.BulletWorld;
import engine.physics.EntityBlueprint;
import static engine.Globals.Dispatcher.UPDATE;
import java.util.HashSet;

/**
 * @author xoppa
 */
public abstract class BaseEngine extends ApplicationAdapter implements Disposable {

    public static boolean shadows = true;

    /**
     * True if Bullet it is initialized.
     */
    private static boolean INITIALIZED = false;

    /**
     * Need to initialize bullet before using it.
     */
    public static void initBullet() {
        if (!INITIALIZED) {
            Bullet.init();
            BaseEngine.INITIALIZED = true;
            Gdx.app.log("Bullet", "Version = " + LinearMath.btGetVersion());
        }
    }

    public Camera camera;

    public Environment environment;
    public DirectionalLight light;
    public ModelBatch shadowBatch;

    private BulletWorld world;
    public ObjLoader objLoader;
    public ModelBuilder modelBuilder;
    public ModelBatch modelBatch;

    private final HashSet<Disposable> disposables;

    private int debugMode = DebugDrawModes.DBG_NoDebug;

    public BaseEngine() {
        this.modelBuilder = new ModelBuilder();
        this.objLoader = new ObjLoader();
        this.disposables = new HashSet<Disposable>();
    }

    @Override
    public void create() {
        BaseEngine.initBullet();

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.input.setCursorCatched(true);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.f));
        light = shadows ? new DirectionalShadowLight(1024, 1024, 20f, 20f, 1f, 300f) : new DirectionalLight();
        light.set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f);
        environment.add(light);
        if (shadows) {
            environment.shadowMap = (DirectionalShadowLight) light;
        }
        shadowBatch = new ModelBatch(new DepthShaderProvider());

        modelBatch = new ModelBatch();

        world = new BulletWorld();

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera.position.set(0, 10f, 0);
        camera.update();

        // Create some simple models
        final Model boxModel = modelBuilder.createBox(1f, 1f, 1f, new Material(ColorAttribute.createDiffuse(Color.WHITE),
                ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)), Usage.Position | Usage.Normal);

        final Model ground = modelBuilder.createBox(20f, 1f, 20f, new Material(ColorAttribute.createDiffuse(Color.WHITE),
                ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(16f)), Usage.Position | Usage.Normal);

        // Add the constructors
        world.addConstructor("ground", new EntityBlueprint(ground, 0f)); // mass = 0: static body
        world.addConstructor("box", new EntityBlueprint(boxModel, 1f)); // mass = 1kg: dynamic body
        world.addConstructor("staticbox", new EntityBlueprint((Model) boxModel, 0f)); // mass = 0: static body
    }

    public BaseEngine addDisposable(Disposable disposable) {
        this.disposables.add(disposable);
        return this;
    }

    @Override
    public void dispose() {
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
        this.disposables.clear();

        modelBatch.dispose();
        modelBatch = null;

        shadowBatch.dispose();
        shadowBatch = null;

        if (shadows) {
            ((DirectionalShadowLight) light).dispose();
        }
        light = null;

        world.dispose();
        world = null;

        MessageManager.getInstance().clear();

        super.dispose();
    }

    /**
     * Callback function used to update the state of the game every frame.
     *
     * @param dt Delta Time is the time it takes for the computer to go through
     * all the processing/rendering for a single frame. It is dynamically
     * updated, so it can fluctuate depending on what level of processing the
     * last frame required.
     */
    public void update(float dt) {
        MessageManager.getInstance().dispatchMessage(UPDATE);
        this.world.update(dt);
    }

    /**
     * Callback function used to render on the screen every frame.
     */
    @Override
    public void render() {
        //<editor-fold defaultstate="uncollapsed" desc="update">
        {
            this.update(Gdx.graphics.getDeltaTime());
        }
        //</editor-fold>

        //<editor-fold defaultstate="uncollapsed" desc="render">
        {
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

            Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            this.camera.update(true);

            if (shadows) {
                ((DirectionalShadowLight) light).begin(Vector3.Zero, camera.direction);
                this.shadowBatch.begin(((DirectionalShadowLight) light).getCamera());
                this.world.render(shadowBatch, null);
                this.shadowBatch.end();
                ((DirectionalShadowLight) light).end();
            }

            this.modelBatch.begin(camera);
            this.world.render(modelBatch, environment);
            this.modelBatch.end();

            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
            if (debugMode != DebugDrawModes.DBG_NoDebug) {
                this.world.setDebugMode(debugMode);
            }
        }
        //</editor-fold>
    }

    public void setDebugMode(final int mode) {
        this.world.setDebugMode(debugMode = mode);
    }

    public void stepDebugMode() {
        if (world.getDebugMode() == DebugDrawModes.DBG_NoDebug) {
            this.setDebugMode(DebugDrawModes.DBG_DrawWireframe
                    | DebugDrawModes.DBG_DrawFeaturesText
                    | DebugDrawModes.DBG_DrawText
                    | DebugDrawModes.DBG_DrawContactPoints
                    | DebugDrawModes.DBG_DrawAabb);
        } else if (world.renderMeshes) {
            this.world.renderMeshes = false;
        } else {
            this.world.renderMeshes = true;
            this.setDebugMode(DebugDrawModes.DBG_NoDebug);
        }
    }

    public BulletWorld getWorld() {
        return this.world;
    }
}
