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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.adapters.PagerAdapter;
import jahirfiquitiva.libs.frames.adapters.WallpapersAdapter;
import jahirfiquitiva.libs.frames.fragments.CollectionFragment;
import jahirfiquitiva.libs.frames.holders.lists.FullListHolder;
import jahirfiquitiva.libs.frames.tasks.DownloadJSON;
import jahirfiquitiva.libs.frames.utils.ColorUtils;
import jahirfiquitiva.libs.frames.utils.FavoritesUtils;
import jahirfiquitiva.libs.frames.utils.ThemeUtils;
import jahirfiquitiva.libs.frames.utils.ToolbarColorizer;

public class StudioActivity extends AppCompatActivity {

    private TabLayout tabs;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private boolean hasFeaturedWallpapers = false;
    private int lastSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new DownloadJSON(this).execute();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ThemeUtils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);

        FavoritesUtils.init(this);

        setContentView(R.layout.activity_studio);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ToolbarColorizer.colorizeToolbar(toolbar, ColorUtils.getMaterialPrimaryTextColor(!
                (ColorUtils.isLightColor(ThemeUtils.darkOrLight(this, R.color.dark_theme_primary,
                        R.color.light_theme_primary)))));
        ToolbarColorizer.tintStatusBar(this);

        tabs = (TabLayout) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);

        setupTabsAndPager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FavoritesUtils.destroy(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (getResources().getBoolean(R.bool.show_popup_icons))
            ToolbarColorizer.makeMenuIconsVisible(menu);
        ToolbarColorizer.tintMenu(menu, ColorUtils.getMaterialPrimaryTextColor(!(ColorUtils
                .isLightColor(ThemeUtils.darkOrLight(this, R.color.dark_theme_primary, R.color
                        .light_theme_primary)))));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int i = item.getItemId();
        if (i == R.id.search) {
            startActivityForResult(new Intent(this, SearchActivity.class), 13);
        } else if (i == R.id.favs) {
            startActivityForResult(new Intent(this, FavoritesActivity.class), 14);
        } else if (i == R.id.refresh) {
            new DownloadJSON(this).execute();
        }
        return true;
    }

    @Override
    public void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (data != null) {
            String modifiedItems = data.getStringExtra("modified");
            if (modifiedItems != null) {
                if (modifiedItems.length() > 0) {
                    if (lastSelected == 0 && pager != null) {
                        if (pager.getAdapter() != null && pager.getAdapter().getCount() > 0) {
                            try {
                                if (((PagerAdapter) pager.getAdapter()).getFragmentAtPosition(0)
                                        != null) {
                                    if (((CollectionFragment) ((PagerAdapter) pager
                                            .getAdapter())
                                            .getFragmentAtPosition(0)).getRVAdapter() != null)
                                        if (((CollectionFragment) ((PagerAdapter) pager
                                                .getAdapter()).getFragmentAtPosition(0))
                                                .getRVAdapter()
                                                instanceof WallpapersAdapter) {
                                            ((WallpapersAdapter) ((CollectionFragment) (
                                                    (PagerAdapter) pager.getAdapter())
                                                    .getFragmentAtPosition(0)).getRVAdapter())
                                                    .updateItems(modifiedItems);
                                        }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (lastSelected > 0) {
            lastSelected = 0;
            pager.setCurrentItem(lastSelected, true);
        } else {
            super.onBackPressed();
        }
    }

    public void hideTabs() {
        if (tabs != null)
            tabs.setVisibility(View.GONE);
        if (pager != null)
            pager.setVisibility(View.GONE);
    }

    public void setupTabsAndPager() {
        if (tabs == null) return;
        tabs.removeAllTabs();
        int index = FullListHolder.get().getCollections().getIndexForCollectionWithName("featured");
        if (index >= 0 && FullListHolder.get().getCollections().getList().get(index)
                .getWallpapers().size() > 0) {
            hasFeaturedWallpapers = true;
            tabs.addTab(tabs.newTab().setText(R.string.featured));
            tabs.addTab(tabs.newTab().setText(R.string.collections));
            tabs.setVisibility(View.VISIBLE);
        }
        if (pager == null) return;
        pager.setVisibility(View.VISIBLE);
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs) {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                lastSelected = position;
                invalidateOptionsMenu();
            }
        });
        pager.setOffscreenPageLimit(hasFeaturedWallpapers ? 2 : 1);
        setupPagerAdapter();
    }

    private void setupPagerAdapter() {
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), hasFeaturedWallpapers);
        if (pager != null) {
            pager.setAdapter(pagerAdapter);
            pager.setCurrentItem(lastSelected, true);
        }
    }

    public PagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }

    public int getCurrentFragmentPosition() {
        return lastSelected;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("lastSelected", lastSelected);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lastSelected = savedInstanceState.getInt("lastSelected", 0);
    }

}