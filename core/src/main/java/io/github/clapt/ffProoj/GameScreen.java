package io.github.clapt.ffProoj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import entity.Entity;
import entity.Player;
import hud.HUD;
import level.Dungeon;
import particle.Particle;
import projectile.Pea;
import projectile.Projectile;
import tile.TileManager;
import item.Item;

import utilityTool.UtilityTools;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

/** First screen of the application. Displayed after the application is created. */
public class GameScreen implements Screen {
    //System
    public Main main;
    public CollisionChecker cCheck;
    public TileManager tileManager = new TileManager();
    public HUD hud;
    public OrthographicCamera cam;
    public Viewport vp;
    public AssetManager am;

    public boolean isDialog = false;


    public enum state {DIALOGUE, GAME, SHOP}
    public state currentState = state.DIALOGUE;

    public BitmapFont font;
    public final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/font.ttf"));

    public Dungeon dungeon;
    private boolean once = false;

    public Spawner spawner = new Spawner(this);

    //Object
    public Player player;
    public Entity[] npc;
    public Entity[] monster;

    public Deque<Integer> monsterFreeStack = new ArrayDeque<>();
    public Deque<Integer> npcFreeStack = new ArrayDeque<>();

    public ArrayList<Item> item = new ArrayList<>();
    public ArrayList<Entity> totalEntity;

    public ArrayList<Particle> particles = new ArrayList<>();

    public ArrayList<Integer> mDump = new ArrayList<>();

    public Particle particle = new Particle(0 ,0, null, null, 10, 10);

    public UtilityTools uTools = new UtilityTools();

    //Projectiles
    public Projectile[] projectiles;
    public ArrayList<Integer> onUsing;
    public ArrayDeque<Integer> unusingProjectiles;

    //World Setting
    int originalTileSize = 32;
    int scale = 2;
    public int tileSize = originalTileSize * scale;
    public int col = 23;
    public int row = 13;
    public int worldWidth = col * tileSize, worldHeight = row * tileSize;

    private float deadDelay = 0;

    public GameScreen(Main main){
        this.main = main;
        am = main.am;

        cam = new OrthographicCamera();
        cam.position.x = 100;
        cam.position.y = 100;
        vp = new ExtendViewport(1280,720, cam);

        player  = new Player(this);
        cCheck = new CollisionChecker(this);


        if(main.type == 1) {
            currentState = state.DIALOGUE;
        }else if(main.type == 2){
            currentState = state.GAME;
        }else if(main.type == 3){
            currentState = state.GAME;
        }

        npc = new Entity[10];
        for(int i = 9; i >= 0; i--) npcFreeStack.push(i);

        totalEntity = new ArrayList<>();
        monster = new Entity[100];

        for(int i = 99; i >= 0; i--) monsterFreeStack.push(i);

        projectiles = new Projectile[100];
        onUsing = new ArrayList<>();
        unusingProjectiles = new ArrayDeque<>();

        for(int i = 0; i < 100; i++){
            projectiles[i] = new Pea(this);
            unusingProjectiles.addLast(i);
        }


        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 45;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        font = generator.generateFont(parameter);

        dungeon = new Dungeon(this);
        dungeon.loadDungeon("Level/Dungeon1.txt");
    }

    @Override
    public void show() {
        // Prepare your screen here.

        Gdx.input.setInputProcessor(null);
        hud = new HUD(main.batch, this);
        if(player.currentWeapon != null) hud.updateWeapon(player.currentWeapon.getTexture(), player.slotIndex);
    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.

        update();
        draw();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.

        vp.update(width, height);
        hud.resize(width, height);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.

        hud.dispose();
        generator.dispose();
        font.dispose();

    }

    private void update(){
        totalEntity.clear();
        totalEntity.add(player);

        if(!mDump.isEmpty()){
            for(Integer i : mDump){
                monster[i] = null;
                monsterFreeStack.push(i);
            }
        }
        mDump.clear();

        switch(currentState){
            case DIALOGUE:
                for(Entity e : npc){
                    if(e != null) {
                        e.update();
                    }
                }

                if(main.type == 1) {
                    for(int i = particles.size() - 1; i >= 0; i--){
                        if(particles.get(i).isAlive()){
                            particles.get(i).update();
                        }else{
                            particles.remove(i);
                        }
                    }

                    for(Item i : item){
                        i.update();
                    }
                }

                for(Entity e : totalEntity) e.update();


                if(main.type != 3) dungeon.update();

                break;
            case GAME:

                if(main.type != 3) {
                    dungeon.update();
                }else{
                    dungeon.endless();
                }

                for (Entity entity : npc) {
                    if (entity != null) {
                        if (!entity.isDeath()) {
                            totalEntity.add(entity);
                        }
                    }
                }

                for(int i = 0; i < monster.length; i++){
                    if(monster[i] != null){
                        if(monster[i].isDeath()){
                            mDump.add(i);
                        }else{
                            totalEntity.add(monster[i]);
                        }
                    }
                }

                for(int i = particles.size() - 1; i >= 0; i--){
                    if(particles.get(i).isAlive()){
                        particles.get(i).update();
                    }else{
                        particles.remove(i);
                    }
                }

                if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
                    main.setScreen(new StopScreen(main));
                }

                for(Item i : item){
                    i.update();
                }

                for(Entity e : totalEntity){
                    e.update();
                }

                hud.update();

                projectilesUpdate();

                if(player.isDeath() && !player.isAlive()){
                    deadDelay += Gdx.graphics.getDeltaTime();
                    if(deadDelay >= 2.2f){
                        deadDelay -= 2.2f;
                        main.setScreen( new GameOverScreen(main));
                    }
                }

                hud.updateLabel((int)player.getHP());

                checkOutOfMap();
                break;
            case SHOP:
                for(Entity e : npc){
                    if(e != null) {
                        totalEntity.add(e);
                    }
                }

                for(Entity e : totalEntity) e.update();

                break;
        }

    }


    private void draw(){
        ScreenUtils.clear(Color.GRAY);
        tileManager.render(cam);

        main.batch.setProjectionMatrix(cam.combined);
        switch(currentState){
            case DIALOGUE:
                main.batch.begin();

                player.draw();
                for(Entity e : npc){
                    if(e != null) e.draw();
                }

                if(main.type==1) {
                    for(Item i : item){
                        i.draw();
                    }

                    for(Particle p : particles){
                        p.draw(main.batch);
                    }
                }

                if(isDialog) dungeon.draw();

                main.batch.end();

                break;
            case GAME:
                main.batch.begin();

                for(Item i : item){
                    i.draw();
                }

                for(Entity e : totalEntity){
                    e.draw();
                }

                for(Particle p : particles){
                    p.draw(main.batch);
                }

                projectilesDraw();

                main.batch.end();

                hud.render();
                break;
            case SHOP:
                main.batch.begin();

                for(Entity e : totalEntity){
                    e.draw();
                }

                main.batch.end();

                hud.render();
                break;
        }
    }

    private void projectilesUpdate(){
        if(onUsing.isEmpty()) return;

        for(int i = onUsing.size() - 1; i >= 0; i--){
            int j = onUsing.get(i);

            if(projectiles[j] == null || !projectiles[j].isAlive ) {
                unusingProjectiles.addLast(j);
                onUsing.remove(i);
                continue;
            }

            projectiles[j].update();
        }
    }

    private void projectilesDraw(){
        if(onUsing.isEmpty()) return;

        for (Integer integer : onUsing) {
            projectiles[integer].draw(main.batch);
        }
    }


    public Vector3 getMouseDir(){
        Vector3 mouseDir = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cam.unproject(mouseDir);
        return mouseDir;
    }

    private void checkOutOfMap(){
        for(Entity e : totalEntity){
            if((e.getPosition().x / tileSize > col + 1 || e.getPosition().x / tileSize < -1) || e.getPosition().y / tileSize > row + 1 || e.getPosition().y / tileSize < -1){
                e.forceDead();
            }
        }
    }
}
