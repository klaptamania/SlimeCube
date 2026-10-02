package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.GameScreen;
import item.Fugus;
import projectile.StatusEffect;

import java.util.ArrayList;

public class MON_ShootingFungus extends Entity{

    private static Texture sheet;

    private float detectRange;
    private final float explodeRadius = 100f * 2;
    protected boolean done = false;

    public MON_ShootingFungus(GameScreen screen, int entityIndex){
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
        if(sheet == null) sheet = screen.am.getTexture("ShootingFungus");
        current = new TextureRegion(sheet);

        //Attributes
        speed = 135;
        HP = 50;
        maxHP = HP;
        detectRange = 10 * screen.tileSize;

        currentWeapon = new Fugus(screen, this);
        ammoAmount = 100000;

    }

    @Override
    public void update(){


        super.update();
        if(detecter()&& !onSpawn) shot();

        currentWeapon.update();
    }

    private void explode() {
        velocity.setZero();
        Rectangle explode = new Rectangle(hitbox.x - explodeRadius/2f + position.x, hitbox.y - explodeRadius/2f + position.y, hitbox.width + explodeRadius, hitbox.height + explodeRadius );

        screen.particle.create(explodeRadius,  position.x , position.y, screen, "FungusExplode", 35, 250, 0.2f);

        ArrayList<Entity> container = new ArrayList<>();
        screen.cCheck.checkInside(explode,screen.totalEntity,container);

        for(Entity c : container){
            c.takeForceDamage(25);
            c.affectStatus("Fungus Poison", new StatusEffect(3,3,5));
        }

        done = true;
    }

    @Override
    protected void onDeath(){
        if(!isAlive && !isDeath) {
            if(timer == 0) {
                screen.am.playSound("FungusDied");
            }

            timer += Gdx.graphics.getDeltaTime();
            if(timer >= 0.2f){
                timer = 0;
                boingH = Math.max(0, boingH - 0.4f);
                deathState++;
            }

            if(deathState >= 5){
                explode();
                screen.am.playSound("FungusExplode");
            }

            if(done){
                isDeath = true;
            }
        }
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
    public void drawDebug(){
        Rectangle explode = new Rectangle(hitbox.x - explodeRadius/2f + position.x, hitbox.y - explodeRadius/2f + position.y, hitbox.width + explodeRadius, hitbox.height + explodeRadius );
        shapeRenderer.setProjectionMatrix(screen.cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(explode.x, explode.y, explode.width, explode.height);
        shapeRenderer.end();
    }

    @Override
    protected String getSheet() { return "ShootingFungus"; }

}
