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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import jahirfiquitiva.libs.frames.R;

public class Preferences {

    private static final String
            PREFERENCES_NAME = "dashboard_preferences",
            WORKING_DASHBOARD = "working_dashboard",
            ASKED_PERMISSIONS = "asked_permissions",
            CURRENT_THEME = "current_theme",
            TINT_NAVBAR = "tint_navbar",
            VERSION_CODE = "version_code",
            WALLS_DOWNLOAD_FOLDER = "walls_download_folder",
            WALLS_COLUMNS_NUMBER = "walls_columns_number",
            INSTAGRAM_LIKE_BEHAVIOR = "instagram_like_behavior",
            MUZEI_REFRESH_INTERVAL = "muzei_refresh_interval",
            MUZEI_REFRESH_ON_WIFI_ONLY = "muzei_refresh_on_wifi_only",
            MUZEI_COLLECTIONS = "muzei_collections";

    private final Context context;

    public Preferences(Context context) {
        this.context = context;
    }

    public SharedPreferences getPrefs() {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public boolean isDashboardWorking() {
        return getPrefs().getBoolean(WORKING_DASHBOARD, false);
    }

    public void setDashboardWorking(boolean enable) {
        getPrefs().edit().putBoolean(WORKING_DASHBOARD, enable).apply();
    }

    public boolean hasAskedPermissions() {
        return getPrefs().getBoolean(ASKED_PERMISSIONS, false);
    }

    @SuppressWarnings("SameParameterValue")
    public void setHasAskedPermissions(boolean asked) {
        getPrefs().edit().putBoolean(ASKED_PERMISSIONS, asked).apply();
    }

    public void setCurrentTheme(int currentTheme) {
        getPrefs().edit().putInt(CURRENT_THEME, currentTheme).apply();
    }

    public int getCurrentTheme() {
        return getPrefs().getInt(CURRENT_THEME, 1);
    }

    public void setTintNavbar(boolean tint) {
        getPrefs().edit().putBoolean(TINT_NAVBAR, tint).apply();
    }

    public boolean canTintNavbar() {
        return getPrefs().getBoolean(TINT_NAVBAR, true);
    }

    public void setMuzeiRefreshInterval(int interval) {
        getPrefs().edit().putInt(MUZEI_REFRESH_INTERVAL, interval).apply();
    }

    public int getMuzeiRefreshInterval() {
        return getPrefs().getInt(MUZEI_REFRESH_INTERVAL, 8);
    }

    public void setMuzeiRefreshOnWiFiOnly(boolean onWifiOnly) {
        getPrefs().edit().putBoolean(MUZEI_REFRESH_ON_WIFI_ONLY, onWifiOnly).apply();
    }

    public boolean getMuzeiRefreshOnWiFiOnly() {
        return getPrefs().getBoolean(MUZEI_REFRESH_ON_WIFI_ONLY, false);
    }

    public void setMuzeiCollections(String collections) {
        getPrefs().edit().putString(MUZEI_COLLECTIONS, collections).apply();
    }

    public String getMuzeiCollections() {
        return getPrefs().getString(MUZEI_COLLECTIONS, "");
    }

    public String getDownloadsFolder() {
        return getPrefs().getString(WALLS_DOWNLOAD_FOLDER,
                context != null
                        ? context.getResources().getString(R.string.walls_save_location,
                        Environment.getExternalStorageDirectory().getAbsolutePath())
                        : Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/Frames/Wallpapers");
    }

    public void setDownloadsFolder(String folder) {
        getPrefs().edit().putString(WALLS_DOWNLOAD_FOLDER, folder).apply();
    }

    public int getWallsColumnsNumber() {
        return getPrefs().getInt(WALLS_COLUMNS_NUMBER,
                context.getResources().getInteger(R.integer.wallpapers_grid_width));
    }

    public void setWallsColumnsNumber(int columnsNumber) {
        getPrefs().edit().putInt(WALLS_COLUMNS_NUMBER, columnsNumber).apply();
    }

    public int getVersionCode() {
        return getPrefs().getInt(VERSION_CODE, 0);
    }

    public void setVersionCode(int versionCode) {
        getPrefs().edit().putInt(VERSION_CODE, versionCode).apply();
    }

    public void setInstagramLikeBehavior(boolean behavior) {
        getPrefs().edit().putBoolean(INSTAGRAM_LIKE_BEHAVIOR, behavior).apply();
    }

    public boolean isInstagramLikeBehavior() {
        return getPrefs().getBoolean(INSTAGRAM_LIKE_BEHAVIOR, false);
    }

}