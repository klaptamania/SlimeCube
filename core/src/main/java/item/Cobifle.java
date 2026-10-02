package item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import entity.Entity;
import entity.Player;
import io.github.clapt.ffProoj.GameScreen;
import projectile.Corn;

public class Cobifle extends Weapon{
    private static Texture weaponTexture;

    public Cobifle(GameScreen screen, Entity user, int currentMagazine){
        super(screen);
        this.user = user;

        setDefaultStats(0,0, currentMagazine);
    }

    public Cobifle(GameScreen screen, float posX, float posY, int currentMagazine, boolean isJustDropped){
        super(screen);

        this.isJustDropped = isJustDropped;
        setDefaultStats(posX, posY, currentMagazine);
    }

    private void setDefaultStats(float posX, float posY, int currentMagazine) {
        //Stats declare
        magazineSize = 30;
        this.currentMagazine = magazineSize;
        if(currentMagazine != -1) this.currentMagazine = currentMagazine;
        energyConsumption = 50;
        fireRate = 0.07f;

        damage = 45;
        speed = 2800f;
        reloadTime = 1f;

        //Attributes declaration
        position = new Vector2();
        if(user != null) position.set(user.getPosition());
        else position.set(posX, posY);

        shotDir = new Vector2();

        offsetX = -10 * 2; offsetY = 0;

        //Texture
        if(weaponTexture == null) weaponTexture = screen.am.getTexture("Cobifle");
        idleD = new TextureRegion(weaponTexture);

        sprite = new Sprite(idleD);
        sprite.setSize(screen.tileSize,screen.tileSize);
        sprite.setOriginCenter();
        sprite.setPosition(position.x, position.y);

        typeProjectile = new Corn(screen);
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
        p.weapons[p.slotIndex] = new Cobifle(screen, p, currentMagazine);
        p.currentWeapon = p.weapons[p.slotIndex];
        screen.am.playSound("WeaponPickup");
    }

    @Override
    public String getName(){
        return "Cobifle";
    }

    @Override
    public void dropped(){
        screen.item.add(new Cobifle(screen, position.x, position.y, screen.player.currentWeapon.getCurrentMagazine(), true));
        screen.player.weapons[screen.player.slotIndex] = null;
    }
}
