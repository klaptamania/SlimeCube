package item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import entity.Entity;
import entity.Player;
import io.github.clapt.ffProoj.GameScreen;
import projectile.Pea;
import projectile.WatermelonSeed;

public class Watermelon extends Weapon{

    private static Texture weaponTexture;

    public Watermelon(GameScreen screen, Entity user, int currentMagazine){
        super(screen);
        this.user = user;

        setDefaultStats(0,0, currentMagazine);
    }

    public Watermelon(GameScreen screen, float posX, float posY, int currentMagazine, boolean isJustDropped){
        super(screen);

        this.isJustDropped = isJustDropped;
        setDefaultStats(posX, posY, currentMagazine);
    }

    private void setDefaultStats(float posX, float posY, int currentMagazine) {
        //Stats declare
        magazineSize = 50;
        this.currentMagazine = magazineSize;

        if(currentMagazine != -1) this.currentMagazine = currentMagazine;

        fireRate = 0.035f;

        damage = 20;
        speed = 1600f;
        reloadTime = 4f;

        //Attributes declaration
        position = new Vector2();
        if(user != null) position.set(user.getPosition());
        else position.set(posX, posY);

        shotDir = new Vector2();

        offsetX = -10 * 2; offsetY = 0;

        typeProjectile = new WatermelonSeed(screen);
    }

    @Override
    public void draw(){}

    @Override
    public void PrimaryUse(){
        if(!user.canMove){
            super.PrimaryUse();
            if(currentMagazine <= 0){
                user.canMove = true;
            }
        }
    }

    @Override
    public Texture getTexture() { return weaponTexture; }

}
