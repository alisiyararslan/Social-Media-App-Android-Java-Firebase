package com.siyararslan.socialmediaapp;

public class Post {

    private String email;
    private String comment;
    private String postDownloadUrl;
    private String profileImgDownloadUrl;

    public Post(String email, String comment, String postDownloadUrl, String profileImgDownloadUrl) {
        this.email = email;
        this.comment = comment;
        this.postDownloadUrl = postDownloadUrl;
        this.profileImgDownloadUrl = profileImgDownloadUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPostDownloadUrl() {
        return postDownloadUrl;
    }

    public void setPostDownloadUrl(String postDownloadUrl) {
        this.postDownloadUrl = postDownloadUrl;
    }

    public String getProfileImgDownloadUrl() {
        return profileImgDownloadUrl;
    }

    public void setProfileImgDownloadUrl(String profileImgDownloadUrl) {
        this.profileImgDownloadUrl = profileImgDownloadUrl;
    }
}
