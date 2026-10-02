package io.github.clapt.ffProoj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class StopScreen implements Screen {
    Stage stopStage;
    private final Main main;
    Texture screenTexture;

    public StopScreen(Main main){
        this.main = main;
        stopStage = new Stage(new ExtendViewport(1920, 1080), main.batch);
        screenTexture = main.am.getTexture("StopScreen");
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            main.setScreen(main.gameScreen);
        }

        main.batch.begin();

        main.batch.draw(screenTexture, 0,0, stopStage.getWidth(), stopStage.getHeight());

        main.batch.end();

        stopStage.act();
        stopStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stopStage.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
