package item;

import com.badlogic.gdx.math.Vector2;
import entity.Entity;
import io.github.clapt.ffProoj.GameScreen;

public class Corn extends Weapon{

    public Corn(GameScreen screen, Entity user){
        super(screen);
        this.user = user;

        setDefaultStats();
    }

    private void setDefaultStats() {
        //Stats declare
        magazineSize = 1;
        currentMagazine = magazineSize;

        fireRate = 3f;
        reloadTime = 0.0001f;

        damage = 15;
        speed = 3000f;

        //Attributes declaration
        position = new Vector2();
        if(user != null) position.set(user.getPosition());

        shotDir = new Vector2();

        typeProjectile = new projectile.Cor(screen);

        accel = 0.5f;

    }
    @Override
    protected void playSound(){
        screen.am.playSound("CornShoot");
    }

    @Override
    public void draw() {}
}
