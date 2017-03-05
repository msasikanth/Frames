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

import com.afollestad.inquiry.Inquiry;

import java.util.ArrayList;
import java.util.Arrays;

import jahirfiquitiva.libs.frames.models.Wallpaper;

@SuppressWarnings("VisibleForTests")
public class FavoritesUtils {

    private static final String DATABASE_NAME = "FAVS_DB";

    public static void init(Context context) {
        Inquiry.newInstance(context, DATABASE_NAME).build();
    }

    public static void destroy(Context context) {
        Inquiry.destroy(context);
    }

    public static ArrayList<Wallpaper> getFavorites(Context context) {
        Wallpaper[] favs = Inquiry.get(context).select(Wallpaper.class).all();
        if (favs == null) return null;
        return new ArrayList<>(Arrays.asList(favs));
    }


    /**
     * Returns true if the item is currently favorited.
     */
    public static boolean isFavorited(Context context, String name) {
        return Inquiry.get(context)
                .select(Wallpaper.class)
                .where("_name = ?", name)
                .first() != null;
    }

    /**
     * Returns true if the item was favorited successfully.
     */
    public static boolean toggleFavorite(Context context, Wallpaper wallpaper) {
        if (!isFavorited(context, wallpaper.getName())) return favorite(context, wallpaper);
        else return unfavorite(context, wallpaper.getName());
    }

    /**
     * Returns true if the item was favorited successfully.
     */
    public static boolean favorite(Context context, Wallpaper wallpaper) {
        try {
            Inquiry.get(context)
                    .insert(Wallpaper.class)
                    .values(new Wallpaper[]{wallpaper})
                    .run();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if the item was unfavorited successfully.
     */
    public static boolean unfavorite(Context context, String name) {
        if (isFavorited(context, name)) {
            try {
                Inquiry.get(context)
                        .delete(Wallpaper.class)
                        .where("_name = ?", name)
                        .run();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Deletes Database
     */
    public static void deleteDB(Context context) {
        Inquiry.get(context).dropTable(Wallpaper.class);
        destroy(context);
    }

}