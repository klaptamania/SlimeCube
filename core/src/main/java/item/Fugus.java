package item;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.GameScreen;
import entity.Entity;

public class Fugus extends Weapon{

    public Fugus(GameScreen screen, Entity user){
        super(screen);
        this.user = user;

        setDefaultStats();
    }

    private void setDefaultStats() {
        //Stats declare
        magazineSize = 6;
        currentMagazine = magazineSize;

        fireRate = 2f;

        damage = 5;
        reloadTime = 0.01f;
        speed = 1500f;

        //Attributes declaration
        position = new Vector2();
        if(user != null) position.set(user.getPosition());

        shotDir = new Vector2();

        typeProjectile = new projectile.Fugus(screen);

    }
    @Override
    protected void playSound(){
        screen.am.playSound("FungusShoot");
    }

    @Override
    public void draw() {}
}
