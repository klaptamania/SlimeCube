package tile;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import java.util.HashMap;

public class TileManager {

    TiledMap map;
    OrthogonalTiledMapRenderer renderer;
    String filePath;
    HashMap<String, Integer> wallHP;
    TiledMapTileLayer destructibleLayer;

    public TileManager(){
        map = new TmxMapLoader().load("Map/TestingMap.tmx");

        renderer = new OrthogonalTiledMapRenderer(map, 2);
    }

    public void updateMap(String filePath){
        map = new TmxMapLoader().load(filePath);
        this.filePath = filePath;

        renderer = new OrthogonalTiledMapRenderer(map, 2);

        reloadWallHP();
    }

    public void render(OrthographicCamera cam){
        renderer.setView(cam);
        renderer.render();
    }

    public TiledMapTileLayer getLayer(int i){
        return (TiledMapTileLayer)map.getLayers().get(i);
    }

    public TiledMapTileLayer getLayer(String layerName){
        return (TiledMapTileLayer)map.getLayers().get(layerName);
    }

    private void reloadWallHP(){
        wallHP = new HashMap<>();
        destructibleLayer = getLayer("Destructible Object");
        for(int row = 0; row < 13; row ++){
            for(int col = 0; col < 23 ; col++ ){
                TiledMapTileLayer.Cell cell = destructibleLayer.getCell(col, row);
                if(cell != null) wallHP.put(keyString(col,row), cell.getTile().getProperties().get("HP", Integer.class));
            }
        }
    }

    public boolean isCellAvailable(int x, int y){
        TiledMapTileLayer.Cell cell = destructibleLayer.getCell(x, y);
        return cell != null;
    }
    public void wallHPDecent(int x,int y, int amount){
        wallHP.put(keyString(x,y), wallHP.get(keyString(x,y)) - amount);
        if(wallHP.get(keyString(x,y)) <= 0){
            wallHP.remove(keyString(x,y));
            destructibleLayer.setCell(x, y, null);
        }
    }

    private String keyString(int x, int y){
        return x + "," + y;
    }

    public boolean isNull(){
        return map != null;
    }

    public void loadTileLayer(TiledMapTileLayer layer, String layerName) {
        wallHP = new HashMap<>();
        if(layerName.contentEquals("Destructible Object")){
            for(int row = 0; row < 13; row ++){
                for(int col = 0; col < 23 ; col++ ){
                    TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                    if(cell != null) wallHP.put(keyString(col,row), cell.getTile().getProperties().get("HP", Integer.class));
                    else destructibleLayer.setCell(col, row, null);
                }
            }
        }
    }
}
