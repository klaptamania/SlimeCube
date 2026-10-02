package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.GameScreen;

public class NPC_OldRobot extends Entity{

    private static Texture sheet;

    public NPC_OldRobot(GameScreen screen){
        super(screen);

        setDefaultStats();
    }

    private void setDefaultStats() {

        speed = 15f;

        position = new Vector2(700, 1400);
        velocity = new Vector2(0,0);
        direction = new Vector2(0,0);

        hitbox = new Rectangle(8* 2,6 * 2,16 * 2,16 * 2);

        if(sheet == null) sheet = new Texture("Entity/NPC/NPC_OldRobotSheett.png");
        regions = TextureRegion.split(sheet, 32, 32);
        idleD = regions[2][0];
        idleU = regions[1][0];
        idleL = regions[0][0];
        idleR = regions[0][0];

        current = idleD;

        HP = 30;

    }


}
