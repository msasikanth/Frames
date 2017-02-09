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

package jahirfiquitiva.libs.frames.holders;

import android.graphics.Bitmap;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.models.Collection;
import jahirfiquitiva.libs.frames.models.Wallpaper;
import jahirfiquitiva.libs.frames.utils.ColorUtils;
import jahirfiquitiva.libs.frames.views.CheckableImageView;

public class WallpaperHolder extends RecyclerView.ViewHolder {

    private ProgressBar progressBar;
    private ImageView wall;
    private RelativeLayout detailsBg;
    private TextView title;
    private TextView author;
    private CheckableImageView heart;
    private Wallpaper item;
    private Collection collection;
    private WallpaperCallback callback;
    private int lastPosition = 0;
    private boolean isCollection;

    public WallpaperHolder(final View itemView, WallpaperCallback nCallback, final boolean
            collections) {
        super(itemView);
        callback = nCallback;
        progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        wall = (ImageView) itemView.findViewById(collections ? R.id.rWall : R.id.sWall);
        detailsBg = (RelativeLayout) itemView.findViewById(R.id.detailsBg);
        title = (TextView) itemView.findViewById(R.id.name);
        author = (TextView) itemView.findViewById(R.id.author);
        heart = (CheckableImageView) itemView.findViewById(R.id.heart);
        isCollection = collections;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null && item != null)
                    callback.onPressed(collection != null ? collection : item);
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!isCollection && callback != null) {
                    callback.onLongPressed(item);
                    return true;
                }
                return false;
            }
        });
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heart.toggle();
                if (heart.isChecked()) {
                    if (callback != null)
                        callback.onFaved();
                } else {
                    if (callback != null)
                        callback.onUnfaved();
                }
            }
        });
    }

    public void setItem(Wallpaper nItem) {
        this.item = nItem;

        wall.setVisibility(View.VISIBLE);

        ViewCompat.setTransitionName(wall, "transition" + getAdapterPosition());

        title.setText(item.getName());
        author.setText(item.getAuthor());

        final String wallURL = item.getURL();
        final String wallThumbURL = item.getThumbnailURL();

        BitmapImageViewTarget target = new BitmapImageViewTarget(wall) {
            @Override
            protected void setResource(Bitmap bitmap) {
                Palette.Swatch wallSwatch = ColorUtils.getPaletteSwatch(bitmap);
                progressBar.setVisibility(View.GONE);
                if (getAdapterPosition() > lastPosition) {
                    wall.setAlpha(0f);
                    detailsBg.setAlpha(0f);
                    wall.setImageBitmap(bitmap);
                    if (wallSwatch != null) setColors(wallSwatch.getRgb());
                    wall.animate().setDuration(250).alpha(1f).start();
                    detailsBg.animate().setDuration(250).alpha(1f).start();
                    lastPosition = getAdapterPosition();
                } else {
                    wall.setImageBitmap(bitmap);
                    if (wallSwatch != null) setColors(wallSwatch.getRgb());
                }
            }
        };

        if (wallThumbURL != null) {
            Glide.with(itemView.getContext())
                    .load(wallURL)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .priority(Priority.HIGH)
                    .thumbnail(
                            Glide.with(itemView.getContext())
                                    .load(wallThumbURL)
                                    .asBitmap()
                                    .priority(Priority.IMMEDIATE)
                                    .thumbnail(0.3f))
                    .into(target);
        } else {
            Glide.with(itemView.getContext())
                    .load(wallURL)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .priority(Priority.HIGH)
                    .thumbnail(0.5f)
                    .into(target);
        }
    }

    public void setItem(Collection collection) {
        this.collection = collection;

        wall.setVisibility(View.VISIBLE);

        ViewCompat.setTransitionName(wall, "transition" + getAdapterPosition());

        title.setText(collection.getName());

        author.setVisibility(View.GONE);
        heart.setVisibility(View.GONE);

        final String wallURL = collection.getPreviewURL();
        final String wallThumbURL = collection.getPreviewThumbnailURL();

        BitmapImageViewTarget target = new BitmapImageViewTarget(wall) {
            @Override
            protected void setResource(Bitmap bitmap) {
                Palette.Swatch wallSwatch = ColorUtils.getPaletteSwatch(bitmap);
                progressBar.setVisibility(View.GONE);
                if (getAdapterPosition() > lastPosition) {
                    wall.setAlpha(0f);
                    detailsBg.setAlpha(0f);
                    wall.setImageBitmap(bitmap);
                    if (wallSwatch != null) setColors(wallSwatch.getRgb());
                    wall.animate().setDuration(250).alpha(1f).start();
                    detailsBg.animate().setDuration(250).alpha(1f).start();
                    lastPosition = getAdapterPosition();
                } else {
                    wall.setImageBitmap(bitmap);
                    if (wallSwatch != null) setColors(wallSwatch.getRgb());
                }
            }
        };

        if (wallThumbURL != null) {
            Glide.with(itemView.getContext())
                    .load(wallURL)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .priority(Priority.HIGH)
                    .thumbnail(
                            Glide.with(itemView.getContext())
                                    .load(wallThumbURL)
                                    .asBitmap()
                                    .priority(Priority.IMMEDIATE)
                                    .thumbnail(0.3f))
                    .into(target);
        } else {
            Glide.with(itemView.getContext())
                    .load(wallURL)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .priority(Priority.HIGH)
                    .thumbnail(0.5f)
                    .into(target);
        }
    }


    public interface WallpaperCallback {
        void onPressed(Object item);

        void onLongPressed(Object item);

        void onFaved();

        void onUnfaved();
    }

    private void setColors(int color) {
        if (detailsBg != null && color != 0) {
            detailsBg.setBackgroundColor(color);
            if (title != null) {
                title.setTextColor(ColorUtils.getMaterialPrimaryTextColor(!ColorUtils
                        .isLightColor(color)));
            }
            if ((author != null) && (!isCollection)) {
                author.setTextColor(ColorUtils.getMaterialPrimaryTextColor(!ColorUtils
                        .isLightColor(color)));
            }
            if ((heart != null) && (!isCollection)) {
                heart.setBackgroundResource(ColorUtils.isLightColor(color) ? R.drawable
                        .light_heart_animated_selector : R.drawable.heart_animated_selector);
            }
        }
        if (heart != null)
            heart.setVisibility(View.VISIBLE);
    }


}