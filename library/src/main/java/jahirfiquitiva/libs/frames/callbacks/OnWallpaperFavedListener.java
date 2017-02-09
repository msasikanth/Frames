package jahirfiquitiva.libs.frames.callbacks;

import jahirfiquitiva.libs.frames.models.Wallpaper;

public interface OnWallpaperFavedListener {
    void onFaved(Wallpaper item);

    void onUnfaved(Wallpaper item);
}
