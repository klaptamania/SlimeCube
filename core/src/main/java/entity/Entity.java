package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.GameScreen;
import item.Ammo;
import item.Weapon;
import particle.Particle;
import projectile.StatusEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class Entity {
    protected int entityIndex = -1;

    //Normal Attributes
    public Weapon currentWeapon;
    protected Vector2 velocity, direction, position;
    protected Rectangle hitbox;

    protected float speed, totalSpeed = 0;
    protected float invincibleTime = 0;
    protected float dodgeRate;

    protected int HP, maxHP;
    protected int deathState = 0;
    protected int ammoAmount;
    protected int defAmount;

    protected boolean isAlive = true, isDeath = false;
    protected boolean isInvincible = false;
    protected boolean isScream = false;
    protected boolean isPlayer = false;
    public boolean canMove = true;

    // holhor
    public boolean charging = false;
    public boolean rage = false;

    public boolean normalCollisionCheck = true;

    protected ArrayList<Float> speedMultiplier = new ArrayList<>();

    //Cabbage
    public int times;

    //Animation stuff;
    public Texture sheet, outline;
    public TextureRegion[][] regions;
    public TextureRegion idleD, idleU,idleL,idleR;

    protected float boingH = 1, boingW = 1;
    public boolean buttonLeftHolding = false;
    //Other stuff
    protected final GameScreen screen;
    protected TextureRegion current;
    private float running = 0;
    private int state = (int)((Math.random() * 4) + 1);
    ShapeRenderer shapeRenderer = new ShapeRenderer();

    protected HashMap<String, StatusEffect> status = new HashMap<>();
    private final ArrayList<String> statusDeleter = new ArrayList<>();

    protected boolean onSpawn;
    protected float spawnFresh = 0;
    protected float wakingTime = 0;

    //Animation sfutt
    protected float boingTime = 0.016f;
    protected float boingCounter = 0.018f;
    protected float boingMax = 1.1f, boingMin = 0.9f;

    protected boolean isIncrease = true;

    protected float timer = 0;

    public Entity(GameScreen screen){
        this.screen = screen;
        onSpawn = true;

    }

    public void draw(){
        float w = screen.tileSize * boingW;
        float h = screen.tileSize * boingH;

        if(isInvincible){
            screen.main.batch.setColor(1, 0.3f, 0.3f, 1);
        }else{
            screen.main.batch.setColor(1, 1, 1, 1);
        }

        screen.main.batch.draw(current,position.x - (w - screen.tileSize) / 2, position.y, w, h);

        if(currentWeapon != null) currentWeapon.draw();
        if(outline != null){
            screen.main.batch.setColor(1,1,1,0.7f);
            screen.main.batch.draw(outline, position.x - screen.tileSize / 2f,position.y - screen.tileSize / 2f, screen.tileSize * 2, screen.tileSize*2);
            screen.main.batch.setColor(1,1,1,1);

        }
    }

    public void update(){
        float deltaTime = Gdx.graphics.getDeltaTime();

        if(onSpawn){
            if(spawnFresh == 0) {
                for(int i = 0; i <= 20; i++){

                    float angle = MathUtils.random(0, 360);
                    float speed = MathUtils.random(15f,200f);

                    Vector2 vel = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).scl(speed);

                    float dst = MathUtils.random(0, 32);

                    Vector2 offset = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).scl(dst);


                    screen.particles.add(new Particle(
                        position.x + hitbox.x + 32 + offset.x,
                        position.y + hitbox.y + 32 + offset.y,
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

        running += deltaTime;

        if(running >= 0.5f){
            state = (int)((Math.random() * 4) + 1);
            running = 0;
        }

        switch(state){
            case 1:
                direction.x = 0;
                direction.y = 1;

                break;
            case 2:
                direction.x = 0;
                direction.y = -1;

                break;
            case 3:
                direction.x = -1;
                direction.y = 0;

                break;
            case 4:
                direction.x = 1;
                direction.y = 0;

                break;
        }

        velocity.x += direction.x * speed;
        velocity.y += direction.y * speed;

        checkCollision();

        if(!velocity.isZero() && !onSpawn){
            wakingTime += deltaTime;
            if(wakingTime >= 1f){
                screen.am.playSound("EntityWalking");
                wakingTime = 0;
            }
        }

        if(canMove) position.mulAdd(velocity,deltaTime);

        velocity.scl(0.75f);

        if(velocity.len2() < 1f){
            velocity.setZero();
        }

        animationUpdate(false, timer);

        invincibleCooldown(deltaTime);

        onDeath();
    }

    protected void statusUpdate(){
        if(status.isEmpty()) return;
        statusDeleter.clear();
        for(String type : status.keySet()){
            statusAffect(type);
            status.get(type).setDuration(status.get(type).getDuration() - 1f);
            if(status.get(type).getDuration() <= 0){
                statusDeleter.add(type);
            }
        }

        for(String type : statusDeleter){
            if(type.contentEquals("CabbageTrapped")){
                canMove = true;
                outline = null;
            }
            status.remove(type);
            speedMultiplier.clear();
        }
    }

    private void statusAffect(String type){
        switch(type){
            case "Poison":
                takeDamage(2 * status.get(type).getLevel());
                break;
            case "Fungus Poison":
                takeDamage(3 * status.get(type).getLevel());
                speedMultiplier.add(Math.max(0.6f, 1f - status.get(type).getLevel() * 0.15f));
                break;
            case "CabbageTrapped":
                canMove = false;
                outline = screen.am.getTexture("CabbageParticle");
                break;
        }
    }

    protected void checkCollision() {
        if(screen.cCheck.checkTileX(this)) velocity.x = 0;
        if(screen.cCheck.checkTileY(this)) velocity.y = 0;

        if(screen.cCheck.checkEntityX(this, screen.totalEntity)) velocity.x = 0;
        if(screen.cCheck.checkEntityY(this, screen.totalEntity)) velocity.y = 0;

        if(screen.cCheck.checkPlayerX(this, screen.player)) velocity.x = 0;
        if(screen.cCheck.checkPlayerY(this, screen.player)) velocity.y = 0;

    }
    public void drawDebug(){
        shapeRenderer.setProjectionMatrix(screen.cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(hitbox.x + position.x, hitbox.y + position.y, hitbox.width, hitbox.height);
        shapeRenderer.end();
    }

    public void takeDamage(int amount){
        if(!isInvincible){
            if(dodgeRate != 0f){
                float chance = MathUtils.clamp(dodgeRate / 100f, 0f, 0.95f);
                if(MathUtils.randomBoolean(chance)){

                    int x = MathUtils.randomBoolean(0.5f) ? -32 : 32;
                    int y = MathUtils.randomBoolean(0.5f) ? -32 : 32;

                    screen.particle.create(1, position.x - (32 + x), position.y - (32 + y), screen, getSheet(), 0, 0, 0.2f, 64);
                    screen.am.playSound("Dodge");
                    return;
                }
            }

            int absorbed = Math.min(amount, defAmount);

            amount -= absorbed;
            defAmount -= absorbed;

            HP -= amount;
            hitSound();
            if(HP < 0) {
                isAlive = false;
            }
            isInvincible = true;
        }
    }

    protected void dropOnDeath(){
        screen.item.add(new Ammo(screen, position));
    }

    protected void hitSound(){
        screen.am.playSound("FungusHit");
    };

    public void takeForceDamage(int amount){
        if(dodgeRate != 0f){
            float chance = MathUtils.clamp(dodgeRate / 100f, 0f, 0.95f);
            if(MathUtils.randomBoolean(chance)){
                screen.particle.create(1, position.x - 64, position.y - 64, screen, getSheet(), 0, 0, 0.9f, 64);
                screen.am.playSound("Dodge");
                return;
            }
        }

        int absorbed = Math.min(amount, defAmount);

        amount -= absorbed;
        defAmount -= absorbed;

        HP -= amount;
        hitSound();
        if(HP < 0) {
            isAlive = false;
        }
    }

    protected void invincibleCooldown(float deltaTime){
        if(isInvincible){
            invincibleTime += deltaTime;
            if(invincibleTime >= 0.2f){
                invincibleTime -= 0.2f;
                isInvincible = false;
            }
        }
    }

    public boolean isAlive() {return isAlive;}

    public void affectStatus(String type, StatusEffect se){

        if(!status.containsKey(type)){
            status.putIfAbsent(type, new StatusEffect(se.getLevel() + 1, se.getMaxLevel(), se.getDuration() + 1f));
            statusAffect(type);
        }else{
            int level = status.get(type).getLevel();
            if(level < se.getMaxLevel()) level++;
            status.put(type, new StatusEffect(level, se.getMaxLevel(), se.getDuration() + 1f));
        }
    }
    protected void onDeath(){
       if(!isAlive && !isDeath) {

            if(!isScream){
                scream();
                isScream = true;
           }
            if(timer == 0){
                died();
            }
            timer += Gdx.graphics.getDeltaTime();
            if(timer >= 0.2f){
                timer = 0;
                boingH = Math.max(0, boingH - 0.4f);
                deathState++;
            }

            if(deathState >= 5){
                isDeath = true;
                dropOnDeath();
            }
       }
    }

    private void died() {
        screen.am.playSound("FungusDied");
    }

    protected void scream() {
    }

    protected void animationUpdate(boolean dashing, float timer) {
        if(dashing) return;

        timer += Gdx.graphics.getDeltaTime();

        if(timer >= boingTime){
            timer -= boingTime;

            if(isIncrease){
                boingH += boingCounter;
                boingW -= boingCounter;
            }else {
                boingH -= boingCounter;
                boingW += boingCounter;
            }

            if(boingH >= boingMax) {
                isIncrease = false;
            }else if(boingH <= boingMin) isIncrease = true;

        }
    }

    public boolean isDeath() { return isDeath; }

    public void forceMove(Vector2 direction){
        velocity.add(direction);
    }

    public void setPosition(float x, float y) { position.x = x; position.y = y; }
    public void setPosition(Vector2 pos) { position.x = pos.x; position.y = pos.y; }
    public void setVelocity(float x, float y){ velocity.x = x; velocity.y = y; }

    public void velocityGain(float scl){
        velocity.scl(scl);
    }

    public int getMaxHP() { return maxHP; }
    public void gainMaxHP(int amount) { maxHP += amount; HP = Math.min(HP + amount, maxHP); }

    public float getSpeed() { return speed; }
    public void gainSpeed(float amount) { speed += amount; }

    public float getDodgeRate() { return dodgeRate; }
    public void gainDodgeRate(int amount) { dodgeRate = Math.min( dodgeRate + amount, 95);}

    public int getDefAmount() { return defAmount; }
    public void gainDef(int amount) { defAmount = Math.min(defAmount + amount, 100); }

    protected String getSheet() { return null; };

    public void forceDead(){
        isAlive = false;
    }

    public Vector2 getVelocity() { return velocity; }
    public Rectangle getTrueHitbox(){ return new Rectangle(position.x + hitbox.x, position.y + hitbox.y, hitbox.width, hitbox.height); }
    public Vector2 getPosition() { return position; }

    public Rectangle getHitbox() { return hitbox;}

    public float getHP() { return HP; }

    public int getAmmoAmount() { return ammoAmount; }
    public void takeAmmo(int amount){
        ammoAmount -= amount;
    }
}
