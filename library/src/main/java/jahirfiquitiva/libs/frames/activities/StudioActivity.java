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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.adapters.PagerAdapter;
import jahirfiquitiva.libs.frames.tasks.DownloadJSON;
import jahirfiquitiva.libs.frames.utils.ThemeUtils;

public class StudioActivity extends AppCompatActivity {

    private ViewPager pager;
    private TabLayout tabs;
    private PagerAdapter pagerAdapter;
    private int lastSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.onActivityCreateSetTheme(this);
        ThemeUtils.onActivityCreateSetNavBar(this);

        super.onCreate(savedInstanceState);

        new DownloadJSON(this).execute();

        setContentView(R.layout.activity_studio);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabs = (TabLayout) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);

        tabs.removeAllTabs();

        tabs.addTab(tabs.newTab().setText(R.string.featured));
        tabs.addTab(tabs.newTab().setText(R.string.collections));

        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
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