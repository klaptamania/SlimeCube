package projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.GameScreen;

public class Corn extends Projectile{
    private static Texture texture;

    public Corn(GameScreen screen){
        super(screen);
        lifeSpan = 3f;

        position = new Vector2(0,0);
        velocity = new Vector2();
        hitbox = new Rectangle(13 * 2, 17 * 2, 6, 4);

        if(texture == null) texture =  screen.am.getTexture("CornPepper");

        img = new Sprite(texture);
        img.setSize(64,64);
        img.setOriginCenter();
    }

    @Override
    public Projectile create(){
        return new Corn(screen);
    }
    @Override
    public void dispose(){
        texture.dispose();
    }
}
