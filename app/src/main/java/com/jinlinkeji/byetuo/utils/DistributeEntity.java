package com.jinlinkeji.byetuo.utils;

/**
 * Created by nijian on 2015/10/30.
 */
public class DistributeEntity {

    private int type;
    private int emergency;
    private int difficulty;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DistributeEntity(int type, int emergency, int difficulty,String content) {
        this.type = type;
        this.emergency = emergency;
        this.difficulty = difficulty;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getEmergency() {
        return emergency;
    }

    public void setEmergency(int emergency) {
        this.emergency = emergency;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
