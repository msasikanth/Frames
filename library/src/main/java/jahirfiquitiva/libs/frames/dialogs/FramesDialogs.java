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

package jahirfiquitiva.libs.frames.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.utils.IconUtils;
import jahirfiquitiva.libs.frames.utils.Preferences;
import jahirfiquitiva.libs.frames.utils.Utils;

public final class FramesDialogs {

    public static void showLicenseSuccessDialog(Context context, MaterialDialog
            .SingleButtonCallback onPositive, MaterialDialog.OnDismissListener onDismiss,
                                                MaterialDialog.OnCancelListener onCancel) {

        String message = context.getResources().getString(R.string.license_success,
                context.getResources().getString(R.string.app_name));

        MaterialDialog licenseSuccessDialog = new MaterialDialog.Builder(context)
                .title(R.string.license_success_title)
                .content(message)
                .positiveText(R.string.close)
                .onPositive(onPositive)
                .build();

        licenseSuccessDialog.setOnCancelListener(onCancel);
        licenseSuccessDialog.setOnDismissListener(onDismiss);

        licenseSuccessDialog.show();
    }

    public static void showShallNotPassDialog(Context context,
                                              MaterialDialog.SingleButtonCallback onPositive,
                                              MaterialDialog.SingleButtonCallback onNegative,
                                              MaterialDialog.OnDismissListener onDismiss,
                                              MaterialDialog.OnCancelListener onCancel) {

        String message = context.getResources().getString(R.string.license_failed,
                context.getResources().getString(R.string.app_name));

        MaterialDialog shallNotPassDialog = new MaterialDialog.Builder(context)
                .title(R.string.license_failed_title)
                .content(message)
                .positiveText(R.string.download)
                .negativeText(R.string.exit)
                .onPositive(onPositive)
                .onNegative(onNegative)
                .autoDismiss(false)
                .build();
        shallNotPassDialog.setOnCancelListener(onCancel);
        shallNotPassDialog.setOnDismissListener(onDismiss);
        shallNotPassDialog.show();
    }

    public static void showLicenseErrorDialog(Context context,
                                              MaterialDialog.SingleButtonCallback onPositive,
                                              MaterialDialog.SingleButtonCallback onNegative,
                                              MaterialDialog.OnDismissListener onDismiss,
                                              MaterialDialog.OnCancelListener onCancel) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(R.string.error)
                .content(R.string.license_error)
                .negativeText(R.string.exit)
                .onNegative(onNegative)
                .autoDismiss(false);
        if (onPositive != null) {
            builder.positiveText(R.string.download);
            builder.onPositive(onPositive);
        }
        MaterialDialog licenseErrorDialog = builder.build();
        licenseErrorDialog.setOnCancelListener(onCancel);
        licenseErrorDialog.setOnDismissListener(onDismiss);
        licenseErrorDialog.show();
    }

    public static void showApplyWallpaperDialog(final Context context,
                                                MaterialDialog.SingleButtonCallback onPositive,
                                                MaterialDialog.SingleButtonCallback onNeutral,
                                                MaterialDialog.SingleButtonCallback onNegative,
                                                MaterialDialog.OnDismissListener onDismiss) {
        showApplyWallpaperDialog(context, onPositive, onNeutral, onNegative, onDismiss, true);
    }

    public static void showApplyWallpaperDialog(final Context context,
                                                MaterialDialog.SingleButtonCallback onPositive,
                                                MaterialDialog.SingleButtonCallback onNeutral,
                                                MaterialDialog.SingleButtonCallback onNegative,
                                                MaterialDialog.OnDismissListener onDismiss,
                                                boolean showNeutralButton) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(R.string.apply)
                .content(R.string.confirm_apply)
                .negativeText(android.R.string.cancel);
        if (onPositive != null) {
            builder.positiveText(R.string.apply);
            builder.onPositive(onPositive);
        }
        if (onNegative != null) {
            builder.onNegative(onNegative);
        }
        if (showNeutralButton) {
            builder.neutralText(R.string.crop);
            if (onNeutral != null)
                builder.onNeutral(onNeutral);
        }
        MaterialDialog dialog = builder.build();
        dialog.setOnDismissListener(onDismiss);
        dialog.show();
    }

    public static void showWallpaperDetailsDialog(final Context context, String wallName,
                                                  String wallAuthor, String wallDimensions,
                                                  String wallCopyright, MaterialDialog
                                                          .OnDismissListener listener) {

        MaterialDialog dialog = new MaterialDialog.Builder(context).title(wallName)
                .customView(R.layout.wallpaper_details, false)
                .positiveText(context.getResources().getString(R.string.close))
                .build();

        dialog.setOnDismissListener(listener);

        View v = dialog.getCustomView();

        ImageView authorIcon, dimensIcon, copyrightIcon;

        if (v != null) {
            authorIcon = (ImageView) v.findViewById(R.id.icon_author);
            dimensIcon = (ImageView) v.findViewById(R.id.icon_dimensions);
            copyrightIcon = (ImageView) v.findViewById(R.id.icon_copyright);
            authorIcon.setImageDrawable(IconUtils.getTintedDrawable(context, "ic_person"));
            dimensIcon.setImageDrawable(IconUtils.getTintedDrawable(context, "ic_dimensions"));
            copyrightIcon.setImageDrawable(IconUtils.getTintedDrawable(context, "ic_copyright"));
        }

        @SuppressWarnings("ConstantConditions") LinearLayout author = (LinearLayout) v
                .findViewById(R.id.authorName);
        LinearLayout dimensions = (LinearLayout) v.findViewById(R.id.wallDimensions);
        LinearLayout copyright = (LinearLayout) v.findViewById(R.id.wallCopyright);

        TextView authorText = (TextView) v.findViewById(R.id.wallpaper_author_text);
        TextView dimensionsText = (TextView) v.findViewById(R.id.wallpaper_dimensions_text);
        TextView copyrightText = (TextView) v.findViewById(R.id.wallpaper_copyright_text);

        if (wallAuthor.equals("null") || wallAuthor.equals("")) {
            author.setVisibility(View.GONE);
        } else {
            authorText.setText(wallAuthor);
        }

        if (wallDimensions.equals("null") || wallDimensions.equals("")) {
            dimensions.setVisibility(View.GONE);
        } else {
            dimensionsText.setText(wallDimensions);
        }

        if (wallCopyright.equals("null") || wallCopyright.equals("")) {
            copyright.setVisibility(View.GONE);
        } else {
            copyrightText.setText(wallCopyright);
        }

        dialog.show();
    }

    public static void showPermissionNotGrantedDialog(Context context) {
        String appName = context.getResources().getString(R.string.app_name);
        new MaterialDialog.Builder(context)
                .title(R.string.error)
                .content(context.getResources().getString(R.string.storage_perm_error, appName))
                .positiveText(android.R.string.ok)
                .show();
    }

    public static void showColumnsSelectorDialog(final Context context) {
        final Preferences mPrefs = new Preferences(context);
        final int current = mPrefs.getWallsColumnsNumber();
        ArrayList<String> columnOptions = new ArrayList<>();
        for (int i = 2; i < 6; i++) {
            int nColumns = (int) (i * 1.5f);
            String option = context.getResources().getString(R.string.column_option, String
                    .valueOf(i), String.valueOf(nColumns));
            columnOptions.add(option);
        }
        new MaterialDialog.Builder(context)
                .title(R.string.columns)
                .content(R.string.columns_desc)
                .items(columnOptions)
                .itemsCallbackSingleChoice(current - 2, new MaterialDialog
                        .ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int position,
                                               CharSequence text) {
                        int newSelected = position + 2;
                        if (newSelected != current) {
                            mPrefs.setWallsColumnsNumber(newSelected);
                        }
                        return true;
                    }
                })
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .show();
    }

    public static void showClearCacheDialog(Context context, MaterialDialog.SingleButtonCallback
            singleButtonCallback) {
        new MaterialDialog.Builder(context)
                .title(R.string.clear_cache_dialog_title)
                .content(R.string.clear_cache_dialog_content)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(singleButtonCallback)
                .show();
    }

    public static void showSherryDialog(final Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.sherry_title)
                .content(R.string.sherry_dialog)
                .neutralText(R.string.follow_her)
                .positiveText(R.string.close)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction
                            which) {
                        Utils.openLink(context, context.getResources().getString(R.string
                                .sherry_link));
                    }
                })
                .show();
    }

    public static void showUICollaboratorsDialog(final Context context, final String[]
            uiCollaboratorsLinks) {
        new MaterialDialog.Builder(context)
                .title(R.string.ui_design)
                .negativeText(R.string.close)
                .items(R.array.ui_collaborators_names)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view,
                                            final int i, CharSequence charSequence) {
                        Utils.openLink(context, uiCollaboratorsLinks[i]);
                    }
                }).show();
    }

}