package item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import entity.Entity;
import entity.Player;
import io.github.clapt.ffProoj.GameScreen;
import particle.Particle;

public class Ammo extends Item{

    private static Texture sheet;

    public Ammo(GameScreen screen, Vector2 position){
        super(screen);
        loadDefaultStats(position);
    }

    private void loadDefaultStats(Vector2 position) {
        if(sheet == null) sheet = screen.am.getTexture("Ammo");
        hitbox = new Rectangle(0,0,32 * 2, 32 * 2);
        this.position = position;
    }

    @Override
    public void onPickup(Player e){
        e.takeAmmo(-12);
        screen.am.playSound("AmmoPickup");
        isAlive = false;
        isDeath = true;

        float angle = 45;
        float speed = 55f;
        Vector2 vel = new Vector2(MathUtils.cosDeg(angle),MathUtils.sinDeg(angle)).scl(speed);

        screen.particles.add(new Particle(
            e.getPosition().x + e.getHitbox().x,
            e.getPosition().y + e.getHitbox().y,
            vel,
            screen.am.getTexture("AmmoParticle"),
            64,
            1.5f
        ));

    }


    @Override
    public void draw(){
        float w = screen.tileSize * boingW;
        float h = screen.tileSize * boingH;

        if(isInvincible){
            screen.main.batch.setColor(1, 0.3f, 0.3f, 1);
        }else{
            screen.main.batch.setColor(1, 1, 1, 1);
        }

        screen.main.batch.draw(sheet,position.x - (w - screen.tileSize) / 2, position.y, w, h);
    }
}
