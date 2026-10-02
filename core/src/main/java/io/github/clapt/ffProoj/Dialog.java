package io.github.clapt.ffProoj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.Collections;

public class Dialog {
    private final ArrayList<String> lines = new ArrayList<>();
    int current;
    boolean active = false;
    float textTimer = 0;
    int c = 0;
    public String fullLine = "";
    public String line = "";
    public Color color;

    private final Texture textBox;

    GameScreen screen;

    public Dialog(GameScreen screen){
        this.screen = screen;
        textBox = screen.am.getTexture("TextBox");
    }

    public void start(String... totalLine){
        lines.clear();
        Collections.addAll(lines, totalLine);

        current = 0;
        active = true;

        fullLine = getCurrentLine();
    }

    public String getCurrentLine(){

        if(current + 1 > lines.size()){
            active = false;
            return null;
        }
        return lines.get(current++);
    }

    public boolean isActive() {return active;}

    public void showDialog(){
        if(isActive()){
            screen.main.batch.setColor(Color.WHITE);
            screen.font.setColor(color);

            float x = screen.cam.position.x - textBox.getWidth();
            float y = screen.cam.position.y - screen.vp.getWorldHeight() / 2f;

            // screen.main.batch.draw(textBox, screen.worldWidth / 2f - textBox.getWidth(), 15f, textBox.getWidth() * 2, textBox.getHeight());
            screen.main.batch.draw(textBox, x, y, textBox.getWidth() * 2, textBox.getHeight());
            screen.font.draw(screen.main.batch, line, x + 55f, y + textBox.getHeight() - 40f, 0 , c, textBox.getWidth() * 2 - 140f , Align.left, true);
        }
    }

    public int updateDialog(){
        screen.isDialog = true;
        int result = -1;

        if(fullLine == null) return result;

        textTimer += Gdx.graphics.getDeltaTime();
        if(textTimer >= 0.02f && c < fullLine.length()){
            textTimer -= 0.02f;
            c++;
            line = fullLine.substring(0,c);

            int i = MathUtils.random(0, 2);
            switch (i){
                case 0:
                    screen.am.playSound("Dialog1");
                    break;
                case 1:
                    screen.am.playSound("Dialog2");
                    break;
                case 2:
                    screen.am.playSound("Dialog3");
                    break;
            }

        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){

            if(c < fullLine.length()){
                c = fullLine.length();
                line = fullLine;
            }else{
                c = 0;
                fullLine = getCurrentLine();
                result += 1;
            }

            if(fullLine == null){
                screen.currentState = GameScreen.state.GAME;
                screen.isDialog = false;
            }
        }

        return result;
    }
}
