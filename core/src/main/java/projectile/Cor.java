package projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.GameScreen;
import entity.Entity;

import java.util.ArrayList;

public class Cor extends Projectile{
    private static Texture texture;
    float explodeRadius = 5 * screen.tileSize;
    Rectangle explode = new Rectangle();

    public Cor(GameScreen screen){
        super(screen);
        lifeSpan = 1.5f;

        position = new Vector2(0,0);
        velocity = new Vector2();

        hitbox = new Rectangle(5 * 2, 5 * 2, 22 * 2, 22 * 2);

        if(texture == null) texture =  screen.am.getTexture("Corn");

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

            e.takeForceDamage((damage));
            e.forceMove(dir);
            screen.cCheck.checkTileHighSpeed(e);
        }

        super.onDeath();
    }

    private void particle() {
        screen.particle.create(explodeRadius, position.x, position.y, screen, "CornExplode", 35, 250, 0.5f);
    }

    @Override
    protected void playSound(){
        screen.am.playSound("CornExplode");
    }

    @Override
    public Projectile create(){
        return new Cor(screen);
    }

    @Override
    public void projectDebug() {
        shape.setProjectionMatrix(screen.cam.combined);
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.rect(explode.x, explode.y, explode.width, explode.height);
        shape.end();
    }
}
