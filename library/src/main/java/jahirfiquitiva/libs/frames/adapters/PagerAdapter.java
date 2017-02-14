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

package jahirfiquitiva.libs.frames.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

import jahirfiquitiva.libs.frames.adapters.base.FragmentStatePagerAdapter;
import jahirfiquitiva.libs.frames.fragments.CollectionFragment;
import jahirfiquitiva.libs.frames.models.Collection;
import jahirfiquitiva.libs.frames.models.Wallpaper;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private boolean hasFeaturedWallpapers = false;
    private boolean isSearch = false;

    public PagerAdapter(FragmentManager fm, boolean hasFeaturedWallpapers) {
        super(fm);
        this.hasFeaturedWallpapers = hasFeaturedWallpapers;
    }

    public PagerAdapter(FragmentManager fm, boolean hasFeaturedWallpapers, boolean isSearch) {
        super(fm);
        this.hasFeaturedWallpapers = hasFeaturedWallpapers;
        this.isSearch = isSearch;
    }

    @Override
    public Fragment getItem(int position) {
        if (isSearch) {
            switch (position) {
                case 1:
                    return CollectionFragment.newInstance(new ArrayList<Collection>(), true);
                default:
                    return CollectionFragment.newInstance(new ArrayList<Wallpaper>(), false,
                            true);
            }
        } else {
            if (!hasFeaturedWallpapers) return CollectionFragment.newInstance(true, null);
            switch (position) {
                case 0:
                    return CollectionFragment.newInstance(false, "featured");
                default:
                    return CollectionFragment.newInstance(true, null);
            }
        }
    }

    @Override
    public int getCount() {
        return isSearch ? 2 : hasFeaturedWallpapers ? 2 : 1;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}