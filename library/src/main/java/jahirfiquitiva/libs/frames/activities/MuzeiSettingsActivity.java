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

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Locale;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.activities.base.ThemedActivity;
import jahirfiquitiva.libs.frames.callbacks.JSONDownloadCallback;
import jahirfiquitiva.libs.frames.holders.lists.FullListHolder;
import jahirfiquitiva.libs.frames.models.Collection;
import jahirfiquitiva.libs.frames.utils.ColorUtils;
import jahirfiquitiva.libs.frames.utils.IconUtils;
import jahirfiquitiva.libs.frames.utils.Preferences;
import jahirfiquitiva.libs.frames.utils.ThemeUtils;
import jahirfiquitiva.libs.frames.utils.ToolbarColorizer;
import jahirfiquitiva.libs.frames.utils.Utils;

public class MuzeiSettingsActivity extends ThemedActivity {

    private static final int SEEKBAR_STEPS = 1;
    private static final int SEEKBAR_MAX_VALUE = 13;
    private static final int SEEKBAR_MIN_VALUE = 0;

    private Preferences mPrefs;
    private String selectedCollections = "";

    private AppCompatSeekBar seekBar;
    private AppCompatCheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isConnected() && ((FullListHolder.get().getCollections() == null) || (FullListHolder
                .get().getCollections().getList() == null) || (FullListHolder
                .get().getCollections().getList().size() <= 0))) {
            executeJsonTask(true);
        }

        setContentView(R.layout.muzei_settings);

        mPrefs = new Preferences(this);
        selectedCollections = mPrefs.getMuzeiCollections();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ToolbarColorizer.colorizeToolbar(toolbar, ColorUtils.getMaterialPrimaryTextColor(!
                (ColorUtils.isLightColor(ThemeUtils.darkOrLight(this, R.color.dark_theme_primary,
                        R.color.light_theme_primary)))));
        ToolbarColorizer.tintStatusBar(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(ColorUtils.getAccentColor(this)));
        fab.setImageDrawable(IconUtils.getTintedDrawable(this, "ic_save", ColorUtils
                .getAccentColor(this)));

        TextView everyTitle = (TextView) findViewById(R.id.every_title);
        everyTitle.setTextColor(ColorUtils.getMaterialPrimaryTextColor(ThemeUtils.isDarkTheme()));

        final TextView everySummary = (TextView) findViewById(R.id.every_summary);
        everySummary.setTextColor(ColorUtils.getMaterialSecondaryTextColor(
                ThemeUtils.isDarkTheme()));
        everySummary.setText(getResources().getString(R.string.every_x, textFromProgress
                (mPrefs.getMuzeiRefreshInterval()).toLowerCase(Locale.getDefault())));

        seekBar = (AppCompatSeekBar) findViewById(R.id.every_seekbar);
        seekBar.setProgress(mPrefs.getMuzeiRefreshInterval());
        seekBar.incrementProgressBy(SEEKBAR_STEPS);
        seekBar.setMax((int) ((SEEKBAR_MAX_VALUE - SEEKBAR_MIN_VALUE) / SEEKBAR_STEPS));

        View divider = findViewById(R.id.divider);
        View otherDivider = findViewById(R.id.other_divider);
        divider.setBackground(new ColorDrawable(ColorUtils.getMaterialDividerColor(
                ThemeUtils.isDarkTheme())));
        otherDivider.setBackground(new ColorDrawable(ColorUtils.getMaterialDividerColor(
                ThemeUtils.isDarkTheme())));

        TextView wifiOnlyTitle = (TextView) findViewById(R.id.wifi_only_title);
        wifiOnlyTitle.setTextColor(ColorUtils.getMaterialPrimaryTextColor(ThemeUtils.isDarkTheme
                ()));

        TextView wifiOnlySummary = (TextView) findViewById(R.id.wifi_only_summary);
        wifiOnlySummary.setTextColor(ColorUtils.getMaterialSecondaryTextColor(ThemeUtils
                .isDarkTheme()));

        checkBox = (AppCompatCheckBox) findViewById(R.id.wifi_checkbox);
        checkBox.setChecked(mPrefs.getMuzeiRefreshOnWiFiOnly());

        LinearLayout wifiOnly = (LinearLayout) findViewById(R.id.wifi_only);
        LinearLayout chooseCollections = (LinearLayout) findViewById(R.id.choose_collections);

        TextView chooseCollectionsTitle = (TextView) findViewById(R.id.choose_collections_title);
        chooseCollectionsTitle.setTextColor(ColorUtils.getMaterialPrimaryTextColor(ThemeUtils
                .isDarkTheme
                        ()));

        TextView chooseCollectionsSummary = (TextView) findViewById(R.id
                .choose_collections_summary);
        chooseCollectionsSummary.setTextColor(ColorUtils.getMaterialSecondaryTextColor(ThemeUtils
                .isDarkTheme()));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = SEEKBAR_MIN_VALUE + (progress * SEEKBAR_STEPS);
                everySummary.setText(getResources().getString(R.string.every_x, textFromProgress
                        (value).toLowerCase(Locale.getDefault())));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        wifiOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBox.toggle();
            }
        });

        chooseCollections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((!isConnected()) || ((FullListHolder.get().getCollections() == null) ||
                        (FullListHolder.get().getCollections().getList() == null) ||
                        (FullListHolder.get().getCollections().getList().size() <= 0))) {
                    showNotConnectedDialog();
                } else {
                    showChooseCollectionsDialog();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveValues();
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showConfirmDialog();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        showConfirmDialog();
    }

    @Override
    protected JSONDownloadCallback getCallback() {
        return new JSONDownloadCallback() {
            @Override
            public void onSuccess(ArrayList<Collection> collections) {
                FullListHolder.get().getCollections().createList(collections);
            }
        };
    }

    private void showConfirmDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.sure_to_exit)
                .content(R.string.sure_to_exit_content)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .neutralText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction
                            which) {
                        saveValues();
                        finish();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction
                            which) {
                        finish();
                    }
                })
                .show();
    }

    private void saveValues() {
        if (seekBar != null)
            mPrefs.setMuzeiRefreshInterval(seekBar.getProgress());
        mPrefs.setMuzeiRefreshOnWiFiOnly(checkBox != null && checkBox.isChecked());
        mPrefs.setMuzeiCollections(selectedCollections);
    }

    private boolean isConnected() {
        return Utils.isConnected(this);
    }

    private void showNotConnectedDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.muzei_not_connected_title)
                .content(R.string.muzei_not_connected_content)
                .positiveText(android.R.string.ok)
                .show();
    }

    private void showChooseCollectionsDialog() {
        ArrayList<String> collections = new ArrayList<>();
        for (Collection collection : FullListHolder.get().getCollections().getList()) {
            if (!(collections.contains(collection.getName())))
                collections.add(collection.getName());
        }
        collections.add("Favorites");

        String[] selectedCollects = selectedCollections.split(",");
        ArrayList<Integer> selected = new ArrayList<>();
        for (int i = 0; i < collections.size(); i++) {
            for (String selectedCollect : selectedCollects) {
                if (collections.get(i).equals(selectedCollect)) {
                    selected.add(i);
                }
            }
        }

        new MaterialDialog.Builder(this)
                .title(R.string.choose_collections_title)
                .items(collections)
                .itemsCallbackMultiChoice(getIntsFromList(selected), new MaterialDialog
                        .ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which,
                                               CharSequence[] text) {
                        StringBuilder s = new StringBuilder("");
                        for (int k = 0; k < text.length; k++) {
                            s.append(text[k]);
                            if (text.length > 1 && (k < (text.length - 1))) {
                                s.append(",");
                            }
                        }
                        selectedCollections = s.toString();
                        return true;
                    }
                })
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .show();
    }

    private Integer[] getIntsFromList(ArrayList<Integer> ints) {
        Integer[] newInts = new Integer[ints.size()];
        for (int i = 0; i < newInts.length; i++) {
            newInts[i] = ints.get(i);
        }
        return newInts;
    }

    private String textFromProgress(int progress) {
        switch (progress) {
            case 0:
                return 15 + " " + getResources().getString(R.string.minutes);
            case 1:
                return 30 + " " + getResources().getString(R.string.minutes);
            case 2:
                return 45 + " " + getResources().getString(R.string.minutes);
            case 3:
                return 1 + " " + getResources().getString(R.string.hours);
            case 4:
                return 2 + " " + getResources().getString(R.string.hours);
            case 5:
                return 3 + " " + getResources().getString(R.string.hours);
            case 6:
                return 6 + " " + getResources().getString(R.string.hours);
            case 7:
                return 9 + " " + getResources().getString(R.string.hours);
            case 8:
                return 12 + " " + getResources().getString(R.string.hours);
            case 9:
                return 18 + " " + getResources().getString(R.string.hours);
            case 10:
                return 1 + " " + getResources().getString(R.string.days);
            case 11:
                return 3 + " " + getResources().getString(R.string.days);
            case 12:
                return 7 + " " + getResources().getString(R.string.days);
            case 13:
                return 14 + " " + getResources().getString(R.string.days);
        }
        return "";
    }

}