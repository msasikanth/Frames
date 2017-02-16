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

package jahirfiquitiva.libs.frames.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.activities.base.BaseWallpaperViewerActivity;
import jahirfiquitiva.libs.frames.callbacks.WallpaperDialogsCallback;
import jahirfiquitiva.libs.frames.dialogs.FramesDialogs;
import jahirfiquitiva.libs.frames.utils.ColorUtils;
import jahirfiquitiva.libs.frames.utils.FavoritesUtils;
import jahirfiquitiva.libs.frames.utils.IconUtils;
import jahirfiquitiva.libs.frames.utils.PermissionsUtils;
import jahirfiquitiva.libs.frames.utils.ThemeUtils;
import jahirfiquitiva.libs.frames.utils.ToolbarColorizer;
import jahirfiquitiva.libs.frames.utils.Utils;
import jahirfiquitiva.libs.frames.views.CheckableImageView;
import jahirfiquitiva.libs.frames.views.TouchImageView;

public class WallpaperViewerActivity extends BaseWallpaperViewerActivity {

    private LinearLayout toHide1, toHide2;
    private Activity context;

    @SuppressWarnings("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setFullScreen(false);
        super.onCreate(savedInstanceState);

        FavoritesUtils.init(this);
        context = this;

        setContentView(R.layout.wallpaper_viewer_activity);
        setLayout((ViewGroup) findViewById(R.id.viewerLayout));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_with_shadow);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ToolbarColorizer.colorizeToolbar(toolbar, ContextCompat.getColor(this, android.R
                .color.white));

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            Utils.setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            Utils.setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        toHide1 = (LinearLayout) findViewById(R.id.iconsA);
        toHide2 = (LinearLayout) findViewById(R.id.iconsB);

        setViewsToHide(toHide1, toHide2);

        setCallback(new WallpaperDialogsCallback() {
            @Override
            public void onDialogShown() {
                if (toHide1 != null && toHide2 != null) {
                    toHide1.setVisibility(View.GONE);
                    toHide2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDialogDismissed() {
                if (toHide1 != null && toHide2 != null) {
                    toHide1.setVisibility(View.VISIBLE);
                    toHide2.setVisibility(View.VISIBLE);
                }
            }
        });

        final CheckableImageView favIV = (CheckableImageView) findViewById(R.id.fav);
        ViewCompat.setTransitionName(favIV, getIntent().getStringExtra("heartTransition"));
        favIV.setWallpaperItem(getItem());
        Drawable prev = favIV.getDrawable();
        if (prev != null)
            prev.mutate();
        favIV.setImageDrawable(ContextCompat.getDrawable(this, ColorUtils.isLightColor(ThemeUtils
                .darkOrLight(this, R.color.dark_theme_card_background, R.color
                        .light_theme_card_background)) ? R.drawable.light_heart_animated_selector
                : R.drawable.heart_animated_selector));
        favIV.setChecked(FavoritesUtils.isFavorited(this, getItem().getName()));
        favIV.setOnWallpaperFavedListener(getOnFavedListener());
        favIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favIV.toggle();
            }
        });

        ImageView saveIV = (ImageView) findViewById(R.id.download);
        if (getItem().isDownloadable()) {
            saveIV.setImageDrawable(IconUtils.getTintedDrawable(context, "ic_save"));
            saveIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PermissionsUtils.checkPermission(context, Manifest.permission
                            .WRITE_EXTERNAL_STORAGE, new PermissionsUtils
                            .PermissionRequestListener() {
                        @Override
                        public void onPermissionRequest() {
                            PermissionsUtils.setViewerActivityAction("save");
                            PermissionsUtils.requestStoragePermission(context);
                        }

                        @Override
                        public void onPermissionDenied() {
                            FramesDialogs.showPermissionNotGrantedDialog(context);
                        }

                        @Override
                        public void onPermissionCompletelyDenied() {
                            FramesDialogs.showPermissionNotGrantedDialog(context);
                        }

                        @Override
                        public void onPermissionGranted() {
                            runWallpaperSave(context);
                        }
                    });
                }
            });
        } else {
            saveIV.setVisibility(View.GONE);
        }

        ImageView applyIV = (ImageView) findViewById(R.id.apply);
        applyIV.setImageDrawable(IconUtils.getTintedDrawable(context, "ic_apply"));
        applyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showApplyWallpaperDialog(context);
            }
        });

        ImageView infoIV = (ImageView) findViewById(R.id.info);
        infoIV.setImageDrawable(IconUtils.getTintedDrawable(context, "ic_info"));
        infoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FramesDialogs.showWallpaperDetailsDialog(context, getItem().getName(),
                        getItem().getAuthor(), getItem().getDimensions(),
                        getItem().getCopyright(), new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                //Do nothing
                            }
                        });
            }
        });

        TouchImageView mPhoto = (TouchImageView) findViewById(R.id.big_wallpaper);
        ViewCompat.setTransitionName(mPhoto, getTransitionName());

        TextView wallNameText = (TextView) findViewById(R.id.wallName);
        ViewCompat.setTransitionName(wallNameText, getIntent().getStringExtra("nameTransition"));
        wallNameText.setText(getItem().getName());
        wallNameText.setTextColor(ColorUtils.getMaterialPrimaryTextColor(!(ColorUtils.isLightColor
                (ThemeUtils.darkOrLight(this, R.color.dark_theme_card_background, R.color
                        .light_theme_card_background)))));

        setupWallpaper(mPhoto);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            //Crop request
            if (toHide1 != null && toHide2 != null) {
                toHide1.setVisibility(View.VISIBLE);
                toHide2.setVisibility(View.VISIBLE);
            }
        }
    }

}