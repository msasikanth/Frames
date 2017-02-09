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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import java.util.Calendar;

import jahirfiquitiva.libs.frames.R;

@SuppressWarnings("ResourceAsColor")
public class ThemeUtils {

    private final static int LIGHT = 0;
    private final static int DARK = 1;
    private final static int CLEAR = 2;
    private final static int AUTO = 3;

    private static boolean darkTheme;
    private static boolean transparent;

    public static int darkOrLight(@ColorRes int dark, @ColorRes int light) {
        return darkTheme ? dark : light;
    }

    public static int darkOrLight(@NonNull Context context, @ColorRes int dark, @ColorRes int
            light) {
        return ContextCompat.getColor(context, darkOrLight(dark, light));
    }

    public static int darkLightOrTransparent(@NonNull Context context, @ColorRes int dark,
                                             @ColorRes int light, @ColorRes int transparentColor) {
        if (transparent) return ContextCompat.getColor(context, transparentColor);
        return darkOrLight(context, dark, light);
    }

    public static boolean isDarkTheme() {
        return darkTheme;
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        int mTheme = 1;
        /*
        int mTheme = sp.getInt("theme", (activity.getResources().getInteger(R.integer.app_theme)
                - 1));
                */
        switch (mTheme) {
            default:
            case LIGHT:
                // activity.setTheme(R.style.AppTheme);
                darkTheme = false;
                transparent = false;
                break;
            case DARK:
                // activity.setTheme(R.style.AppThemeDark);
                darkTheme = true;
                transparent = false;
                break;
            case CLEAR:
                // activity.setTheme(R.style.AppThemeClear);
                darkTheme = true;
                transparent = true;
                break;
            case AUTO:
                Calendar c = Calendar.getInstance();
                transparent = false;
                int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
                if (timeOfDay >= 7 && timeOfDay < 20) {
                    // activity.setTheme(R.style.AppTheme);
                    darkTheme = false;
                } else {
                    // activity.setTheme(R.style.AppThemeDark);
                    darkTheme = true;
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void onActivityCreateSetNavBar(Activity activity) {
        /*
        activity.getWindow().setNavigationBarColor(darkTheme ?
                ContextCompat.getColor(activity, R.color.dark_theme_navigation_bar) :
                ContextCompat.getColor(activity, R.color.light_theme_navigation_bar));
                */
    }

    public static void restartActivity(Activity activity) {
        activity.recreate();
    }

}