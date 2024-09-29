
package firman.music.app.model.album;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Entry {

    @SerializedName("id")
    @Expose
    private Id_ id;
    @SerializedName("published")
    @Expose
    private Published published;
    @SerializedName("updated")
    @Expose
    private Updated_ updated;
    @SerializedName("title")
    @Expose
    private Title_ title;
    @SerializedName("content")
    @Expose
    private Content content;
    @SerializedName("link")
    @Expose
    private List<Link_> link = null;
    @SerializedName("author")
    @Expose
    private List<Author_> author = null;
    @SerializedName("media$thumbnail")
    @Expose
    private Media$thumbnail media$thumbnail;
    @SerializedName("thr$total")
    @Expose
    private Thr$total thr$total;

    public Id_ getId() {
        return id;
    }

    public void setId(Id_ id) {
        this.id = id;
    }

    public Published getPublished() {
        return published;
    }

    public void setPublished(Published published) {
        this.published = published;
    }

    public Updated_ getUpdated() {
        return updated;
    }

    public void setUpdated(Updated_ updated) {
        this.updated = updated;
    }

    public Title_ getTitle() {
        return title;
    }

    public void setTitle(Title_ title) {
        this.title = title;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public List<Link_> getLink() {
        return link;
    }

    public void setLink(List<Link_> link) {
        this.link = link;
    }

    public List<Author_> getAuthor() {
        return author;
    }

    public void setAuthor(List<Author_> author) {
        this.author = author;
    }

    public Media$thumbnail getMedia$thumbnail() {
        return media$thumbnail;
    }

    public void setMedia$thumbnail(Media$thumbnail media$thumbnail) {
        this.media$thumbnail = media$thumbnail;
    }

    public Thr$total getThr$total() {
        return thr$total;
    }

    public void setThr$total(Thr$total thr$total) {
        this.thr$total = thr$total;
    }

}
