package item;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import entity.Entity;
import entity.MON_Cabbage;
import io.github.clapt.ffProoj.GameScreen;
import projectile.StatusEffect;

import java.util.ArrayList;

public class Cabbage extends Weapon{
    float explodeRadius = 2 * screen.tileSize;
    Rectangle explode = new Rectangle();

    public Cabbage(GameScreen screen, Entity user){
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

        damage = 15;
        speed = 2000f;

        //Attributes declaration
        position = new Vector2();
        if(user != null){
            position.set(user.getPosition());
            if(user.times == 1){
                damage = 7;
            }else if(user.times == 0){
                damage = 10;
            }
        }
    }

    @Override
    public void PrimaryUse(){
        if(canShoot && currentMagazine > 0){
            currentMagazine--;
            screen.am.playSound("CarrotExplode");
            explode.set(user.getHitbox().x - explodeRadius/2f + user.getPosition().x, user.getHitbox().y - explodeRadius/2f + user.getPosition().y, user.getHitbox().width + explodeRadius, user.getHitbox().height + explodeRadius );

            ArrayList<Entity> container = new ArrayList<>();

            screen.cCheck.checkInside(explode, screen.totalEntity, container);

            System.out.println(container.isEmpty());

            screen.particle.create(explodeRadius,user.getPosition().x, user.getPosition().y,screen,"CabbageParticle",45,200,0.5f);

            if(!container.isEmpty()){
                for(Entity e : container){
                    e.takeDamage(damage);
                    if(user.times == 2){
                        e.affectStatus("CabbageTrapped", new StatusEffect(1, 1, 0.5f));
                    }else if(user.times == 1){
                        e.affectStatus("CabbageTrapped", new StatusEffect(1, 1, 1f));
                    }else if(user.times == 0){
                        e.affectStatus("CabbageTrapped", new StatusEffect(1, 1, 1.5f));
                    }
                }
            }
            isDeath = true;
        }
    }


    @Override
    public void draw() {}

}
