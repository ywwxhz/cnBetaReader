package com.ywwxhz.entitys;

import com.ywwxhz.lib.database.annotation.Id;

import java.io.Serializable;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/3 20:47.
 */
public class TopicItem implements Serializable{
    @Id
    private String topicId;
    private String latter;
    private String topicName;
    private String topicImage;
    private boolean saved = false;

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }


    public TopicItem(String latter,String topicId,  String topicName,String topicImage,boolean save) {
        this.latter = latter;
        this.topicId = topicId;
        this.topicImage = topicImage;
        this.topicName = topicName;
        this.saved = save;
    }

    public TopicItem() {
    }

    public String getTopicId() {
        return topicId;
    }
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
    public String getTopicName() {
        return topicName;
    }
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
    public String getTopicImage() {
        return topicImage;
    }
    public void setTopicImage(String topicImage) {
        this.topicImage = topicImage;
    }
    public String getLatter() {
        return latter;
    }
    public void setLatter(String latter) {
        this.latter = latter;
    }
}
