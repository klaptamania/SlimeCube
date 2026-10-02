package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.GameScreen;
import item.Watermelon;
import utilityTool.HitDetect;

public class BOSS_Watermelon extends Entity{
    enum Action{MACHINE_GUN, ROLLER_COASTER, NONE, CHILL};

    private static Texture sheet;

    private Action currentAction = Action.NONE;

    private float delay = 0, running = 0, rested = 0;
    private final float[] timer = new float[10];

    //MachineGun Stuff
    private boolean playSound = false, canShot = false;

    public boolean shooting = false;

    //Cart stuff
    private boolean isChanging = true, cartPlayed = false, onFriction = false;

    public BOSS_Watermelon(GameScreen screen, int entityIndex){
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
        if(sheet == null) sheet = screen.am.getTexture("Watermelon");
        current = new TextureRegion(sheet);

        //Attributes
        speed = 45;
        HP = 100;
        maxHP = HP;
        defAmount = 25;
        dodgeRate = 10;

        currentWeapon = new Watermelon(screen, this, 50);
        ammoAmount = 100000;

    }

    @Override
    public void update(){
        float deltaTime = Gdx.graphics.getDeltaTime();
        if(canMove)super.update();
        else {
            super.animationUpdate(false, super.timer);
            invincibleCooldown(deltaTime);
        }

        if(rested > 0){
            rested -= deltaTime;
        }{
            skillSelection();
        }

        skillActivation(deltaTime);

        skillCooldown(deltaTime);


        if(onFriction){
            velocity.scl(0.9f);

            HitDetect hit = screen.cCheck.DDA_Boarding(this);

            if (hit.hit) {
                float remainingTime = deltaTime * (1f - hit.entryTime);
                position.mulAdd(hit.velo, remainingTime);

                float dot = velocity.dot(hit.hitDetect);
                velocity.sub(2f * dot * hit.hitDetect.x, 2f * dot * hit.hitDetect.y);

                direction.set(velocity).nor();
                screen.am.playSound("Squash");
                screen.particle.create(1, position.x, position.y, screen, "WatermelonBreak", 0, 200, 0.5f, screen.tileSize * 1.5f);

            } else {
                position.mulAdd(velocity, deltaTime);
            }

            if(velocity.len2() <= 1f){
                velocity.setZero();
                onFriction = false;
                canMove = true;
                currentAction = Action.NONE;
            }
        }

        currentWeapon.update();
    }

    private void skillCooldown(float deltaTime) {
        for(int i = 0; i < timer.length; i++){
            if(timer[i] > 0){
                timer[i] = MathUtils.clamp(timer[i] - deltaTime, 0, timer[i]);
            }
        }
    }

    private void skillActivation(float deltaTime) {
        switch(currentAction){
            case NONE:
                break;
            case MACHINE_GUN:
                machineGunFiring(deltaTime);
                break;
            case ROLLER_COASTER:
                CARRTT(deltaTime);
                break;
        }

    }

    private void CARRTT(float deltaTime) {

        canMove = false;

        running += deltaTime;
        if(running >= 4f){
            onFriction = true;
            running -= 4f;
            currentAction = Action.CHILL;
            isChanging = true;
            timer[1] = 5f;
            rested = 6f;
            cartPlayed = false;
            return;
        }

        if(isChanging){
            direction = screen.player.getPosition().cpy();
            direction.sub(position).nor();
            isChanging = false;
            if(!cartPlayed){
                cartPlayed = true;
                screen.am.playSound("Cart");
            }
        }

        velocity.x += direction.x * 1000 * deltaTime;
        velocity.y += direction.y * 1000 * deltaTime;

        if (velocity.len() > 2700) {
            velocity.nor().scl(7000);
        }

        //Check hit entity
        for(Entity e : screen.totalEntity){
            if(e == this) continue;
            HitDetect hit = screen.cCheck.sweptAABB(position.x, position.y, hitbox.width, hitbox.height, velocity.cpy(), e.getPosition().x, e.getPosition().y, e.getHitbox().width, e.getHitbox().height);
            if(hit.hit){
                //Move to the closet edge
                float remainingTime = deltaTime * (hit.entryTime - 0.001f);
                position.mulAdd(velocity, remainingTime);

                //Calc the force

                float force = velocity.len();

                e.forceMove(direction.scl(force * 1.5f));

                float dot = velocity.dot(hit.hitDetect);
                velocity.sub(2f * dot * hit.hitDetect.x, 2f * dot * hit.hitDetect.y);

                direction.set(velocity).nor();
                screen.am.playSound("Squash");
                e.takeDamage((int)(force * 0.008f));
                screen.particle.create(1, position.x, position.y, screen, "WatermelonBreak", 1, 200, 0.5f, screen.tileSize * 1.5f);
            }
        }

        // checkHighSpeedBouncing
        HitDetect hit = screen.cCheck.DDA_Boarding(this);

        if (hit.hit) {
            float remainingTime = deltaTime * (1f - hit.entryTime);
            position.mulAdd(hit.velo, remainingTime);

            float dot = velocity.dot(hit.hitDetect);
            velocity.sub(2f * dot * hit.hitDetect.x, 2f * dot * hit.hitDetect.y);

            direction.set(velocity).nor();
            screen.am.playSound("Squash");
            screen.particle.create(1, position.x, position.y, screen, "WatermelonBreak", 0, 200, 0.5f, screen.tileSize * 1.5f);

        } else {
            position.mulAdd(velocity, deltaTime);
        }

    }

    private void machineGunFiring(float deltaTime) {
        canMove = false;
        //reset boolean when firing done
        if(currentWeapon.getCurrentMagazine() <= 0){
            canMove = true;
            currentWeapon.reload();
            playSound = false;
            shooting = false;
            canShot = false;
            currentAction = Action.NONE;
            rested = 4f;

            timer[0] = 2.f;
        }

        //sound
        if(shooting && !playSound){
            playSound = true;
            screen.am.playSound("WatermelonShooting");
        }

        //shot
        if(shooting){
            shooting(deltaTime);
        }
    }

    private void shooting(float deltaTime) {
        //Delay before firing
        if(!canShot){
            delay += deltaTime;
            if(delay >= 0.61f){
                canShot = true;
                delay = 0;
            }
        }

        if(canShot){
            currentWeapon.PrimaryUse();
        }
    }

    private void skillSelection(){

        if(currentAction != Action.NONE) return;
        if(rested > 0) return;

        int rate = MathUtils.random(1, 100);

        if(rate >= 1 && rate < 35){
            //Machine Gun firing
            if(isAlive && timer[0] <= 0){
                currentAction = Action.MACHINE_GUN;
                shooting = true;
            }
        }else if(rate >= 35 && rate < 75){
            //Roller Coaster
            if(isAlive && timer[1] <= 0){
                currentAction = Action.ROLLER_COASTER;
            }
        }
//        if(isAlive && timer[1] <= 0){
//                System.out.println("Vaicac");
//                currentAction = Action.ROLLER_COASTER;
//        }
    }

    @Override
    protected String getSheet(){ return "Watermelon"; }

}

