/*
 * Copyright (c) 2017. Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jahirfiquitiva.libs.frames.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Wallpaper implements Parcelable {
    private String name;
    private String author;
    private String copyright;
    private String dimensions;
    private String URL;
    private String thumbnailURL;
    private String collections;
    private boolean downloadable;

    public Wallpaper(String name, String author, String copyright, String dimensions, String URL,
                     String thumbnailURL, String collections, boolean downloadable) {
        this.name = name;
        this.author = author;
        this.copyright = copyright;
        this.dimensions = dimensions;
        this.URL = URL;
        if (thumbnailURL != null) {
            this.thumbnailURL = thumbnailURL;
        } else {
            this.thumbnailURL = URL;
        }
        if (collections != null) {
            this.collections = collections;
        } else {
            this.collections = "featured";
        }
        this.downloadable = downloadable;
    }

    protected Wallpaper(Parcel in) {
        name = in.readString();
        author = in.readString();
        copyright = in.readString();
        dimensions = in.readString();
        URL = in.readString();
        thumbnailURL = in.readString();
        collections = in.readString();
        downloadable = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(copyright);
        dest.writeString(dimensions);
        dest.writeString(URL);
        dest.writeString(thumbnailURL);
        dest.writeString(collections);
        dest.writeByte((byte) (downloadable ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Wallpaper> CREATOR = new Creator<Wallpaper>() {
        @Override
        public Wallpaper createFromParcel(Parcel in) {
            return new Wallpaper(in);
        }

        @Override
        public Wallpaper[] newArray(int size) {
            return new Wallpaper[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getDimensions() {
        return dimensions;
    }

    public String getURL() {
        return URL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public String getCollections() {
        return collections;
    }

    public boolean isDownloadable() {
        return downloadable;
    }
}