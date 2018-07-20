package com.ude.debuggerlibrary.data;

/**
 * Created by ude on 2017-10-18.
 */

public class CheckData {
    private String content;//内容
    private String fileName;//文件名
    private boolean isFile;//是否为文件
    private boolean isCheck = false;//是否被选中
    private int where;//内容在文件的位置

    public CheckData(String content,String fileName,boolean isFile,boolean isCheck,int where){
        this.content = content;
        this.fileName = fileName;
        this.isFile = isFile;
        this.isCheck = isCheck;
        this.where = where;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getWhere() {
        return where;
    }

    public void setWhere(int where) {
        this.where = where;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    @Override
    public String toString() {
        return "CheckData{" +
                "content='" + content + '\'' +
                ", fileName='" + fileName + '\'' +
                ", isFile=" + isFile +
                ", isCheck=" + isCheck +
                ", where=" + where +
                '}';
    }
}
