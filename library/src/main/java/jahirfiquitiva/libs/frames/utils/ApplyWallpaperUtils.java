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

package jahirfiquitiva.libs.frames.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.Timer;
import java.util.TimerTask;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.callbacks.WallpaperDialogsCallback;
import jahirfiquitiva.libs.frames.models.Wallpaper;
import jahirfiquitiva.libs.frames.tasks.ApplyWallpaper;

public class ApplyWallpaperUtils {

    public static void onApplyWallpaperClick(final Context context, final MaterialDialog
            otherDialog, final WallpaperDialogsCallback callback, final Wallpaper item) {
        if (otherDialog != null) {
            otherDialog.dismiss();
        }

        if (callback != null) {
            callback.onDialogShown();
        }

        final ApplyWallpaper[] applyTask = new ApplyWallpaper[1];

        final boolean[] enteredApplyTask = {false};

        final MaterialDialog nDialog = new MaterialDialog.Builder(context)
                .content(R.string.downloading_wallpaper)
                .progress(true, 0)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull
                            DialogAction which) {
                        if (applyTask[0] != null) {
                            applyTask[0].cancel(true);
                        }
                        dialog.dismiss();
                        if (callback != null) {
                            callback.onDialogDismissed();
                        }
                    }
                })
                .show();

        Glide.with(context)
                .load(item.getURL())
                .asBitmap()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(
                            final Bitmap resource,
                            GlideAnimation<? super Bitmap> glideAnimation) {
                        if ((otherDialog != null && otherDialog.isShowing())) {
                            otherDialog.dismiss();
                        }
                        if ((nDialog != null && nDialog.isShowing())) {
                            nDialog.dismiss();
                        }
                        if (resource != null) {
                            enteredApplyTask[0] = true;
                            applyTask[0] = showWallpaperApplyOptionsDialogAndGetTask(context,
                                    resource, callback, nDialog);
                            if (applyTask[0] != null)
                                applyTask[0].execute();
                        }
                    }
                });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUIThread(context, new Runnable() {
                    @Override
                    public void run() {
                        if (!enteredApplyTask[0]) {
                            String newContent = context.getString(R.string
                                    .downloading_wallpaper)
                                    + "\n"
                                    + context.getString(R.string
                                    .download_takes_longer);
                            if (nDialog != null) {
                                nDialog.setContent(newContent);
                                nDialog.setActionButton(DialogAction.POSITIVE,
                                        android.R.string.cancel);
                            }
                        }
                    }
                });
            }
        }, 10000);
    }

    private static ApplyWallpaper showWallpaperApplyOptionsDialogAndGetTask(final Context context,
                                                                            final Bitmap resource,
                                                                            final
                                                                            WallpaperDialogsCallback callback,
                                                                            final MaterialDialog
                                                                                    otherDialog) {
        final ApplyWallpaper[] applyTask = {null};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            new MaterialDialog.Builder(context)
                    .title(R.string.set_wall_to)
                    .listSelector(android.R.color.transparent)
                    .items(R.array.wall_options)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int position,
                                                CharSequence text) {
                            dialog.dismiss();

                            if (otherDialog != null)
                                otherDialog.dismiss();

                            String extra = "";

                            switch (position) {
                                case 0:
                                    extra = context.getResources().getString(R.string.home_screen);
                                    break;
                                case 1:
                                    extra = context.getResources().getString(R.string.lock_screen);
                                    break;
                                case 2:
                                    extra = context.getResources().getString(R.string
                                            .home_lock_screens);
                                    break;
                            }

                            MaterialDialog nDialog = new MaterialDialog.Builder(context)
                                    .content(context.getResources().getString(R.string
                                            .setting_wall_title, extra.toLowerCase()))
                                    .progress(true, 0)
                                    .cancelable(false)
                                    .show();

                            buildApplyTask(context, resource, callback, nDialog, position == 0,
                                    position == 1, position == 2).execute();
                        }
                    })
                    .show();
        } else {
            MaterialDialog nDialog = new MaterialDialog.Builder(context)
                    .content(R.string.setting_wall_title)
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            return buildApplyTask(context, resource, callback, nDialog, false, false, true);
        }
        return applyTask[0];
    }

    private static ApplyWallpaper buildApplyTask(final Context context, Bitmap resource,
                                                 final WallpaperDialogsCallback callback,
                                                 final MaterialDialog dialog, boolean
                                                         setToHomeScreen, boolean
                                                         setToLockScreen, boolean setToBoth) {
        return new ApplyWallpaper((Activity) context, dialog, resource, new ApplyWallpaper
                .ApplyCallback() {
            @Override
            public void afterApplied() {
                runOnUIThread(context, new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null) {
                            dialog.dismiss();
                        }

                        MaterialDialog nDialog = new MaterialDialog.Builder(context)
                                .content(R.string.set_as_wall_done)
                                .positiveText(android.R.string.ok)
                                .show();

                        nDialog.setOnDismissListener
                                (new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        if (callback != null) {
                                            callback.onDialogDismissed();
                                        }
                                    }
                                });
                    }
                });
            }
        }, setToHomeScreen, setToLockScreen, setToBoth);
    }

    private static Handler handler(Context context) {
        return new Handler(context.getMainLooper());
    }

    private static void runOnUIThread(Context context, Runnable r) {
        handler(context).post(r);
    }

}