package item;

import com.badlogic.gdx.graphics.Texture;
import entity.Entity;
import entity.Player;
import io.github.clapt.ffProoj.GameScreen;

public class Item  extends Entity {
    public boolean isJustDropped = false;

    public Item(GameScreen screen){
        super(screen);
    }

    @Override
    public void update(){
        if(isJustDropped){
            if(screen.player.getPosition().dst(position) >= screen.tileSize * 1.2) isJustDropped = false;
        }
    }

    public void PrimaryUse(){}
    public void SecondaryUse(){}
    public void onPickup(Player p){};

    public Texture getTexture() { return null; }

}
