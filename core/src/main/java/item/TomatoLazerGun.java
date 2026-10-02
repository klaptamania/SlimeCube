package item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import entity.Entity;

import entity.Player;
import io.github.clapt.ffProoj.GameScreen;

import java.util.ArrayList;

public class TomatoLazerGun extends Weapon{
    private static Texture weaponTexture, trail;
    private Sprite trailSprite;
    private float timer = 0;
    private final ArrayList<Entity> container = new ArrayList<>();

    public TomatoLazerGun(GameScreen screen, Entity user, int currentMagazine){
        super(screen);
        this.user = user;

        setDefaultStats(0,0, currentMagazine);
    }

    public TomatoLazerGun(GameScreen screen, float posX, float posY, int currentMagazine, boolean isJustDropped){
        super(screen);

        this.isJustDropped = isJustDropped;
        setDefaultStats(posX, posY, currentMagazine);
    }

    private void setDefaultStats(float posX, float posY, int currentMagazine) {
        //Stats declare
        magazineSize = 9;
        this.currentMagazine = magazineSize;
        if(currentMagazine != -1) this.currentMagazine = currentMagazine;
        energyConsumption = 50;
        fireRate = 0.4f;

        damage = 35;
        speed = 2500f;
        reloadTime = 0.8f;

        //Attributes declaration
        position = new Vector2();
        if (user != null) position.set(user.getPosition());
        else position.set(posX, posY);

        shotDir = new Vector2();

        offsetX = -10 * 2;
        offsetY = 0;

        //Texture
        if (weaponTexture == null) weaponTexture = screen.am.getTexture("TomatoLazerGun");
        if (trail == null) trail = screen.am.getTexture("TomatoTrail");

        idleD = new TextureRegion(weaponTexture);

        sprite = new Sprite(idleD);
        sprite.setSize(screen.tileSize, screen.tileSize);
        sprite.setOrigin(32, 32);
        sprite.setPosition(position.x, position.y);

        trailSprite = new Sprite(trail);

    }

    @Override
    public void PrimaryUse(){
        if(currentMagazine > 0){

            Vector3 mouseDir = screen.getMouseDir();
            shotDir.set(mouseDir.x - (position.x + 32) , mouseDir.y - (position.y + 32));

            trailSprite.setSize(shotDir.len(),32 *2);
            trailSprite.setOrigin(0, trailSprite.getHeight() / 2f);
            trailSprite.setPosition(position.x + 32f,position.y);

            float angle = MathUtils.atan2(shotDir.y, shotDir.x);
            angle *= MathUtils.radiansToDegrees;
            trailSprite.setRotation(angle);

            container.clear();
            screen.cCheck.checkBullet(position.x + 32f, position.y, mouseDir.x, mouseDir.y, screen.totalEntity, user, 16, container);

            if(!container.isEmpty()){
                for(Entity e : container){
                    e.takeDamage(15);
                    screen.particle.create(1, e.getPosition().x - 32, e.getPosition().y - 32, screen, "LaserParticle", 10, 200, 0.2f, 54);
                }
            }
            screen.particle.create(0, mouseDir.x - 64, mouseDir.y - 64, screen, "LaserParticle", 0, 0, 1.3f, 54);
            timer += Gdx.graphics.getDeltaTime();
            if(timer >= 0.2f){
                timer -= 0.4f;
                currentMagazine -= 1;
                screen.am.playSound("TomatoLaser");
            }
        }
    }
    @Override
    protected void playSoundReloadDone(){
        screen.am.playSound("GunReloadDone");
    }
    @Override
    protected void playSoundReload(){
        screen.am.playSound("GunReload");
    }
    @Override
    public void draw() {
        trailSprite.draw(screen.main.batch);
        trailSprite.setPosition(-999,-999);
        super.draw();
    }
    @Override
    public Texture getTexture() { return weaponTexture; }
    @Override
    public void onPickup(Player p){
        if(p.weapons[p.slotIndex] != null){
            p.weapons[p.slotIndex].dropped();
        }
        p.weapons[p.slotIndex] = new TomatoLazerGun(screen, p, currentMagazine);
        p.currentWeapon = p.weapons[p.slotIndex];
        p.currentWeapon.isActive = true;
        screen.am.playSound("WeaponPickup");
    }

    @Override
    public String getName(){
        return "Tomato Lazer Gun";
    }

    @Override
    public void dropped(){
        screen.item.add(new TomatoLazerGun(screen, position.x, position.y, screen.player.currentWeapon.getCurrentMagazine(), true));
        screen.player.weapons[screen.player.slotIndex] = null;
    }
}
