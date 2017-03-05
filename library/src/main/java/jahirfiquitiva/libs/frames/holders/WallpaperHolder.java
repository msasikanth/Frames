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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.callbacks.OnWallpaperClickListener;
import jahirfiquitiva.libs.frames.callbacks.OnWallpaperFavedListener;
import jahirfiquitiva.libs.frames.models.Collection;
import jahirfiquitiva.libs.frames.models.Wallpaper;
import jahirfiquitiva.libs.frames.utils.ColorUtils;
import jahirfiquitiva.libs.frames.utils.FavoritesUtils;
import jahirfiquitiva.libs.frames.utils.Preferences;
import jahirfiquitiva.libs.frames.utils.Utils;
import jahirfiquitiva.libs.frames.views.CheckableImageView;
import jahirfiquitiva.libs.frames.views.ComplexClickListener;

public class WallpaperHolder extends RecyclerView.ViewHolder {

    private ProgressBar progressBar;
    private ImageView wall;
    private RelativeLayout detailsBg;
    private LinearLayout wallDetails;
    private LinearLayout collDetails;
    private TextView name;
    private TextView colName;
    private TextView author;
    private TextView amount;
    private ImageView bigHeart;
    private CheckableImageView heart;
    private Wallpaper item;
    private Collection collection;
    private BitmapImageViewTarget target;
    private Bitmap picture;

    private OnWallpaperFavedListener onFavedListener;

    private int lastPosition = 0;
    private boolean isCollection;

    private static final int SHOW_ANIMATION_DURATION = 400;
    private static final int SHOWN_DURATION = 100;
    private static final int HIDE_ANIMATION_DURATION = 300;
    private static final float MAX_SIZE = 0.65f;

    public WallpaperHolder(final View itemView, final OnWallpaperClickListener onClickListener,
                           final OnWallpaperFavedListener onFavedListener, final boolean
                                   isCollection) {
        super(itemView);
        this.isCollection = isCollection;

        progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        wall = (ImageView) itemView.findViewById(isCollection ? R.id.rWall : R.id.sWall);
        detailsBg = (RelativeLayout) itemView.findViewById(R.id.detailsBg);
        wallDetails = (LinearLayout) itemView.findViewById(R.id.wallpaper_details);
        collDetails = (LinearLayout) itemView.findViewById(R.id.collection_details);
        name = (TextView) itemView.findViewById(R.id.name);
        colName = (TextView) itemView.findViewById(R.id.collection_name);
        author = (TextView) itemView.findViewById(R.id.author);
        amount = (TextView) itemView.findViewById(R.id.amount);
        bigHeart = (ImageView) itemView.findViewById(R.id.bigHeart);
        heart = (CheckableImageView) itemView.findViewById(R.id.heart);

        if (isCollection) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickListener != null && collection != null)
                        onClickListener.onClick(collection, picture, wall, null, colName, null);
                }
            });
        } else {
            itemView.setOnClickListener(new ComplexClickListener() {
                @Override
                public void onSimpleClick() {
                    if (onClickListener != null && item != null)
                        onClickListener.onClick(item, picture, wall, heart, name, author);
                }

                @Override
                public void onDoubleTap() {
                    doFav();
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onClickListener != null && item != null)
                        onClickListener.onLongClick(item);
                    return false;
                }
            });
        }

        this.onFavedListener = onFavedListener;
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doFav(false);
            }
        });

        ViewCompat.setTransitionName(wall, (isCollection ? "collectionTransition_" :
                "wallTransition_") + getAdapterPosition());
        ViewCompat.setTransitionName(isCollection ? colName : name, (isCollection ?
                "collectionNameTransition_" : "nameTransition_") + getAdapterPosition());
        ViewCompat.setTransitionName(author, "authorTransition_" + getAdapterPosition());
        ViewCompat.setTransitionName(heart, "heartTransition_" + getAdapterPosition());

        createTarget();
    }

    private void createTarget() {
        target = new BitmapImageViewTarget(wall) {
            @Override
            protected void setResource(Bitmap bitmap) {
                picture = bitmap;
                Palette.Swatch wallSwatch = ColorUtils.getPaletteSwatch(picture);
                progressBar.setVisibility(View.GONE);
                if (getAdapterPosition() > lastPosition) {
                    wall.setAlpha(0f);
                    detailsBg.setAlpha(0f);
                    wall.setImageBitmap(picture);
                    if (wallSwatch != null) setColors(wallSwatch.getRgb());
                    wall.animate().setDuration(250).alpha(1f).start();
                    detailsBg.animate().setDuration(250).alpha(1f).start();
                    lastPosition = getAdapterPosition();
                } else {
                    wall.setImageBitmap(picture);
                    if (wallSwatch != null) setColors(wallSwatch.getRgb());
                }
            }
        };
    }

    public void setItem(Wallpaper nItem) {
        this.item = nItem;

        collDetails.setVisibility(View.GONE);
        colName.setVisibility(View.GONE);
        amount.setVisibility(View.GONE);

        name.setText(item.getName());
        author.setText(item.getAuthor());
        wall.setVisibility(View.VISIBLE);
        wallDetails.setVisibility(View.VISIBLE);

        heart.setChecked(FavoritesUtils.isFavorited(itemView.getContext(), nItem.getName()));

        loadPicture(item.getURL(), item.getThumbnailURL());
    }

    public void setItem(Collection nCollection) {
        this.collection = nCollection;
        wallDetails.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        author.setVisibility(View.GONE);
        heart.setVisibility(View.GONE);
        bigHeart.setVisibility(View.GONE);

        wall.setVisibility(View.VISIBLE);
        collDetails.setVisibility(View.VISIBLE);
        colName.setVisibility(View.VISIBLE);

        int pad = Utils.dpToPx(itemView.getContext(), 12);
        detailsBg.setPadding(pad, pad, pad, pad);

        colName.setText(collection.getName());

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
            if (isCollection) {
                if (colName != null) {
                    colName.setTextColor(ColorUtils.getMaterialPrimaryTextColor(!ColorUtils
                            .isLightColor(color)));
                }
            } else {
                if (name != null) {
                    name.setTextColor(ColorUtils.getMaterialPrimaryTextColor(!ColorUtils
                            .isLightColor(color)));
                }
            }
            if ((author != null) && (!isCollection)) {
                author.setTextColor(ColorUtils.getMaterialSecondaryTextColor(!ColorUtils
                        .isLightColor(color)));
            }
            if ((amount != null) && (isCollection)) {
                amount.setTextColor(ColorUtils.getMaterialSecondaryTextColor(!ColorUtils
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

    private void doFav() {
        doFav(true);
    }

    private void doFav(boolean playAnimation) {
        if (isCollection) return;
        boolean isFavorited = FavoritesUtils.isFavorited(itemView.getContext(),
                item.getName());
        boolean markAsFavorite = !isFavorited;
        boolean success = false;
        try {
            if (isFavorited) {
                if (FavoritesUtils.unfavorite(itemView.getContext(), item.getName())) {
                    markAsFavorite = false;
                    success = true;
                }
            } else {
                if (FavoritesUtils.favorite(itemView.getContext(), item)) {
                    markAsFavorite = true;
                    success = true;
                }
            }
        } catch (Exception e) {
            Log.e(Utils.LOG_TAG, "Exception: " + e.getMessage() + " - due to: " + e.getCause());
            // e.printStackTrace();
        }
        if (success) {
            final Preferences mPrefs = new Preferences(itemView.getContext());
            if (playAnimation) {
                if (bigHeart != null) {
                    if (bigHeart.getAnimation() != null) {
                        bigHeart.clearAnimation();
                    }
                    if ((!(markAsFavorite)) && (!(mPrefs.isInstagramLikeBehavior()))) {
                        bigHeart.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R
                                .drawable.ic_simple_broken_heart));
                    } else {
                        bigHeart.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R
                                .drawable.ic_simple_heart));
                    }
                    final boolean reallyMarkAsFavorite = markAsFavorite;
                    bigHeart.setScaleX(0.1f);
                    bigHeart.setScaleY(0.1f);
                    bigHeart.setAlpha(0f);
                    bigHeart.setVisibility(View.VISIBLE);
                    bigHeart.animate().alpha(0.8f).scaleX(MAX_SIZE).scaleY(MAX_SIZE)
                            .setStartDelay(50)
                            .setDuration(SHOW_ANIMATION_DURATION)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    doHeartCheck(reallyMarkAsFavorite, mPrefs);
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
            } else {
                doHeartCheck(markAsFavorite, mPrefs);
            }
        }
    }

    private void doHeartCheck(boolean markAsFavorite, Preferences mPrefs) {
        if (heart.isChecked() != markAsFavorite) {
            if (mPrefs.isInstagramLikeBehavior()) {
                if (!heart.isChecked()) {
                    heart.setChecked(true);
                    if (onFavedListener != null)
                        onFavedListener.onFaved(item);
                }
            } else {
                heart.setChecked(markAsFavorite);
                if (onFavedListener != null) {
                    if (markAsFavorite) onFavedListener.onFaved(item);
                    else onFavedListener.onUnfaved(item);
                }
            }
        }
    }

    public ImageView getHeart() {
        return heart;
    }

    public TextView getName() {
        return isCollection ? colName : name;
    }

    public TextView getAuthor() {
        return author;
    }

}