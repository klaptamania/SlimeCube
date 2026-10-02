package utilityTool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.Dialog;
import io.github.clapt.ffProoj.GameScreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import entity.Entity;

public class UtilityTools {
    public void loadDialog(GameScreen screen, String filePath, ArrayList<Dialog> dM){
        try {
            BufferedReader br = Gdx.files.internal(filePath).reader(1024);
            ArrayList<String> container = new ArrayList<>();
            String line;
            while((line = br.readLine()) != null){
                if(line.equals("BreakRightHere")){
                    Dialog tmp = new Dialog(screen);
                    tmp.start(container.toArray(new String[0]));

                    tmp.color = Color.YELLOW;
                    dM.add(tmp);
                    container.clear();
                    continue;
                }
                container.add(line);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 45;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        screen.font = screen.generator.generateFont(parameter);
    }
    public Vector2 getCentered(Entity entity){
        return new Vector2(entity.getTrueHitbox().x + entity.getTrueHitbox().width /2f, entity.getTrueHitbox().y + entity.getTrueHitbox().height / 2f);
    }
    public Vector2 getCentered(float x, float y, float w, float h){
        return new Vector2(x + w /2f, y + h / 2f);
    }
}
