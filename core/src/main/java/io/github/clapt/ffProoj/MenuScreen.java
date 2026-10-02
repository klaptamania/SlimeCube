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

public class MenuScreen implements Screen {
    private final Main main;
    Stage menuStage;
    Texture screen;
    Table table;
    Button startGame, pathOfGlory, load;

    public MenuScreen(Main main){
        this.main = main;
        screen = main.am.getTexture("MenuScreen");
        menuStage = new Stage(new ExtendViewport(1920, 1080), main.batch);
        Gdx.input.setInputProcessor(menuStage);
        table = new Table();
        table.setFillParent(true);

        startGame = new Button(new TextureRegionDrawable(main.am.getTexture("StartButtonUp")),new TextureRegionDrawable(main.am.getTexture("StartButtonDown")));
        startGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                main.type = 1;
                main.am.playSound("ButtonPressed");
                main.gameScreen = new GameScreen(main);
                main.setScreen(main.gameScreen);
            }
        });

        pathOfGlory = new Button(new TextureRegionDrawable(main.am.getTexture("EndlessButton")));
        pathOfGlory.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                main.type = 3;
                main.am.playSound("ButtonPressed");
                main.gameScreen = new GameScreen(main);
                main.setScreen(main.gameScreen);
            }
        });

        load = new Button(new TextureRegionDrawable(main.am.getTexture("LoadButton")));
        load.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                if(!Gdx.files.local("save.json").exists()){
                    return;
                }
                main.type = 2;
                main.am.playSound("ButtonPressed");
                main.gameScreen = new GameScreen(main);
                main.setScreen(main.gameScreen);
                main.saveLoad.load(main.gameScreen);
            }
        });

        table.center().left();
        table.add(startGame).width(64 * 3).height( 32 * 3).padLeft(10);
        table.row();
        table.add(load).width(64*3).height(32 *3).padTop(50f).padLeft(10);
        table.row();
        table.add(pathOfGlory).width(64*3).height(32 *3).padTop(50f).padLeft(10);

        menuStage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        main.batch.begin();

        main.batch.draw(screen, 0, 0, menuStage.getWidth(), menuStage.getHeight());
        main.batch.end();

        menuStage.act();
        menuStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        menuStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        menuStage.dispose();
    }

    @Override
    public void dispose() {
        menuStage.dispose();
    }
}
