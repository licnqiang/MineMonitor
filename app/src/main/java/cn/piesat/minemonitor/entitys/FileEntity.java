package cn.piesat.minemonitor.entitys;

/**
 * 作者：wangyi
 * <p>
 * 邮箱：wangyi@piesat.cn
 */
public class FileEntity {
    public enum Type{
        FLODER,FILE
    }
    private String filePath;
    private String fileName;
    private String fileSize;
    private Type fileType;


    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFileSize() {
        return fileSize;
    }
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
    public Type getFileType() {
        return fileType;
    }
    public void setFileType(Type fileType) {
        this.fileType = fileType;
    }
}
