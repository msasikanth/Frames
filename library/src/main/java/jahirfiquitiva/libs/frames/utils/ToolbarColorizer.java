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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jahirfiquitiva.libs.frames.R;

public class ToolbarColorizer {

    /**
     * Use this method to colorize toolbar icons to the desired target color
     *
     * @param toolbar           toolbar view being colored
     * @param toolbarIconsColor the target color of toolbar icons
     */
    public static void colorizeToolbar(Toolbar toolbar, final int toolbarIconsColor) {
        final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(toolbarIconsColor,
                PorterDuff.Mode.SRC_IN);

        for (int i = 0; i < toolbar.getChildCount(); i++) {
            final View v = toolbar.getChildAt(i);

            //Step 1 : Changing the color of back button (or open drawer button).
            if (v instanceof ImageButton) {
                //Action Bar back button
                ((ImageButton) v).getDrawable().setColorFilter(colorFilter);
            }

            if (v instanceof ActionMenuView) {
                for (int j = 0; j < ((ActionMenuView) v).getChildCount(); j++) {
                    //Step 2: Changing the color of any ActionMenuViews - icons that are not back
                    // button, nor text, nor overflow menu icon.
                    //Colorize the ActionViews -> all icons that are NOT: back button | overflow
                    // menu
                    final View innerView = ((ActionMenuView) v).getChildAt(j);
                    if (innerView instanceof ActionMenuItemView) {
                        for (int k = 0; k < ((ActionMenuItemView) innerView).getCompoundDrawables
                                ().length; k++) {
                            if (((ActionMenuItemView) innerView).getCompoundDrawables()[k] !=
                                    null) {
                                final int finalK = k;

                                //Important to set the color filter in separate thread, by adding
                                // it to the message queue
                                //Won't work otherwise.
                                innerView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((ActionMenuItemView) innerView).getCompoundDrawables()
                                                [finalK].setColorFilter(colorFilter);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }

        //Step 3: Changing the color of title and subtitle.
        toolbar.setTitleTextColor(toolbarIconsColor);
        toolbar.setSubtitleTextColor(toolbarIconsColor);

        //Step 4: Change the color of overflow menu icon.
        Drawable drawable = toolbar.getOverflowIcon();
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            toolbar.setOverflowIcon(IconUtils.getTintedIcon(drawable,
                    toolbarIconsColor));
        }
    }

    /**
     * This method's code was created by Aidan Follestad. Complete credits to him.
     */
    @SuppressWarnings("PrivateResource")
    public static void tintSearchView(Context context, @NonNull Toolbar toolbar, MenuItem item,
                                      @NonNull SearchView searchView, @ColorInt int color) {
        item.setIcon(IconUtils.getTintedIcon(context, R.drawable.ic_search, color));
        final Class<?> searchViewClass = searchView.getClass();
        try {
            final Field mCollapseIconField = toolbar.getClass().getDeclaredField("mCollapseIcon");
            mCollapseIconField.setAccessible(true);
            final Drawable drawable = (Drawable) mCollapseIconField.get(toolbar);
            if (drawable != null)
                mCollapseIconField.set(toolbar, IconUtils.getTintedIcon(drawable, color));

            final Field mSearchSrcTextViewField = searchViewClass.getDeclaredField
                    ("mSearchSrcTextView");
            mSearchSrcTextViewField.setAccessible(true);
            final EditText mSearchSrcTextView = (EditText) mSearchSrcTextViewField.get(searchView);
            mSearchSrcTextView.setTextColor(color);
            mSearchSrcTextView.setHintTextColor(ColorUtils.adjustAlpha(color, 0.65f));
            setCursorTint(mSearchSrcTextView, color);

            hideSearchHintIcon(context, searchView);

            Field field = searchViewClass.getDeclaredField("mSearchButton");
            tintImageView(searchView, field, color);
            field = searchViewClass.getDeclaredField("mGoButton");
            tintImageView(searchView, field, color);
            field = searchViewClass.getDeclaredField("mCloseButton");
            tintImageView(searchView, field, color);
            field = searchViewClass.getDeclaredField("mVoiceButton");
            tintImageView(searchView, field, color);
            field = searchViewClass.getDeclaredField("mCollapsedIcon");
            tintImageView(searchView, field, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void hideSearchHintIcon(Context context, SearchView searchView) {
        if (context != null) {
            final Class<?> searchViewClass = searchView.getClass();
            try {
                final Field mSearchHintIcon = searchViewClass.getDeclaredField("mSearchHintIcon");
                mSearchHintIcon.setAccessible(true);
                Drawable mSearchHintIconDrawable = (Drawable) mSearchHintIcon.get(searchView);
                mSearchHintIconDrawable.setBounds(0, 0, 0, 0);
                mSearchHintIconDrawable.setAlpha(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void setCursorTint(@NonNull EditText editText, @ColorInt int color) {
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[2];
            drawables[0] = IconUtils.getTintedIcon(editText.getContext(), mCursorDrawableRes,
                    color);
            drawables[1] = IconUtils.getTintedIcon(editText.getContext(), mCursorDrawableRes,
                    color);
            fCursorDrawable.set(editor, drawables);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void tintImageView(Object target, Field field, int tintColor) throws Exception {
        field.setAccessible(true);
        final ImageView imageView = (ImageView) field.get(target);
        if (imageView == null) return;
        if (imageView.getDrawable() != null)
            imageView.setImageDrawable(IconUtils.getTintedIcon(imageView.getDrawable(),
                    tintColor));
    }

    public static void setupCollapsingToolbarIconsAndTextsColors(final Context context,
                                                                 AppBarLayout appbar,
                                                                 final Toolbar toolbar) {

        final int defaultIconsColor = ContextCompat.getColor(context, android.R.color.white);

        if (appbar != null) {
            appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @SuppressWarnings("ResourceAsColor")
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    double ratio = Utils.round(((double) (verticalOffset * -1) / 255.0), 1);
                    if (ratio > 1) {
                        ratio = 1;
                    } else if (ratio < 0) {
                        ratio = 0;
                    }
                    int paletteColor = ColorUtils.blendColors(defaultIconsColor, ColorUtils
                            .getMaterialPrimaryTextColor(!(ColorUtils.isLightColor(ThemeUtils
                                    .darkOrLight(context, R.color.dark_theme_primary, R.color
                                            .light_theme_primary)))), (float) ratio);
                    if (toolbar != null) {
                        // Collapsed offset = -352
                        colorizeToolbar(toolbar, paletteColor);
                    }
                }
            });
        }
    }

    public static void setupCollapsingToolbarTextColors(Context context, CollapsingToolbarLayout
            collapsingToolbarLayout) {
        setupCollapsingToolbarTextColors(context, collapsingToolbarLayout, false);
    }

    @SuppressWarnings("ResourceAsColor")
    private static void setupCollapsingToolbarTextColors(Context context, CollapsingToolbarLayout
            collapsingToolbarLayout, boolean transparentWhenExpanded) {
        int textColor = ColorUtils.getMaterialPrimaryTextColor(!(ColorUtils.isLightColor
                (ThemeUtils.darkOrLight(context, R.color.dark_theme_primary, R.color
                        .light_theme_primary))));
        if (transparentWhenExpanded) {
            collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(context, android.R
                    .color.transparent));
        } else {
            collapsingToolbarLayout.setExpandedTitleColor(textColor);
        }
        collapsingToolbarLayout.setCollapsedTitleTextColor(textColor);
    }

    public static void makeMenuIconsVisible(Menu menu) {
        try {
            Class<?> MenuBuilder = menu.getClass();
            Method setOptionalIconsVisible =
                    MenuBuilder.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            if (!setOptionalIconsVisible.isAccessible()) {
                setOptionalIconsVisible.setAccessible(true);
            }
            setOptionalIconsVisible.invoke(menu, true);
        } catch (Exception ignored) {
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void tintStatusBar(Activity activity) {
        int statusBarColor = ThemeUtils.darkOrLight(activity, R.color.dark_theme_primary_dark, R
                .color.light_theme_primary_dark);
        if (ColorUtils.isLightColor(statusBarColor)) {
            setLightStatusBar(activity);
        } else {
            clearLightStatusBar(activity);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static void setLightStatusBar(@NonNull Activity activity) {
        int flags = activity.getWindow().getDecorView().getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        activity.getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void clearLightStatusBar(@NonNull Activity activity) {
        int flags = activity.getWindow().getDecorView().getSystemUiVisibility();
        flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        activity.getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    public static void tintMenu(final Menu menu, @ColorInt final int iconsColor) {
        if (menu != null) {
            for (int i = 0, size = menu.size(); i < size; i++) {
                MenuItem menuItem = menu.getItem(i);
                if (isInOverflow(menuItem)) {
                    colorMenuItem(menuItem, iconsColor);
                }
                if (menuItem.hasSubMenu()) {
                    SubMenu subMenu = menuItem.getSubMenu();
                    for (int j = 0; j < subMenu.size(); j++) {
                        colorMenuItem(subMenu.getItem(j), iconsColor);
                    }
                }
            }
        }
    }

    private static boolean isInOverflow(MenuItem item) {
        return !isActionButton(item);
    }

    private static boolean isActionButton(MenuItem item) {
        if (item instanceof MenuItemImpl) {
            return ((MenuItemImpl) item).isActionButton();
        }
        Method nativeIsActionButton = null;
        try {
            Class<?> MenuItemImpl = Class.forName("com.android.internal.view.menu" +
                    ".MenuItemImpl");
            nativeIsActionButton = MenuItemImpl.getDeclaredMethod("isActionButton");
            if (!nativeIsActionButton.isAccessible()) {
                nativeIsActionButton.setAccessible(true);
            }
        } catch (Exception ignored) {
        }
        try {
            //noinspection ConstantConditions
            return (boolean) nativeIsActionButton.invoke(item, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private static void colorMenuItem(MenuItem menuItem, int color) {
        Drawable drawable = menuItem.getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        }
    }

}