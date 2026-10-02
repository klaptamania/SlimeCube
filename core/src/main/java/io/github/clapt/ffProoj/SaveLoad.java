package io.github.clapt.ffProoj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import item.CarrotSword;
import item.Cobifle;
import item.PeaGun;
import item.Weapon;

public class SaveLoad {
    public float positionX, positionY;
    public String wea1, wea2;
    public int HP;
    public int currentAmmo;
    public int gunAmmo;

    public String dungeon;

    public boolean isDialog;

    public void save(GameScreen screen){
        Json json = new Json();
        json.setUsePrototypes(false);
        SaveLoad data = new SaveLoad();
        data.positionX = screen.player.getPosition().x; data.positionY = screen.player.getPosition().y;

        data.wea1 = screen.player.weapons[0] != null ? screen.player.weapons[0].getName() : "";
        data.wea2 = screen.player.weapons[1] != null ? screen.player.weapons[1].getName() : "";

        data.HP = (int)screen.player.getHP();
        data.currentAmmo = screen.player.getAmmoAmount();
        data.gunAmmo = screen.player.currentWeapon.getCurrentMagazine();
        data.dungeon = screen.dungeon.getFilePath();
        data.isDialog = screen.isDialog;

        Gdx.files.local("save.json").writeString(json.prettyPrint(data), false);
    }

    public void load(GameScreen screen){
        Json json = new Json();
        SaveLoad data = json.fromJson(SaveLoad.class, Gdx.files.local("save.json"));
        screen.player.setPosition(data.positionX, data.positionY);
        screen.player.setHP(data.HP);

        screen.player.weapons[0] = loadWeapon(data.wea1, data.gunAmmo, screen);
        screen.player.weapons[1] = loadWeapon(data.wea2, data.gunAmmo, screen);

        screen.player.setAmmo(data.currentAmmo);
        screen.dungeon.loadDungeon(data.dungeon);
        screen.isDialog = data.isDialog;

        if(screen.player.weapons[0] != null) screen.hud.updateWeapon(screen.player.weapons[0].getTexture(), 0);
        if(screen.player.weapons[1] != null) screen.hud.updateWeapon(screen.player.weapons[1].getTexture(), 1);

        screen.player.currentWeapon = screen.player.weapons[0];
        screen.player.slotIndex = 0;
        if(screen.player.currentWeapon != null && !screen.player.currentWeapon.isActive) screen.player.currentWeapon.isActive = true;
    }

    private Weapon loadWeapon(String name, int gunAmmo, GameScreen screen){
        switch(name){
            case "PeaGun":
                return new PeaGun(screen, screen.player, gunAmmo);
            case "Cobifle":
                return new Cobifle(screen, screen.player, gunAmmo);
            case "Carrot Sword":
                return new CarrotSword(screen, screen.player, gunAmmo);
        }
        return null;
    }
}
