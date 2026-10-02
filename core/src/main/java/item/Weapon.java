package item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import entity.Entity;
import entity.Player;
import io.github.clapt.ffProoj.GameScreen;
import projectile.Projectile;

public class Weapon extends Item{

    //Stats
    protected float fireRate, energyConsumption;
    protected int currentMagazine, magazineSize;
    protected float offsetX, offsetY;
    protected float speed;
    protected int damage;
    protected float accel;
    protected float reloadTime;
    protected float reload = 0;
    protected boolean isReload = false;

    //Other class
    protected Entity user;
    protected Vector2 shotDir;
    public boolean isActive = false;
    public boolean hasAbility = false;

    //Some attributes
    private float running = 0;
    protected boolean canShoot = true;

    //Texture
    protected Sprite sprite;

    protected Projectile typeProjectile;

    public Weapon(GameScreen screen){
        super(screen);
        hitbox = new Rectangle(0,0,32*2,32*2);
    }


    @Override
    public void PrimaryUse(){
        if(user instanceof Player){
            if(canShoot && currentMagazine > 0 && !isReload){
                canShoot = false;
                currentMagazine--;

                Vector3 mouseDir = screen.getMouseDir();
                shotDir.set(mouseDir.x - (user.getPosition().x + offsetX + 32 /2f) - 9 * 2, mouseDir.y - (user.getPosition().y + offsetY + 32 /2f) - 9 * 2);

                //Project pool
                playSound();
                if(!screen.unusingProjectiles.isEmpty()){
                    int index = screen.unusingProjectiles.removeFirst();

                    if(screen.projectiles[index].getClass() != typeProjectile.getClass()){
                        screen.projectiles[index] = typeProjectile.create();
                    }

                    screen.projectiles[index].fire(new Vector2(user.getPosition().x + offsetX  + 32 /2f, user.getPosition().y + offsetY + hitbox.y + 32 / 2f),shotDir, damage, speed, user);
                    screen.onUsing.add(index);
                }

            }
        }else{
            if(canShoot && currentMagazine > 0 && !isReload){
                canShoot = false;
                currentMagazine--;

                float predictTime;
                if(accel == 0){
                     predictTime =
                        user.getPosition().dst(screen.player.getPosition()) / speed;
                }else{
                    predictTime =
                        user.getPosition().dst(screen.player.getPosition()) / (speed * 0.1f + speed);
                }

                Vector2 tmp = screen.player.getPosition().cpy();
                tmp.add(screen.player.getHitbox().width/2f, screen.player.getHitbox().height/2f);
                tmp.mulAdd(screen.player.getVelocity(), predictTime);

                shotDir.set(tmp.x - (user.getPosition().x + user.getHitbox().width/2) - 9 * 2, tmp.y - (user.getPosition().y + user.getHitbox().height/2) - 9 * 2);
                playSound();

                //Project pool

                if(!screen.unusingProjectiles.isEmpty()){
                    int index = screen.unusingProjectiles.removeFirst();
                    if(screen.projectiles[index].getClass() != typeProjectile.getClass()){
                        screen.projectiles[index] = typeProjectile.create();
                    }
                    if(accel != 0){
                        screen.projectiles[index].fire(new Vector2(user.getPosition().x + user.getHitbox().width/2, user.getPosition().y + user.getHitbox().height/2),shotDir, damage, speed, user,accel);
                    }else screen.projectiles[index].fire(new Vector2(user.getPosition().x + user.getHitbox().width/2, user.getPosition().y + user.getHitbox().height/2),shotDir, damage, speed, user);
                    screen.onUsing.add(index);
                }

            }
        }
    }

    @Override
    public void update(){
        if(isReload){
            reload += Gdx.graphics.getDeltaTime();
            if(reload >= reloadTime){
                int amount = magazineSize - currentMagazine;
                amount = Math.min(amount, user.getAmmoAmount());
                currentMagazine += amount;
                user.takeAmmo(amount);
                reload -= reloadTime;
                isReload = false;
                playSoundReloadDone();
            }
        }

        if(!canShoot){
            running += Gdx.graphics.getDeltaTime();
            if(running >= fireRate){
                running = 0;
                canShoot = true;
            }
        }
        if(user != null){
            if(user instanceof Player){
                Vector2 temper = new Vector2(user.getPosition());
                temper.add(new Vector2(offsetX, offsetY));
                position.set(temper);

                Vector3 mouseDir = screen.getMouseDir();
                shotDir.set(mouseDir.x - (position.x + 32/2f) - 9 * 2, mouseDir.y - (position.y + 32 /2f) - 9 * 2);

                shotDir.nor();
                float angle = MathUtils.atan2(shotDir.y, shotDir.x);

                angle *= MathUtils.radiansToDegrees;

                boolean shouldFlip = angle > 90 || angle < -90;

                if(sprite.isFlipY() != shouldFlip)
                    sprite.flip(false, true);

                if(!isReload) {
                    sprite.setRotation(angle);

                }else sprite.rotate(50);
                sprite.setPosition(position.x, position.y);
            }
            else{
                Vector2 temper = new Vector2(user.getPosition());
                temper.add(new Vector2(offsetX, offsetY));
                position.set(temper);

            }
        }else{
            if(isJustDropped){
                if(screen.player.getPosition().dst(position) >= screen.tileSize * 1.2) isJustDropped = false;
            }
        }
    }
    public void reload() {
        if(!isReload){
            isReload = currentMagazine < magazineSize && user.getAmmoAmount() != 0;
            if(isReload) playSoundReload();
        }
    }
    protected void playSoundReloadDone(){}
    protected void playSoundReload() {}

    @Override
    public void draw() {
        sprite.draw(screen.main.batch);
    }


    public int getCurrentMagazine() { return currentMagazine; }

    protected void playSound(){};

    public String getName(){ return null;}
    public void setCurrentMagazine(int amount) { currentMagazine = amount; }

    public void dropped(){};
}
