package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import io.github.clapt.ffProoj.Dialog;
import io.github.clapt.ffProoj.GameScreen;
import item.Upgrade;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class NPC_Merchant extends Entity{
    private static Texture sheet;
    private float detectRange, dialogRange;
    private ArrayList<Dialog> dM = new ArrayList<>();
    private boolean talking = false;
    private boolean isTalked = false;
    private boolean notEnoughCores = false;
    private int currentDialog = 1;

    public String dialogPath;

    public int[] itemStorage = new int[8];

    private static final Json json = new Json();

    public NPC_Merchant(GameScreen screen){
        super(screen);
        SetDefaultStats();
    }

    private void SetDefaultStats(){

        //Vector
        velocity = new Vector2(0,0);
        position = new Vector2(100,100);
        direction = new Vector2(0,0);

        //Hitbox
        hitbox = new Rectangle(3 * 2, 3 * 2, 26 * 2 , 26 * 2);

        //Texture
        if(sheet == null) sheet = screen.am.getTexture("Merchant");
        current = new TextureRegion(sheet);

        //Attributes
        speed = 40;
        HP = 25;
        maxHP = HP;

        detectRange = 3 * screen.tileSize;
        dialogRange = 2 * screen.tileSize;
        dialogPath = "Dialogue/Merchant.txt";
        currentWeapon = null;
        dM = new ArrayList<>();
        screen.uTools.loadDialog(screen, dialogPath, dM);
    }

    public void setShop(String filePath){
        try{
            BufferedReader br = new BufferedReader(Gdx.files.internal(filePath).reader());
            String line;
            int currentIndex = 0;
            while((line = br.readLine()) != null){
                String[] container = line.split(" ");
                for(var c : container){
                    itemStorage[currentIndex++] = Integer.parseInt(c);
                }
            }

            br.close();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        screen.hud.setShopUI(this);
    }

    public void reloadShop(int[] container){
        System.arraycopy(container, 0, itemStorage, 0, itemStorage.length);
        screen.hud.setShopUI(this);
    }

    public int getItemPosition(int index){ return itemStorage[index]; }

    public Upgrade getUpgradeInfo(int index) {
        Upgrade needed = null;
        switch (itemStorage[index]){
            case 0:
                needed = json.fromJson(Upgrade.class, Gdx.files.internal("ShopItem/hp_upgrade.json"));
                break;
            case 1:
                needed = json.fromJson(Upgrade.class, Gdx.files.internal("ShopItem/def_upgrade.json"));
                break;
            case 2:
                needed = json.fromJson(Upgrade.class, Gdx.files.internal("ShopItem/speed_upgrade.json"));
                break;
            case 3:
                needed = json.fromJson(Upgrade.class, Gdx.files.internal("ShopItem/dodge_upgrade.json"));
                break;
        }
        return needed;
    }

    public void bought(int index) { itemStorage[index] = -1;}

    @Override
    public void update(){
        float dist2 = screen.player.getPosition().cpy().add(32,32).sub(position.cpy().add(32,32)).len2();
        if(dist2 <= detectRange * detectRange){
            if(dist2 <= dialogRange * dialogRange && !isTalked) {
                screen.currentState = GameScreen.state.DIALOGUE;
                talking = true;
            }
        }else{
            isTalked = false;
            if(screen.hud.isToggle()) screen.hud.toggleShopUI();
        }

        if(talking && !isTalked){
            talking();
        }

        if(talking && screen.currentState != GameScreen.state.DIALOGUE){
            talking = false;
            isTalked = true;
            currentDialog++;
        }

        if(notEnoughCores) talk();

        animationUpdate(false, timer);
    }

    @Override
    public void draw(){
        if(talking) dM.get(currentDialog).showDialog();
        if(notEnoughCores) dM.get(0).showDialog();
        super.draw();
    }

    public void talk(){
        if(!screen.isDialog){
            screen.hud.toggleShopUI();
            notEnoughCores = true;
            screen.currentState = GameScreen.state.DIALOGUE;
        }
        dM.get(0).updateDialog();
        if(!dM.get(0).isActive()){
            screen.currentState = GameScreen.state.SHOP;
            notEnoughCores = false;
            dM.get(0).start("Uh oh, looks like you don't have enough Monster Cores for that, little slime.");
            screen.hud.toggleShopUI();
        }
    }

    @Override
    public void takeDamage(int amount){};

    @Override
    public void takeForceDamage(int amount){}

    private void talking(){
        dM.get(currentDialog).updateDialog();
        if(!screen.isDialog){
            isTalked = true;
            screen.hud.toggleShopUI();
        }
    }

    @Override
    protected String getSheet() { return "Merchant"; }
}
