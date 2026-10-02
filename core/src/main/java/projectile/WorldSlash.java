package projectile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import entity.Entity;
import io.github.clapt.ffProoj.GameScreen;

import java.util.ArrayList;

public class WorldSlash extends Projectile{
    private static Texture texture;
    private final float range = 1000f;
    private final ArrayList<Entity> container = new ArrayList<>();

    public WorldSlash(GameScreen screen){
        super(screen);
        lifeSpan = 0.2f;

        position = new Vector2(0,0);
        velocity = new Vector2();

        hitbox = new Rectangle(5 * 2, 5 * 2, 22 * 2, 22 * 2);

        if(texture == null) texture =  screen.am.getTexture("WorldSlash");

        img = new Sprite(texture);
        img.setSize(3000,32 * 3);
        img.setOriginCenter();
    }

    @Override
    public void fire(Vector2 firedPosition, Vector2 direction, int damage, float speed, Entity user, float accelTime){
        velocity = direction.cpy();

        angle = MathUtils.atan2(velocity.y, velocity.x);
        angle *= MathUtils.radiansToDegrees;

        Vector2 start = firedPosition.cpy().mulAdd(velocity, -range);
        Vector2 end   = firedPosition.cpy().mulAdd(velocity,  range);
        this.damage = damage;
        this.user = user;

        img.setCenter(firedPosition.x, firedPosition.y);
        img.setRotation(angle);

        container.clear();
        screen.cCheck.checkBullet(start.x, start.y , end.x, end.y, screen.totalEntity,user, hitbox.height/2f, container);
//        if(screen.cCheck.checkBulletWall(start.x , start.y , end.x, end.y,hitbox.height/2f, (int)damage))        {
//            playSound();
//        }

        if(!container.isEmpty()) {
            for(Entity e : container){
                e.takeForceDamage(damage);
                screen.particle.create(1, e.getPosition().x - 64, e.getPosition().y - 64, screen, "SwordHeavyParticle" , 0, 200, 0.4f, 32 * 3);
            }
        };

        isAlive = true;
    }
    @Override
    public void draw(SpriteBatch batch){
        img.draw(batch);
    }

    @Override
    public void update(){
        float deltaTime = Gdx.graphics.getDeltaTime();
        running += deltaTime;
        if(running >= lifeSpan && isAlive){
            running = 0;
            isAlive = false;
        }
        if(running >= lifeSpan /10f && isAlive){
            img.setColor(1,1,1, 1- running / lifeSpan);
        }

    }

    @Override
    public Projectile create(){
        return new WorldSlash(screen);
    }
}
