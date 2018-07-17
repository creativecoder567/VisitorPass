package com.example.vinoth.blogreader.model;

/**
 * Created by vinoth on 10/11/17.
 */

public class News {
    private String id;
    private String title;
    private String body;
    private String createAt;
    private String bannerUrl;

    public News() {
    }

    public News(String title, String body, String bannerUrl) {
        this.title = title;
        this.body = body;
        this.bannerUrl = bannerUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
