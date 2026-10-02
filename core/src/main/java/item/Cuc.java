package item;

import com.badlogic.gdx.math.Vector2;
import entity.Entity;
import io.github.clapt.ffProoj.GameScreen;


public class Cuc extends Weapon{

    public Cuc(GameScreen screen, Entity user){
        super(screen);
        this.user = user;

        setDefaultStats();
    }

    private void setDefaultStats() {
        //Stats declare
        magazineSize = 1000;
        currentMagazine = magazineSize;

        fireRate = 5f;

        damage = 35;
        speed = 3000f;
        reloadTime = 0.01f;

        //Attributes declaration
        position = new Vector2();
        if(user != null) position.set(user.getPosition());


        accel = 0.5f;

    }

    @Override
    public void PrimaryUse(){
        if(canShoot && currentMagazine > 0){
            currentMagazine--;
            user.charging = true;
        }
    }
    @Override
    protected void playSound(){
        screen.am.playSound("CornShoot");
    }

    @Override
    public void draw() {}
}
