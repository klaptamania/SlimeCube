package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.AfterImage;
import io.github.clapt.ffProoj.GameScreen;
import item.Cuc;

import java.util.ArrayList;

public class MON_Cucumber extends Entity
{
    private static Texture sheet;

    private float detectRange;
    private float timer = 0;
    private float charge = 0;
    private float cooldown = 8;
    private float af = 0;

    private final ArrayList<AfterImage> afI = new ArrayList<>();

    boolean running = false;

    public MON_Cucumber(GameScreen screen, int monsterIndex){
        super(screen);

        setDefaultStats();
        this.entityIndex = monsterIndex;
    }

    private void setDefaultStats() {

        //Vector
        velocity = new Vector2(0,0);
        position = new Vector2(100,100);
        direction = new Vector2(0,0);

        //Hitbox
        hitbox = new Rectangle(3 * 2, 3 * 2, 26 * 2 , 26 * 2);

        //Texture
        if(sheet == null) sheet = screen.am.getTexture("Cucumber");
        current = new TextureRegion(sheet);

        //Attributes
        speed = 135;
        HP = 100;
        maxHP = HP;
        detectRange = 25 * screen.tileSize;

        currentWeapon = new Cuc(screen, this);
        ammoAmount = 100000;

    }

    @Override
    public void update(){

        if(!charging && !rage){
            super.update();
            cooldown += Gdx.graphics.getDeltaTime();
            running = false;
            if(cooldown >= 10f){
                cooldown = 0;
                if(detecter() && !onSpawn) shot();
                screen.am.playSound("HorseWhistle");
            }
        }else if(charging && !rage){

            charge += Gdx.graphics.getDeltaTime();
            if(charge >= 1.2f){
                charge = 0;
                rage = true;
            }
        }

        if(!afI.isEmpty()){
            for(int i = afI.size() - 1; i >= 0; i--){
                afI.get(i).alpha -= Gdx.graphics.getDeltaTime() * 4f;

                if(afI.get(i).alpha <= 0f) afI.remove(i);
            }
        }

        if(rage){
            CHARGE();

            if(!running){
                running = true;

                screen.am.playSound("HorseRunning");
            }
        }

        currentWeapon.update();
    }

    private void CHARGE() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        Vector2 lastDir = screen.player.getPosition().cpy().add(screen.tileSize / 2f, screen.tileSize / 2f);
        Vector2 chargeDir = lastDir.sub(position).nor();

        velocity.x += chargeDir.x * 1500f;
        velocity.y += chargeDir.y * 1500f;

        if(screen.cCheck.checkTileHighSpeed(this)) velocity.setZero();
        int i = screen.cCheck.sweptCheckEntity(this);

        if(i != -999){

            screen.totalEntity.get(i).forceMove(velocity.cpy().add(3000f, 3000f));
            if(screen.cCheck.checkTileHighSpeed(screen.totalEntity.get(i))) screen.totalEntity.get(i).velocity.setZero();
            screen.totalEntity.get(i).takeForceDamage(35);

            screen.particle.create(2 * screen.tileSize, position.x, position.y, screen, "CucumberParticle", 30, 300, 2f);
            if(screen.totalEntity.get(i) instanceof Player || screen.player.isDeath){
                if(!screen.player.isDashing()){
                    cooldown = 7;
                }
                if(screen.cCheck.checkTileHighSpeed(this)) velocity.setZero();
                timer = 0;
                rage = false;
                charging = false;
                return;
            }

        }
        position.mulAdd(velocity, deltaTime);

        velocity.scl(0.7f);

        af+= deltaTime;
        if(af >= 0.01){
            af = 0;
            afI.add(new AfterImage(position.cpy(), 1, boingW, boingH));
        }

        timer += deltaTime;
        if(timer >= 1f){
            timer = 0;
            rage = false;
            charging = false;
            running = false;
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
    public void draw(){
        for(AfterImage a : afI){
            screen.main.batch.setColor(1, 1, 1, a.alpha);
            float w = screen.tileSize * a.boingW;
            float h = screen.tileSize * a.boingH;
            screen.main.batch.draw(current, a.position.x - (w - screen.tileSize) / 2, a.position.y, w,h);
            screen.main.batch.setColor(1, 1, 1, 1);
        }

        super.draw();
    }

    @Override
    protected String getSheet() { return "Cucumber"; }
}
