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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.activities.base.ThemedActivity;
import jahirfiquitiva.libs.frames.dialogs.FramesDialogs;
import jahirfiquitiva.libs.frames.fragments.SettingsFragment;
import jahirfiquitiva.libs.frames.utils.ColorUtils;
import jahirfiquitiva.libs.frames.utils.FavoritesUtils;
import jahirfiquitiva.libs.frames.utils.PermissionsUtils;
import jahirfiquitiva.libs.frames.utils.ThemeUtils;
import jahirfiquitiva.libs.frames.utils.ToolbarColorizer;

public class SettingsActivity extends ThemedActivity {

    private SettingsFragment settings;
    private boolean hasCleanedFavs = false;

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

        settings = new SettingsFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.content, settings, "settings")
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            FavoritesUtils.destroy(this);
        }catch (Exception ignored){}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAndSendData();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finishAndSendData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == PermissionsUtils.PERMISSION_REQUEST_CODE) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                if (settings != null)
                    settings.showFolderChooserDialog();
            } else if (PermissionsUtils.getListener() != null) {
                PermissionsUtils.getListener().onPermissionGranted();
            } else {
                FramesDialogs.showPermissionNotGrantedDialog(this);
            }
        } else {
            FramesDialogs.showPermissionNotGrantedDialog(this);
        }
    }

    public void setHasCleanedFavs(boolean hasCleanedThem) {
        this.hasCleanedFavs = hasCleanedThem;
    }

    @SuppressWarnings("ConstantConditions")
    private void finishAndSendData() {
        Intent intent = new Intent();
        if (hasCleanedFavs)
            intent.putExtra("modified", "::clean::");
        setResult(15, intent);
        finish();
    }

}