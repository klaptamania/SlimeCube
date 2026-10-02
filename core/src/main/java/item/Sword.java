package item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import entity.Entity;
import entity.Player;
import io.github.clapt.ffProoj.GameScreen;


public class Sword extends Weapon{
    private static Texture weaponTexture;
    private static Texture trail;
    private Sprite trailSprite;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final float range = 160;
    private final float attackAngle = 180;
    private final float threshHold = MathUtils.cosDeg(attackAngle /2f);
    Vector2 toEnemy = new Vector2();
    private boolean isRightHolding = false, rightActivated = false;

    private float delayed = 0;

    public Sword(GameScreen screen, Entity user, int currentMagazine){
        super(screen);
        this.user = user;

        setDefaultStats(0,0, currentMagazine);
    }

    public Sword(GameScreen screen, float posX, float posY, int currentMagazine, boolean isJustDropped){
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

        //Attributes declaration
        position = new Vector2();
        if(user != null) position.set(user.getPosition());
        else position.set(posX, posY);

        shotDir = new Vector2();

        offsetX = -10 * 2; offsetY = -5 * 2;
        direction = new Vector2();

        //Texture
        if(weaponTexture == null) weaponTexture = screen.am.getTexture("Sword");
        if(trail == null) trail = screen.am.getTexture("SwordTrail");
        idleD = new TextureRegion(weaponTexture);

        sprite = new Sprite(idleD);
        sprite.setSize(screen.tileSize,screen.tileSize);
        sprite.setOriginCenter();
        sprite.setPosition(position.x, position.y);

        trailSprite = new Sprite(new TextureRegion(trail));
        trailSprite.setSize(160 * 1.7f, 160 * 1.7f);
        trailSprite.setOriginCenter();

        typeProjectile = new projectile.WorldSlash(screen);
    }

    @Override
    public void update(){
        super.update();
        if(rightActivated){
            if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)){
                if(!isRightHolding){
                    delayed += Gdx.graphics.getDeltaTime();
                    if(delayed >= 0.4f){
                        delayed -= 0.4f;
                        isRightHolding = true;
                        screen.am.playSound("SwordSheathed");
                    }
                }
            }else{
                rightActivated = false;
                delayed = 0;
            }
        }
    }

    @Override
    public void draw() {
        super.draw();
        trailSprite.draw(screen.main.batch);
        trailSprite.setPosition(-999,-999);
    }
    @Override
    public void drawDebug(){
        shapeRenderer.setProjectionMatrix(screen.cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.arc(
            user.getPosition().x + 32,
            user.getPosition().y + 32,
            range,
            direction.angleDeg() - attackAngle/2f,
            attackAngle
        );

        shapeRenderer.end();
    }

    @Override
    public void PrimaryUse(){
        if(user.buttonLeftHolding) return;

        Vector3 mouseDir = screen.getMouseDir();
        direction.set(mouseDir.x - (user.getPosition().x + 32), mouseDir.y - (user.getPosition().y + 32)).nor();

        float angle = MathUtils.atan2(direction.y, direction.x);
        angle *= MathUtils.radiansToDegrees;
        trailSprite.setRotation(angle);
        trailSprite.setPosition(user.getPosition().x + 32 - trailSprite.getWidth()/2f, user.getPosition().y + 32 - trailSprite.getHeight()/2f);

        if(!isRightHolding){
            if(!rightActivated){
                screen.am.playSound("SwordSlash");

                for(Entity e : screen.totalEntity){
                    if(e == user) continue;

                    toEnemy.set(e.getPosition().x + 32 - (user.getPosition().x + 32), e.getPosition().y + 32 - (user.getPosition().y + 32));

                    if(toEnemy.len2() <= range * range){
                        toEnemy.nor();

                        if(direction.dot(toEnemy) >= threshHold){
                            e.takeDamage(damage);
                            screen.particle.create(1,e.getPosition().x - 64, e.getPosition().y - 64, screen,"SwordParticle", 0, 15, 0.23f, 32 * 2);

                        }
                    }
                }
            }

        }else{
            isRightHolding = false;
            rightActivated = false;
            screen.am.playSound("SwordSlash");
            if(user.getAmmoAmount() >= 10){
                delayed = 0;
                screen.am.playSound("SwordUnsheathed");
                if(!screen.unusingProjectiles.isEmpty()){
                    int index = screen.unusingProjectiles.removeFirst();

                    if(screen.projectiles[index].getClass() != typeProjectile.getClass()){
                        screen.projectiles[index] = typeProjectile.create();
                    }

                    screen.projectiles[index].fire(user.getPosition().cpy().add(32,32), direction, 102, 1000, user);
                    screen.onUsing.add(index);
                }

                user.takeAmmo(10);
            }else{
                for(Entity e : screen.totalEntity){
                    if(e == user) continue;

                    toEnemy.set(e.getPosition().x + 32 - (user.getPosition().x + 32), e.getPosition().y + 32 - (user.getPosition().y + 32));

                    if(toEnemy.len2() <= range * range){
                        toEnemy.nor();

                        if(direction.dot(toEnemy) >= threshHold){
                            e.takeDamage(damage);
                            screen.particle.create(1,e.getPosition().x - 64, e.getPosition().y - 64, screen,"SwordParticle", 0, 15, 0.23f, 32 * 2);

                        }
                    }
                }
            }
        }
    }

    @Override
    public void SecondaryUse(){
        rightActivated = true;
    }

    @Override
    public Texture getTexture() { return weaponTexture; }
    @Override
    public void onPickup(Player p){
        if(p.weapons[p.slotIndex] != null){
            p.weapons[p.slotIndex].dropped();
        }
        p.weapons[p.slotIndex] = new Sword(screen, p, currentMagazine);
        p.currentWeapon = p.weapons[p.slotIndex];
        screen.am.playSound("WeaponPickup");
    }

    @Override
    public String getName(){
        return "Iron sword";
    }

    @Override
    public void dropped(){
        screen.item.add(new Sword(screen, position.x, position.y, screen.player.currentWeapon.getCurrentMagazine(), true));
        screen.player.weapons[screen.player.slotIndex] = null;
    }
}
