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
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.callbacks.OnWallpaperFavedListener;
import jahirfiquitiva.libs.frames.callbacks.WallpaperGestureDetector;
import jahirfiquitiva.libs.frames.holders.WallpaperHolder;
import jahirfiquitiva.libs.frames.models.Wallpaper;
import jahirfiquitiva.libs.frames.utils.FavoritesUtils;

public class WallpapersAdapter extends RecyclerView.Adapter<WallpaperHolder> {

    private FragmentActivity activity;
    private ArrayList<Wallpaper> wallpapers = null;
    private boolean isFavs = false;

    public WallpapersAdapter(FragmentActivity activity) {
        this.activity = activity;
        setupFavorites(activity);
        this.isFavs = true;
    }

    public WallpapersAdapter(FragmentActivity activity, ArrayList<Wallpaper> wallpapers) {
        this.activity = activity;
        this.wallpapers = wallpapers;
    }

    @Override
    public WallpaperHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WallpaperHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .item_wallpaper, parent, false), null, new WallpaperGestureDetector
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

    private void doOnPressed(final Object item) {
        runOnUIThread(activity, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "Pressed wallpaper: " + ((Wallpaper) item).getName(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doFav(Wallpaper item) {
        FavoritesUtils.favorite(activity, item);
    }

    private void doUnfav(Wallpaper item) {
        FavoritesUtils.unfavorite(activity, item.getName());
        if (isFavs)
            setupFavorites(activity);
    }

    private void setupFavorites(FragmentActivity activity) {
        this.wallpapers = FavoritesUtils.getFavorites(activity);
        notifyDataSetChanged();
    }

}