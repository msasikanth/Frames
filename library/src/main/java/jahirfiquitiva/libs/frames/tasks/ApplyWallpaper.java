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

package jahirfiquitiva.libs.frames.tasks;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.IOException;
import java.lang.ref.WeakReference;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.activities.StudioActivity;

public class ApplyWallpaper extends AsyncTask<Void, String, Boolean> {

    private String url;
    private boolean isPicker;
    private final WeakReference<Activity> wrActivity;
    private LinearLayout toHide1, toHide2;
    private volatile boolean wasCancelled = false;
    private ApplyCallback callback;
    private DownloadCallback downloadCallback;
    private Bitmap resource;
    private MaterialDialog dialog;

    private final boolean setToHomeScreen;
    private final boolean setToLockScreen;
    private final boolean setToBoth;

    public ApplyWallpaper(Activity activity, MaterialDialog dialog, Bitmap resource, boolean
            isPicker, boolean setToHomeScreen, boolean setToLockScreen, boolean setToBoth) {
        this.wrActivity = new WeakReference<>(activity);
        this.dialog = dialog;
        this.resource = resource;
        this.isPicker = isPicker;
        this.setToHomeScreen = setToHomeScreen;
        this.setToLockScreen = setToLockScreen;
        this.setToBoth = setToBoth;
    }

    private ApplyWallpaper(Activity activity, @NonNull String url, ApplyCallback callback, boolean
            setToHomeScreen, boolean setToLockScreen, boolean setToBoth) {
        this.wrActivity = new WeakReference<>(activity);
        this.url = url;
        this.callback = callback;
        this.setToHomeScreen = setToHomeScreen;
        this.setToLockScreen = setToLockScreen;
        this.setToBoth = setToBoth;
    }

    public ApplyWallpaper(Activity activity, @NonNull String url, ApplyCallback callback,
                          DownloadCallback downloadCallback, boolean setToHomeScreen, boolean
                                  setToLockScreen, boolean setToBoth) {
        this(activity, url, callback, setToHomeScreen, setToLockScreen, setToBoth);
        this.downloadCallback = downloadCallback;
    }

    public ApplyWallpaper(Activity activity, @NonNull Bitmap resource, ApplyCallback callback,
                          boolean setToHomeScreen, boolean setToLockScreen, boolean setToBoth) {
        this.wrActivity = new WeakReference<>(activity);
        this.resource = resource;
        this.callback = callback;
        this.setToHomeScreen = setToHomeScreen;
        this.setToLockScreen = setToLockScreen;
        this.setToBoth = setToBoth;
    }

//    public ApplyWallpaper(Activity activity, MaterialDialog dialog, Bitmap resource,
//                          View layout, LinearLayout toHide1, LinearLayout toHide2) {
//        this.wrActivity = new WeakReference<>(activity);
////        this.dialog = dialog;
////        this.resource = resource;
//        this.isPicker = false;
//        this.layout = layout;
//        this.toHide1 = toHide1;
//        this.toHide2 = toHide2;
//    }

//    @Override
//    protected void onPreExecute() {
//        if (wrActivity != null) {
//            activity = wrActivity.get();
//        } else if (context != null) {
//            activity = (Activity) context.get();
//        }
//    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (dialog != null) {
            applyWallpaper(resource);
        } else if (resource != null) {
            applyWallpaper(resource);
        } else if (url != null) {
            wrActivity.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(wrActivity.get())
                            .load(url)
                            .asBitmap()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(final Bitmap resource,
                                                            GlideAnimation<? super Bitmap>
                                                                    glideAnimation) {
                                    if (resource != null) {
                                        if (downloadCallback != null) {
                                            downloadCallback.afterDownloaded();
                                        }
                                        try {
                                            Thread.sleep(50);
                                            applyWallpaper(resource);
                                        } catch (InterruptedException ex) {
                                            cancel(true);
                                        }
                                    } else {
                                        cancel(true);
                                    }
                                }
                            });
                }
            });
        }

//        Boolean worked;
//        if ((!wasCancelled) && (activity != null)) {
//            WallpaperManager wm = WallpaperManager.getInstance(activity);
//            try {
//                try {
//                    wm.setBitmap(scaleToActualAspectRatio(resource));
//                } catch (OutOfMemoryError ex) {
//                    Timber.d("OutOfMemoryError: " + ex.getLocalizedMessage());
//                    showRetrySnackbar();
//                }
//                worked = true;
//            } catch (IOException e2) {
//                worked = false;
//            }
//        } else {
//            worked = false;
//        }
//        return worked;
        return true;
    }

    private void applyWallpaper(Bitmap resource) {
        WallpaperManager wm = WallpaperManager.getInstance(wrActivity.get());
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (setToHomeScreen) {
                    wm.setBitmap(scaleToActualAspectRatio(resource), null, true, WallpaperManager
                            .FLAG_SYSTEM);
                } else if (setToLockScreen) {
                    wm.setBitmap(scaleToActualAspectRatio(resource), null, true, WallpaperManager
                            .FLAG_LOCK);
                } else if (setToBoth) {
                    wm.setBitmap(scaleToActualAspectRatio(resource), null, true);
                }
            } else {
                wm.setBitmap(scaleToActualAspectRatio(resource));
            }
            if (callback != null) {
                callback.afterApplied();
            }
        } catch (OutOfMemoryError ex) {
            showRetrySnackbar();
            cancel(true);
        } catch (IOException e2) {
            cancel(true);
        }
    }

    @Override
    protected void onPostExecute(Boolean worked) {
        if (worked) {
            if (wrActivity.get() instanceof StudioActivity) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                dialog = new MaterialDialog.Builder(wrActivity.get())
                        .content(R.string.wallpaper_set)
                        .positiveText(android.R.string.ok)
                        .show();
                if (isPicker) {
                    wrActivity.get().finish();
                }
            }
        }
//        if (!isCancelled()) {
//            if (worked) {
//                dialog.dismiss();
//                if (!isPicker) {
//                    if (toHide1 != null && toHide2 != null) {
//                        toHide1.setVisibility(View.GONE);
//                        toHide2.setVisibility(View.GONE);
//                    } else {
//                        ((ShowcaseActivity) activity).setupToolbarHeader();
//                        ColorUtils.setupToolbarIconsAndTextsColors(activity,
//                                ((ShowcaseActivity) activity).getAppbar(), ((ShowcaseActivity)
// activity).getToolbar());
//                    }
//
//                    Snackbar longSnackbar = Snackbar.make(layout,
//                            activity.getString(R.string.set_as_wall_done), Snackbar.LENGTH_LONG);
//                    final int snackbarLight = ContextCompat.getColor(activity, R.color
// .snackbar_light);
//                    final int snackbarDark = ContextCompat.getColor(activity, R.color
// .snackbar_dark);
//                    ViewGroup snackbarView = (ViewGroup) longSnackbar.getView();
//                    snackbarView.setBackgroundColor(ThemeUtils.darkTheme ? snackbarDark :
// snackbarLight);
//                    snackbarView.setPadding(snackbarView.getPaddingLeft(),
//                            snackbarView.getPaddingTop(), snackbarView.getPaddingRight(),
//                            Utils.getNavigationBarHeight((Activity) context.get()));
//                    longSnackbar.show();
//                    longSnackbar.addCallback(
//                            new Snackbar.Callback() {
//                                @Override
//                                public void onDismissed(Snackbar snackbar, int event) {
//                                    super.onDismissed(snackbar, event);
//                                    if (toHide1 != null && toHide2 != null) {
//                                        toHide1.setVisibility(View.VISIBLE);
//                                        toHide2.setVisibility(View.VISIBLE);
//                                    }
//                                    EventBus.getDefault().post(new WallpaperEvent(true));
//                                }
//                            });
//                }
//            } else {
//                showRetrySnackbar();
//            }
//            if (isPicker) {
//                activity.finish();
//            }
//        }
    }

    @SuppressWarnings("ConstantConditions")
    private Bitmap scaleToActualAspectRatio(Bitmap bitmap) {
        if (bitmap != null) {
            boolean flag = true;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            wrActivity.get().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int deviceWidth = displayMetrics.widthPixels;
            int deviceHeight = displayMetrics.heightPixels;

            int bitmapHeight = bitmap.getHeight();
            int bitmapWidth = bitmap.getWidth();
            if (bitmapWidth > deviceWidth) {
                flag = false;
                int scaledHeight = deviceHeight;
                int scaledWidth = (scaledHeight * bitmapWidth) / bitmapHeight;
                try {
                    if (scaledHeight > deviceHeight) { //TODO check; this is always false?
                        scaledHeight = deviceHeight;
                    }
                    bitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth,
                            scaledHeight, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (flag) {
                if (bitmapHeight > deviceHeight) {
                    int scaledWidth = (deviceHeight * bitmapWidth)
                            / bitmapHeight;
                    try {
                        if (scaledWidth > deviceWidth)
                            scaledWidth = deviceWidth;
                        bitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth,
                                deviceHeight, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bitmap;
    }

    private void showRetrySnackbar() {
//        String retry = wrActivity.get().getResources().getString(R.string.retry);
//        Snackbar snackbar = Snackbar
//                .make(layout, R.string.error, Snackbar.LENGTH_INDEFINITE)
//                .setAction(retry.toUpperCase(), new DebouncedClickListener() {
//                    @Override
//                    public void onDebouncedClick(View view) {
//                        new ApplyWallpaper(activity, dialog, resource, isPicker, layout);
//                    }
//                });
//        final int snackbarLight = ContextCompat.getColor(context.get(), R.color.snackbar_light);
//        final int snackbarDark = ContextCompat.getColor(context.get(), R.color.snackbar_dark);
//        ViewGroup snackbarView = (ViewGroup) snackbar.getView();
//        snackbarView.setBackgroundColor(ThemeUtils.darkTheme ? snackbarDark : snackbarLight);
//        snackbarView.setPadding(snackbarView.getPaddingLeft(),
//                snackbarView.getPaddingTop(), snackbarView.getPaddingRight(),
//                Utils.getNavigationBarHeight((Activity) context.get()));
//        TypedValue typedValue = new TypedValue();
//        Resources.Theme theme = activity.getTheme();
//        theme.resolveAttribute(R.attr.accentColor, typedValue, true);
//        int actionTextColor = typedValue.data;
//        snackbar.setActionTextColor(actionTextColor);
//        snackbar.show();
    }

    public interface ApplyCallback {
        void afterApplied();
    }

    public interface DownloadCallback {
        void afterDownloaded();
    }

}
