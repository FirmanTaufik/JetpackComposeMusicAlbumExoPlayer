
package firman.music.app.model.album;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Author {

    @SerializedName("name")
    @Expose
    private Name name;
    @SerializedName("uri")
    @Expose
    private Uri uri;
    @SerializedName("email")
    @Expose
    private Email email;
    @SerializedName("gd$image")
    @Expose
    private Gd$image gd$image;

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public Gd$image getGd$image() {
        return gd$image;
    }

    public void setGd$image(Gd$image gd$image) {
        this.gd$image = gd$image;
    }

}
