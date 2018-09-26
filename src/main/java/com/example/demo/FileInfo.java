package com.example.demo;

public class FileInfo {
	private String fileName;
    private long fileId;
    private long fileSize;
    private String fileInfo;
    
    
    @Override
	public String toString() {
		return "FileInfo [fileName=" + fileName + ", fileId=" + fileId + ", fileSize=" + fileSize + ", fileInfo="
				+ fileInfo + "]";
	}
	public FileInfo(String fileName, long fileId, long fileSize, String fileInfo) {
		super();
		this.fileName = fileName;
		this.fileId = fileId;
		this.fileSize = fileSize;
		this.fileInfo = fileInfo;
	}
	public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public long getFileId() {
        return fileId;
    }
    public void setFileId(long fileId) {
        this.fileId = fileId;
    }
    public long getFileSize() {
        return fileSize;
    }
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    public String getFileInfo() {
        return fileInfo;
    }
    public void setFileInfo(String fileInfo) {
        this.fileInfo = fileInfo;
    }
}
