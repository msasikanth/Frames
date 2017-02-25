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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.activities.base.ThemedActivity;
import jahirfiquitiva.libs.frames.adapters.WallpapersAdapter;
import jahirfiquitiva.libs.frames.dialogs.FramesDialogs;
import jahirfiquitiva.libs.frames.fragments.CollectionFragment;
import jahirfiquitiva.libs.frames.utils.ColorUtils;
import jahirfiquitiva.libs.frames.utils.FavoritesUtils;
import jahirfiquitiva.libs.frames.utils.ThemeUtils;
import jahirfiquitiva.libs.frames.utils.ToolbarColorizer;
import jahirfiquitiva.libs.frames.utils.Utils;

public class FavoritesActivity extends ThemedActivity {

    private CollectionFragment favsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FavoritesUtils.init(this);

        setContentView(R.layout.activity_simple);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ToolbarColorizer.colorizeToolbar(toolbar, ColorUtils.getMaterialPrimaryTextColor(!
                (ColorUtils.isLightColor(ThemeUtils.darkOrLight(this, R.color.dark_theme_primary,
                        R.color.light_theme_primary)))));
        ToolbarColorizer.tintStatusBar(this);

        favsFragment = CollectionFragment.newInstance(FavoritesUtils.getFavorites(this), true,
                false);

        getSupportFragmentManager().beginTransaction().replace(R.id.content, favsFragment, "favs")
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectionAndLicense();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FavoritesUtils.destroy(this);
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

    private void checkConnectionAndLicense() {
        if (Utils.isConnected(this)) {
            checkLicense();
        } else {
            FramesDialogs.showLicenseErrorDialog(this, null,
                    new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull
                                DialogAction which) {
                            finish();
                        }
                    }, new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            finish();
                        }
                    }, new MaterialDialog.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            finish();
                        }
                    });
        }
    }

    private void checkLicense() {
        Utils.runLicenseChecker(this, getIntent().getBooleanExtra("check", true),
                getIntent().getStringExtra("key"), getIntent().getBooleanExtra("allAma", false),
                new Utils.SuccessCallback() {
                    @Override
                    public void onSuccess() {
                        // Do nothing
                    }
                });
    }

    private void finishAndSendData() {
        Intent intent = new Intent();
        StringBuilder s = new StringBuilder("");
        if (favsFragment != null) {
            ArrayList<String> list = ((WallpapersAdapter) favsFragment.getRVAdapter())
                    .getModifiedWallpapers();
            for (int i = 0; i < list.size(); i++) {
                s.append(list.get(i));
                if (list.size() > 1 && i < (list.size() - 1)) {
                    s.append(",");
                }
            }
        }
        intent.putExtra("modified", s.toString());
        setResult(14, intent);
        finish();
    }

}