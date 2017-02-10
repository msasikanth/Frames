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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jahirfiquitiva.libs.frames.R;

/**
 * <p>Apply colors and/or transparency to menu icons in a {@link Menu}.</p> <p> <p>Example
 * usage:</p>
 * <p/>
 * <pre class="prettyprint"> public boolean onCreateOptionsMenu(Menu menu) { ... int color =
 * getResources().getColor(R.color.your_awesome_color); int alpha = 204; // 80% transparency
 * ToolbarTinter.on(menu).setMenuItemIconColor(color).setMenuItemIconAlpha(alpha).apply(this); ... }
 * </pre>
 */
public class ToolbarTinter {

    private static final String TAG = "ToolbarTinter";

    private static Method nativeIsActionButton;
    private final Menu menu;
    private final Toolbar toolbar;
    private final int originalIconsColor;
    @DrawableRes
    private final int overflowDrawableId;
    private final boolean reApplyOnChange;
    private final boolean forceIcons;
    @ColorInt
    private int iconsColor = -1;
    private ImageView overflowButton;
    private ViewGroup actionBarView;

    private ToolbarTinter(Builder builder) {
        menu = builder.menu;
        originalIconsColor = builder.originalIconsColor;
        iconsColor = builder.iconsColor;
        overflowDrawableId = builder.overflowDrawableId;
        reApplyOnChange = builder.reApplyOnChange;
        forceIcons = builder.forceIcons;
        toolbar = builder.toolbar;
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
                            .getMaterialPrimaryTextColor(ColorUtils.isLightColor(ThemeUtils
                                    .darkOrLight(context, R.color.light_theme_primary, R.color
                                            .dark_theme_primary))), (float) ratio);
                    if (toolbar != null) {
                        // Collapsed offset = -352
                        tintToolbar(toolbar, paletteColor);
                    }
                }
            });
        }
    }

    @SuppressWarnings("ResourceAsColor")
    public static void setupCollapsingToolbarTextColors(Context context, CollapsingToolbarLayout
            collapsingToolbarLayout) {
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(context, android.R
                .color.transparent));
        collapsingToolbarLayout.setCollapsedTitleTextColor(ColorUtils.getMaterialPrimaryTextColor
                (ColorUtils.isLightColor(ThemeUtils.darkOrLight(context, R.color
                        .light_theme_primary, R.color.dark_theme_primary))));
    }

    /**
     * Check if an item is showing (not in the overflow menu).
     *
     * @param item the MenuItem.
     * @return {@code true} if the MenuItem is visible on the ActionBar.
     */
    private static boolean isActionButton(MenuItem item) {
        if (item instanceof MenuItemImpl) {
            return ((MenuItemImpl) item).isActionButton();
        }
        if (nativeIsActionButton == null) {
            try {
                Class<?> MenuItemImpl = Class.forName("com.android.internal.view.menu" +
                        ".MenuItemImpl");
                nativeIsActionButton = MenuItemImpl.getDeclaredMethod("isActionButton");
                if (!nativeIsActionButton.isAccessible()) {
                    nativeIsActionButton.setAccessible(true);
                }
            } catch (Exception ignored) {
            }
        }
        try {
            //noinspection ConstantConditions
            return (boolean) nativeIsActionButton.invoke(item, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Check if an item is in the overflow menu.
     *
     * @param item the MenuItem
     * @return {@code true} if the MenuItem is in the overflow menu.
     * @see #isActionButton(MenuItem)
     */
    private static boolean isInOverflow(MenuItem item) {
        return !isActionButton(item);
    }

    /**
     * Sets the color filter and/or the alpha transparency on a {@link MenuItem}'s icon.
     *
     * @param menuItem The {@link MenuItem} to theme.
     * @param color    The color to set for the color filter or {@code null} for no changes.
     */
    private static void colorMenuItem(MenuItem menuItem, int color) {
        if (color == -1) {
            return; // nothing to do.
        }
        Drawable drawable = menuItem.getIcon();
        if (drawable != null) {
            // If we don't mutate the drawable, then all drawables with this id will have the
            // ColorFilter
            drawable.mutate();
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        }
    }

    /**
     * Set the menu to show MenuItem icons in the overflow window.
     *
     * @param menu the menu to force icons to show
     */
    private static void forceMenuIcons(Menu menu) {
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

    public static Builder on(Menu menu) {
        return new Builder(menu);
    }

    public static Builder on(Toolbar toolbar) {
        return new Builder(toolbar);
    }

    public static Builder on(Menu menu, Toolbar toolbar) {
        return new Builder(menu, toolbar);
    }

    /**
     * Apply a ColorFilter with the specified color to all icons in the menu.
     *
     * @param activity the Activity.
     * @param menu     the menu after items have been added.
     * @param color    the color for the ColorFilter.
     */
    public static void colorIcons(Activity activity, Menu menu, int color) {
        ToolbarTinter.on(menu).setIconsColor(color).apply(activity);
    }

    /**
     * @param activity the Activity
     * @return the OverflowMenuButton or {@code null} if it doesn't exist.
     */
    public static ImageView getOverflowMenuButton(Activity activity) {
        return findOverflowMenuButton(activity, findActionBar(activity));
    }

    private static ImageView findOverflowMenuButton(Activity activity, ViewGroup viewGroup) {
        if (viewGroup == null) {
            return null;
        }
        ImageView overflow = null;
        for (int i = 0, count = viewGroup.getChildCount(); i < count; i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ImageView
                    && (v.getClass().getSimpleName().equals("OverflowMenuButton")
                    || v instanceof ActionMenuView.ActionMenuChildView)) {
                overflow = (ImageView) v;
            } else if (v instanceof ViewGroup) {
                overflow = findOverflowMenuButton(activity, (ViewGroup) v);
            }
            if (overflow != null) {
                break;
            }
        }
        return overflow;
    }

    private static ViewGroup findActionBar(Activity activity) {
        int id = activity.getResources().getIdentifier("action_bar", "id", "android");
        ViewGroup actionBar = null;
        if (id != 0) {
            actionBar = (ViewGroup) activity.findViewById(id);
        }
        if (actionBar == null) {
            actionBar = findToolbar((ViewGroup) activity.findViewById(android.R.id.content)
                    .getRootView());
        }
        return actionBar;
    }

    private static ViewGroup findToolbar(ViewGroup viewGroup) {
        ViewGroup toolbar = null;
        for (int i = 0, len = viewGroup.getChildCount(); i < len; i++) {
            View view = viewGroup.getChildAt(i);
            if (view.getClass() == android.support.v7.widget.Toolbar.class
                    || view.getClass().getName().equals("android.widget.Toolbar")) {
                toolbar = (ViewGroup) view;
            } else if (view instanceof ViewGroup) {
                toolbar = findToolbar((ViewGroup) view);
            }
            if (toolbar != null) {
                break;
            }
        }
        return toolbar;
    }

    /**
     * <p>Sets a ColorFilter and/or alpha on all the {@link MenuItem}s in the menu, including the
     * OverflowMenuButton.</p> <p> <p>Call this method after inflating/creating your menu in {@link
     * Activity#onCreateOptionsMenu(Menu)}.</p> <p> <p>Note: This is targeted for the native
     * ActionBar/Toolbar, not AppCompat.</p>
     *
     * @param activity the activity to apply the menu tinting on.
     */
    private void apply(final Activity activity) {
        if (menu != null) {
            if (forceIcons) {
                forceMenuIcons(menu);
            }

            for (int i = 0, size = menu.size(); i < size; i++) {
                MenuItem item = menu.getItem(i);
                colorMenuItem(item, iconsColor);
                if (reApplyOnChange) {
                    View view = item.getActionView();
                    if (view != null) {
                        if (item instanceof MenuItemImpl) {
                            ((MenuItemImpl) item).setSupportOnActionExpandListener(
                                    new SupportActionExpandListener(this));
                        } else {
                            item.setOnActionExpandListener(new NativeActionExpandListener(this));
                        }
                    }
                }
            }
        }

        if (toolbar != null) {
            actionBarView = toolbar;
        } else {
            actionBarView = findActionBar(activity);
        }
        if (actionBarView == null) {
            Log.w(TAG, "Could not find the ActionBar");
            return;
        }

        tintToolbar(actionBarView, iconsColor);
        tintMenu(activity, menu, actionBarView);
    }

    private static void tintToolbar(final ViewGroup toolbar, @ColorInt
    final int iconsColor) {
        // We must wait for the view to be created to set a color filter on the drawables.
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(iconsColor,
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
                            //Step 2: Changing the color of any ActionMenuViews - icons that are
                            // not back
                            // button, nor text, nor overflow menu icon.
                            //Colorize the ActionViews -> all icons that are NOT: back button |
                            // overflow
                            // menu
                            final View innerView = ((ActionMenuView) v).getChildAt(j);
                            if (innerView instanceof ActionMenuItemView) {
                                for (int k = 0; k < ((ActionMenuItemView) innerView)
                                        .getCompoundDrawables
                                                ().length; k++) {
                                    if (((ActionMenuItemView) innerView).getCompoundDrawables()
                                            [k] != null) {
                                        final int finalK = k;

                                        //Important to set the color filter in separate thread,
                                        // by adding
                                        // it to the message queue
                                        //Won't work otherwise.
                                        innerView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                ((ActionMenuItemView) innerView)
                                                        .getCompoundDrawables()[finalK]
                                                        .setColorFilter(colorFilter);
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
                if (iconsColor != -1) {
                    try {
                        ((Toolbar) toolbar).setTitleTextColor(iconsColor);
                        ((Toolbar) toolbar).setSubtitleTextColor(iconsColor);
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void tintMenu(final Activity activity, final Menu menu, final ViewGroup toolbar) {
        if (menu == null) return;
        toolbar.post(new Runnable() {
            @Override
            public void run() {
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
                if (iconsColor != -1) {
                    overflowButton = findOverflowMenuButton(activity, toolbar);
                    if (overflowButton != null)
                        colorOverflowMenuItem(activity, overflowButton);
                }
            }
        });
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
            mSearchSrcTextView.setHintTextColor(ColorUtils.adjustAlpha(color, 0.5f));
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

    /**
     * <p>Sets a ColorFilter and/or alpha on all the {@link MenuItem}s in the menu, including the
     * OverflowMenuButton.</p> <p> <p>This should only be called after calling {@link
     * #apply(Activity)}. It is useful for when {@link MenuItem}s might be re-arranged due to an
     * action view being collapsed or expanded.</p>
     */
    private void reapply(final Activity activity) {
        if (menu != null) {
            for (int i = 0, size = menu.size(); i < size; i++) {
                MenuItem item = menu.getItem(i);
                if (isActionButton(item)) {
                    colorMenuItem(menu.getItem(i), iconsColor);
                }
            }
        }

        if (actionBarView == null) {
            return;
        }

        actionBarView.post(new Runnable() {
            @Override
            public void run() {
                if (menu != null) {
                    for (int i = 0, size = menu.size(); i < size; i++) {
                        MenuItem menuItem = menu.getItem(i);
                        if (isInOverflow(menuItem)) {
                            colorMenuItem(menuItem, iconsColor);
                        } else {
                            colorMenuItem(menu.getItem(i), iconsColor);
                        }
                        if (menuItem.hasSubMenu()) {
                            SubMenu subMenu = menuItem.getSubMenu();
                            for (int j = 0; j < subMenu.size(); j++) {
                                colorMenuItem(subMenu.getItem(j), iconsColor);
                            }
                        }
                    }
                }
                if (iconsColor != -1) {
                    colorOverflowMenuItem(activity, overflowButton);
                }
            }

        });
    }

    private void colorOverflowMenuItem(Activity activity, ImageView overflow) {
        if (overflow != null) {
            try {
                if (activity != null) {
                    Drawable overflowDrawable = ContextCompat.getDrawable(activity,
                            overflowDrawableId);
                    if (overflowDrawable != null) {
                        overflow.setImageDrawable(overflowDrawable);
                    }
                } else {
                    if (overflowDrawableId > 0) {
                        overflow.setImageResource(overflowDrawableId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (iconsColor != -1) {
                overflow.setColorFilter(new PorterDuffColorFilter(iconsColor, PorterDuff.Mode
                        .SRC_IN));
            }
        }
    }

    public Menu getMenu() {
        return menu;
    }

    public ImageView getOverflowMenuButton() {
        return overflowButton;
    }

    private void setMenuItemIconColor(@ColorInt int color) {
        iconsColor = color;
    }

    public static class NativeActionExpandListener implements OnActionExpandListener {

        private final ToolbarTinter menuTint;

        public NativeActionExpandListener(ToolbarTinter menuTint) {
            this.menuTint = menuTint;
        }

        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            int color = menuTint.iconsColor != -1 ? menuTint.iconsColor :
                    menuTint.originalIconsColor;
            menuTint.setMenuItemIconColor(color);
            menuTint.reapply(null);
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            int color = menuTint.iconsColor != -1 ? menuTint.iconsColor :
                    menuTint.originalIconsColor;
            menuTint.setMenuItemIconColor(color);
            menuTint.reapply(null);
            return true;
        }

    }

    public static class SupportActionExpandListener implements
            MenuItemCompat.OnActionExpandListener {

        private final ToolbarTinter menuTint;

        public SupportActionExpandListener(ToolbarTinter menuTint) {
            this.menuTint = menuTint;
        }

        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            int color = menuTint.iconsColor != -1 ? menuTint.iconsColor :
                    menuTint.originalIconsColor;
            menuTint.setMenuItemIconColor(color);
            menuTint.reapply(null);
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            int color = menuTint.iconsColor != -1 ? menuTint.iconsColor :
                    menuTint.originalIconsColor;
            menuTint.setMenuItemIconColor(color);
            menuTint.reapply(null);
            return true;
        }

    }

    // --------------------------------------------------------------------------------------------

    public static final class Builder {

        private Menu menu;
        private Toolbar toolbar;
        @ColorInt
        private int iconsColor;
        private int overflowDrawableId;
        private int originalIconsColor;
        private boolean reApplyOnChange;
        private boolean forceIcons;

        private Builder(Menu menu) {
            this.menu = menu;
        }

        private Builder(Toolbar toolbar) {
            this.toolbar = toolbar;
        }

        private Builder(Menu menu, Toolbar toolbar) {
            this.menu = menu;
            this.toolbar = toolbar;
        }

        /**
         * <p>Sets an {@link OnActionExpandListener} on all {@link MenuItem}s with views, so when
         * the menu is updated, the colors will be also.</p> <p> <p>This is useful when the overflow
         * menu is showing icons and {@link MenuItem}s might be pushed to the overflow menu when a
         * action view is expanded e.g. android.widget.SearchView. </p>
         *
         * @param reapply {@code true} to set the listeners on all {@link MenuItem}s with action
         *                views.
         * @return this Builder object to allow for chaining of calls to set methods
         */
        @SuppressWarnings("SameParameterValue")
        public Builder reapplyOnChange(boolean reapply) {
            reApplyOnChange = reapply;
            return this;
        }

        /**
         * Specify a color for visible MenuItem icons, including the OverflowMenuButton.
         *
         * @param color the color to apply on visible MenuItem icons, including the
         *              OverflowMenuButton.
         * @return this Builder object to allow for chaining of calls to set methods
         */
        public Builder setIconsColor(int color) {
            iconsColor = color;
            return this;
        }

        /**
         * Specify a color that is applied when an action view is expanded or collapsed.
         *
         * @param color the color to apply on MenuItems when an action-view is expanded or
         *              collapsed.
         * @return this Builder object to allow for chaining of calls to set methods
         */
        public Builder setOriginalIconsColor(int color) {
            originalIconsColor = color;
            return this;
        }

        /**
         * Set the drawable id to set on the OverflowMenuButton.
         *
         * @param drawableId the resource identifier of the drawable
         * @return this Builder object to allow for chaining of calls to set methods
         */
        public Builder setOverflowDrawableId(int drawableId) {
            overflowDrawableId = drawableId;
            return this;
        }

        /**
         * Set the menu to show MenuItem icons in the overflow window.
         *
         * @return this Builder object to allow for chaining of calls to set methods
         */
        public Builder forceIcons() {
            forceIcons = true;
            return this;
        }

        /**
         * <p>Sets a ColorFilter and/or alpha on all the MenuItems in the menu, including the
         * OverflowMenuButton.</p> <p> <p>Call this method after inflating/creating your menu in</p>
         * {@link Activity#onCreateOptionsMenu(Menu)}.</p> <p> <p>Note: This is targeted for the
         * native ActionBar/Toolbar, not AppCompat.</p>
         */
        public void apply(Activity activity) {
            ToolbarTinter theme = new ToolbarTinter(this);
            theme.apply(activity);
        }

        /**
         * <p>Creates a {@link ToolbarTinter} with the arguments supplied to this builder.</p> <p>
         * <p>It does not apply the theme. Call {@link ToolbarTinter#apply(Activity)} to do so.</p>
         *
         * @see #apply(Activity)
         */
        public ToolbarTinter create() {
            return new ToolbarTinter(this);
        }

    }

    // --------------------------------------------------------------------------------------------

    /**
     * Auto collapses the SearchView when the soft keyboard is dismissed.
     */
    public static class SearchViewFocusListener implements View.OnFocusChangeListener {

        private final MenuItem item;

        public SearchViewFocusListener(MenuItem item) {
            this.item = item;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus && item != null) {
                item.collapseActionView();
                if (v instanceof SearchView) {
                    ((SearchView) v).setQuery("", false);
                }
            }
        }

    }

}