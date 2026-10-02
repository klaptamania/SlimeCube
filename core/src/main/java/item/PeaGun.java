package item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import entity.Entity;
import entity.Player;
import io.github.clapt.ffProoj.GameScreen;
import projectile.Pea;

public class PeaGun extends Weapon{

    private static Texture weaponTexture;

    public PeaGun(GameScreen screen, Entity user, int currentMagazine){
        super(screen);
        this.user = user;

        setDefaultStats(0,0, currentMagazine);
    }

    public PeaGun(GameScreen screen, float posX, float posY, int currentMagazine, boolean isJustDropped){
        super(screen);

        this.isJustDropped = isJustDropped;
        setDefaultStats(posX, posY, currentMagazine);
    }

    private void setDefaultStats(float posX, float posY, int currentMagazine) {
        //Stats declare
        magazineSize = 12;
        this.currentMagazine = magazineSize;

        if(currentMagazine != -1) this.currentMagazine = currentMagazine;

        energyConsumption = 50;
        fireRate = 0.4f;

        damage = 35;
        speed = 2500f;
        reloadTime = 0.8f;

        //Attributes declaration
        position = new Vector2();
        if(user != null) position.set(user.getPosition());
        else position.set(posX, posY);

        shotDir = new Vector2();

        offsetX = -10 * 2; offsetY = 0;

        //Texture
        if(weaponTexture == null) weaponTexture = screen.am.getTexture("PeaGun");
        idleD = new TextureRegion(weaponTexture);

        sprite = new Sprite(idleD);
        sprite.setSize(screen.tileSize,screen.tileSize);
        sprite.setOrigin(32,32);
        sprite.setPosition(position.x, position.y);

        typeProjectile = new Pea(screen);
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
    protected void playSound(){
        screen.am.playSound("PeaShoot");
    }
    @Override
    public Texture getTexture() { return weaponTexture; }
    @Override
    public void onPickup(Player p){
        if(p.weapons[p.slotIndex] != null){
            p.weapons[p.slotIndex].dropped();
        }
        p.weapons[p.slotIndex] = new PeaGun(screen, p, currentMagazine);
        p.currentWeapon = p.weapons[p.slotIndex];
        p.currentWeapon.isActive = true;
        screen.am.playSound("WeaponPickup");
    }

    @Override
    public String getName(){
        return "PeaGun";
    }

    @Override
    public void dropped(){
        screen.item.add(new PeaGun(screen, position.x, position.y, screen.player.currentWeapon.getCurrentMagazine(), true));
        screen.player.weapons[screen.player.slotIndex] = null;
    }
}
