package tech.qvanphong.tllnbot.dto;

public class Emoji {
private boolean isAnimated;
private long id;
private String fileType;

public Emoji(boolean isAnimated, long id) {
        this.isAnimated = isAnimated;
        this.id = id;
        this.fileType = isAnimated ? "gif" : "png";
        }

public boolean isAnimated() {
        return isAnimated;
        }

public void setAnimated(boolean animated) {
        isAnimated = animated;
        setFileType(animated ? "gif" : "png");
        }

public long getId() {
        return id;
        }

public void setId(long id) {
        this.id = id;
        }

public String getFileType() {
        return fileType;
        }

public void setFileType(String fileType) {
        this.fileType = fileType;
        }
        }
