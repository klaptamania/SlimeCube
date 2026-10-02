package io.github.clapt.ffProoj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import entity.Entity;
import entity.StupidPortal;
import item.Item;
import utilityTool.HitDetect;

import java.util.ArrayList;
import java.util.Vector;

public class CollisionChecker {
    GameScreen screen;
    TiledMapTileLayer colLayer;
    TiledMapTileLayer destructableLayer;
    TiledMapTileLayer gateLayer;

    private final Rectangle tmp = new Rectangle();
    private final Vector2 one = new Vector2();
    private final Vector2 two = new Vector2();
    private final Vector2 three = new Vector2();

    public CollisionChecker(GameScreen screen){
        this.screen = screen;

        reloadMap();
    }

    public boolean checkTileX(Entity entity){
        float nextX = entity.getPosition().x + entity.getVelocity().x * Gdx.graphics.getDeltaTime();
        int nextTileX = -999;

        if(entity.getVelocity().x > 0) nextTileX = (int)((nextX + entity.getHitbox().width + entity.getHitbox().x)/ screen.tileSize);
        else if(entity.getVelocity().x < 0) nextTileX = (int)((nextX + entity.getHitbox().x)/screen.tileSize);

        int topTileY = (int)((entity.getPosition().y + entity.getHitbox().y + entity.getHitbox().height)/ screen.tileSize);
        int botTileY = (int)((entity.getPosition().y + entity.getHitbox().y)/ screen.tileSize);

        TiledMapTileLayer.Cell tileUpY = colLayer.getCell(nextTileX, topTileY);
        TiledMapTileLayer.Cell tileDoY = colLayer.getCell(nextTileX, botTileY);

        TiledMapTileLayer.Cell detruUpY = destructableLayer.getCell(nextTileX, topTileY);
        TiledMapTileLayer.Cell detruDoY = destructableLayer.getCell(nextTileX, botTileY);

        TiledMapTileLayer.Cell gateUpY = gateLayer.getCell(nextTileX, topTileY);
        TiledMapTileLayer.Cell gateDoY = gateLayer.getCell(nextTileX, botTileY);

        return (tileUpY != null || tileDoY != null || detruUpY != null || detruDoY != null || gateUpY != null || gateDoY != null);
    }

    public boolean checkTileY(Entity entity){
        float nextY = entity.getPosition().y + entity.getVelocity().y * Gdx.graphics.getDeltaTime();

        int nextTileY = -999;

        if(entity.getVelocity().y > 0) nextTileY = (int)((nextY + entity.getHitbox().y + entity.getHitbox().height) / screen.tileSize);
        else if(entity.getVelocity().y < 0) nextTileY = (int)((nextY + entity.getHitbox().y) / screen.tileSize);

        int leftTileX = (int)((entity.getPosition().x + entity.getHitbox().x) / screen.tileSize);
        int rightTileX = (int)((entity.getPosition().x + entity.getHitbox().x + entity.getHitbox().width) / screen.tileSize);

        TiledMapTileLayer.Cell tileLeX = colLayer.getCell(leftTileX, nextTileY);
        TiledMapTileLayer.Cell tileRiX = colLayer.getCell(rightTileX, nextTileY);

        TiledMapTileLayer.Cell detruUpX = destructableLayer.getCell(leftTileX, nextTileY);
        TiledMapTileLayer.Cell detruDoX = destructableLayer.getCell(rightTileX, nextTileY);

        TiledMapTileLayer.Cell gateUpX = gateLayer.getCell(leftTileX, nextTileY);
        TiledMapTileLayer.Cell gateDoX = gateLayer.getCell(rightTileX, nextTileY);

        return (tileLeX != null || tileRiX != null || detruUpX != null || detruDoX != null || gateUpX != null || gateDoX != null);

    }

    public int checkBullet(float oldX, float oldY, float newX, float newY, ArrayList<Entity> entity, Entity user, float bulletRadius){
        int index = -999;

        for(int i = 0; i < entity.size(); i++){
            if(entity.get(i) == null || !entity.get(i).isAlive() || entity.get(i) == user) continue;

            tmp.set(entity.get(i).getHitbox());

            tmp.x += entity.get(i).getPosition().x - bulletRadius;
            tmp.y += entity.get(i).getPosition().y - bulletRadius;
            tmp.width += bulletRadius * 2;
            tmp.height += bulletRadius * 2;

            boolean hit = Intersector.intersectSegmentRectangle(oldX, oldY, newX, newY, tmp);
            if(hit){
                index = i;
                break;
            }
        }

        return index;
    }

    public void checkBullet(float oldX, float oldY, float newX, float newY, ArrayList<Entity> entity, Entity user, float bulletRadius, ArrayList<Entity> container){

        for (Entity value : entity) {
            if (value == null || !value.isAlive() || value == user) continue;

            tmp.set(value.getHitbox());

            tmp.x += value.getPosition().x - bulletRadius;
            tmp.y += value.getPosition().y - bulletRadius;
            tmp.width += bulletRadius * 2;
            tmp.height += bulletRadius * 2;

            boolean hit = Intersector.intersectSegmentRectangle(oldX, oldY, newX, newY, tmp);
            if (hit) {
                container.add(value);
            }
        }

    }

    public boolean checkTileSpeed(Entity entity){
        one.set(entity.getTrueHitbox().x + entity.getHitbox().width/2f, entity.getTrueHitbox().y + entity.getHitbox().height/2f );
        two.set(one).mulAdd(entity.getVelocity(), Gdx.graphics.getDeltaTime());

        float dst = one.dst(two);

        if(dst == 0) return false;

        float halfW = entity.getHitbox().width / 2f;
        float halfH = entity.getHitbox().height / 2f;


        for(float i = 0; i <= dst; i += screen.tileSize / 4f) {
            float alpha = i / dst;
            float x = MathUtils.lerp(one.x, two.x, alpha);
            float y = MathUtils.lerp(one.y, two.y, alpha);
            tmp.set(
                x - halfW,
                y - halfH,
                entity.getHitbox().width,
                entity.getHitbox().height
            );

            float startX = (tmp.x / screen.tileSize);
            float endX =  ((tmp.x + tmp.width) / screen.tileSize);

            float startY = (tmp.y / screen.tileSize);
            float endY = ((tmp.y + tmp.height) / screen.tileSize);

            for(float ty = startY; ty <= endY; ty++){
                for(float tx = startX; tx <= endX; tx++){
                    if(colLayer.getCell((int)tx, (int)ty) != null || gateLayer.getCell((int)tx, (int)ty) != null){

                        entity.setPosition(tx,ty);

                        return true;
                    }
                }
            }

        }
        return false;
    }

    public boolean checkTileHighSpeed(Entity entity){
        //getMovement
        float deltaTime = Gdx.graphics.getDeltaTime();
        three.set(entity.getVelocity().cpy()).scl(deltaTime);

        //get full Broad
        float broadLeft =
            Math.min(entity.getTrueHitbox().x,
                entity.getTrueHitbox().x + three.x);

        float broadBottom =
            Math.min(entity.getTrueHitbox().y,
                entity.getTrueHitbox().y + three.y);

        float broadRight =
            Math.max(entity.getTrueHitbox().x + entity.getHitbox().width,
                entity.getTrueHitbox().x + three.x + entity.getHitbox().width);

        float broadTop =
            Math.max(entity.getTrueHitbox().y + entity.getHitbox().height,
                entity.getTrueHitbox().y + three.y + entity.getHitbox().height);

        //getCenteredPosition
        one.set(screen.uTools.getCentered(entity));

        //get all border
        int left = (int)(broadLeft / screen.tileSize);
        int right = (int)(broadRight / screen.tileSize);
        int top = (int)(broadTop / screen.tileSize);
        int bot = (int)(broadBottom / screen.tileSize);

        left  = Math.max(left, 0);
        bot   = Math.max(bot, 0);

        right = Math.min(right, screen.col - 1);
        top   = Math.min(top, screen.row - 1);

        HitDetect nearest = null;
        float nearestTime = 1f;
        for(int i = bot; i <= top; i++){
            for(int j = left; j <= right; j++){
                if(colLayer.getCell(j,i) != null || destructableLayer.getCell(j,i) != null || gateLayer.getCell(j,i) != null){
                    HitDetect hit = sweptAABB(entity.getTrueHitbox().x, entity.getTrueHitbox().y, entity.getTrueHitbox().width, entity.getTrueHitbox().height, entity.getVelocity().cpy(),
                        j * screen.tileSize, i * screen.tileSize, screen.tileSize, screen.tileSize);

                    if(hit.hit && hit.entryTime < nearestTime){
                        nearest = hit;
                        nearestTime = hit.entryTime;
                    }
                }
            }
        }

        if(nearest != null){
            Vector2 pos = new Vector2(entity.getTrueHitbox().x, entity.getTrueHitbox().y);
            pos.mulAdd(entity.getVelocity(), (nearestTime - 0.0001f) * deltaTime);
            pos.sub(screen.tileSize/2f, screen.tileSize/2f);
            entity.setPosition(pos);

            if(nearest.hitDetect.x != 0) entity.setVelocity(0, entity.getVelocity().y);
            if(nearest.hitDetect.y != 0) entity.setVelocity(entity.getVelocity().x, 0);

            return true;
        }
        return false;
    }

    public HitDetect DDA_Boarding(Entity entity){
        HitDetect hitDetect = new HitDetect();
        hitDetect.entryTime = 0;
        hitDetect.hit = false;
        hitDetect.tileX = -1;
        hitDetect.tileY = -1;
        hitDetect.hitDetect = new Vector2(0,0);
        hitDetect.velo = new Vector2();

        Vector2 current = entity.getPosition().cpy().add(screen.tileSize/2f, screen.tileSize/2f);
        Vector2 destination = current.cpy().mulAdd(entity.getVelocity(), Gdx.graphics.getDeltaTime());
        Vector2 direction = destination.cpy().sub(current).nor();
        float length = destination.dst(current);

        //get current cell
        int cellX = (int) (current.x / screen.tileSize);
        int cellY = (int) (current.y / screen.tileSize);

        //get the deltaDistance
        float deltaDistX = screen.tileSize / Math.abs(direction.x);
        float deltaDistY = screen.tileSize / Math.abs(direction.y);

        //get step and the distance to the next edge;
        int stepX, stepY;
        float sideDistX, sideDistY, tMaxX, tMaxY;
        if(direction.x < 0){
            stepX = -1;
            sideDistX = current.x - (cellX) * screen.tileSize;
        }else{
            stepX = 1;
            sideDistX = (cellX + 1) * screen.tileSize - current.x;
        }

        tMaxX = sideDistX / Math.abs(direction.x);

        if(direction.y < 0){
            stepY = -1;
            sideDistY = current.y - (cellY) * screen.tileSize;
        }else{
            stepY = 1;
            sideDistY = (cellY + 1) * screen.tileSize - current.y;
        }

        tMaxY= sideDistY / Math.abs(direction.y);

        int hitSide = -1;
        while(Math.min(tMaxX, tMaxY) < length){
            if (tMaxX < tMaxY) {
                cellX += stepX;
                tMaxX += deltaDistX;
                hitSide = 0;
            } else {
                cellY += stepY;
                tMaxY += deltaDistY;
                hitSide = 1;
            }

            if (cellX < 0 || cellY < 0 || cellX >= screen.col || cellY >= screen.row) {
                hitDetect.tileX = cellX;
                hitDetect.tileY = cellY;
                break;
            }

            if (isWall(cellX * screen.tileSize, cellY * screen.tileSize)) {
                hitDetect.hit = true;
                hitDetect.tileX = cellX;
                hitDetect.tileY = cellY;

                float hitDistance = (hitSide == 0) ? (tMaxX) : (tMaxY);

                hitDistance = Math.max(0, hitDistance);

                hitDetect.entryTime = hitDistance / length;

                // Vector dịch chuyển thực tế từ vị trí cũ tới điểm chạm tường
                hitDetect.velo.set(direction).scl(hitDistance);

                // 4. Correct Distance & Normal Calculation
                if (hitSide == 0) {
                    hitDetect.hitDetect.set(-stepX, 0);
                } else {
                    hitDetect.hitDetect.set(0, -stepY);
                }
                return hitDetect;
            }

            if (destroyWall(cellX * screen.tileSize, cellY * screen.tileSize, 10000)) {
                return hitDetect;
            }
        }

        return hitDetect;
    }

    public boolean DDAcheckBulletWall(float oldX, float oldY, float newX, float newY, float bulletRadius, int amount){
        //get position
        one.set(oldX, oldY);
        //get destination
        two.set(newX, newY);
        //getDirection
        Vector2 direction = two.cpy().sub(one).nor();

        //get currentCell
        int cellX = (int)one.x / screen.tileSize;
        int cellY = (int)one.y / screen.tileSize;

        //get step
        int stepX, stepY;
        if(direction.x > 0) stepX = 1;
        else if(direction.x < 0) stepX = -1;
        else stepX = 0;

        if(direction.y > 0) stepY = 1;
        else if(direction.y < 0) stepY = -1;
        else stepY = 0;

        //Calculate how far from position to the nearest edge
        float tMaxX, tMaxY, dst;

        if(direction.x == 0) tMaxX = Float.POSITIVE_INFINITY;
        else{
            if(direction.x > 0){
                dst = (cellX + 1) * screen.tileSize - one.x;
            }else dst = one.x - cellX * screen.tileSize;

            tMaxX = dst / Math.abs(direction.x);
        }
        if(direction.y == 0) tMaxY = Float.POSITIVE_INFINITY;
        else{
            if(direction.y > 0){
                dst = (cellY + 1) * screen.tileSize - one.y;
            }else dst = one.y - cellY * screen.tileSize;

            tMaxY = dst / Math.abs(direction.y);
        }

        //Calc delta
        float deltaX = direction.x == 0 ? Float.POSITIVE_INFINITY : screen.tileSize / Math.abs(direction.x);
        float deltaY = direction.y == 0 ? Float.POSITIVE_INFINITY : screen.tileSize / Math.abs(direction.y);

        float maxT = one.dst(two);

        while(Math.min(tMaxX, tMaxY) <= maxT){

            if (tMaxX < tMaxY) {
                tMaxX += deltaX;
                cellX += stepX;
            } else if (tMaxY < tMaxX) {
                tMaxY += deltaY;
                cellY += stepY;
            } else {
                tMaxX += deltaX;
                tMaxY += deltaY;
                cellX += stepX;
                cellY += stepY;
            }

            if (cellX < 0 || cellY < 0 ||
                cellX >= screen.col || cellY >= screen.row) {
                break;
            }

            if (isWall(cellX * screen.tileSize, cellY * screen.tileSize))
                return true;

            if (destroyWall(cellX * screen.tileSize, cellY * screen.tileSize, amount))
                return true;
        }

        return false;
    }

    public int checkEntitySpeed(Entity entity, ArrayList<Entity> entities){
        int index = -999;
        one.set(entity.getPosition().cpy()
            .mulAdd(entity.getVelocity().cpy(), Gdx.graphics.getDeltaTime()));
        for(int i = 0; i < entities.size(); i++){
            Entity e = entities.get(i);
            if(e == null || !e.isAlive() || e == entity) continue;

            tmp.set(e.getTrueHitbox());

            tmp.x -= entity.getTrueHitbox().width;
            tmp.y -= entity.getTrueHitbox().height;
            tmp.width += entity.getTrueHitbox().width * 2;
            tmp.height += entity.getTrueHitbox().height * 2;

            boolean hit = Intersector.intersectSegmentRectangle(
                entity.getTrueHitbox().x + entity.getTrueHitbox().width / 2f,
                entity.getTrueHitbox().y + entity.getTrueHitbox().height / 2f,
                one.x,
                one.y,
                tmp
            );

            if(hit){
                index = i;
                break;
            }
        }

        return index;
    }

    private boolean isWall(float x, float y) {
        x /= screen.tileSize;
        y /= screen.tileSize;
        TiledMapTileLayer.Cell tile = colLayer.getCell((int)x, (int) y);
        TiledMapTileLayer.Cell tilee = gateLayer.getCell((int)x, (int) y);
        return tile != null || tilee != null;
    }

    private boolean destroyWall(float x, float y, int amount){
        int tx = (int)(x / screen.tileSize);
        int ty = (int)(y / screen.tileSize);

        if(screen.tileManager.isCellAvailable(tx,ty)){
            screen.tileManager.wallHPDecent(tx,ty, amount);

            return true;
        }

        return false;
    }


    public boolean checkEntityX(Entity entity, ArrayList<Entity> entities){
        float nextX = entity.getPosition().x + entity.getHitbox().x + entity.getVelocity().x * Gdx.graphics.getDeltaTime();

        tmp.set(nextX, entity.getPosition().y + entity.getHitbox().y, entity.getHitbox().width, entity.getHitbox().height);

        for(Entity e : entities){
            if(e == null || e == entity || !e.isAlive() || (e instanceof StupidPortal)) continue;

            if(tmp.overlaps(e.getTrueHitbox())) return true;
        }

        return false;
    }

    public boolean checkEntityY(Entity entity, ArrayList<Entity> entities){
        float nextY = entity.getPosition().y + entity.getHitbox().y + entity.getVelocity().y * Gdx.graphics.getDeltaTime();

        tmp.set(entity.getPosition().x + entity.getHitbox().x,nextY, entity.getHitbox().width, entity.getHitbox().height);

        for(Entity e : entities){
            if(e == null || e == entity || !e.isAlive() || (e instanceof StupidPortal)) continue;

            if(tmp.overlaps(e.getTrueHitbox())) return true;
        }

        return false;
    }

    public void checkInside(Rectangle rect, ArrayList<Entity> entities, ArrayList<Entity> container, Entity user){
        container.clear();

        for(Entity e : entities){
            if(e == null || !e.isAlive() || e == user) continue;

            if(rect.overlaps(e.getTrueHitbox())) container.add(e);
        }
    }

    public void checkInside(Rectangle rect, ArrayList<Entity> entities, ArrayList<Entity> container){
        checkInside(rect, entities, container, null);
    }

    public boolean checkPlayerX(Entity entity, Entity player){
       if(entity == player) return false;

       float nextX = entity.getPosition().x + entity.getHitbox().x + entity.getVelocity().x * Gdx.graphics.getDeltaTime();
       tmp.set(nextX, entity.getPosition().y + entity.getHitbox().y, entity.getHitbox().width, entity.getHitbox().height);

       return tmp.overlaps(player.getTrueHitbox());
    }
    public boolean checkPlayerY(Entity entity, Entity player){
        if(entity == player) return false;

        float nextY = entity.getPosition().y + entity.getHitbox().y + entity.getVelocity().y * Gdx.graphics.getDeltaTime();
        tmp.set(entity.getPosition().x + entity.getHitbox().x,nextY, entity.getHitbox().width, entity.getHitbox().height);

        return tmp.overlaps(player.getTrueHitbox());
    }

    public int checkItem(ArrayList<Item> entity, Entity player){

        float nextX = player.getPosition().x + player.getHitbox().x + player.getVelocity().x * Gdx.graphics.getDeltaTime();
        float nextY = player.getPosition().y + player.getHitbox().y + player.getVelocity().y * Gdx.graphics.getDeltaTime();
        tmp.set(nextX,nextY, player.getHitbox().width, player.getHitbox().height);

        for (int i = 0; i < entity.size(); i++) {
            if (tmp.overlaps(entity.get(i).getTrueHitbox())) return i;
        }

        return -1;
    }

    public void destroyTileArea(Rectangle area, int amount){
        int startX = (int)(area.x / screen.tileSize);
        int endX = (int)((area.x + area.width) / screen.tileSize);

        int startY = (int)(area.y / screen.tileSize);
        int endY = (int)((area.y + area.height) / screen.tileSize);

        for(int i = startY ; i <= endY; i++){
            for(int j = startX ; j <= endX; j++){
                if(screen.tileManager.isCellAvailable(j,i)){
                    screen.tileManager.wallHPDecent(j,i, amount);
                }
            }
        }
    }

    public int sweptCheckEntity(Entity entity){
        int index = -999;
        float nearestTime = 1f;
        HitDetect nearestHit = null;
        for(int i = 0; i < screen.totalEntity.size(); i ++){
            Entity e = screen.totalEntity.get(i);
            if( e == entity) continue;

            HitDetect hit = sweptAABB(entity.getTrueHitbox().x, entity.getTrueHitbox().y, entity.getHitbox().width, entity.getHitbox().height,
               entity.getVelocity(), e.getTrueHitbox().x, e.getTrueHitbox().y, e.getHitbox().width, e.getTrueHitbox().height);

                if(hit.hit && hit.entryTime < nearestTime){
                    nearestTime = hit.entryTime;
                    nearestHit = hit;
                    index = i;
                }
        }

        if(nearestHit != null) {
            Vector2 pos = entity.getPosition().cpy();
            nearestTime = Math.max(0f, nearestTime - 0.0001f);
            pos.mulAdd(entity.getVelocity(), nearestTime * Gdx.graphics.getDeltaTime());
            entity.setPosition(pos);

            if(nearestHit.hitDetect.x < 0 && entity.getVelocity().x > 0 || nearestHit.hitDetect.x > 0 && entity.getVelocity().x < 0 ) entity.setVelocity(0, entity.getVelocity().y);
            if(nearestHit.hitDetect.y < 0 && entity.getVelocity().y > 0 || nearestHit.hitDetect.y > 0 && entity.getVelocity().y < 0 ) entity.setVelocity(entity.getVelocity().x, 0);
        }

        return index;
    }

    public HitDetect sweptAABB(float currentX, float currentY, float currentW, float currentH, Vector2 velocity, float destX, float destY, float destW, float destH){
        HitDetect hit = new HitDetect();
        //Get the next Position
        two.set(velocity.cpy()).scl(Gdx.graphics.getDeltaTime());
        //Make the dest FATTER
        one.set(screen.uTools.getCentered(currentX, currentY, currentW, currentH));

        currentX = one.x;
        currentY = one.y;


        one.set(screen.uTools.getCentered(destX, destY, destW, destH));
        destX = one.x;
        destY = one.y;
        destW += currentW;
        destH += currentH;

        //Calc every point
        float left = destX - destW / 2f;
        float right = destX + destW / 2f;
        float top = destY + destH / 2f;
        float bot = destY - destH / 2f;

        if(currentX >= left && currentX <= right && currentY >= bot && currentY <= top){
            hit.hitDetect = new Vector2(1,1);
            hit.hit = true;
            hit.entryTime = 0;
            return hit;
        }

        float tx1, tx2, ty1, ty2;
        //Calc time
        if (two.x == 0) {
            if (currentX < left || currentX > right) {
                hit.hit = false;
                hit.entryTime = Float.POSITIVE_INFINITY;
                return hit;
            }
            tx1 = Float.NEGATIVE_INFINITY;
            tx2 = Float.POSITIVE_INFINITY;

        }else{
            tx1 = (left - currentX) / two.x;
            tx2 = (right - currentX) / two.x;
        }

        if(two.y == 0){
            if(currentY < bot || currentY > top){
                hit.hit= false;
                hit.entryTime = Float.POSITIVE_INFINITY;
                return hit;
            }

            ty1 = Float.NEGATIVE_INFINITY;
            ty2 = Float.POSITIVE_INFINITY;
        }else{
            ty1 = (bot - currentY) / two.y;
            ty2 = (top - currentY) / two.y;
        }

        //Calc entry & exit

        float entryTimeX = Math.min(tx1,tx2);
        float exitTimeX = Math.max(tx1,tx2);
        float entryTimeY = Math.min(ty1,ty2);
        float exitTimeY = Math.max(ty1,ty2);

        float entryTime = Math.max(entryTimeX, entryTimeY);
        float exitTime = Math.min(exitTimeX, exitTimeY);

        if(entryTime <= exitTime && (exitTime >= 0 && entryTime <= 1 && entryTime >= 0)){
            if(entryTimeX > entryTimeY){
                if(two.x > 0){
                    one.set(-1, 0);
                }else if(two.x < 0){
                    one.set(1,0);
                }
            }else{
                if(two.y > 0){
                    one.set(0, -1);
                }else if(two.y < 0){
                    one.set(0,1);
                }
            }
            hit.hit = true;
        }

        if(hit.hit){
            hit.entryTime = entryTime;
        }else hit.entryTime = Float.POSITIVE_INFINITY;
        hit.hitDetect = one.cpy();

        return hit;
    }

    public HitDetect sweptAABBNewer(float currentX, float currentY, float currentW, float currentH,
                               Vector2 move, // Truyền trực tiếp Vector dịch chuyển (velocity * dt)
                               float destX, float destY, float destW, float destH) {

        HitDetect hit = new HitDetect();
        hit.hitDetect = new Vector2(0, 0);

        // 1. Mở rộng tường theo kích thước của Entity (Minkowski Sum)
        float expandedX = destX - currentW;
        float expandedY = destY - currentH;
        float expandedW = destW + currentW;
        float expandedH = destH + currentH;

        // Tọa độ góc của ô tường mở rộng
        float left   = expandedX;
        float right  = expandedX + expandedW;
        float bot    = expandedY;
        float top    = expandedY + expandedH;

        // 2. Nếu điểm xuất phát đã nằm BÊN TRONG tường mở rộng -> Va chạm ngay từ đầu (entryTime = 0)
        if (currentX > left && currentX < right && currentY > bot && currentY < top) {
            hit.hit = true;
            hit.entryTime = 0;
            hit.hitDetect.set(1, 1); // Trả về để kích hoạt nảy
            return hit;
        }

        // 3. Tính khoảng thời gian va chạm trên 2 trục X và Y
        float tx1, tx2, ty1, ty2;

        if (move.x == 0) {
            if (currentX <= left || currentX >= right) {
                hit.hit = false;
                hit.entryTime = Float.POSITIVE_INFINITY;
                return hit;
            }
            tx1 = Float.NEGATIVE_INFINITY;
            tx2 = Float.POSITIVE_INFINITY;
        } else {
            tx1 = (left - currentX) / move.x;
            tx2 = (right - currentX) / move.x;
        }

        if (move.y == 0) {
            if (currentY <= bot || currentY >= top) {
                hit.hit = false;
                hit.entryTime = Float.POSITIVE_INFINITY;
                return hit;
            }
            ty1 = Float.NEGATIVE_INFINITY;
            ty2 = Float.POSITIVE_INFINITY;
        } else {
            ty1 = (bot - currentY) / move.y;
            ty2 = (top - currentY) / move.y;
        }

        // Lấy thời điểm vào/ra thực sự
        float entryTimeX = Math.min(tx1, tx2);
        float exitTimeX  = Math.max(tx1, tx2);
        float entryTimeY = Math.min(ty1, ty2);
        float exitTimeY  = Math.max(ty1, ty2);

        float entryTime = Math.max(entryTimeX, entryTimeY);
        float exitTime  = Math.min(exitTimeX, exitTimeY);

        // 4. Kiểm tra điều kiện cắt nhau hợp lệ trong khoảng [0, 1]
        if (entryTime <= exitTime && exitTime >= 0 && entryTime >= 0 && entryTime <= 1.0f) {
            hit.hit = true;
            hit.entryTime = entryTime;

            // Xác định mặt tường bị đâm vào để trả về Normal Vector
            if (entryTimeX > entryTimeY) {
                if (move.x > 0) hit.hitDetect.set(-1, 0); // Đâm vào vách bên trái
                else hit.hitDetect.set(1, 0);           // Đâm vào vách bên phải
            } else {
                if (move.y > 0) hit.hitDetect.set(0, -1); // Đâm vào vách dưới
                else hit.hitDetect.set(0, 1);           // Đâm vào vách trên
            }
        } else {
            hit.hit = false;
            hit.entryTime = Float.POSITIVE_INFINITY;
        }

        return hit;
    }

    public void reloadMap(){
        colLayer = screen.tileManager.getLayer("Undestructible Object");
        destructableLayer = screen.tileManager.getLayer("Destructible Object");
        gateLayer = screen.tileManager.getLayer("Gate");
    }
}
