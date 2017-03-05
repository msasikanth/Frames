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
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.FileOutputStream;
import java.util.ArrayList;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.activities.FabbedViewerActivity;
import jahirfiquitiva.libs.frames.activities.WallpaperViewerActivity;
import jahirfiquitiva.libs.frames.callbacks.OnWallpaperClickListener;
import jahirfiquitiva.libs.frames.callbacks.OnWallpaperFavedListener;
import jahirfiquitiva.libs.frames.dialogs.FramesDialogs;
import jahirfiquitiva.libs.frames.holders.WallpaperHolder;
import jahirfiquitiva.libs.frames.models.Wallpaper;
import jahirfiquitiva.libs.frames.utils.ApplyWallpaperUtils;
import jahirfiquitiva.libs.frames.utils.FavoritesUtils;
import jahirfiquitiva.libs.frames.utils.GlideConfiguration;

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
                .item_wallpaper, parent, false), new OnWallpaperClickListener() {
            @Override
            public void onClick(Object item, Bitmap picture, ImageView wall, ImageView heart,
                                TextView name, TextView author) {
                doOnPressed(item, picture, wall, heart, name, author);
            }

            @Override
            public void onLongClick(Object item) {
                doOnLongPressed((Wallpaper) item);
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

    @Override
    public void onBindViewHolder(final WallpaperHolder holder, int position) {
        holder.setItem(wallpapers.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return wallpapers != null ? wallpapers.size() : 0;
    }

    @SuppressWarnings("unchecked")
    private void doOnPressed(Object item, Bitmap bitmap, ImageView wall, ImageView heart,
                             TextView name, TextView author) {
        Intent wallpaperViewer = new Intent(activity, activity.getResources().getBoolean(R.bool
                .fabbed_viewer) ? FabbedViewerActivity.class : WallpaperViewerActivity.class);
        wallpaperViewer.putExtra("item", (Wallpaper) item);
        wallpaperViewer.putExtra("wallTransition", ViewCompat.getTransitionName(wall));
        wallpaperViewer.putExtra("nameTransition", ViewCompat.getTransitionName(name));
        wallpaperViewer.putExtra("authorTransition", ViewCompat.getTransitionName(author));
        wallpaperViewer.putExtra("heartTransition", ViewCompat.getTransitionName(heart));
        try {
            if (bitmap != null) {
                String filename = "thumb.png";
                FileOutputStream stream = activity.openFileOutput(filename, Context
                        .MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG,
                        GlideConfiguration.getPictureMaxRes(activity), stream);
                stream.flush();
                stream.close();
                wallpaperViewer.putExtra("image", filename);
            }
            Pair<View, String> wallPair = Pair.create((View) wall, ViewCompat.getTransitionName
                    (wall));
            Pair<View, String> heartPair = Pair.create((View) heart,
                    ViewCompat.getTransitionName(heart));
            Pair<View, String> namePair = Pair.create((View) name, ViewCompat.getTransitionName
                    (name));
            Pair<View, String> authorPair = Pair.create((View) author,
                    ViewCompat.getTransitionName(author));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation
                    (activity, wallPair, heartPair, namePair, authorPair);
            ActivityCompat.startActivityForResult(activity, wallpaperViewer, 12,
                    options.toBundle());
        } catch (Exception e) {
            e.printStackTrace();
            ActivityCompat.startActivityForResult(activity, wallpaperViewer, 12, null);
        }
    }

    private void doOnLongPressed(final Wallpaper item) {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(30);
        FramesDialogs.showApplyWallpaperDialog(activity, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                ApplyWallpaperUtils.onApplyWallpaperClick(activity, null, null, item);
            }
        }, null, null, null, false);
    }

    private void doFav(Wallpaper item) {
        if (!(modifiedWallpapers.contains(item.getName())))
            modifiedWallpapers.add(item.getName());
    }

    private void doUnfav(Wallpaper item) {
        if (!(modifiedWallpapers.contains(item.getName())))
            modifiedWallpapers.add(item.getName());
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