package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.GameScreen;
import item.Carrot;
import particle.Particle;


public class MON_CarrotSuicide extends Entity{
    private static Texture sheet;

    private float detectRange;

    public MON_CarrotSuicide(GameScreen screen, int entityIndex){
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
        if(sheet == null) sheet = screen.am.getTexture("CarrotSuicide");
        current = new TextureRegion(sheet);

        //Attributes
        speed = 40;
        HP = 25;
        maxHP = HP;
        detectRange = 2 * screen.tileSize;

        currentWeapon = new Carrot(screen, this);
    }

    @Override
    public void update(){
        float deltaTime = Gdx.graphics.getDeltaTime();
        if(onSpawn){
           if(spawnFresh == 0){
               for(int i = 0; i <= 20; i++){

                   float angle = MathUtils.random(0, 360);
                   float speed = MathUtils.random(15f,200f);

                   Vector2 vel = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).scl(speed);

                   float dst = MathUtils.random(0, 32);

                   Vector2 offset = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).scl(dst);


                   screen.particles.add(new Particle(
                       position.x + hitbox.x + sheet.getWidth() / 2f + offset.x,
                       position.y + hitbox.y + sheet.getHeight() /2f + offset.y,
                       vel,
                       screen.am.getTexture("SpawnEffect"),
                       32,
                       0.3f
                   ));
               }
           }
            spawnFresh += deltaTime;
            if(spawnFresh >= 0.5f){
                onSpawn = false;
            }

            return;
        }

        if(detecter() && !onSpawn) shot();
        else{
            direction.x = screen.player.getPosition().x - position.x;
            direction.y = screen.player.getPosition().y - position.y;
            direction.nor();

            velocity.x += direction.x * speed;
            velocity.y += direction.y * speed;

            checkCollision();

            if(!velocity.isZero() && !onSpawn){
                wakingTime+= deltaTime;
                if(wakingTime >= 1f){
                    wakingTime = 0;
                    screen.am.playSound("EntityWalking");
                }
            }

            position.mulAdd(velocity, deltaTime);

            velocity.scl(0.75f);

            if(velocity.len2() < 1f){
                velocity.setZero();
            }
            animationUpdate(false, timer);

            invincibleCooldown(deltaTime);
        }

        onDeath();
    }

    private boolean detecter() {
        return position.dst2(screen.player.getPosition()) <= detectRange * detectRange;
    }

    @Override
    protected void onDeath(){
        super.onDeath();
        if(isDeath){
            if(currentWeapon.getCurrentMagazine() != 0 ){
                currentWeapon.PrimaryUse();
            }else currentWeapon.reload();
        }
    }
    private void shot(){
        isDeath = true;
    }
    @Override
    protected String getSheet() { return "CarrotSuicide"; }
}
