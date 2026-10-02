package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.AfterImage;
import io.github.clapt.ffProoj.GameScreen;
import item.*;
import projectile.StatusEffect;
import utilityTool.HitDetect;

import java.util.ArrayList;

public class Player extends Entity{

    //Attributes
    float friction = 0.7f;

    float accel = 1f;
    float dashingTime = 0.2f;
    float dashCoolDown = 0.9f;
    boolean canDash = true;
    boolean running = false;
    boolean dashing = false;

    //cameraStuff
    float deadZoneHeight;
    float deadZoneWidth;

    float left;
    float right;
    float bottom;
    float top;


    //timer
    float[] timer = new float[10];
    int DASHING = 0;
    int DASHCOOLDOWN = 1;
    int BOINGTIME = 2;
    int AFTERIMAGE = 3;
    int STATUS = 4;
    int SOUNDWALK = 5;
    int HEALING = 6;

    //player stuff
    public Vector2 lastDirection = new Vector2(0,-1);
    private final ArrayList<AfterImage> afI = new ArrayList<>();
    private boolean healing = false;
    private boolean isPlayed = false;
    private boolean isHealing = false;

    public Weapon[] weapons = new Weapon[2];
    public int slotIndex = 0;

    public Player(GameScreen screen){
        super(screen);
        setDefaultStats();
    }

    private void setDefaultStats(){

        //Vector
        velocity = new Vector2(0,0);
        direction = new Vector2(0,0);
        position = new Vector2(12 * screen.tileSize,7 * screen.tileSize);

        //Hitbox
        hitbox = new Rectangle(3 * 2, 3 * 2, 26 * 2 , 26 * 2);

        //CameraStuff
        deadZoneHeight = screen.vp.getWorldHeight() * 0.5f;
        deadZoneWidth = screen.vp.getWorldWidth() * 0.4f;

        //Textures
        sheet = screen.am.getTexture("Player");
        isPlayer = true;

        current = new TextureRegion(sheet);

        //Attributes
        speed = 125;
        HP = 100;
        maxHP = HP;

        if(screen.main.type != 3){
            weapons[slotIndex] = null;
            ammoAmount = 24;
        }else{
            weapons[slotIndex] = new PeaGun(screen,this, -1);
            weapons[slotIndex].isActive = true;
            ammoAmount = 50;
        }
        currentWeapon = weapons[slotIndex];
    }
    @Override
    protected String getSheet() { return "Player"; }
    @Override
    public void update(){

        if(screen.hud.type == 0) return;

        float deltaTime = Gdx.graphics.getDeltaTime();
        if(!isDeath){
            personalUpdate(deltaTime);
            input(deltaTime);
            super.animationUpdate(dashing, timer[BOINGTIME]);

            cameraUpdate();

            afterImageUpdate();

            if(weapons[0] != null && weapons[0].isActive) weapons[0].update();
            if(weapons[1] != null && weapons[1].isActive) weapons[1].update();

            super.invincibleCooldown(deltaTime);

            if(currentWeapon != null) screen.hud.updateAmmo("AMMO: " + currentWeapon.getCurrentMagazine() + " / " + ammoAmount);
        }

        super.onDeath();
        if(isDeath){
            currentWeapon = null;
        }

    }

    private void personalUpdate(float deltaTime) {
        if(!status.isEmpty()){
            timer[STATUS] += deltaTime;
            if(timer[STATUS] >= 1f){
                speedMultiplier.clear();
                timer[STATUS] -= 1f;
                statusUpdate();
            }
        }
        if(isHealing){
            if(!healing){
                if(!isPlayed){
                    screen.am.playSound("ReadyHeal");
                    isPlayed = true;
                }

                timer[HEALING] += deltaTime;
                if(timer[HEALING] >= 0.5f){
                    timer[HEALING] = 0;
                    healing = true;
                }
            }else{
                screen.particle.create(1, position.x, position.y,screen,"HealingParticle",0,55,2f, 64);
                screen.am.playSound("HealingDone");

                int amount = maxHP - (int)HP;
                if(ammoAmount >= amount){
                    ammoAmount -= amount;
                }else{
                    amount = ammoAmount;
                    ammoAmount = 0;
                }

                HP += amount;

                HP = Math.min(HP, maxHP);
                healing = false;
                isPlayed = false;
                isHealing = false;
            }
        }
    }

    private void afterImageUpdate() {
        if(!afI.isEmpty()){
            for(int i = afI.size() - 1; i >= 0; i--){
                afI.get(i).alpha -= Gdx.graphics.getDeltaTime() * 4f;

                if(afI.get(i).alpha <= 0f) afI.remove(i);
            }
        }
    }

    //    private void cameraUpdate() {
//
//        updateDeadZone();
//
//        float targetX = screen.cam.position.x;
//        float targetY = screen.cam.position.y;
//
//        if(position.x < left) targetX = position.x + deadZoneWidth/2f;
//        else if(position.x > right) targetX = position.x - deadZoneWidth/2f;
//
//        if(position.y < bottom) targetY = position.y + deadZoneHeight/2f;
//        else if(position.y > top) targetY = position.y - deadZoneHeight/2f;
//
//        screen.cam.position.x += (targetX - screen.cam.position.x) * 0.125f;
//        screen.cam.position.y += (targetY - screen.cam.position.y) * 0.125f;
//
//        screen.cam.update();
//    }
    public void cameraUpdate() {


        float halfWidth = screen.cam.viewportWidth * screen.cam.zoom / 2f;
        float halfHeight = screen.cam.viewportHeight * screen.cam.zoom / 2f;

        updateDeadZone();

        float targetX = screen.cam.position.x;
        float targetY = screen.cam.position.y;

        if(position.x < left) targetX = position.x + deadZoneWidth / 2f;
        else if(position.x > right) targetX = position.x - deadZoneWidth / 2f;

        if(position.y < bottom) targetY = position.y + deadZoneHeight / 2f;
        else if(position.y > top) targetY = position.y - deadZoneHeight / 2f;

        screen.cam.position.x += (targetX - screen.cam.position.x) * 0.125f;
        screen.cam.position.y += (targetY - screen.cam.position.y) * 0.125f;

        screen.cam.position.x = MathUtils.clamp(
            screen.cam.position.x,
            halfWidth,
            Math.max(halfWidth, screen.worldWidth - halfWidth)
        );

        screen.cam.position.y = MathUtils.clamp(
            screen.cam.position.y,
            halfHeight,
            Math.max(halfHeight, screen.worldHeight - halfHeight)
        );

        screen.cam.update();
    }

    private void updateDeadZone() {
        left   = screen.cam.position.x - deadZoneWidth / 2f;
        right  = screen.cam.position.x + deadZoneWidth / 2f;
        bottom = screen.cam.position.y - deadZoneHeight / 2f;
        top    = screen.cam.position.y + deadZoneHeight / 2f;
    }

    private void input(float deltaTime) {
        //DASH
        if(canDash && Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) && timer[DASHCOOLDOWN] <= 0 && screen.currentState == GameScreen.state.GAME && canMove){
            startDash();
        }

        if(dashing){
            dashing(deltaTime);
        }
        if(running){
            running();
        }

        //Change Weapon
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) && screen.currentState == GameScreen.state.GAME && slotIndex != 0){
            slotIndex = 0;
            currentWeapon = weapons[slotIndex];

            screen.hud.changeSlot(slotIndex);
            screen.am.playSound("WeaponChange");
            if(currentWeapon != null && !currentWeapon.isActive) currentWeapon.isActive = true;
            if(weapons[slotIndex + 1] != null && !weapons[slotIndex + 1].hasAbility) weapons[slotIndex + 1].isActive = false;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) && screen.currentState == GameScreen.state.GAME && slotIndex != 1){
            slotIndex = 1;
            currentWeapon = weapons[slotIndex];

            screen.hud.changeSlot(slotIndex);
            screen.am.playSound("WeaponChange");
            if(currentWeapon != null && !currentWeapon.isActive) currentWeapon.isActive = true;
            if(weapons[slotIndex - 1] != null && !weapons[slotIndex - 1].hasAbility) weapons[slotIndex - 1].isActive = false;
        }

        //SHOT
        if(Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT) && screen.currentState == GameScreen.state.GAME && !isHealing)
            if(currentWeapon != null) currentWeapon.SecondaryUse();

        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT) && screen.currentState == GameScreen.state.GAME && !isHealing){
            if(currentWeapon != null ) fire();
            buttonLeftHolding = true;
        }else buttonLeftHolding = false;

        //RELOAD
        if(Gdx.input.isKeyJustPressed(Input.Keys.R) && screen.currentState == GameScreen.state.GAME && !isHealing) {
            if(currentWeapon != null) {
                currentWeapon.reload();
            }
        }

        //HEAL
        if(Gdx.input.isKeyJustPressed(Input.Keys.F) && screen.currentState == GameScreen.state.GAME){
            heal();
        }

        //?
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            screen.main.saveLoad.save(screen);
        }

        if (!dashing && (screen.currentState == GameScreen.state.GAME || screen.currentState == GameScreen.state.SHOP || screen.main.type == 1)) {
            moving(deltaTime);
        }

        cooldown(deltaTime);
    }

    private void heal() {
        if(ammoAmount > 0 && HP < maxHP){
           isHealing = true;
        }
    }

    private void fire() {
        currentWeapon.PrimaryUse();
    }

    private void cooldown(float deltaTime) {
        if(timer[DASHCOOLDOWN] > 0) {
            timer[DASHCOOLDOWN] -= deltaTime;
        }
        if(timer[DASHCOOLDOWN] <=0 && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) canDash = true;
    }

    private void running() {
        accel = 1.5f;
        boingMax = 1.2f;
        boingMin = 0.6f;
        boingCounter = 0.08f;
        if(!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            accel = 1f;
            running = false;
            boingMax = 1.1f;
            boingMin = 0.9f;
            boingCounter = 0.018f;
        }
    }

    private void dashing(float deltaTime) {
        timer[DASHING] -= deltaTime;
        timer[AFTERIMAGE] += deltaTime;

        velocity.set(lastDirection.x * speed * 5f, lastDirection.y * speed * 5f);

        boingW = 1.4f;
        boingH = 0.7f;

        if(timer[AFTERIMAGE] >= 0.05f){
            timer[AFTERIMAGE] = 0;

            AfterImage tmp = new AfterImage(position.cpy(), 1f, boingW, boingH);
            afI.add(tmp);
        }

        checkCollision();

        position.mulAdd(velocity,deltaTime);

        if(timer[DASHING] <= 0f){
            dashing = false;
            running = true;
            boingH = 0.8f;
            boingW = 1.2f;
        }
    }

    private void startDash() {
        canDash = false;
        dashing = true;
        timer[DASHING] = dashingTime;
        timer[DASHCOOLDOWN] = dashCoolDown;
        boingH = 0.7f;
        boingW = 1.3f;
    }

    private void moving(float deltaTime) {

        direction.setZero();

        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            direction.y++;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            direction.y--;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            direction.x--;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            direction.x++;
        }

        if(direction.len2() > 0){
            direction.nor();
        }

        if(direction.x != 0f || direction.y != 0f) lastDirection.set(direction);

        totalSpeed = 0;

        totalSpeed += speed * accel;

        velocity.x += direction.x * totalSpeed;
        velocity.y += direction.y * totalSpeed;

        for(float s : speedMultiplier){
            velocity.scl(s);
        }

        checkCollision();

        if(!velocity.isZero()){
            timer[SOUNDWALK] += deltaTime;
            if(timer[SOUNDWALK] >= 0.4f){
                timer[SOUNDWALK] = 0;
                screen.am.playSound("Walking");
            }
        }

        if(velocity.len() >= 1000){
            HitDetect h = screen.cCheck.DDA_Boarding(this);
            if (h.hit) {
                HitDetect hit = screen.cCheck.sweptAABB(position.x, position.y, hitbox.width, hitbox.height, velocity.cpy(), h.tileX * screen.tileSize, h.tileY * screen.tileSize, screen.tileSize, screen.tileSize);
                if(hit.hit){
                    float remainingTime = deltaTime * (hit.entryTime - 0.001f);
                    position.mulAdd(h.velo, remainingTime);
                }

            }else{
                if(canMove) position.mulAdd(velocity,deltaTime);
            }
        }else{
            if(canMove) position.mulAdd(velocity,deltaTime);
        }

        velocity.scl(friction);

        if(velocity.len2() < 1f){
            velocity.setZero();
        }
    }

    @Override
    public void checkCollision() {

        if(normalCollisionCheck) {
            if(screen.cCheck.checkTileX(this)) velocity.x = 0;
            if(screen.cCheck.checkTileY(this)) velocity.y = 0;
        }
        else{
            screen.cCheck.checkTileHighSpeed(this);
        }


        screen.cCheck.sweptCheckEntity(this);

        int i = screen.cCheck.checkItem(screen.item, this);
        if(i != -1 && !screen.item.get(i).isJustDropped){
            screen.item.get(i).onPickup(this);
            if(weapons[slotIndex] != null && screen.item.get(i) instanceof Weapon) screen.hud.updateWeapon(currentWeapon.getTexture(), slotIndex);
            screen.item.remove(i);
        }
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
    public void takeDamage(int amount){
        if(!dashing) super.takeDamage(amount);
    }
    @Override
    public void takeForceDamage(int amount){
        if(!dashing) super.takeForceDamage(amount);
    }

    @Override
    public void affectStatus(String type, StatusEffect se){
        if(!dashing) super.affectStatus(type,se);
    }

    @Override
    protected void scream(){
        screen.am.playSound("PlayerDied");
    }

    public boolean isDashing() { return dashing; }

    public void setHP(int amount){ HP = amount;}
    public void setAmmo(int amount) { ammoAmount = amount; }
}
