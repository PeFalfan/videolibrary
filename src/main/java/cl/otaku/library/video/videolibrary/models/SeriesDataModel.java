package cl.otaku.library.video.videolibrary.models;

import java.util.ArrayList;

public class SeriesDataModel {
    private Long id;
    private String title;
    private int currentChapters;
    private int totalChapters;
    private String mainTag;
    private String[] allTags;
    private String originalName;
    private String description;
    private ArrayList<ChapterModel> chapters;
    private String mainImageUrl;
    private int yearOfRelease;

    public SeriesDataModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCurrentChapters() {
        return currentChapters;
    }

    public void setCurrentChapters(int currentChapters) {
        this.currentChapters = currentChapters;
    }

    public int getTotalChapters() {
        return totalChapters;
    }

    public void setTotalChapters(int totalChapters) {
        this.totalChapters = totalChapters;
    }

    public String getMainTag() {
        return mainTag;
    }

    public void setMainTag(String mainTag) {
        this.mainTag = mainTag;
    }

    public String[] getAllTags() {
        return allTags;
    }

    public void setAllTags(String[] allTags) {
        this.allTags = allTags;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<ChapterModel> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<ChapterModel> chapters) {
        this.chapters = chapters;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public int getYearOfRelease() {
        return yearOfRelease;
    }

    public void setYearOfRelease(int yearOfRelease) {
        this.yearOfRelease = yearOfRelease;
    }
}
