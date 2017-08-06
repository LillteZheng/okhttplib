package com.rachel.okhttp;

/**
 * Created by zhengshaorui on 2017/8/6.
 */

public class Root {

    /**
     * name : 电视管家
     * versioncode : 3
     * versionname : v1.2
     * content : 内容
     * url : http://upgrade.toptech-developer.com/file/TvHouseManager/TvHouseManager.apk
     * isemenrgant : false
     */

    private String name;
    private int versioncode;
    private String versionname;
    private String content;
    private String url;
    private String isemenrgant;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }

    public String getVersionname() {
        return versionname;
    }

    public void setVersionname(String versionname) {
        this.versionname = versionname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIsemenrgant() {
        return isemenrgant;
    }

    public void setIsemenrgant(String isemenrgant) {
        this.isemenrgant = isemenrgant;
    }
}
