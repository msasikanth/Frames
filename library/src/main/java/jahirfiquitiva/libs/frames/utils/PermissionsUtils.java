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

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import jahirfiquitiva.libs.frames.utils.Preferences;

/**
 * @author Jahir Fiquitiva
 */

public class PermissionsUtils {

    public static final int PERMISSION_REQUEST_CODE = 42;
    private static String VIEWER_ACTIVITY_ACTION;
    private static PermissionRequestListener requestListener;

    /*
    * Check if version is marshmallow and above.
    * Used in deciding to ask runtime permission
    */
    private static boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    private static boolean shouldAskPermission(Context context, String permission) {
        if (shouldAskPermission()) {
            int permissionResult = ActivityCompat.checkSelfPermission(context, permission);
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    public static void requestStoragePermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission
                .WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    public static String getViewerActivityAction() {
        return VIEWER_ACTIVITY_ACTION;
    }

    public static void setViewerActivityAction(String viewerActivityAction) {
        VIEWER_ACTIVITY_ACTION = viewerActivityAction;
    }

    @SuppressWarnings("SameParameterValue")
    @TargetApi(Build.VERSION_CODES.M)
    public static void checkPermission(Context context, String permission, PermissionRequestListener
            listener) {
        requestListener = listener;
        /*
        * If permission is not granted
        */
        if (shouldAskPermission(context, permission)) {
            /*
            * If permission denied previously
            */
            if (((Activity) context).shouldShowRequestPermissionRationale(permission)) {
                if (requestListener != null) requestListener.onPermissionDenied();
            } else {
                /*
                * Permission denied or first time requested
                */
                Preferences mPrefs = new Preferences(context);
                if (!(mPrefs.hasAskedPermissions())) {
                    mPrefs.setHasAskedPermissions(true);
                    if (requestListener != null) requestListener.onPermissionRequest();
                } else {
                    /*
                    * Handle the feature without permission or ask user to manually allow permission
                    */
                    if (requestListener != null) requestListener.onPermissionCompletelyDenied();
                }
            }
        } else {
            if (requestListener != null) requestListener.onPermissionGranted();
        }
    }

    public static PermissionRequestListener getListener() {
        return requestListener;
    }

    /*
    * Callback on various cases on checking permission
    *
    * 1.  Below M, runtime permission not needed. In that case onPermissionGranted() would be
     * called.
    *     If permission is already granted, onPermissionGranted() would be called.
    *
    * 2.  Above M, if the permission is being asked first time onPermissionAsk() would be
    * called.
    *
    * 3.  Above M, if the permission is previously asked but not granted,
    * onPermissionPreviouslyDenied()
    *     would be called.
    *
    * 4.  Above M, if the permission is disabled by device policy or the user checked "Never
    * ask again"
    *     check box on previous request permission, onPermissionDisabled() would be called.
    * */
    public interface PermissionRequestListener {
        void onPermissionRequest();

        void onPermissionDenied();

        void onPermissionCompletelyDenied();

        void onPermissionGranted();
    }
}