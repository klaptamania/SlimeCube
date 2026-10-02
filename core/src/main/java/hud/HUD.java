package hud;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import entity.NPC_Merchant;
import io.github.clapt.ffProoj.GameScreen;
import entity.Entity;
import item.Upgrade;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public class HUD {

    private final Stage gameScreen;
    private final Label ammo;
    private final ProgressBar hpBar;
    private Table hud;
    Table inventory = new Table();

    Stack shopUI = new Stack();
    Image shopBG;
    Table shopTable = new Table();
    Stack[] shopSlot = new Stack[8];
    TextureRegionDrawable[] itemSell = new TextureRegionDrawable[8];
    Label[] itemPrice = new Label[8];
    int shopIndex = 0;
    Button[] shopButton = new Button[8];
    Stack currentCurrency;
    Label currency;
    Stack stats = new Stack();
    Table statsInfo = new Table();
    Label[] statsString = new Label[4];
    Upgrade[] upgrade = new Upgrade[8];
    Stack itemInfo = new Stack();
    Table itemInfotable = new Table();
    Label itemInfoLabel;


    private final Skin skin;
    Stack stack1 = new Stack();
    Stack stack2 = new Stack();

    Image wea1 = new Image();
    Image wea2 = new Image();

    Image select1;
    Image select2;

    public int type = 1;

    boolean isNothingChange = false;
    boolean isJustCalled = false;
    float timer = 0;

    GameScreen screen;

    public HUD(SpriteBatch batch, GameScreen screen){
        this.screen = screen;
        gameScreen = new Stage(new ExtendViewport(1280, 720), batch);
        gameScreen.setDebugAll(false);
        skin = new Skin();

        skin.add("default-font", screen.font);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = screen.font;

        skin.add("default", labelStyle);

        Texture healthBG = screen.am.getTexture("HealthBG");
        Texture healthFill = screen.am.getTexture("HealthFill");

        ProgressBar.ProgressBarStyle style =
            new ProgressBar.ProgressBarStyle();

        style.background =
            new TextureRegionDrawable(
                new TextureRegion(healthBG)
            );

        style.knobBefore =
            new TextureRegionDrawable(
                new TextureRegion(healthFill));

        skin.add("default-horizontal", style);

        stats.add(new Image(screen.am.getTexture("StatsBackground")));
        stats.add(statsInfo);
        itemInfo.add(new Image(screen.am.getTexture("ItemInfoBackground")));
        itemInfo.add(itemInfotable);

        select1 = new Image(screen.am.getTexture("SlotSelected"));
        select2 = new Image(screen.am.getTexture("SlotSelected"));

        select2.setVisible(false);

        stack1.add(new Image(screen.am.getTexture("Slot")));
        stack2.add(new Image(screen.am.getTexture("Slot")));
        stack1.add(wea1);
        stack2.add(wea2);
        stack1.add(select1);
        stack2.add(select2);

        currentCurrency = new Stack();
        currentCurrency.add(new Image(screen.am.getTexture("Ammo")));
        currency = new Label("100", skin);
        currency.setSize(64 * 2, 64 *2);
        currency.setAlignment(Align.bottom);
        Table table = new Table();
        table.left().bottom();
        table.add(currency);
        currentCurrency.add(table);

        shopBG = new Image(screen.am.getTexture("ShopBackground"));
        shopBG.setSize(
            shopBG.getWidth() * 6,
            shopBG.getHeight() * 4
        );
        shopUI.add(shopBG);

        hpBar = new ProgressBar(0, 100, 1, false, skin);
        ammo = new Label("AMMO: ", skin);

        statsInfo.left().padLeft(50);
        statsString[0] = new Label("Max HP: ",skin);
        statsString[1] = new Label("Speed: ",skin);
        statsString[2] = new Label("Def: ",skin);
        statsString[3] = new Label("Dodge Rate: ",skin);
        statsString[0].setAlignment(Align.left);
        statsString[1].setAlignment(Align.left);
        statsString[2].setAlignment(Align.left);
        statsString[3].setAlignment(Align.left);

        statsInfo.add(statsString[0]).left().row();
        statsInfo.add(statsString[1]).left().row();
        statsInfo.add(statsString[2]).left().row();
        statsInfo.add(statsString[3]).left();
        itemInfoLabel = new Label("", skin);
        itemInfotable.add(itemInfoLabel).width(screen.tileSize * 7).height(screen.tileSize * 3);

        setHUD();
    }


    private void setHUD() {
        hud = new Table();
        hud.setFillParent(true);
        hud.setDebug(false);
        hud.top().left();
        hud.add(hpBar).width(150 * 2).height(50 * 2);

        hud.row();
        hud.add(ammo).pad(20).width(150 * 2).height(50 * 2);

        inventory.setFillParent(true);
        inventory.left().bottom();
        inventory.add(stack1).pad(10).width(64 * 2).height(64 * 2);
        inventory.add(stack2).pad(10).width(64 * 2).height(64 * 2);

        shopTable.setFillParent(true);

        shopUI.setVisible(false);

        Table temp = new Table();
        temp.setFillParent(true);
        temp.right();
        temp.setVisible(true);
        temp.add(shopUI).width(shopBG.getWidth() - 150f).height(shopBG.getHeight());

        currentCurrency.setPosition(screen.cam.position.x + 420, screen.cam.position.y + 420);
        currentCurrency.setVisible(false);

        stats.setVisible(false);

        Table temp1 = new Table();
        temp1.setFillParent(true);
        temp1.left().top();
        temp1.setVisible(true);
        temp1.add(stats).width(screen.tileSize * 8).height(screen.tileSize * 6);

        itemInfo.setVisible(false);
        Table temp2 = new Table();
        temp2.setFillParent(true);
        temp2.left().bottom();
        temp2.setVisible(true);
        temp2.add(itemInfo).width(screen.tileSize * 8).height(screen.tileSize * 5);

        gameScreen.addActor(inventory);
        gameScreen.addActor(hud);
        gameScreen.addActor(currentCurrency);
        gameScreen.addActor(temp);
        gameScreen.addActor(temp1);
        gameScreen.addActor(temp2);

        updateStats();
    }

    //It would be called from Merchant's codesd
    public void setShopUI(NPC_Merchant merchant){
        currency.setText(screen.player.getAmmoAmount());
        Gdx.input.setInputProcessor(gameScreen);
        //reset Every
        shopIndex = 0;
        Arrays.fill(shopSlot, null);
        Arrays.fill(upgrade, null);
        Arrays.fill(itemSell, null);
        Arrays.fill(itemPrice, null);
        Arrays.fill(shopButton, null);

        shopTable = new Table();
        shopTable.center();

        for(int i = 0; i < merchant.itemStorage.length; i++){
            shopSlot[shopIndex] = new Stack();
            shopSlot[shopIndex].setSize(64 * 2, 64 * 2);

            int id = merchant.getItemPosition(i);
            upgrade[shopIndex] = merchant.getUpgradeInfo(i);

            if(upgrade[shopIndex] != null){
                itemSell[shopIndex] = new TextureRegionDrawable(screen.am.getTexture(upgrade[shopIndex].textureName));
                itemPrice[shopIndex] = new Label(upgrade[shopIndex].price + "", skin);

                itemPrice[shopIndex].setSize(64 * 2, 64 *2);
                itemPrice[shopIndex].setAlignment(Align.bottom);
                Table table = new Table();
                table.left().bottom();
                table.add(itemPrice[shopIndex]);
                shopSlot[shopIndex].add(table);

                shopButton[shopIndex] = new Button(itemSell[shopIndex]);
                shopButton[shopIndex].add(shopSlot[shopIndex]).size(64 * 2, 64 * 2);

                int index = shopIndex;
                shopButton[shopIndex].addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y){
                        if(canBuyItem(id, index)) {
                            shopButton[index].remove();
                            merchant.bought(index);
                        }
                        updateStats();
                    }
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor){
                        itemInfo.setVisible(true);
                        itemInfoLabel.setText(upgrade[index].name + ":\n - "+ upgrade[index].description);
                        updateStats();
                        updateExactlyStats(id,upgrade[index].amount);
                        screen.am.playSound("ItemSwitch");
                    }
                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor){
                        itemInfo.setVisible(false);
                        updateStats();
                        screen.am.playSound("ItemSwitchOff");
                    }
                });


                shopTable.add(shopButton[shopIndex]).width(64*2).height(64*2).padRight(20);
            }else{
                shopTable.add().width(64*2).height(64*2).padRight(20);
            }



            if(i == 1 || i == 3 || i == 5 || i == 7) shopTable.row();
            shopIndex++;
        }


        shopTable.row();
        shopUI.add(shopTable);
    }

    //this will also be call from Merchant's codes
    public void toggleShopUI(){
        if(shopUI.isVisible()){
            shopUI.setVisible(false);
            currentCurrency.setVisible(false);
            hud.setVisible(true);
            stats.setVisible(false);
            screen.currentState = GameScreen.state.GAME;
        }else{
            shopUI.setVisible(true);
            currentCurrency.setVisible(true);
            currency.setText(screen.player.getAmmoAmount());
            hud.setVisible(false);
            stats.setVisible(true);
            screen.currentState = GameScreen.state.SHOP;
            if(inventory.isVisible()) inventory.setVisible(false);
        }
    }

    public boolean isToggle(){
        return shopUI.isVisible();
    }


    //Useless
    private boolean canBuyItem(int id, int index){
        int price = Integer.parseInt(itemPrice[index].getText().toString());
        if(screen.player.getAmmoAmount() < price){
            for(Entity e : screen.npc){
                if(e instanceof NPC_Merchant){
                    ((NPC_Merchant) e).talk();
                }
            }
            return false;
        }else{
            screen.player.takeAmmo(price);
            currency.setText(screen.player.getAmmoAmount());
        }
        screen.am.playSound("Purchase");
        int amount = upgrade[index].amount;
        switch(id){
            case 0:
                //HP
                screen.player.gainMaxHP(amount);
                break;
            case 1:
                //Def
                screen.player.gainDef(amount);
                break;
            case 2:
                //Speed
                screen.player.gainSpeed(amount);
                break;
            case 3:
                //Dodge
                screen.player.gainDodgeRate(amount);
                break;
        }
        return true;
    }

    public void resize(int width, int height){
        gameScreen.getViewport().update(width, height, true);
    }

    public void updateLabel(int value){
        hpBar.setValue(value);
    }

    public void updateAmmo(String text){
        ammo.setText(text);
    }

    public void update(){
        if(!isNothingChange){
            timer += Gdx.graphics.getDeltaTime();
            if(isJustCalled){
                timer = 0;
                isJustCalled = false;
            }
            if(timer >= 1.2f){
                timer = 0;
                isNothingChange = true;
                inventory.setVisible(false);
            }
        }
    }

    public void updateWeapon(Texture texture, int index){
        isNothingChange = false;
        inventory.setVisible(true);
        if(index == 0){
            wea1.setDrawable(new TextureRegionDrawable( new TextureRegion( texture )));
        }else if (index == 1){
            wea2.setDrawable(new TextureRegionDrawable( new TextureRegion( texture )));
        }
        isJustCalled = true;
    }

    public void changeSlot(int slot){
        isNothingChange = false;
        inventory.setVisible(true);
        select1.setVisible(slot == 0);
        select2.setVisible(slot == 1);
        isJustCalled = true;
    }

    public void render(){
        gameScreen.act();
        gameScreen.draw();
    }

    public void dispose(){
        gameScreen.dispose();
        skin.dispose();
    }

    private void updateStats(){
        String temper;
        temper = "Max HP: " + screen.player.getMaxHP();
        statsString[0].setText(temper);

        temper = "Speed: " + screen.player.getSpeed();
        statsString[1].setText(temper);

        temper = "Def: " + screen.player.getDefAmount();
        statsString[2].setText(temper);
        temper = "Dodge Rate: " + screen.player.getDodgeRate();
        statsString[3].setText(temper);
    }

    private void updateExactlyStats(int i, int amount){
        String temper;

        switch(i){
            case 0:
                temper = "Max HP: " + screen.player.getMaxHP() + " -> " + amount;
                statsString[0].setText(temper);
                break;
            case 1:
                temper = "Speed: " + screen.player.getSpeed() + " -> " + amount;
                statsString[1].setText(temper);
                break;
            case 2:
                temper = "Def: " + screen.player.getDefAmount() + " -> " + amount;
                statsString[2].setText(temper);
                break;
            case 3:
                temper = "Dodge Rate: " + screen.player.getDodgeRate() + " -> " + amount;
                statsString[3].setText(temper);
                break;
        }
    }

}
