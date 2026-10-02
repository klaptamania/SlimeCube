package io.github.clapt.ffProoj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import java.util.HashMap;

public class AssetManager {
    private HashMap<String, Sound> sounds;
    private HashMap<String, Texture> texture;
    private float masterVolume = 0.3f;

    public AssetManager(){
        loadDefault();
    }

    private void loadDefault() {
        loadSound();
        loadTexture();
    }

    private void loadTexture() {
        texture = new HashMap<>();
        texture.put("Player", new Texture("Entity/Player/cube.png"));
        texture.put("Merchant", new Texture("Entity/NPC/merchant.png"));
        texture.put("CarrotSuicide", new Texture("Entity/Monster/carrotSuicide.png"));
        texture.put("CornRocket", new Texture("Entity/Monster/CornRocket.png"));
        texture.put("ShootingFungus", new Texture("Entity/Monster/ShootingFungus.png"));
        texture.put("OldRobot", new Texture("Entity/NPC/NPC_OldRobotSheett.png"));
        texture.put("Corn", new Texture("Entity/Projectile/Corn.png"));
        texture.put("Fungus", new Texture("Entity/Projectile/fungus.png"));
        texture.put("Pea", new Texture("Entity/Projectile/pea.png"));
        texture.put("WatermelonSeed", new Texture("Entity/Projectile/watermelon_seed.png"));
        texture.put("PeaGun", new Texture("Item/peaGun.png"));
        texture.put("Ammo", new Texture("Item/ammo.png"));
        texture.put("Cucumber", new Texture("Entity/Monster/cucumberTrain.png"));
        texture.put("Portal", new Texture("Entity/portal.png"));
        texture.put("Sword", new Texture("Item/sword.png"));
        texture.put("WorldSlash", new Texture("Entity/Projectile/worldSlash.png"));
        texture.put("Cobifle", new Texture("Item/cobifle.png"));
        texture.put("CornPepper", new Texture("Entity/Projectile/CornPepper.png"));
        texture.put("HorseStick", new Texture("Item/horseStick.png"));
        texture.put("TomatoLazerGun", new Texture("Item/tomatoLazerGun.png"));
        texture.put("CarrotSword", new Texture("Item/carrotSword.png"));
        texture.put("CarrotBullet", new Texture("Entity/Projectile/carrotBullet.png"));
        texture.put("Cabbage", new Texture("Entity/Monster/cabbage.png"));
        texture.put("Watermelon", new Texture("Entity/Monster/watermelon.png"));


        //Upgrade
        texture.put("UPGRADE:HP", new Texture("Item/UPGRADE_HP.png"));
        texture.put("UPGRADE:DEF", new Texture("Item/UPGRADE_DEF.png"));
        texture.put("UPGRADE:SPEED", new Texture("Item/UPGRADE_SPEED.png"));
        texture.put("UPGRADE:DODGE", new Texture("Item/UPGRADE_DODGE.png"));

        //Particle
        texture.put("FungusExplode", new Texture("Entity/Projectile/fungus.png"));
        texture.put("CornExplode", new Texture("Particle/popcorn.png"));
        texture.put("CarrotExplode", new Texture("Particle/carrotExplodet.png"));
        texture.put("SpawnEffect", new Texture("Particle/spawnParticle.png"));
        texture.put("AmmoParticle", new Texture("Particle/ammoParticle.png"));
        texture.put("HealingParticle", new Texture("Particle/healingParticle.png"));
        texture.put("CucumberParticle", new Texture("Particle/cucumberParticle.png"));
        texture.put("SwordTrail", new Texture("Particle/swordTrail.png"));
        texture.put("SwordParticle", new Texture("Particle/swordParticle.png"));
        texture.put("SwordHeavyParticle", new Texture("Particle/swordHeavyParticle.png"));
        texture.put("TomatoTrail", new Texture("Particle/tomatoTrail.png"));
        texture.put("SlimeParticle", new Texture("Particle/slimeParticle.png"));
        texture.put("LaserParticle", new Texture("Particle/laserParticle.png"));
        texture.put("CarrotSwordParticle", new Texture("Particle/carrotSwordParticle.png"));
        texture.put("CarrotSwordTrail", new Texture("Particle/carrotTrail.png"));
        texture.put("CarrotCut", new Texture("Particle/carrotCut.png"));
        texture.put("CarrotPerfect", new Texture("Particle/carrotPerfect.png"));
        texture.put("CabbageParticle", new Texture("Particle/cabbageParticle.png"));
        texture.put("WatermelonBreak", new Texture("Particle/watermelonParticle.png"));

        //HUD
        texture.put("HealthBG", new Texture("hud/health/healthBG.png"));
        texture.put("HealthFill", new Texture("hud/health/healthFill.png"));
        texture.put("StartButtonUp", new Texture("hud/StartScreen/newButtonUP.png"));
        texture.put("StartButtonDown", new Texture("hud/StartScreen/newButtonDO.png"));
        texture.put("TextBox", new Texture("hud/textBox.png"));
        texture.put("EndlessButton", new Texture("hud/StartScreen/endless.png"));
        texture.put("GameOverScreen", new Texture("hud/StartScreen/gameOve.png"));
        texture.put("RestartButton", new Texture("hud/StartScreen/restartButton.png"));
        texture.put("MenuButton", new Texture("hud/StartScreen/menuButton.png"));
        texture.put("MenuScreen", new Texture("hud/StartScreen/cubve.png"));
        texture.put("LoadButton", new Texture("hud/StartScreen/loadButton.png"));
        texture.put("Slot", new Texture("hud/inventory/slotInventory.png"));
        texture.put("SlotSelected", new Texture("hud/inventory/slotSelected.png"));
        texture.put("ShopBackground", new Texture("hud/inventory/shopUI.png"));
        texture.put("StatsBackground", new Texture("hud/inventory/keyCard.png"));
        texture.put("ItemInfoBackground", new Texture("hud/inventory/itemInfo.png"));
        texture.put("StopScreen", new Texture("hud/StartScreen/stopsc.png"));
    }

    private void loadSound() {
        sounds = new HashMap<>();
        sounds.put("Spawn", Gdx.audio.newSound(Gdx.files.internal("Sound/spawn.wav")));
        sounds.put("FungusShoot", Gdx.audio.newSound(Gdx.files.internal("Sound/fungusShoot.wav")));
        sounds.put("FungusHit", Gdx.audio.newSound(Gdx.files.internal("Sound/fungusHit.wav")));
        sounds.put("FungusDied", Gdx.audio.newSound(Gdx.files.internal("Sound/fungusDied.wav")));
        sounds.put("FungusExplode", Gdx.audio.newSound(Gdx.files.internal("Sound/FungusExplode.wav")));
        sounds.put("Hit", Gdx.audio.newSound(Gdx.files.internal("Sound/hit.wav")));
        sounds.put("Walking",Gdx.audio.newSound(Gdx.files.internal("Sound/walking.wav")));
        sounds.put("PlayerDied",Gdx.audio.newSound(Gdx.files.internal("Sound/playerDied.wav")));
        sounds.put("CornShoot",Gdx.audio.newSound(Gdx.files.internal("Sound/CornShoot.wav")));
        sounds.put("CornExplode",Gdx.audio.newSound(Gdx.files.internal("Sound/cornExplode.wav")));
        sounds.put("CarrotExplode",Gdx.audio.newSound(Gdx.files.internal("Sound/CarrotExplode.wav")));
        sounds.put("EntityWalking",Gdx.audio.newSound(Gdx.files.internal("Sound/entityWalking.wav")));
        sounds.put("PeaShoot",Gdx.audio.newSound(Gdx.files.internal("Sound/peaShoot.wav")));
        sounds.put("AmmoPickup", Gdx.audio.newSound(Gdx.files.internal("Sound/ammoPickup.wav")));
        sounds.put("ButtonPressed", Gdx.audio.newSound(Gdx.files.internal("Sound/select.wav")));
        sounds.put("HorseWhistle", Gdx.audio.newSound(Gdx.files.internal("Sound/horseWhistle.wav")));
        sounds.put("HorseRunning", Gdx.audio.newSound(Gdx.files.internal("Sound/horseRunning.wav")));
        sounds.put("Dialog1", Gdx.audio.newSound(Gdx.files.internal("Sound/dialogSound1.wav")));
        sounds.put("Dialog2", Gdx.audio.newSound(Gdx.files.internal("Sound/dialogSound2.wav")));
        sounds.put("Dialog3", Gdx.audio.newSound(Gdx.files.internal("Sound/dialogSound3.wav")));
        sounds.put("WeaponPickup", Gdx.audio.newSound(Gdx.files.internal("Sound/weaponPickup.wav")));
        sounds.put("GunReload", Gdx.audio.newSound(Gdx.files.internal("Sound/gunReload.wav")));
        sounds.put("GunReloadDone", Gdx.audio.newSound(Gdx.files.internal("Sound/gunReloadDone.wav")));
        sounds.put("ReadyHeal", Gdx.audio.newSound(Gdx.files.internal("Sound/readyHealing.wav")));
        sounds.put("HealingDone", Gdx.audio.newSound(Gdx.files.internal("Sound/healingDone.wav")));
        sounds.put("SwordSlash", Gdx.audio.newSound(Gdx.files.internal("Sound/swordSlash.wav")));
        sounds.put("SwordSheathed", Gdx.audio.newSound(Gdx.files.internal("Sound/swordSealed.wav")));
        sounds.put("SwordUnsheathed", Gdx.audio.newSound(Gdx.files.internal("Sound/swordDraw.wav")));
        sounds.put("HorseStick", Gdx.audio.newSound(Gdx.files.internal("Sound/horseStickSound.wav")));
        sounds.put("StackLoad", Gdx.audio.newSound(Gdx.files.internal("Sound/stackLoad.wav")));
        sounds.put("StacksFull", Gdx.audio.newSound(Gdx.files.internal("Sound/stacksFull.wav")));
        sounds.put("WindBreak", Gdx.audio.newSound(Gdx.files.internal("Sound/WindBreak.wav")));
        sounds.put("TomatoLaser", Gdx.audio.newSound(Gdx.files.internal("Sound/laserTomato.wav")));
        sounds.put("WeaponChange", Gdx.audio.newSound(Gdx.files.internal("Sound/weaponChange.wav")));
        sounds.put("Purchase", Gdx.audio.newSound(Gdx.files.internal("Sound/purchaseSound.wav")));
        sounds.put("ItemSwitch", Gdx.audio.newSound(Gdx.files.internal("Sound/itemSwitch.wav")));
        sounds.put("ItemSwitchOff", Gdx.audio.newSound(Gdx.files.internal("Sound/itemSwitchOff.wav")));
        sounds.put("Dodge", Gdx.audio.newSound(Gdx.files.internal("Sound/dodge.wav")));
        sounds.put("WatermelonShooting", Gdx.audio.newSound(Gdx.files.internal("Sound/watermelonMachineGun.wav")));
        sounds.put("Cart", Gdx.audio.newSound(Gdx.files.internal("Sound/cart.wav")));
        sounds.put("Squash", Gdx.audio.newSound(Gdx.files.internal("Sound/squash.mp3")));

    }

    public void playSound(String sound){
        sounds.get(sound).play(masterVolume);
    }

    public Texture getTexture(String texture) { return this.texture.get(texture);}

    public void changeVolume(float amount) { masterVolume = MathUtils.clamp(amount, 0, 1f);
    }

    public void dispose(){
        for(Sound s : sounds.values()){
            s.dispose();
        }
        for(Texture txt : texture.values()){
            txt.dispose();
        }
    }
}
