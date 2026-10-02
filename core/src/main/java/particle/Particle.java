package particle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.GameScreen;

public class Particle {
    protected Vector2 position, velocity;
    protected boolean isAlive = true;
    protected float lifeTime, maxLifeTime, size;
    protected Texture texture;
    public Particle(){

    }
    public Particle(float x, float y, Vector2 velocity, Texture texture, float size, float lifeTime){
        this.velocity = velocity;
        position = new Vector2(x,y);
        this.texture = texture;
        this.lifeTime = lifeTime;
        maxLifeTime = lifeTime;
        this.size = size;
    }

    public void update(){
        if(texture != null){
            float deltaTime = Gdx.graphics.getDeltaTime();
            lifeTime -= deltaTime;

            position.mulAdd(velocity, deltaTime);

            velocity.scl(0.95f);

            if(lifeTime <= 0){
                isAlive = false;
            }
        }
    }

    public void draw(SpriteBatch batch){
       if(texture != null){
           float alpha = lifeTime / maxLifeTime;

           batch.setColor(1, 1, 1, alpha);

           batch.draw(
               texture,
               position.x,
               position.y,
               size,
               size
           );

           batch.setColor(Color.WHITE);
       }
    }

    public void dispose(){
        texture.dispose();
    }

    public boolean isAlive(){
        return isAlive;
    }
    public void create(float radius, float x, float y, GameScreen screen, String textureName, int number, int maxSpeed, float lifeTime){
        create(radius, x, y, screen, textureName, number, maxSpeed, lifeTime, radius * 0.25f);
    }

    public void create(float radius, float x, float y, GameScreen screen, String textureName, int number, int maxSpeed, float lifeTime, float size){
        for(int i = 0; i <= number; i++){

            float angle = MathUtils.random(0, 360);
            float speed = MathUtils.random(15f, maxSpeed);

            Vector2 vel = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).scl(speed);

            float dist = MathUtils.random(0f, radius / 2f);

            Vector2 offset = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).scl(dist);

            screen.particles.add(new Particle(
                x + 32 + offset.x,
                y + 32 + offset.y,
                vel,
                screen.am.getTexture(textureName),
                size,
                lifeTime
            ));
        }
    }
    public void create(float radius, float x, float y, GameScreen screen, Texture texture, int number, int maxSpeed, float lifeTime, float size){
        for(int i = 0; i <= number; i++){

            float angle = MathUtils.random(0, 360);
            float speed = MathUtils.random(15f, maxSpeed);

            Vector2 vel = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).scl(speed);

            float dist = MathUtils.random(0f, radius / 2f);

            Vector2 offset = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).scl(dist);

            screen.particles.add(new Particle(
                x + 32 + offset.x,
                y + 32 + offset.y,
                vel,
                texture,
                size,
                lifeTime
            ));
        }
    }
}
