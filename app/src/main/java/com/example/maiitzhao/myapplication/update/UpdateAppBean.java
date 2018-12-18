package com.example.maiitzhao.myapplication.update;

import java.io.Serializable;

/**
 * 版本信息
 */
public class UpdateAppBean implements Serializable {

    private int id;// 下载任务的唯一标识
    private int state;// 下载状态
    private long currentLength;// 已经下载的长度
    private long size;// 总长度
    private String downloadUrl;// 下载地址
    private String path;// 每个任务下载文件保存的绝对路径

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(long currentLength) {
        this.currentLength = currentLength;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "UpdateAppBean{" +
                "id=" + id +
                ", state=" + state +
                ", currentLength=" + currentLength +
                ", size=" + size +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
