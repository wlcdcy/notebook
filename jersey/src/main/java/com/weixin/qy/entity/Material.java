package com.weixin.qy.entity;

import java.util.Map;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Administrator 素材
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Material {
    private int agentid;
    private String media_id;
    private Map<String, ?> mpnews;

    public int getAgentid() {
        return agentid;
    }

    public void setAgentid(int agentid) {
        this.agentid = agentid;
    }

    public Map<String, ?> getMpnews() {
        return mpnews;
    }

    public void setMpnews(Map<String, ?> mpnews) {
        this.mpnews = mpnews;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

}
