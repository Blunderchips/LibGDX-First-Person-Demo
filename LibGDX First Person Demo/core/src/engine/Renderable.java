package engine;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Environment;

public interface Renderable {

    public void render(ModelBatch batch, Environment lights);
}
