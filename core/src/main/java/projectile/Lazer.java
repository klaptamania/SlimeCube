package projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import entity.Entity;
import io.github.clapt.ffProoj.GameScreen;

public class Lazer extends Projectile{
    private static Texture texture;

    public Lazer(GameScreen screen){
        super(screen);

        position = new Vector2(0,0);
        velocity = new Vector2();
        hitbox = new Rectangle(9 * 2, 9 * 2, 9, 9);

        if(texture == null) texture =  screen.am.getTexture("Pea");

        img = new Sprite(texture);
        img.setSize(32,32);
        img.setOriginCenter();
    }
    @Override
    public void fire(Vector2 firedPosition, Vector2 direction, int damage, float speed, Entity user, float accelTime){

        angle = MathUtils.atan2(velocity.y, velocity.x);
        angle *= MathUtils.radiansToDegrees;

        this.damage = damage;
        this.user = user;

        img.setCenter(firedPosition.x, firedPosition.y);
        img.setRotation(angle);

    }
    @Override
    public Projectile create(){
        return new Lazer(screen);
    }
    @Override
    public void dispose(){
        texture.dispose();
    }
}
