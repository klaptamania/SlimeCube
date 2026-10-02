package level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import entity.Entity;
import entity.NPC_Merchant;
import item.*;
import io.github.clapt.ffProoj.Dialog;
import io.github.clapt.ffProoj.GameScreen;
import io.github.clapt.ffProoj.Spawner;
import projectile.Pea;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class Room {
    private Spawner spawner;
    public enum RoomType {INTRO, NORMAL, ENDLESS, MERCHANT}

    //Json reader Variables
    public String mapPath;
    public int totalWave;
    public String introDialog;
    public RoomType roomType;
    public int difficulty;
    public int amount;

    //Room Variable//

    //SaveData
    public ArrayList<Direction> gateDirection = new ArrayList<>();
    public enum Direction {UP, DOWN, LEFT, RIGHT, NONE}
    public int playerMoveDirX = 0, playerMoveDirY = 0;
    public boolean gateOpened = false, isDone = false;
    public ArrayList<Item> item;
    public TiledMapTileLayer objectLayer;

    public int[] merchantStorage = new int[8];
    private boolean saved = false;

    //Can't save
    private int wave = 0;
    private final ArrayList<Dialog> dialog = new ArrayList<>();
    private int currentIntroDialog = 0, introStage = 0, eventStep = 0;

    private boolean start = false, gunSpawned = false;
    private float delay = 0;

    private boolean haveMerchant = false;

    public void update(GameScreen screen){
        if(spawner == null) spawner = new Spawner(screen);
        switch(roomType){
            case INTRO:
                if(dialog.isEmpty()) screen.uTools.loadDialog(screen, introDialog, dialog);
                if(screen.currentState == GameScreen.state.DIALOGUE){
                    introDialog(screen);
                }else if(screen.currentState == GameScreen.state.GAME){
                    if(introStage ==  0){
                        introGameState(screen);
                    }else if(introStage == 1){
                        introGameState1(screen);
                    }
                }

                break;
            case NORMAL:
                normal(screen);
                break;
            case ENDLESS:
                endless(screen);
                break;
            case MERCHANT:
                shopping(screen);
                break;
        }
    }

    private void shopping(GameScreen screen) {

        checkGate(screen);

        if(!haveMerchant){
            spawner.spawnExactly(1, 16 * screen.tileSize , 6 * screen.tileSize);
            haveMerchant = true;
        }

    }

    private void checkGate(GameScreen screen) {
        if(!gateOpened){
            for(var e : gateDirection){
                switch (e){
                    case UP:

                        for(int i = 0; i < screen.row; i++){
                            for(int j = 0; j < screen.col; j++){
                                if(i == screen.row - 1 && (j >= 9 && j <= 13) ){
                                    screen.tileManager.getLayer("Gate").setCell(j,i, null);
                                }
                            }
                        }

                        break;
                    case DOWN:

                        for(int i = 0; i < screen.row; i++){
                            for(int j = 0; j < screen.col; j++){
                                if(i == 0 && (j >= 9 && j <= 13) ){
                                    screen.tileManager.getLayer("Gate").setCell(j,i, null);
                                }
                            }
                        }

                        break;
                    case LEFT:

                        for(int i = 0; i < screen.row; i++){
                            for(int j = 0; j < screen.col; j++){
                                if((i >= 4 && i <= 8) && j == 0 ){
                                    screen.tileManager.getLayer("Gate").setCell(j,i, null);
                                }
                            }
                        }

                        break;
                    case RIGHT:

                        for(int i = 0; i < screen.row; i++){
                            for(int j = 0; j < screen.col; j++){
                                if((i >= 4 && i <= 8) && j == screen.col - 1 ){
                                    screen.tileManager.getLayer("Gate").setCell(j,i, null);
                                }
                            }
                        }

                        break;
                }
            }
            gateOpened = true;
        }else{
            if((int)((screen.player.getPosition().x + screen.tileSize/2f) / screen.tileSize) >= screen.col - 1){

                playerMoveDirX = 1;
                screen.player.setPosition(screen.tileSize * 2,screen.player.getPosition().y);

            }else if((int)((screen.player.getPosition().x + screen.tileSize/2f) / screen.tileSize) <= 0){

                playerMoveDirX = -1;
                screen.player.setPosition((screen.col - 1) * screen.tileSize - screen.tileSize, screen.player.getPosition().y);

            }else if((int)((screen.player.getPosition().y + screen.tileSize/2f) / screen.tileSize) >= (screen.row - 1)){

                playerMoveDirY = -1;
                screen.player.setPosition(screen.player.getPosition().x, screen.tileSize * 2);

            }else if((int)((screen.player.getPosition().y + screen.tileSize/2f) / screen.tileSize) <= 0){

                playerMoveDirY = 1;
                screen.player.setPosition(screen.player.getPosition().x, (screen.row - 1) * screen.tileSize - screen.tileSize);

            }

            if(playerMoveDirX != 0 || playerMoveDirY != 0){
                objectLayer = screen.tileManager.getLayer("Destructible Object");

                if(screen.npcFreeStack.size() != 10){
                    for(int i = 0; i < screen.npc.length; i++){
                        if(screen.npc[i] instanceof NPC_Merchant){
                            System.arraycopy(((NPC_Merchant )screen.npc[i]).itemStorage, 0, merchantStorage, 0, merchantStorage.length);
                            saved = true;
                        }
                    }
                }
            }
        }
    }

    private void normal(GameScreen screen) {

        if(isDone){
            if(wave != totalWave) wave = totalWave;
            if(!start) start = true;

            checkGate(screen);
        }

        if(!start){
            delay += Gdx.graphics.getDeltaTime();
            if(delay >= 2.9f){
                delay = 0;
                start = true;
            }
        }else {
            spawner.update();
            if(wave < totalWave){
                if(spawner.isCanSpawn() && isAllDead(screen)){
                    spawner.spawnMonster(amount, difficulty);
                    wave++;
                }
            }

            if(wave == totalWave && isAllDead(screen)){
               isDone = true;
            }
        }
    }

    public void draw(){
        if(!dialog.isEmpty()) dialog.get(currentIntroDialog).showDialog();
    }

    private void introGameState1(GameScreen screen) {
        if(wave == 3 && isAllDead(screen) && screen.onUsing.isEmpty()){
            wave = 0;
            screen.isDialog = true;
            screen.currentState = GameScreen.state.DIALOGUE;
            currentIntroDialog++;
            introStage++;
            eventStep = 0;
        }

        spawner.update();
        if(spawner.isCanSpawn() && isAllDead(screen) & wave < totalWave){
            wave++;
            spawner.spawnMonster(3, 0);
        }
    }

    private void introGameState(GameScreen screen) {
        if(wave == 3 && isAllDead(screen) && introStage == 0 && screen.onUsing.isEmpty()){
            screen.isDialog = true;
            screen.currentState = GameScreen.state.DIALOGUE;
            currentIntroDialog++;
            wave = 0;
            introStage++;
            eventStep = 0;
        }
        spawner.update();
        if(spawner.isCanSpawn() && isAllDead(screen) && wave < totalWave){
            wave++;
            spawner.spawnMonster(1, 0);
        }
    }

    private void introDialog(GameScreen screen) {
        if(!start){
            delay += Gdx.graphics.getDeltaTime();
            if(delay >= 2.9f){
                delay = 0;
                start = true;
                screen.isDialog = true;
            }
        }else{

            int result = dialog.get(currentIntroDialog).updateDialog();

            if(result != -1 && wave < totalWave && introStage == 0) {
                eventStep++;
            }
            if(result != -1 && introStage == 2) {
                eventStep++;
            }

            //First Encounter

            if (eventStep == 5 && screen.npcFreeStack.size() == 10 && introStage == 2) {
                spawner.spawnExactly(0, 12 * screen.tileSize, 6 * screen.tileSize);
            }

            if (eventStep == 9 && !gunSpawned) {
                screen.item.add(new PeaGun(screen, 11 * screen.tileSize, 8 * screen.tileSize, -1, false));
                gunSpawned = true;
                screen.particle.create(screen.tileSize, 11 * screen.tileSize, 8 * screen.tileSize, screen, "SpawnEffect", 25, 100, 0.5f);
                screen.am.playSound("Spawn");
            }

            for (int i = screen.particles.size() - 1; i >= 0; i--) {
                if (screen.particles.get(i).isAlive()) {
                    screen.particles.get(i).update();
                } else {
                    screen.particles.remove(i);
                }
            }
        }
    }

    public boolean isAllDead(GameScreen screen){
        for(Entity m : screen.monster){
            if(m != null && !m.isDeath()){
                return false;}
        }
        return true;
    }
    private void endless(GameScreen screen) {
        if(!start) {
            delay += Gdx.graphics.getDeltaTime();
            if (delay >= 2.9f) {
                delay = 0;
                start = true;
                screen.item.add(new CarrotSword(screen, 100, 100, 9, false));
                //spawner.spawnExactly(1, 100, 400);
            }
        }else {
            spawner.update();
            if (spawner.isCanSpawn() && isAllDead(screen)) {
                wave++;
                if (wave >= 0 && wave < 3) {
//                    spawner.spawnMonster(1, 2);
                    spawner.spawnExactly(2, 500, 500);
                } else if (wave >= 3 && wave < 8) {
                    spawner.spawnMonster(1, 1);
                } else if (wave >= 8 && wave < 13) {
                    spawner.spawnMonster(2, 1);
                } else if (wave >= 13 && wave < 15) {
                    spawner.spawnMonster(3, 1);
                } else if (wave >= 15 && wave < 17) {
                    spawner.spawnMonster(3, 2);
                } else if (wave >= 17 && wave < 20) {
                    spawner.spawnMonster(4, 2);
                } else if (wave >= 20 && wave < 25) {
                    spawner.spawnMonster(4, 2);
                } else if (wave >= 25) {
                    spawner.spawnMonster(5, 2);
                }
            }
        }
    }

    public void loadMap(GameScreen screen){
        screen.tileManager.updateMap(mapPath);
        if(objectLayer != null) screen.tileManager.loadTileLayer(objectLayer, "Destructible Object");
        if(saved) {
            for(Entity e : screen.npc){
                if(e instanceof  NPC_Merchant) ((NPC_Merchant)e).reloadShop(merchantStorage);
            }
        }
        screen.cCheck.reloadMap();
    }
}
