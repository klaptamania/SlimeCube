package projectile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.GameScreen;
import entity.Entity;

public abstract class Projectile {
    protected Vector2 position, velocity;

    protected float speed, lifeSpan, accelTime;
    int damage;
    protected float running = 0;
    public boolean isAlive = false;
    public Rectangle hitbox;

    public Sprite img;
    protected float angle = 0;

    protected GameScreen screen;
    protected final ShapeRenderer shape = new ShapeRenderer();

    protected Entity user;

    public Projectile(GameScreen screen){
        this.screen = screen;
    }

    public void fire(Vector2 firedPosition, Vector2 direction, int damage, float speed, Entity user){
        fire(firedPosition,direction,damage,speed,user, 0);
    }

    public void fire(Vector2 firedPosition, Vector2 direction, int damage, float speed, Entity user, float accelTime){
        position = firedPosition.cpy();

        velocity = direction.cpy().nor();

        angle = MathUtils.atan2(velocity.y, velocity.x);
        angle *= MathUtils.radiansToDegrees;

        this.damage = damage;
        this.speed = speed;
        this.accelTime = accelTime;
        this.user = user;

        isAlive = true;
        running = 0;
    }

    public void update(){
        float deltaTime = Gdx.graphics.getDeltaTime();
        running += deltaTime;
        if(running <= lifeSpan && isAlive){

            float oldX = position.x, oldY = position.y;

            float speed;

            if(accelTime == 0){
                speed = this.speed;
            }else{
                if(running < accelTime){
                    speed = this.speed * 0.1f;
                }else{
                    speed = this.speed;
                }
            }

            position.x += velocity.x * speed * deltaTime;
            position.y += velocity.y * speed * deltaTime;


            int i = screen.cCheck.checkBullet(oldX + hitbox.x, oldY + hitbox.y, position.x + hitbox.x, position.y + hitbox.y,screen.totalEntity, user, hitbox.height/2f);
            if(screen.cCheck.DDAcheckBulletWall(oldX + hitbox.x, oldY + hitbox.y, position.x + hitbox.x, position.y + hitbox.y,hitbox.height/2f, (int)damage))        {
                playSound();
                onDeath();
            }

            if(i != -999) {
                playSound();
                position = screen.totalEntity.get(i).getPosition().cpy();
                screen.totalEntity.get(i).takeDamage(damage);
                onDeath();
                specialEffect(i);
            };

        }else{
            onDeath();
        }
    }

    protected void specialEffect(int index){};

    protected void onDeath(){ isAlive = false; };

    public void draw(SpriteBatch batch){
        img.setPosition(position.x, position.y);
        img.setRotation(angle);
        img.draw(batch);
    }
    public void projectDebug(){
        shape.setProjectionMatrix(screen.cam.combined);
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.rect(hitbox.x + position.x, hitbox.y + position.y, hitbox.width,hitbox.height);
        shape.end();
    }
    protected void playSound(){};
    public abstract Projectile create();

    public void dispose(){}
}
