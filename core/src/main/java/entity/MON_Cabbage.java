package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.GameScreen;

import item.Ammo;
import item.Cabbage;
import particle.Particle;

public class MON_Cabbage extends Entity{
    private static Texture sheet;

    private float detectRange;

    public MON_Cabbage(GameScreen screen, int entityIndex){
        super(screen);

        setDefaultStats(2);
        this.entityIndex = entityIndex;
    }

    public MON_Cabbage(GameScreen screen, int entityIndex, int times){
        super(screen);

        setDefaultStats(times);
        this.entityIndex = entityIndex;
    }


    private void setDefaultStats(int times) {
        this.times = times;
        //Vector
        velocity = new Vector2(0,0);
        position = new Vector2(100,100);
        direction = new Vector2(0,0);

        //Hitbox
        hitbox = new Rectangle(3 * 2, 3 * 2, 26 * 2 , 26 * 2);

        //Texture
        if(sheet == null) sheet = screen.am.getTexture("Cabbage");
        current = new TextureRegion(sheet);

        //Attributes
        if(times == 2){
            speed = 140;
            HP = 45;
        }else if(times == 1){
            speed = 100;
            HP = 15;
        }else if(times == 0){
            speed = 40;
            HP = 1;
        }

        maxHP = HP;
        detectRange = 2 * screen.tileSize;
        currentWeapon = new Cabbage(screen, this);
        ammoAmount = 100000;
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
            times--;
            if(times >= 0){
                int i = screen.monsterFreeStack.pop();
                int j = screen.monsterFreeStack.pop();

                Entity e = new MON_Cabbage(screen, i, times);

                int x = MathUtils.randomBoolean(0.5f) ? -1 : 1;
                int y = MathUtils.randomBoolean(0.5f) ? -1 : 1;

                e.setPosition(position.x + (64 * 1.5f * x), position.y + 64 * y);

                screen.am.playSound("Spawn");
                screen.particle.create(3, e.getPosition().x, e.getPosition().y, screen, "SpawnEffect", 14, 16, 0.4f);

                screen.monster[i] = e;

                Entity ee = new MON_Cabbage(screen, j, times);
                ee.setPosition(position.x + 64 * 1.5f * x * -1, position.y + 64 * y * -1);

                screen.am.playSound("Spawn");
                screen.particle.create(3, ee.getPosition().x, ee.getPosition().y, screen, "SpawnEffect", 14, 16, 0.4f);

                screen.monster[j] = ee;
            }
        }
    }

    @Override
    public void draw(){

        if(isInvincible){
            screen.main.batch.setColor(1, 0.3f, 0.3f, 1);
        }else{
            screen.main.batch.setColor(1, 1, 1, 1);
        }

        if(times == 2){
            float w = screen.tileSize * boingW;
            float h = screen.tileSize * boingH;
            screen.main.batch.draw(current,position.x - (w - screen.tileSize) / 2, position.y, w, h);
        }else if(times == 1){
            float w = 25 * 2 * boingW;
            float h = 25 * 2 * boingH;
            screen.main.batch.draw(current,position.x - (w - 25 * 2) / 2, position.y, w, h);
        }
        else if(times == 0){
            float w = 16 * 2 * boingW;
            float h = 16 * 2 * boingH;
            screen.main.batch.draw(current,position.x - (w - 16 * 2) / 2, position.y, w, h);
        }

        if(currentWeapon != null) currentWeapon.draw();
    }
    private void shot(){
        isDeath = true;
    }
    @Override
    protected String getSheet(){ return "Cabbage"; }

    @Override
    protected void dropOnDeath(){
        if(times == 0) screen.item.add(new Ammo(screen, position));
    }
}

