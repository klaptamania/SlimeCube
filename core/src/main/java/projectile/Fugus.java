package projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.GameScreen;

public class Fugus extends Projectile{
    private static Texture texture;
    private final StatusEffect se = new StatusEffect(0,3,2);

    public Fugus(GameScreen screen){
        super(screen);
        lifeSpan = 1f;

        position = new Vector2(0,0);
        velocity = new Vector2();

        hitbox = new Rectangle(5 * 2, 5 * 2, 22 * 2, 22 * 2);

        if(texture == null) texture = screen.am.getTexture("Fungus");

        img = new Sprite(texture);
        img.setSize(32 * 2,32 * 2);
        img.setOriginCenter();
    }

    @Override
    protected void specialEffect(int index) {
        screen.totalEntity.get(index).affectStatus("Fungus Poison", se);
    }
    @Override
    protected void playSound(){
        screen.am.playSound("FungusHit");
    }
    @Override
    public Projectile create(){
        return new Fugus(screen);
    }

}
