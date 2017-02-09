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

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.adapters.PagerAdapter;
import jahirfiquitiva.libs.frames.tasks.DownloadJSON;
import jahirfiquitiva.libs.frames.utils.IconUtils;

public class StudioActivity extends AppCompatActivity {

    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private int lastSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Replace with ThemeUtils
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        new DownloadJSON(this).execute();

        setContentView(R.layout.activity_studio);

        final Context context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);

        tabs.removeAllTabs();
        tabs.addTab(tabs.newTab().setIcon(IconUtils.getTintedIcon(this, R.drawable.ic_collection,
                ContextCompat.getColor(context, (lastSelected == 0) ? R.color
                        .tabsSelectedTextColor : R.color.tabsTextColor))));
        tabs.addTab(tabs.newTab().setIcon(IconUtils.getTintedIcon(this, R.drawable.ic_wallpapers,
                ContextCompat.getColor(context, (lastSelected == 1) ? R.color
                        .tabsSelectedTextColor : R.color.tabsTextColor))));

        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                if (tab.getIcon() != null)
                    tab.getIcon().setColorFilter(ContextCompat.getColor(context, android.R.color
                            .white), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                if (tab.getIcon() != null)
                    tab.getIcon().setColorFilter(ContextCompat.getColor(context, R.color
                            .tabsTextColor), PorterDuff.Mode.SRC_IN);
            }
        });
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs) {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                lastSelected = position;
                invalidateOptionsMenu();
            }
        });
        pager.setOffscreenPageLimit(2);
        setupPagerAdapter();
    }

    public void setupPagerAdapter() {
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        if (pager != null)
            pager.setAdapter(pagerAdapter);
    }

    public PagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }

    public int getCurrentFragmentPosition() {
        return lastSelected;
    }

}