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
import android.support.v7.widget.Toolbar;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.activities.base.ThemedActivity;
import jahirfiquitiva.libs.frames.fragments.CreditsFragment;
import jahirfiquitiva.libs.frames.utils.ColorUtils;
import jahirfiquitiva.libs.frames.utils.ThemeUtils;
import jahirfiquitiva.libs.frames.utils.ToolbarColorizer;

public class CreditsActivity extends ThemedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_simple);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ToolbarColorizer.colorizeToolbar(toolbar, ColorUtils.getMaterialPrimaryTextColor(!
                (ColorUtils.isLightColor(ThemeUtils.darkOrLight(this, R.color.dark_theme_primary,
                        R.color.light_theme_primary)))));
        ToolbarColorizer.tintStatusBar(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.content,
                new CreditsFragment(), "credits").commit();
    }

}