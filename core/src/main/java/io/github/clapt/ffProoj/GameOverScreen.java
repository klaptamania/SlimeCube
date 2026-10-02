package io.github.clapt.ffProoj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class GameOverScreen implements Screen {

    private final Main main;
    Texture screen;
    Stage stage;
    Table table;
    Button restart;
    Button toMenu;

    public GameOverScreen(Main main){
        this.main = main;
        screen = main.am.getTexture("GameOverScreen");

        stage = new Stage(new ExtendViewport(1280, 720), main.batch);
        table = new Table();
        table.setFillParent(true);
        Gdx.input.setInputProcessor(stage);

        restart = new Button(new TextureRegionDrawable(main.am.getTexture("RestartButton")));
        restart.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent even, float x, float y){
                main.gameScreen = new GameScreen(main);
                main.setScreen(main.gameScreen);
            }
        });

        toMenu = new Button(new TextureRegionDrawable(main.am.getTexture("MenuButton")));
        toMenu.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                main.type = 0;
                main.setScreen(new MenuScreen(main));
            }
        });

        table.center().bottom();
        table.add(restart).width(64 * 3).height( 32 * 3);
        table.row();
        table.add(toMenu).width(64 * 3).height( 32 * 3).padTop(25);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        main.batch.begin();

        main.batch.draw(screen, 0, 0, stage.getWidth(), stage.getHeight());

        main.batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
    }
}
