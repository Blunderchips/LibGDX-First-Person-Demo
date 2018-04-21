/**
 * *****************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *****************************************************************************
 */
package com.badlogic.gdx;

import com.badlogic.gdx.Input.Keys;

import static engine.Core.Game;

/**
 * An adapter class for {@link InputProcessor}. You can derive from this and
 * only override what you are interested in.
 *
 * <p>
 * TODO: why overridden?
 * </p>
 *
 * @author mzechner
 */
public class InputAdapter implements InputProcessor {

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.F1:
                Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
                break;
            case Keys.F2:
                Game.stepDebugMode();
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return true;
    }
}
