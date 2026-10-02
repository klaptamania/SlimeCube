package io.github.clapt.ffProoj;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    public SpriteBatch batch;
    public AssetManager am;
    public int type = 0;
    public SaveLoad saveLoad = new SaveLoad();
    public GameScreen gameScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        am = new AssetManager();
        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose(){
        am.dispose();
    }
}
