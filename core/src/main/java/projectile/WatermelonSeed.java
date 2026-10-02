package projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.GameScreen;

public class WatermelonSeed extends Projectile{

    private static Texture texture;

    public WatermelonSeed(GameScreen screen){
        super(screen);
        lifeSpan = 3f;

        position = new Vector2(0,0);
        velocity = new Vector2();
        hitbox = new Rectangle(9 * 2, 9 * 2, 9, 9);

        if(texture == null) texture =  screen.am.getTexture("WatermelonSeed");

        img = new Sprite(texture);
        img.setSize(32,32);
        img.setOriginCenter();
    }

    @Override
    public Projectile create(){
        return new WatermelonSeed(screen);
    }
    @Override
    public void dispose(){
        texture.dispose();
    }
}
