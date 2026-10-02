package projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import entity.Entity;
import io.github.clapt.ffProoj.GameScreen;

import java.util.ArrayList;

public class CarrotBullet extends Projectile{
    private static Texture texture;
    float explodeRadius = screen.tileSize;
    Rectangle explode = new Rectangle();

    public CarrotBullet(GameScreen screen){
        super(screen);
        lifeSpan = 1.5f;

        position = new Vector2(0,0);
        velocity = new Vector2();

        hitbox = new Rectangle(5 * 2, 5 * 2, 22 * 2, 22 * 2);

        if(texture == null) texture =  screen.am.getTexture("CarrotBullet");

        img = new Sprite(texture);
        img.setSize(32 * 2,32 * 2);
        img.setOriginCenter();
    }

    @Override
    protected void onDeath(){
        explode.set(hitbox.x - explodeRadius/2f + position.x, hitbox.y - explodeRadius/2f + position.y, hitbox.width + explodeRadius, hitbox.height + explodeRadius );
        ArrayList<Entity> container = new ArrayList<>();
        screen.cCheck.checkInside(explode,screen.totalEntity,container);

        System.out.println(container.isEmpty());
        particle();
        screen.cCheck.destroyTileArea(explode, 100);
        for(Entity e : container){

            Vector2 center = new Vector2(
                position.x + screen.tileSize/2f,
                position.y + screen.tileSize/2f
            );

            Vector2 dir = e.getPosition().cpy().add(screen.tileSize/2f,screen.tileSize/2f).sub(center);

            float dist = dir.len();
            if(dist <= 1f){
                dist = dir.set(velocity).len();
            }
            float force = Math.max(0, 1f - dist/explodeRadius);

            dir.nor().scl(force * 3500f);

            e.takeForceDamage((10));
            e.forceMove(dir);
        }

        super.onDeath();
    }

    private void particle() {
        screen.particle.create(explodeRadius, position.x, position.y, screen, "CarrotExplode", 35, 250, 0.5f, 64);
    }

    @Override
    protected void playSound(){
        screen.am.playSound("CarrotExplode");
    }

    @Override
    public Projectile create(){
        return new CarrotBullet(screen);
    }

    @Override
    public void projectDebug() {
        shape.setProjectionMatrix(screen.cam.combined);
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.rect(explode.x, explode.y, explode.width, explode.height);
        shape.end();
    }
}
