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
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.dialogs.FramesDialogs;
import jahirfiquitiva.libs.frames.tasks.DownloadJSON;
import jahirfiquitiva.libs.repellomaxima.mess.RepelloCallback;
import jahirfiquitiva.libs.repellomaxima.mess.RepelloMaxima;

public class Utils {

    public static final String LOG_TAG = "Frames";

    public static int getAppVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                    .versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // this should never happen
            return -1;
        }
    }

    public static String getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                    .versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // this should never happen
            return "Unknown";
        }
    }

    public static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isConnectedToWiFi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && (activeNetwork.getType() == ConnectivityManager
                .TYPE_WIFI) && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager pm = context.getPackageManager();
        boolean installed;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public static void openLink(Context context, String link) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //Do nothing
        }
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @SuppressWarnings("SameParameterValue")
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static Bitmap drawableToBitmap(Context context, Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, GlideConfiguration.getBitmapsConfig(context));
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable
                    .getIntrinsicHeight(), GlideConfiguration.getBitmapsConfig(context));
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static int getNavigationBarHeight(Activity activity) {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = activity.getResources().getBoolean(R.bool.isTablet) ? metrics
                    .heightPixels : activity.getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE ? metrics.widthPixels : metrics
                    .heightPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = activity.getResources().getBoolean(R.bool.isTablet) ? metrics
                    .heightPixels : activity.getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE ? metrics.widthPixels : metrics
                    .heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static void runLicenseChecker(Context context, boolean ch, String lic, boolean allAma,
                                         SuccessCallback callback) {
        Preferences mPrefs = new Preferences(context);
        if (ch && isNewVersion(context)) {
            checkLicense(context, lic, allAma, callback);
        } else {
            mPrefs.setDashboardWorking(true);
            if (callback != null) callback.onSuccess();
        }
    }

    private static boolean isNewVersion(Context context) {
        Preferences mPrefs = new Preferences(context);
        int prevVersionCode = mPrefs.getVersionCode();
        int curVersionCode = getAppVersionCode(context);
        if ((curVersionCode > prevVersionCode) && (curVersionCode > -1)) {
            mPrefs.setVersionCode(curVersionCode);
            return true;
        }
        return false;
    }

    private static void checkLicense(final Context context, String lic, boolean allAma,
                                     final SuccessCallback callback) {
        final Preferences mPrefs = new Preferences(context);
        final RepelloMaxima[] spell = new RepelloMaxima[1];
        spell[0] = new RepelloMaxima.Speller(context)
                .withLicKey(lic)
                .allAmazon(allAma)
                .thenDo(new RepelloCallback() {
                    @Override
                    public void onRepelled() {
                        FramesDialogs.showLicenseSuccessDialog(context, new MaterialDialog
                                .SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull
                                    DialogAction dialogAction) {
                                mPrefs.setDashboardWorking(true);
                                if (callback != null) callback.onSuccess();
                            }
                        }, new MaterialDialog.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                mPrefs.setDashboardWorking(true);
                                if (callback != null) callback.onSuccess();
                            }
                        }, new MaterialDialog.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                mPrefs.setDashboardWorking(true);
                                if (callback != null) callback.onSuccess();
                            }
                        });
                    }

                    @Override
                    public void onLucky() {
                        try {
                            showNotLicensedDialog(((Activity) context), mPrefs);
                        } catch (Exception e) {
                            ((Activity) context).finish();
                        }
                    }

                    @Override
                    public void onCastError(PiracyCheckerError error) {
                        FramesDialogs.showLicenseErrorDialog(context, new MaterialDialog
                                .SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull
                                    DialogAction which) {
                                if (spell[0] != null) spell[0].cast();
                            }
                        }, new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull
                                    DialogAction which) {
                                ((Activity) context).finish();
                            }
                        }, new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                ((Activity) context).finish();
                            }
                        }, new MaterialDialog.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                ((Activity) context).finish();
                            }
                        });
                    }
                })
                .construct();
        spell[0].cast();
    }

    private static void showNotLicensedDialog(final Activity act, Preferences mPrefs) {
        mPrefs.setDashboardWorking(false);
        FramesDialogs.showShallNotPassDialog(act, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction
                    dialogAction) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + act
                                .getPackageName()));
                act.startActivity(browserIntent);
            }
        }, new MaterialDialog.SingleButtonCallback() {

            @Override
            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction
                    dialogAction) {
                act.finish();
            }
        }, new MaterialDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                act.finish();
            }
        }, new MaterialDialog.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                act.finish();
            }
        });
    }

    public interface SuccessCallback {
        void onSuccess();
    }

}