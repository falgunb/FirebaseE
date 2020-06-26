package com.example.firebasee.model;

public class Post {
    private String description;
    private String postId;
    private String imageurl;
    private String publisher;

    public Post() {
    }

    public Post(String description, String postId,String imageurl, String publisher) {
        this.description = description;
        this.postId = postId;
        this.imageurl = imageurl;
        this.publisher = publisher;
    }

    public String getImageUrl() {
        return imageurl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageurl = imageurl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
