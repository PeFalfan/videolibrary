package cl.otaku.library.video.videolibrary.models;

public class ChapterModel {
    private Long id;
    private String title;
    private int chapterNumber;
    private String chapterDescription;
    private String chapterThumbnailUrl;

    public ChapterModel() { }

    public ChapterModel(Long id, String title, int chapterNumber, String chapterDescription, String chapterThumbnailUrl) {
        this.id = id;
        this.title = title;
        this.chapterNumber = chapterNumber;
        this.chapterDescription = chapterDescription;
        this.chapterThumbnailUrl = chapterThumbnailUrl;
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

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getChapterDescription() {
        return chapterDescription;
    }

    public void setChapterDescription(String chapterDescription) {
        this.chapterDescription = chapterDescription;
    }

    public String getChapterThumbnailUrl() {
        return chapterThumbnailUrl;
    }

    public void setChapterThumbnailUrl(String chapterThumbnailUrl) {
        this.chapterThumbnailUrl = chapterThumbnailUrl;
    }
}
