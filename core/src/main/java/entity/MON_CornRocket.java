package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.GameScreen;

import item.Corn;

public class MON_CornRocket extends Entity{
    private static Texture sheet;

    private float detectRange;

    public MON_CornRocket(GameScreen screen, int entityIndex){
        super(screen);

        setDefaultStats();
        this.entityIndex = entityIndex;
    }

    private void setDefaultStats() {

        //Vector
        velocity = new Vector2(0,0);
        position = new Vector2(100,100);
        direction = new Vector2(0,0);

        //Hitbox
        hitbox = new Rectangle(3 * 2, 3 * 2, 26 * 2 , 26 * 2);

        //Texture
        if(sheet == null) sheet = screen.am.getTexture("CornRocket");
        current = new TextureRegion(sheet);

        //Attributes
        speed = 135;
        HP = 100;
        maxHP = HP;
        detectRange = 25 * screen.tileSize;

        currentWeapon = new Corn(screen, this);
        ammoAmount = 100000;

    }

    @Override
    public void update(){

        super.update();
        if(detecter() && !onSpawn) shot();

        currentWeapon.update();
    }

    private boolean detecter() {
        return position.dst2(screen.player.getPosition()) <= detectRange * detectRange;
    }

    private void shot(){
        if(currentWeapon.getCurrentMagazine() != 0 ){
            currentWeapon.PrimaryUse();
        }else currentWeapon.reload();
    }

    @Override
    protected String getSheet(){ return "CornRocket"; }

}

