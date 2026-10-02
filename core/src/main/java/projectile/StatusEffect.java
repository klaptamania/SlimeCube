package projectile;

public class StatusEffect {
    int level;
    int maxLevel;
    float duration;

    public StatusEffect(int level, int maxLevel, float duration){
        this.level = level;
        this.maxLevel = maxLevel;
        this.duration = duration;
    }

    public int getLevel(){return level;}
    public int getMaxLevel(){return maxLevel;}
    public float getDuration(){return duration;}

    public void setLevel(int level){ this.level += level;}
    public void setDuration(float duration){ this.duration = duration;}
}
