package com.mrmq.uyoutube.beans;

public class UploadEvent implements Event {
    private int totalUpload = 0;
    private int uploaded = 0;

    public int getTotalUpload() {
        return totalUpload;
    }

    public void setTotalUpload(int totalUpload) {
        this.totalUpload = totalUpload;
    }

    public int getUploaded() {
        return uploaded;
    }

    public void setUploaded(int uploaded) {
        this.uploaded = uploaded;
    }
}