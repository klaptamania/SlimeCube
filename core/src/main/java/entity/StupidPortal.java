package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.clapt.ffProoj.GameScreen;
import io.github.clapt.ffProoj.MenuScreen;

public class StupidPortal extends Entity{

    private static Texture sheet;

    public StupidPortal(GameScreen screen){
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
        if(sheet == null) sheet = screen.am.getTexture("Portal");
        current = new TextureRegion(sheet);

        //Attributes
        speed = 40;
        HP = 25;
        maxHP = HP;

        currentWeapon = null;
    }

    @Override
    public void update(){
        if(!screen.isDialog) checkCollision();
        animationUpdate(false, timer);
    }

    @Override
    public void takeDamage(int amount){};

    @Override
    public void takeForceDamage(int amount){}

    @Override
    public void checkCollision(){

        int i = 0;

        if(screen.cCheck.checkPlayerX(this, screen.player)){
            i = 1;
        }
        if(screen.cCheck.checkPlayerY(this, screen.player)){
            i = 1;
        }

        if(i != 0){
            screen.dungeon.loadDungeon("Level/Dungeon2.txt");
            isDeath = true;
            isAlive = false;

            for(int ir = 0; ir < screen.npc.length; ir++){
                if(screen.npc[ir] != null && screen.npc[ir] instanceof StupidPortal){
                    screen.npcFreeStack.push(ir);
                    screen.npc[ir] = null;
                }
            }

            if(screen.main.type == 1) screen.main.type++;
        }

    }
}
