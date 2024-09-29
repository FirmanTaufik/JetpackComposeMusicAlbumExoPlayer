
package firman.music.app.model.album;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Author_ {

    @SerializedName("name")
    @Expose
    private Name_ name;
    @SerializedName("uri")
    @Expose
    private Uri_ uri;
    @SerializedName("email")
    @Expose
    private Email_ email;
    @SerializedName("gd$image")
    @Expose
    private Gd$image_ gd$image;

    public Name_ getName() {
        return name;
    }

    public void setName(Name_ name) {
        this.name = name;
    }

    public Uri_ getUri() {
        return uri;
    }

    public void setUri(Uri_ uri) {
        this.uri = uri;
    }

    public Email_ getEmail() {
        return email;
    }

    public void setEmail(Email_ email) {
        this.email = email;
    }

    public Gd$image_ getGd$image() {
        return gd$image;
    }

    public void setGd$image(Gd$image_ gd$image) {
        this.gd$image = gd$image;
    }

}
