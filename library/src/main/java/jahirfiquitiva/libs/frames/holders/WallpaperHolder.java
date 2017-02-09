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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
import jahirfiquitiva.libs.frames.callbacks.OnWallpaperFavedListener;
import jahirfiquitiva.libs.frames.callbacks.OnWallpaperPressListener;
import jahirfiquitiva.libs.frames.callbacks.WallpaperGestureDetector;
import jahirfiquitiva.libs.frames.models.Collection;
import jahirfiquitiva.libs.frames.models.Wallpaper;
import jahirfiquitiva.libs.frames.utils.ColorUtils;
import jahirfiquitiva.libs.frames.utils.FavoritesUtils;
import jahirfiquitiva.libs.frames.utils.Utils;
import jahirfiquitiva.libs.frames.views.CheckableImageView;

public class WallpaperHolder extends RecyclerView.ViewHolder {

    private ProgressBar progressBar;
    private ImageView wall;
    private RelativeLayout detailsBg;
    private TextView title;
    private TextView author;
    private TextView amount;
    private ImageView bigHeart;
    private CheckableImageView heart;
    private Wallpaper item;
    private Collection collection;
    private WallpaperGestureDetector wgd;
    private OnWallpaperFavedListener onFavedListener;
    private BitmapImageViewTarget target;
    private int lastPosition = 0;
    private boolean isCollection;

    private static final int SHOW_ANIMATION_DURATION = 400;
    private static final int SHOWN_DURATION = 200;
    private static final int HIDE_ANIMATION_DURATION = 300;
    private static final float MAX_SIZE = 0.65f;

    public WallpaperHolder(final View itemView, OnWallpaperPressListener onPressListener,
                           WallpaperGestureDetector.OnWallpaperDoubleTapListener
                                   onDoubleTapListener,
                           final OnWallpaperFavedListener onFavedListener, final boolean
                                   isCollection) {
        super(itemView);
        this.onFavedListener = onFavedListener;
        this.isCollection = isCollection;

        progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        wall = (ImageView) itemView.findViewById(isCollection ? R.id.rWall : R.id.sWall);
        detailsBg = (RelativeLayout) itemView.findViewById(R.id.detailsBg);
        title = (TextView) itemView.findViewById(R.id.name);
        author = (TextView) itemView.findViewById(R.id.author);
        amount = (TextView) itemView.findViewById(R.id.amount);
        bigHeart = (ImageView) itemView.findViewById(R.id.bigHeart);
        bigHeart.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable
                .ic_heart_100));
        heart = (CheckableImageView) itemView.findViewById(R.id.heart);

        wgd = new WallpaperGestureDetector(this, isCollection ? collection : item, onPressListener,
                isCollection ? null : onDoubleTapListener);
        final GestureDetector detector = new GestureDetector(itemView.getContext(), wgd);
        detector.setOnDoubleTapListener(wgd);
        itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                detector.onTouchEvent(motionEvent);
                return true;
            }
        });

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkHeart(!heart.isChecked());
            }
        });

        ViewCompat.setTransitionName(wall, "transition" + getAdapterPosition());

        createTarget();
    }

    private void createTarget() {
        target = new BitmapImageViewTarget(wall) {
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
    }

    public void setItem(Wallpaper nItem) {
        this.item = nItem;
        wgd.setItem(item);
        wall.setVisibility(View.VISIBLE);

        title.setText(item.getName());
        author.setText(item.getAuthor());
        amount.setVisibility(View.GONE);

        heart.setChecked(FavoritesUtils.isFavorited(itemView.getContext(), nItem.getName()));

        loadPicture(item.getURL(), item.getThumbnailURL());
    }

    public void setItem(Collection nCollection) {
        this.collection = nCollection;
        wgd.setItem(collection);
        author.setVisibility(View.GONE);
        heart.setVisibility(View.GONE);
        bigHeart.setVisibility(View.GONE);
        wall.setVisibility(View.VISIBLE);

        int pad = Utils.dpToPx(itemView.getContext(), 12);
        detailsBg.setPadding(pad, pad, pad, pad);

        title.setText(collection.getName());

        String exactAmount = collection.getWallpapers().size() > 99 ? "99+" : String.valueOf
                (collection.getWallpapers().size());
        amount.setText(exactAmount);
        amount.setVisibility(View.VISIBLE);

        loadPicture(collection.getPreviewURL(), collection.getPreviewThumbnailURL());
    }

    private void loadPicture(String URL, String thumbURL) {
        if (target == null) createTarget();
        if (thumbURL != null) {
            Glide.with(itemView.getContext())
                    .load(URL)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .priority(Priority.HIGH)
                    .thumbnail(
                            Glide.with(itemView.getContext())
                                    .load(thumbURL)
                                    .asBitmap()
                                    .priority(Priority.IMMEDIATE)
                                    .thumbnail(0.5f))
                    .into(target);
        } else {
            Glide.with(itemView.getContext())
                    .load(URL)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .priority(Priority.HIGH)
                    .thumbnail(0.5f)
                    .into(target);
        }
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
            if ((amount != null) && (isCollection)) {
                amount.setTextColor(ColorUtils.getMaterialPrimaryTextColor(!ColorUtils
                        .isLightColor(color)));
            }
            if ((heart != null) && (!isCollection)) {
                Drawable prev = heart.getDrawable();
                if (prev != null)
                    prev.mutate();
                heart.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(),
                        ColorUtils.isLightColor(color) ? R.drawable.light_heart_animated_selector
                                : R.drawable.heart_animated_selector));
            }
        }
        if (heart != null)
            heart.setVisibility(View.VISIBLE);
    }

    public void doFav() {
        if (isCollection) return;
        if (bigHeart != null) {
            if (bigHeart.getAnimation() != null) {
                bigHeart.clearAnimation();
            }
            bigHeart.setScaleX(0.1f);
            bigHeart.setScaleY(0.1f);
            bigHeart.setAlpha(0f);
            bigHeart.setVisibility(View.VISIBLE);
            bigHeart.animate().alpha(0.75f).scaleX(MAX_SIZE).scaleY(MAX_SIZE).setStartDelay(50)
                    .setDuration(SHOW_ANIMATION_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (!heart.isChecked())
                                checkHeart(true);
                            bigHeart.animate().alpha(0).scaleX(0).scaleY(0).setStartDelay
                                    (SHOWN_DURATION)
                                    .setDuration(HIDE_ANIMATION_DURATION)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            bigHeart.setVisibility(View.INVISIBLE);
                                        }
                                    }).start();
                        }
                    }).start();
        }
    }

    private void checkHeart(boolean check) {
        boolean prevState = heart.isChecked();
        heart.setChecked(check);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (check != prevState) {
            if (heart.isChecked()) {
                if (onFavedListener != null && item != null)
                    onFavedListener.onFaved(item);
            } else {
                if (onFavedListener != null && item != null)
                    onFavedListener.onUnfaved(item);
            }
        }
    }

}