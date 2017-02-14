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

package jahirfiquitiva.libs.frames.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.util.ArrayList;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.activities.FabbedViewerActivity;
import jahirfiquitiva.libs.frames.activities.WallpaperViewerActivity;
import jahirfiquitiva.libs.frames.callbacks.OnWallpaperFavedListener;
import jahirfiquitiva.libs.frames.callbacks.OnWallpaperPressListener;
import jahirfiquitiva.libs.frames.callbacks.WallpaperGestureDetector;
import jahirfiquitiva.libs.frames.holders.WallpaperHolder;
import jahirfiquitiva.libs.frames.models.Wallpaper;
import jahirfiquitiva.libs.frames.utils.ApplyWallpaperUtils;
import jahirfiquitiva.libs.frames.utils.FavoritesUtils;
import jahirfiquitiva.libs.frames.utils.Utils;

public class WallpapersAdapter extends RecyclerView.Adapter<WallpaperHolder> {

    private FragmentActivity activity;
    private ArrayList<Wallpaper> wallpapers;
    private boolean isFavs = false;
    private ArrayList<String> modifiedWallpapers = new ArrayList<>();

    public WallpapersAdapter(FragmentActivity activity, ArrayList<Wallpaper> wallpapers, boolean
            isFavs) {
        this.activity = activity;
        this.wallpapers = wallpapers;
        this.isFavs = isFavs;
    }

    @Override
    public WallpaperHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WallpaperHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .item_wallpaper, parent, false), new OnWallpaperPressListener() {
            @Override
            public void onPressed(Object item, ImageView wall) {
                doOnPressed(item, wall);
            }

            @Override
            public void onLongPressed(Object item) {
                doOnLongPressed((Wallpaper) item);
            }
        }, new WallpaperGestureDetector
                .OnWallpaperDoubleTapListener() {
            @Override
            public void onDoubleTap(WallpaperHolder holder) {
                showDoubleTapAnimation(holder);
            }
        }, new OnWallpaperFavedListener() {
            @Override
            public void onFaved(Wallpaper item) {
                doFav(item);
            }

            @Override
            public void onUnfaved(Wallpaper item) {
                doUnfav(item);
            }
        }, false);
    }

    private void showDoubleTapAnimation(final WallpaperHolder holder) {
        runOnUIThread(activity, new Runnable() {
            @Override
            public void run() {
                holder.doFav();
            }
        });
    }

    @Override
    public void onBindViewHolder(final WallpaperHolder holder, int position) {
        holder.setItem(wallpapers.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return wallpapers != null ? wallpapers.size() : 0;
    }

    private Handler handler(Context context) {
        return new Handler(context.getMainLooper());
    }

    private void runOnUIThread(Context context, Runnable r) {
        handler(context).post(r);
    }

    private void doOnPressed(final Object item, final ImageView wall) {
        Intent wallpaperViewer = new Intent(activity, activity.getResources().getBoolean(R.bool
                .fabbed_viewer) ? FabbedViewerActivity.class : WallpaperViewerActivity.class);
        wallpaperViewer.putExtra("item", (Wallpaper) item);
        wallpaperViewer.putExtra("transitionName", ViewCompat.getTransitionName(wall));

        if (wall.getDrawable() != null) {
            Bitmap bitmap = Utils.drawableToBitmap(wall.getDrawable());

            try {
                String filename = "temp.png";
                FileOutputStream stream = activity.openFileOutput(filename, Context
                        .MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                stream.close();
                wallpaperViewer.putExtra("image", filename);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(activity, wall, ViewCompat
                            .getTransitionName(wall));
            activity.startActivityForResult(wallpaperViewer, 12, options.toBundle());
        } else {
            activity.startActivityForResult(wallpaperViewer, 12);
        }
    }

    private void doOnLongPressed(Wallpaper item) {
        ApplyWallpaperUtils.onApplyWallpaperClick(activity, null, null, item);
    }

    private void doFav(Wallpaper item) {
        if (!(modifiedWallpapers.contains(item.getName())))
            modifiedWallpapers.add(item.getName());
        FavoritesUtils.favorite(activity, item);
    }

    private void doUnfav(Wallpaper item) {
        if (!(modifiedWallpapers.contains(item.getName())))
            modifiedWallpapers.add(item.getName());
        FavoritesUtils.unfavorite(activity, item.getName());
        if (isFavs)
            removeItem(item);
    }

    public void changeWallpapers(ArrayList<Wallpaper> nWallpapers) {
        if (wallpapers == null)
            wallpapers = new ArrayList<>();
        wallpapers.clear();
        if (nWallpapers != null)
            wallpapers.addAll(nWallpapers);
        notifyDataSetChanged();
    }

    public void updateItems(String items) {
        if (wallpapers == null) return;
        for (String item : items.split(",")) {
            for (int i = 0; i < wallpapers.size(); i++) {
                if (wallpapers.get(i).getName().equals(item)) {
                    notifyItemChanged(i);
                }
            }
        }
    }

    private void removeItem(Wallpaper item) {
        if (wallpapers == null) return;
        for (int i = 0; i < wallpapers.size(); i++) {
            if (wallpapers.get(i).equals(item)) {
                wallpapers.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public ArrayList<String> getModifiedWallpapers() {
        return modifiedWallpapers;
    }

}