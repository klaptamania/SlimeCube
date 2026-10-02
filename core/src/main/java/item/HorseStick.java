package item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import entity.Entity;
import entity.Player;
import io.github.clapt.ffProoj.GameScreen;

public class HorseStick extends Weapon{
    private static Texture weaponTexture;
    private boolean played = false, activated = false;
    private float stacks = 0, time = 0;
    private Vector2 target = new Vector2();

    public HorseStick(GameScreen screen, Entity user, int currentMagazine){
        super(screen);
        this.user = user;

        setDefaultStats(0,0, currentMagazine);
    }

    public HorseStick(GameScreen screen, float posX, float posY, int currentMagazine, boolean isJustDropped){
        super(screen);

        this.isJustDropped = isJustDropped;
        setDefaultStats(posX, posY, currentMagazine);
    }
    private void setDefaultStats(float posX, float posY, int currentMagazine) {
        //Stats declare
        magazineSize = 1;
        this.currentMagazine = magazineSize;
        if(currentMagazine != -1) this.currentMagazine = currentMagazine;
        fireRate = 0.2f;

        damage = 50;
        reloadTime = 0.01f;

        hasAbility = true;

        //Attributes declaration
        position = new Vector2();
        if(user != null) position.set(user.getPosition());
        else position.set(posX, posY);

        shotDir = new Vector2();

        offsetX = -10 * 2; offsetY = -5 * 2;
        direction = new Vector2();

        //Texture
        if(weaponTexture == null) weaponTexture = screen.am.getTexture("HorseStick");
        idleD = new TextureRegion(weaponTexture);

        sprite = new Sprite(idleD);
        sprite.setSize(screen.tileSize,screen.tileSize);
        sprite.setOriginCenter();
        sprite.setPosition(position.x, position.y);

        typeProjectile = new projectile.WorldSlash(screen);
    }
    @Override
    public void update(){
        super.update();
        if(user == null) return;
        sprite.setPosition(user.getPosition().x + 32 - sprite.getWidth()/2f, user.getPosition().y + 32 - sprite.getHeight()/2f);
        if(activated){
            user.velocityGain(speed);
            user.normalCollisionCheck = false;

            if(!played){
                if(((speed - 1)/0.2f) > 2) screen.am.playSound("HorseStick");
                else screen.am.playSound("WindBreak");
                played = true;
            }

            if(!screen.cCheck.checkTileHighSpeed(user)){
                int i = screen.cCheck.checkEntitySpeed(user,screen.totalEntity);
                if(i != -999){
                    target.set(screen.totalEntity.get(i).getPosition().cpy());

                    direction.set(target.cpy().sub(position.cpy())).nor();
                    screen.totalEntity.get(i).forceMove(direction.cpy().scl(user.getVelocity().cpy().len2() * stacks));

                    screen.particle.create(3 * screen.tileSize, target.x, target.y, screen, "SlimeParticle", 35, 400, 0.4f);

                    screen.totalEntity.get(i).takeDamage((int)(((speed - 1)/0.2) * 40));
                    activated = false;
                    played = false;
                }
            }else{
                user.velocityGain(0);
                System.out.println("Hitt");
                activated = false;
                played = false;
                user.normalCollisionCheck = true;
            }

            time+= Gdx.graphics.getDeltaTime();
            if(time >= 2 + ((speed - 1)/0.2f) ){
                time = 0;
                activated = false;
                played = false;
                user.normalCollisionCheck = true;
                if(!(user.currentWeapon instanceof HorseStick)){
                    isActive = false;
                }
            }

        }
    }

    @Override
    public void PrimaryUse(){
        if(stacks > 0 && !activated){
            speed = 1 + stacks * 0.2f;
            stacks = 0;
            activated = true;
            isActive = true;
        }
    }
    @Override
    public void SecondaryUse(){
        if(user.getAmmoAmount() > 0 && !activated){
            if(stacks < 5){
                stacks += 1;
                user.takeAmmo(1);
                screen.am.playSound("StackLoad");
            }else screen.am.playSound("StacksFull");
        }
    }

    @Override
    public Texture getTexture() { return weaponTexture; }
    @Override
    public void onPickup(Player p){
        if(p.weapons[p.slotIndex] != null){
            p.weapons[p.slotIndex].dropped();
        }
        p.weapons[p.slotIndex] = new HorseStick(screen, p, currentMagazine);
        p.currentWeapon = p.weapons[p.slotIndex];
        screen.am.playSound("WeaponPickup");
        p.currentWeapon.isActive = true;
    }

    @Override
    public String getName(){
        return "HorseStick";
    }

    @Override
    public void dropped(){
        screen.item.add(new HorseStick(screen, position.x, position.y, screen.player.currentWeapon.getCurrentMagazine(), true));
        screen.player.weapons[screen.player.slotIndex] = null;
    }
}
