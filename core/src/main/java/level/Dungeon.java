package level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import io.github.clapt.ffProoj.GameScreen;

import java.io.BufferedReader;
import java.io.IOException;

public class Dungeon {
    GameScreen screen;

    private int[][] roomIndex = new int[10][10];
    private int x, y;
    private Room currentRoom;
    private Room[][] roomContainer = new Room[10][10];

    public Dungeon(GameScreen screen) { this.screen = screen; }
    private boolean setted = false;
    Json json = new Json();

    String[] mapPath = {"Level/Room1.json", "Level/Room2.json"};

    private String dungeonPath;

    public void loadDungeon(String dungeonPath){
        this.dungeonPath = dungeonPath;
        roomContainer = new Room[10][10];
        roomIndex = new int[10][10];

        if(dungeonPath != null){
            try {
                int j = 0;
                BufferedReader br = Gdx.files.internal(dungeonPath).reader(1024);
                String line;
                while((line = br.readLine()) != null){
                    String[] container = line.split(",");

                    for(int i = 0; i < container.length  && i < roomIndex[j].length; i++){
                        roomIndex[j][i] = Integer.parseInt(container[i].trim());

                        switch(roomIndex[j][i]){
                            case -1:
                                x = i; y = j;
                                roomContainer[y][x] = json.fromJson(Room.class, Gdx.files.internal("Level/Intro.json"));
                                currentRoom = roomContainer[y][x];
                                break;
                            case 1:
                                x = i; y = j;
                                int ran = MathUtils.random(0,1);
                                roomContainer[y][x] = json.fromJson(Room.class, Gdx.files.internal(mapPath[ran]));
                                currentRoom = roomContainer[y][x];
                                break;
                        }
                    }
                    j++;
                }

                setGateDirection(currentRoom,x,y);

                if(dungeonPath.equals("Level/Dungeon2.txt")){
                    for (int[] rooms : roomIndex) {
                        for (int u = 0; u < roomIndex[y].length; u++) {
                            System.out.print(rooms[u] + " ");
                        }
                        System.out.println();
                    }

                    System.out.println(roomIndex[0][2]);
                    System.out.println(roomIndex[1][2]);
                    System.out.println(roomIndex[y][x]);
                    System.out.println(y + " " + x);
                }

                br.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            loadMap();
        }
    }

    private void setGateDirection(Room room, int x, int y) {
        room.gateDirection.clear();
        if( y - 1 >= 0 && roomIndex[y - 1][x] != 0){
            room.gateDirection.add(Room.Direction.UP);
        }
        if(y + 1 <= 9 && roomIndex[y + 1][x] != 0 ){
            room.gateDirection.add(Room.Direction.DOWN);
        }
        if(x + 1 <= 9 && roomIndex[y][x + 1] != 0){
            room.gateDirection.add(Room.Direction.RIGHT);
        }
        if(x - 1 >= 0 && roomIndex[y][x - 1] != 0 ){
            room.gateDirection.add(Room.Direction.LEFT);
        }
    }

    public void loadMap(){
        currentRoom.loadMap(screen);
    }

    public void update(){
        currentRoom.update(screen);
        if(currentRoom.playerMoveDirX != 0 || currentRoom.playerMoveDirY != 0){
            int ran = MathUtils.random(0,1);
            if(currentRoom.playerMoveDirX != 0){
                x += currentRoom.playerMoveDirX;
                System.out.println(x);
            }else{
                y += currentRoom.playerMoveDirY;
                System.out.println(y);
            }

            if(roomContainer[y][x] == null) {

                switch(roomIndex[y][x]){
                    case 2:
                        roomContainer[y][x] = json.fromJson(Room.class, Gdx.files.internal(mapPath[ran]));
                        break;
                    case 3:
                        roomContainer[y][x] = json.fromJson(Room.class, Gdx.files.internal("Level/MerchantRoom.json"));
                        break;
                }

            }
            roomContainer[y][x].playerMoveDirX = 0;
            roomContainer[y][x].playerMoveDirY = 0;

            currentRoom = roomContainer[y][x];
            if(currentRoom.isDone){
                currentRoom.gateOpened = false;
            }
            setGateDirection(currentRoom, x, y);
            currentRoom.loadMap(screen);
        }
    }

    public void draw(){
        currentRoom.draw();
    }

    public void endless(){

        if(!setted) {
            currentRoom = new Room();
            currentRoom = json.fromJson(Room.class, Gdx.files.internal("Level/Endless.json"));
            currentRoom.loadMap(screen);
            setted = true;
        }

        update();
    }

    public String getFilePath() { return dungeonPath;}
    public String getCurrentRoomPath() { return currentRoom.mapPath;}
}
