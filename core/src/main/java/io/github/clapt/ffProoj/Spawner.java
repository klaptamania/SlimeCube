package io.github.clapt.ffProoj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import entity.*;

import java.util.ArrayList;

public class Spawner {
    GameScreen screen;
    private boolean canSpawn = true;
    private float timer = 0;

    private final Rectangle box;
    private ArrayList<Entity> container = new ArrayList<>();

    private final Vector2[][] spawnPointList = new Vector2[6][6];

    private int[][] diffRateSpawn = new int[3][10];

    public Spawner(GameScreen screen) {
        this.screen = screen;
        this.box = new Rectangle(0,0, 2 * screen.tileSize, 2 * screen.tileSize);
        setSpawnPointList();
        setDiffRateSpawn();
    }


    public void update(){
        if(!canSpawn){
            boolean isAllDead = true;
            for(int i = 0; i < screen.monster.length; i++){
                if(screen.monster[i] != null){
                    if(!screen.monster[i].isDeath() && screen.monster[i].isAlive()){
                        isAllDead = false;
                        break;
                    }
                }
            }

            if(isAllDead){
                timer += Gdx.graphics.getDeltaTime();
                if(timer >= 2f){
                    canSpawn = true;
                    timer = 0;
                }
            }
        }
    }
    private void setDiffRateSpawn(){
        int carrot = 0;
        int fungus = 1;
        int corn = 2;
        int cucumber = 3;
        int cabbage = 4;

        diffRateSpawn[0] = new int[]{carrot, fungus, cabbage};
        diffRateSpawn[1] = new int[]{carrot, fungus, cabbage, corn};
        diffRateSpawn[2] = new int[]{carrot, fungus, cabbage, corn, cucumber};

    }

    private void setSpawnPointList(){
        //1 enemy

        spawnPointList[0][0] = new Vector2(12 * screen.tileSize, 7 * screen.tileSize);

        //2 enemies

        spawnPointList[1][0] = new Vector2(12 * screen.tileSize, 4 * screen.tileSize);
        spawnPointList[1][1] = new Vector2(12 * screen.tileSize, 10 * screen.tileSize);

        //3

        spawnPointList[2][0] =  new Vector2(12 * screen.tileSize, 7 * screen.tileSize);
        spawnPointList[2][1] = new Vector2(12 * screen.tileSize, 10 * screen.tileSize);
        spawnPointList[2][2] = new Vector2(12 * screen.tileSize, 4 * screen.tileSize);

        //4

        spawnPointList[3][0] =  new Vector2(16 * screen.tileSize, 7 * screen.tileSize);
        spawnPointList[3][1] = new Vector2(12 * screen.tileSize, 10 * screen.tileSize);
        spawnPointList[3][2] = new Vector2(12 * screen.tileSize, 4 * screen.tileSize);
        spawnPointList[3][3] =  new Vector2(8 * screen.tileSize, 7 * screen.tileSize);

        //5

        spawnPointList[4][0] =  new Vector2(16 * screen.tileSize, 7 * screen.tileSize);
        spawnPointList[4][1] = new Vector2(12 * screen.tileSize, 10 * screen.tileSize);
        spawnPointList[4][2] = new Vector2(12 * screen.tileSize, 4 * screen.tileSize);
        spawnPointList[4][3] =  new Vector2(8 * screen.tileSize, 7 * screen.tileSize);
        spawnPointList[4][4] =  new Vector2(12 * screen.tileSize, 7 * screen.tileSize);

    }

    public void spawnMonster(int amount, int difficulty){
        int type;
        for(int i = 0; i < amount; i++){
            type = rate(difficulty);

            spawn(type, amount, i);
        }

        canSpawn = false;
    }

    private int rate(int difficulty) {
        int result = MathUtils.random(1, 100);

        switch (difficulty){
            case 0:
                if(result >= 1 && result < 60){
                    return 0;
                }else if(result >= 60 && result < 80){
                    return 1;
                }else{
                    return 4;
                }
            case 1:
                if(result >= 1 && result < 40){
                    return 0;
                }else if(result >= 40 && result < 60){
                    return 1;
                }else if (result >= 60 && result < 85){
                    return 4;
                }else return 2;
            case 2:
                if(result >= 1 && result < 25){
                    return 0;
                }else if(result >= 25 && result < 30){
                    return 1;
                }else if (result >= 30 && result < 60){
                    return 4;
                }else if(result >= 60 && result < 80){
                    return 2;
                }else return 3;
        }
        return -1;
    }

    private void spawn(int type, int amount, int index){
        int i = screen.monsterFreeStack.pop();
        if(screen.monster[i] == null || !screen.monster[i].isAlive() || screen.monster[i].isDeath()) {
            Entity e = null;
            switch(type){
                case 0:
                    e = new MON_CarrotSuicide(screen,i);
                    break;
                case 1:
                    e = new MON_ShootingFungus(screen,i);
                    break;
                case 2:
                    e = new MON_CornRocket(screen,i);
                    break;
                case 3:
                    e = new MON_Cucumber(screen,i);
                    break;
                case 4:
                    e = new MON_Cabbage(screen, i);
                    break;
                case 5:
                    e = new BOSS_Watermelon(screen, i);
                    break;
            }
            if(e != null) {
                e.setPosition(spawnPointList[amount - 1][index].x, spawnPointList[amount - 1][index].y);
                container.clear();
                box.x = e.getPosition().x; box.y = e.getPosition().y;
                screen.cCheck.checkInside(box,screen.totalEntity,container);

                if(!container.isEmpty()){
                    for(Entity tar : container){

                        if(tar == e) continue;;
                        Vector2 center = new Vector2(
                            box.x + screen.tileSize/2f,
                            box.y + screen.tileSize/2f
                        );

                        Vector2 dir = tar.getPosition().cpy().add(screen.tileSize/2f,screen.tileSize/2f).sub(center);

                        float dist = dir.len();
                        if(dist < 1f){
                            dir.set(MathUtils.random(-1f,1f), MathUtils.random(-1f,1f)).nor();
                        }
                        float force = Math.max(0, 1f - dist/box.width);

                        dir.nor().scl(force * 3500f);
                        tar.forceMove(dir);
                    }
                }

                screen.am.playSound("Spawn");
                screen.particle.create(3, e.getPosition().x, e.getPosition().y, screen, "SpawnEffect", 14, 16, 0.4f);
            }

            screen.monster[i] = e;
        }
    }

    public void spawnExactly(int type, float x, float y){
        Entity e = null;
        switch(type){
            case 0:
                e = new StupidPortal(screen);
                e.setPosition(x,y);
                break;
            case 1:
                e = new NPC_Merchant(screen);
                ((NPC_Merchant) e).setShop("ShopItem/upgrade1.txt");
                e.setPosition(x,y);
                break;
            case 2:
                e = new BOSS_Watermelon(screen, -1);
                e.setPosition(x,y);
                break;
        }
        if(e!= null){
            int i;
            if(e instanceof NPC_Merchant)i = screen.npcFreeStack.pop();
            else i = screen.monsterFreeStack.pop();

            container.clear();
            box.x = e.getPosition().x; box.y = e.getPosition().y;
            screen.cCheck.checkInside(box, screen.totalEntity, container);

            if(!container.isEmpty()){
                for(Entity tar : container){
                    if(tar == e) continue;;

                    Vector2 center = new Vector2(
                        box.x + screen.tileSize/2f,
                        box.y + screen.tileSize/2f
                    );

                    Vector2 dir = tar.getPosition().cpy().add(screen.tileSize/2f,screen.tileSize/2f).sub(center);

                    float dist = dir.len();
                    if(dist < 1f){
                        dir.set(MathUtils.random(-1f,1f), MathUtils.random(-1f,1f));
                    }
                    float force = Math.max(0, 1f - dist/box.width);

                    dir.nor().scl(force * 3500f);
                    tar.forceMove(dir);
                }
                screen.am.playSound("Spawn");
                screen.particle.create(3, e.getPosition().x, e.getPosition().y, screen, "SpawnEffect", 14, 16, 0.4f);
            }
            if(e instanceof NPC_Merchant) screen.npc[i] = e;
            else screen.monster[i] = e;
        }
    }
    public boolean isCanSpawn() { return canSpawn;}
}
