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
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import java.lang.reflect.Field;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.activities.base.BaseWallpaperViewerActivity;
import jahirfiquitiva.libs.frames.callbacks.WallpaperDialogsCallback;
import jahirfiquitiva.libs.frames.dialogs.FramesDialogs;
import jahirfiquitiva.libs.frames.utils.ColorUtils;
import jahirfiquitiva.libs.frames.utils.FavoritesUtils;
import jahirfiquitiva.libs.frames.utils.IconUtils;
import jahirfiquitiva.libs.frames.utils.PermissionsUtils;
import jahirfiquitiva.libs.frames.utils.ToolbarColorizer;
import jahirfiquitiva.libs.frames.utils.Utils;
import jahirfiquitiva.libs.frames.views.CheckableFloatingActionButton;
import jahirfiquitiva.libs.frames.views.TouchImageView;

public class FabbedViewerActivity extends BaseWallpaperViewerActivity {

    private boolean fabOpened = false;

    private FloatingActionButton fab;
    private FloatingActionButton applyFab;
    private FloatingActionButton saveFab;
    private FloatingActionButton infoFab;
    private CheckableFloatingActionButton favFab;

    @SuppressWarnings("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setFullScreen(true);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fabbed_viewer_activity);
        setLayout((ViewGroup) findViewById(R.id.viewerLayout));

        setCallback(new WallpaperDialogsCallback() {
            @Override
            public void onSaveAction() {
                if (fabOpened) {
                    closeMenu();
                    fabOpened = false;
                }
                hideFab(fab);
            }

            @Override
            public void onDialogShown() {
                onSaveAction();
            }

            @Override
            public void onDialogDismissed() {
                reshowFab(fab);
                setupFullScreen();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        applyFab = (FloatingActionButton) findViewById(R.id.applyFab);
        saveFab = (FloatingActionButton) findViewById(R.id.saveFab);
        infoFab = (FloatingActionButton) findViewById(R.id.infoFab);
        favFab = (CheckableFloatingActionButton) findViewById(R.id.favFab);

        fab.setImageDrawable(IconUtils.getTintedDrawable(this, "ic_plus", ColorUtils
                .getAccentColor(this)));
        applyFab.setImageDrawable(IconUtils.getTintedDrawable(this, "ic_apply", ColorUtils
                .getAccentColor(this)));
        saveFab.setImageDrawable(IconUtils.getTintedDrawable(this, "ic_save", ColorUtils
                .getAccentColor(this)));
        infoFab.setImageDrawable(IconUtils.getTintedDrawable(this, "ic_info", ColorUtils
                .getAccentColor(this)));

        favFab.setWallpaperItem(getItem());
        Drawable prev = favFab.getDrawable();
        if (prev != null)
            prev.mutate();
        favFab.setImageDrawable(ContextCompat.getDrawable(this, ColorUtils.isLightColor
                (ColorUtils.getAccentColor(this)) ? R.drawable.light_heart_animated_selector
                : R.drawable.heart_animated_selector));
        favFab.setChecked(FavoritesUtils.isFavorited(this, getItem().getName()));

        hideFab(applyFab);
        hideFab(saveFab);
        hideFab(infoFab);
        hideFab(favFab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getItem().getName());
            getSupportActionBar().setSubtitle(getItem().getAuthor());
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_with_shadow);
            changeToolbarTextAppearance(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (Build.VERSION.SDK_INT < 19) {
            ToolbarColorizer.colorizeToolbar(toolbar, ContextCompat.getColor(this, android.R
                    .color.white));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabOpened) {
                    closeMenu();
                } else {
                    openMenu();
                }
                fabOpened = !fabOpened;
            }
        });

        favFab.setOnWallpaperFavedListener(getOnFavedListener());
        favFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favFab.toggle();
            }
        });

        applyFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showApplyWallpaperDialog(FabbedViewerActivity.this);
            }
        });

        if (getItem().isDownloadable()) {
            saveFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PermissionsUtils.checkPermission(FabbedViewerActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            new PermissionsUtils.PermissionRequestListener() {
                                @Override
                                public void onPermissionRequest() {
                                    PermissionsUtils.setViewerActivityAction("save");
                                    PermissionsUtils.requestStoragePermission
                                            (FabbedViewerActivity.this);
                                }

                                @Override
                                public void onPermissionDenied() {
                                    FramesDialogs.showPermissionNotGrantedDialog
                                            (FabbedViewerActivity.this);
                                }

                                @Override
                                public void onPermissionCompletelyDenied() {
                                    FramesDialogs.showPermissionNotGrantedDialog
                                            (FabbedViewerActivity.this);
                                }

                                @Override
                                public void onPermissionGranted() {
                                    runWallpaperSave(FabbedViewerActivity.this);
                                }
                            });
                }
            });
        } else {
            saveFab.setVisibility(View.GONE);
        }

        infoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabOpened) {
                    closeMenu();
                    fabOpened = false;
                }
                hideFab(fab);
                FramesDialogs.showWallpaperDetailsDialog(FabbedViewerActivity.this,
                        getItem().getName(), getItem().getAuthor(), getItem().getDimensions(),
                        getItem().getCopyright(), new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                reshowFab(fab);
                                setupFullScreen();
                            }
                        });
            }
        });

        TouchImageView mPhoto = (TouchImageView) findViewById(R.id.big_wallpaper);
        ViewCompat.setTransitionName(mPhoto, getTransitionName());

        mPhoto.setOnSingleTapListener(new TouchImageView.OnSingleTapListener() {
            @Override
            public boolean onSingleTap() {
                navBarVisibilityChange();
                return true;
            }
        });

        setupWallpaper(mPhoto);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reshowFab(fab);
        setupFullScreen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        reshowFab(fab);
        setupFullScreen();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void changeToolbarTextAppearance(Toolbar toolbar) {
        TextView title, subtitle;
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            title = (TextView) f.get(toolbar);
            setTextAppearance(title, R.style.ToolbarTitleWithShadow);
            try {
                Field f2 = toolbar.getClass().getDeclaredField("mSubtitleTextView");
                f2.setAccessible(true);
                subtitle = (TextView) f2.get(toolbar);
                setTextAppearance(subtitle, R.style.ToolbarSubtitleWithShadow);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                //Do nothing
            }
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            //Do nothing
        }
    }

    @SuppressWarnings("deprecation")
    private void setTextAppearance(TextView text, @StyleRes int style) {
        if (Build.VERSION.SDK_INT < 23) {
            text.setTextAppearance(this, style);
        } else {
            text.setTextAppearance(style);
        }
    }

    public void closeViewer() {
        if (fab != null && fab.getVisibility() != View.VISIBLE) {
            reshowFab(fab);
            setupFullScreen();
        } else {
            super.closeViewer();
        }
    }

    private void openMenu() {
        fab.animate().rotation(45.0f).withLayer().setDuration(300).setInterpolator(new
                OvershootInterpolator(10.0F)).start();
        showFab(applyFab);
        showFab(saveFab);
        showFab(infoFab);
        showFab(favFab);
    }

    private void closeMenu() {
        hideFab(favFab);
        hideFab(infoFab);
        hideFab(saveFab);
        hideFab(applyFab);
        fab.animate().rotation(0.0f).withLayer().setDuration(300).setInterpolator(new
                OvershootInterpolator(10.0F)).start();
    }

    private void showFab(FloatingActionButton fab) {
        if (fab != null) {
            fab.show();
            fab.setVisibility(View.VISIBLE);
        }
    }

    private void hideFab(FloatingActionButton fab) {
        if (fab != null) {
            fab.hide();
            fab.setVisibility(View.GONE);
        }
    }

    private void reshowFab(FloatingActionButton fab) {
        if (fab != null) {
            fab.show(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onShown(FloatingActionButton fab) {
                    super.onShown(fab);
                    fab.animate().rotation(0.0f).withLayer().setDuration(300).setInterpolator(new
                            OvershootInterpolator(10.0F)).start();
                }
            });
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void showNotConnectedSnackBar() {
        Snackbar notConnectedSnackBar = Snackbar.make(getLayout(), getString(R.string
                .no_connection_title), Snackbar.LENGTH_LONG);
        ViewGroup snackbarView = (ViewGroup) notConnectedSnackBar.getView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            snackbarView.setPadding(snackbarView.getPaddingLeft(), snackbarView.getPaddingTop(),
                    snackbarView.getPaddingRight(), Utils.getNavigationBarHeight(this));
        }
        notConnectedSnackBar.show();
    }

}