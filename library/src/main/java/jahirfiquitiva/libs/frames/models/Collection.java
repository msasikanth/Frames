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

import java.util.ArrayList;

public class Collection implements Parcelable {

    private String name;
    private String previewURL;
    private String previewThumbnailURL;
    private ArrayList<Wallpaper> wallpapers;

    public Collection(String name, String previewURL, String previewThumbnailURL) {
        this.name = name;
        this.previewURL = previewURL;
        if (previewThumbnailURL != null) {
            this.previewThumbnailURL = previewThumbnailURL;
        } else {
            this.previewThumbnailURL = previewURL;
        }
    }

    public Collection(String name, String previewURL, String previewThumbnailURL,
                      ArrayList<Wallpaper> wallpapers) {
        this(name, previewURL, previewThumbnailURL);
        this.wallpapers = wallpapers;
    }

    protected Collection(Parcel in) {
        name = in.readString();
        previewURL = in.readString();
        previewThumbnailURL = in.readString();
        wallpapers = in.createTypedArrayList(Wallpaper.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(previewURL);
        dest.writeString(previewThumbnailURL);
        dest.writeTypedList(wallpapers);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Collection> CREATOR = new Creator<Collection>() {
        @Override
        public Collection createFromParcel(Parcel in) {
            return new Collection(in);
        }

        @Override
        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };

    public void setWallpapers(ArrayList<Wallpaper> nWallpapers) {
        if (wallpapers != null) {
            wallpapers.clear();
        } else {
            wallpapers = new ArrayList<>();
        }
        wallpapers.addAll(nWallpapers);
    }

    public void addWallpaper(Wallpaper wallpaper) {
        if (wallpapers == null) {
            wallpapers = new ArrayList<>();
        }
        wallpapers.add(wallpaper);
    }

    public String getName() {
        return name;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public String getPreviewThumbnailURL() {
        return previewThumbnailURL;
    }

    public ArrayList<Wallpaper> getWallpapers() {
        return wallpapers;
    }
}