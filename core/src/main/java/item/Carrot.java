package item;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import entity.Entity;
import io.github.clapt.ffProoj.GameScreen;

import java.util.ArrayList;

public class Carrot extends Weapon{
    float explodeRadius = 4 * screen.tileSize;
    Rectangle explode = new Rectangle();

    public Carrot(GameScreen screen, Entity user){
        super(screen);
        this.user = user;

        setDefaultStats();
    }

    private void setDefaultStats() {
        //Stats declare
        magazineSize = 1;
        currentMagazine = magazineSize;

        fireRate = 2f;
        reloadTime = 0.01f;

        damage = 50;
        speed = 2000f;

        //Attributes declaration
        position = new Vector2();
        if(user != null) position.set(user.getPosition());
    }

    @Override
    public void PrimaryUse(){
        if(canShoot && currentMagazine > 0){
            currentMagazine--;
            screen.cCheck.destroyTileArea(explode, 50);
            screen.am.playSound("CarrotExplode");
            explode.set(user.getHitbox().x - explodeRadius/2f + user.getPosition().x, user.getHitbox().y - explodeRadius/2f + user.getPosition().y, user.getHitbox().width + explodeRadius, user.getHitbox().height + explodeRadius );

            ArrayList<Entity> container = new ArrayList<>();

            screen.cCheck.checkInside(explode, screen.totalEntity, container);
            screen.cCheck.destroyTileArea(explode, 25);

            System.out.println(container.isEmpty());

            screen.particle.create(explodeRadius,user.getPosition().x, user.getPosition().y,screen,"CarrotExplode",25,200,0.5f);

            if(!container.isEmpty()){
                for(Entity e : container){
                    Vector2 center = new Vector2(
                        position.x + screen.tileSize/2f,
                        position.y + screen.tileSize/2f
                    );

                    Vector2 dir = e.getPosition().cpy().add(screen.tileSize/2f,screen.tileSize/2f).sub(center);

                    float dist = dir.len();
                    float force = Math.max(0, 1f - dist/explodeRadius);

                    dir.nor().scl(force * 1500f);


                    e.takeForceDamage(damage);
                    e.forceMove(dir);

                }
            }
            isDeath = true;
        }
    }


    @Override
    public void draw() {}

}
