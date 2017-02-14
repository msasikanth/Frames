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

package jahirfiquitiva.libs.frames.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.activities.SettingsActivity;
import jahirfiquitiva.libs.frames.dialogs.FolderSelectorDialog;
import jahirfiquitiva.libs.frames.dialogs.FramesDialogs;
import jahirfiquitiva.libs.frames.fragments.base.PreferenceFragment;
import jahirfiquitiva.libs.frames.utils.FavoritesUtils;
import jahirfiquitiva.libs.frames.utils.PermissionsUtils;
import jahirfiquitiva.libs.frames.utils.Preferences;
import jahirfiquitiva.libs.frames.utils.ThemeUtils;

public class SettingsFragment extends PreferenceFragment implements FolderSelectorDialog
        .FolderSelectionCallback {

    private Preferences mPrefs;
    private Preference wallpapersSaveLocation, data;
    private String location, cacheSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FavoritesUtils.init(getActivity());

        mPrefs = new Preferences(getActivity());
        location = mPrefs.getDownloadsFolder();
        cacheSize = fullCacheDataSize(getActivity().getApplicationContext());

        addPreferencesFromResource(R.xml.preferences);

        final PreferenceScreen preferences = (PreferenceScreen) findPreference("preferences");

        PreferenceCategory uiCategory = (PreferenceCategory) findPreference("ui_preferences");

        Preference themes = findPreference("theme");
        themes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.themes_pref_title)
                        .items(R.array.themes_options)
                        .itemsCallbackSingleChoice(mPrefs.getCurrentTheme() - 1, new MaterialDialog
                                .ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int
                                    which, CharSequence text) {
                                if (which + 1 != mPrefs.getCurrentTheme()) {
                                    mPrefs.setCurrentTheme(which + 1);
                                    ThemeUtils.restartActivity(getActivity());
                                }
                                return true;
                            }
                        })
                        .positiveText(android.R.string.ok)
                        .negativeText(android.R.string.cancel)
                        .show();
                return false;
            }
        });

        SwitchPreference colorNavbar = (SwitchPreference) findPreference("color_navbar");
        if (colorNavbar != null) {
            colorNavbar.setChecked(mPrefs.canTintNavbar());
            colorNavbar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    boolean tint = o.toString().equals("true");
                    if (tint != mPrefs.canTintNavbar()) {
                        mPrefs.setTintNavbar(tint);
                        ThemeUtils.restartActivity(getActivity());
                    }
                    return true;
                }
            });
        }

        Preference columns = findPreference("columns");
        columns.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FramesDialogs.showColumnsSelectorDialog(getActivity());
                return false;
            }
        });

        SwitchPreference instagramLike = (SwitchPreference) findPreference("instagram_double_tap");
        if (instagramLike != null) {
            instagramLike.setChecked(mPrefs.isInstagramLikeBehavior());
            instagramLike.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object o) {
                            boolean like = o.toString().equals("true");
                            if (like != mPrefs.isInstagramLikeBehavior()) {
                                mPrefs.setInstagramLikeBehavior(like);
                            }
                            return true;
                        }
                    });
        }

        data = findPreference("clear_data");
        data.setSummary(getResources().getString(R.string.cache_pref_summary, cacheSize));
        data.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog
                        .SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull
                            DialogAction dialogAction) {
                        clearApplicationDataAndCache(getActivity());
                        changeValues(getActivity());
                    }
                };
                FramesDialogs.showClearCacheDialog(getActivity(), positiveCallback);
                return true;
            }
        });

        Preference clearDatabase = findPreference("clear_database");
        clearDatabase.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.database_pref_title)
                        .content(R.string.database_clear_confirm)
                        .positiveText(android.R.string.ok)
                        .negativeText(android.R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull
                                    DialogAction which) {
                                if (FavoritesUtils.getFavorites(getActivity()) != null) {
                                    //noinspection ConstantConditions
                                    if (FavoritesUtils.getFavorites(getActivity()).size() > 0) {
                                        FavoritesUtils.deleteDB(getActivity());
                                        ((SettingsActivity) getActivity()).setHasCleanedFavs(true);
                                    }
                                }
                            }
                        })
                        .show();
                return false;
            }
        });

        wallpapersSaveLocation = findPreference("wallpapers_save_location");
        wallpapersSaveLocation.setSummary(getResources().getString(R.string.wsl_pref_summary,
                location));
        wallpapersSaveLocation.setOnPreferenceClickListener(new Preference
                .OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                PermissionsUtils.checkPermission(getActivity(), Manifest.permission
                                .WRITE_EXTERNAL_STORAGE,
                        new PermissionsUtils.PermissionRequestListener() {
                            @Override
                            public void onPermissionRequest() {
                                PermissionsUtils.requestStoragePermission(getActivity());
                            }

                            @Override
                            public void onPermissionDenied() {
                                FramesDialogs.showPermissionNotGrantedDialog(getActivity());
                            }

                            @Override
                            public void onPermissionCompletelyDenied() {
                                FramesDialogs.showPermissionNotGrantedDialog(getActivity());
                            }

                            @Override
                            public void onPermissionGranted() {
                                showFolderChooserDialog();
                            }
                        });
                return true;
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FavoritesUtils.destroy(getActivity());
    }

    private void changeValues(Context context) {
        location = mPrefs.getDownloadsFolder();
        wallpapersSaveLocation.setSummary(context.getResources().getString(R.string
                .wsl_pref_summary, location));
        cacheSize = fullCacheDataSize(context);
        data.setSummary(context.getResources().getString(R.string.cache_pref_summary, cacheSize));
    }

    private void clearApplicationDataAndCache(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
        clearCache(context);
        mPrefs.setDownloadsFolder(null);
    }

    private void clearCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            //Do nothing
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                if (!deleteDir(new File(dir, aChildren))) return false;
            }
        }

        return dir != null && dir.delete();
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("DefaultLocale")
    private String fullCacheDataSize(Context context) { //TODO add permission check?
        String finalSize;

        long cache = 0;
        long extCache = 0;
        double finalResult, mbFinalResult;

        File[] fileList = context.getCacheDir().listFiles();
        for (File aFileList : fileList) {
            if (aFileList.isDirectory()) {
                cache += dirSize(aFileList);
            } else {
                cache += aFileList.length();
            }
        }
        try {
            File[] fileExtList = new File[0];
            try {
                fileExtList = context.getExternalCacheDir().listFiles();
            } catch (NullPointerException e) {
                //Do nothing
            }
            if (fileExtList != null) {
                for (File aFileExtList : fileExtList) {
                    if (aFileExtList.isDirectory()) {
                        extCache += dirSize(aFileExtList);
                    } else {
                        extCache += aFileExtList.length();
                    }
                }
            }
        } catch (NullPointerException npe) {
            Log.d("CACHE", Log.getStackTraceString(npe));
        }

        finalResult = (cache + extCache) / 1000;

        if (finalResult > 1001) {
            mbFinalResult = finalResult / 1000;
            finalSize = String.format("%.2f", mbFinalResult) + " MB";
        } else {
            finalSize = String.format("%.2f", finalResult) + " KB";
        }

        return finalSize;
    }

    private long dirSize(File dir) {
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    result += dirSize(aFileList);
                } else {
                    result += aFileList.length();
                }
            }
            return result;
        }
        return 0;
    }

    public void showFolderChooserDialog() {
        new FolderSelectorDialog().show((AppCompatActivity) getActivity(), this);
    }

    @Override
    public void onFolderSelection(File folder) {
        location = folder.getAbsolutePath();
        mPrefs.setDownloadsFolder(location);
        wallpapersSaveLocation.setSummary(getString(R.string.wsl_pref_summary, location));
    }

}