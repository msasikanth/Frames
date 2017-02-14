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

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import de.psdev.licensesdialog.LicenseResolver;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.License;

import java.util.ArrayList;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.dialogs.FramesDialogs;
import jahirfiquitiva.libs.frames.models.CreditsItem;
import jahirfiquitiva.libs.frames.models.DetailedCreditsItem;
import jahirfiquitiva.libs.frames.utils.IconUtils;
import jahirfiquitiva.libs.frames.utils.Preferences;
import jahirfiquitiva.libs.frames.utils.Utils;
import jahirfiquitiva.libs.frames.views.SplitButtonsLayout;

public class CreditsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<DetailedCreditsItem> detailedCredits = new ArrayList<>();
    private final ArrayList<CreditsItem> credits;
    private final Context context;
    private final Preferences mPrefs;

    public CreditsAdapter(Context context) {
        this.context = context;
        this.mPrefs = new Preferences(context);

        Resources r = context.getResources();

        final String[] titles = r.getStringArray(R.array.credits_titles);
        final String[] contents = r.getStringArray(R.array.credits_contents);
        final String[] photos = r.getStringArray(R.array.credits_photos);
        final String[] banners = r.getStringArray(R.array.credits_banners);

        final String[] buttonsNames = r.getStringArray(R.array.credits_buttons);
        final String[][] buttonsNames2 = new String[buttonsNames.length][];
        for (int i = 0; i < buttonsNames.length; i++)
            buttonsNames2[i] = buttonsNames[i].split("\\|");

        final String[] buttonsLinks = r.getStringArray(R.array.credits_links);
        final String[][] buttonsLinks2 = new String[buttonsLinks.length][];
        for (int i = 0; i < buttonsLinks.length; i++)
            buttonsLinks2[i] = buttonsLinks[i].split("\\|");

        for (int i = 0; i < titles.length; i++) {
            detailedCredits.add(new DetailedCreditsItem(banners[i], photos[i], titles[i],
                    contents[i],
                    buttonsNames2[i], buttonsLinks2[i]));
        }

        final String[] jahirBtns = {
                r.getString(R.string.visit_website),
                "Google+",
                "Play Store"
        };

        final String[] jahirLinks = {
                "https://www.jahirfiquitiva.me/",
                "https://www.google.com/+JahirFiquitivaJDev",
                "http://play.google.com/store/apps/dev?id=7438639276314720952"
        };

        detailedCredits.add(new DetailedCreditsItem(
                "https://github.com/jahirfiquitiva/Website-Resources/raw/master/myself/BannerM.png",
                "https://github.com/jahirfiquitiva/Website-Resources/raw/master/myself/me-square" +
                        "-white.png",
                "Jahir Fiquitiva",
                r.getString(R.string.dashboard_author_copyright),
                jahirBtns,
                jahirLinks));

        final String[] extraCreditsTitles = r.getStringArray(R.array.more_credits_titles);
        final String[] extraCreditsDrawablesNames = r.getStringArray(R.array.credits_drawables);

        credits = new ArrayList<>(extraCreditsTitles.length);
        for (int j = 0; j < extraCreditsTitles.length; j++) {
            credits.add(new CreditsItem(extraCreditsTitles[j],
                    IconUtils.getTintedDrawable(context, extraCreditsDrawablesNames[j])));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (position < detailedCredits.size()) {
            return new DetailedCreditsHolder(inflater.inflate(R.layout.item_detailed_credit,
                    parent, false));
        }
        if (position >= detailedCredits.size()) {
            return new CreditsHolder(inflater.inflate(R.layout.item_credit, parent, false),
                    position - detailedCredits.size());
        }
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < detailedCredits.size()) {
            DetailedCreditsItem item = detailedCredits.get(holder.getAdapterPosition());
            DetailedCreditsHolder detailedCreditsHolder = (DetailedCreditsHolder) holder;

            detailedCreditsHolder.title.setText(item.getTitle());
            detailedCreditsHolder.content.setText(item.getContent());

            Glide.with(context).load(item.getPhotoLink()).diskCacheStrategy(DiskCacheStrategy
                    .SOURCE)
                    .priority(Priority.IMMEDIATE).into(detailedCreditsHolder.photo);
            Glide.with(context).load(item.getBannerLink()).diskCacheStrategy
                    (DiskCacheStrategy.SOURCE)
                    .priority(Priority.HIGH).into(detailedCreditsHolder.banner);

            if (item.getBtnTexts().length > 0) {
                detailedCreditsHolder.buttons.setButtonCount(item.getBtnTexts().length);
                if (!detailedCreditsHolder.buttons.hasAllButtons()) {
                    if (item.getBtnTexts().length != item.getBtnLinks().length)
                        throw new IllegalStateException(
                                "Button names and button links must have the same number of items" +
                                        ".");
                    for (int i = 0; i < item.getBtnTexts().length; i++)
                        detailedCreditsHolder.buttons.addButton(item.getBtnTexts()[i], item
                                .getBtnLinks()[i]);
                }
            } else {
                detailedCreditsHolder.buttons.setVisibility(View.GONE);
            }

            for (int i = 0; i < detailedCreditsHolder.buttons.getChildCount(); i++)
                detailedCreditsHolder.buttons.getChildAt(i).setOnClickListener(new View
                        .OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getTag() instanceof String) {
                            try {
                                Utils.openLink(context, (String) view.getTag());
                            } catch (Exception e) {
                                Toast.makeText(context, e.getLocalizedMessage(), Toast
                                        .LENGTH_SHORT).show();
                            }
                        }

                    }
                });
        }

        if (position >= detailedCredits.size()) {
            CreditsItem item = credits.get(holder.getAdapterPosition() - detailedCredits.size());
            CreditsHolder creditsHolder = (CreditsHolder) holder;
            creditsHolder.text.setText(item.getText());
            creditsHolder.icon.setImageDrawable(item.getIcon());
        }

    }

    @Override
    public int getItemCount() {
        int count = credits.size();
        if (detailedCredits != null) count += detailedCredits.size();
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private class CreditsHolder extends RecyclerView.ViewHolder {

        final View view;
        private final TextView text;
        private final ImageView icon;

        public CreditsHolder(View itemView, final int position) {
            super(itemView);
            view = itemView;
            text = (TextView) view.findViewById(R.id.title);
            icon = (ImageView) view.findViewById(R.id.icon);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 0:
                            FramesDialogs.showSherryDialog(context);
                            break;
                        case 1:
                            FramesDialogs.showUICollaboratorsDialog(context,
                                    context.getResources().getStringArray(R.array
                                            .ui_collaborators_links));
                            break;
                        case 2:
                            License ccLicense = new License() {
                                @Override
                                public String getName() {
                                    return "CreativeCommons Attribution-ShareAlike 4.0 " +
                                            "International License";
                                }

                                @Override
                                public String readSummaryTextFromResources(Context context) {
                                    return readFullTextFromResources(context);
                                }

                                @Override
                                public String readFullTextFromResources(Context context) {
                                    return "Copyright 2017 Jahir Fiquitiva\n" +
                                            "\n" +
                                            "\tLicensed under the CreativeCommons " +
                                            "Attribution-ShareAlike \n" +
                                            "\t4.0 International License. You may not use this " +
                                            "file except in compliance \n" +
                                            "\twith the License. You may obtain a copy of the " +
                                            "License at\n" +
                                            "\n" +
                                            "\t\thttp://creativecommons.org/licenses/by-sa/4" +
                                            ".0/legalcode\n" +
                                            "\n" +
                                            "\tUnless required by applicable law or agreed to in " +
                                            "writing, software\n" +
                                            "\tdistributed under the License is distributed on an" +
                                            " \"AS IS\" BASIS,\n" +
                                            "\tWITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, " +
                                            "either express or implied.\n" +
                                            "\tSee the License for the specific language " +
                                            "governing permissions and\n" +
                                            "\tlimitations under the License.";
                                }

                                @Override
                                public String getVersion() {
                                    return "4.0";
                                }

                                @Override
                                public String getUrl() {
                                    return "http://creativecommons.org/licenses/by-sa/4" +
                                            ".0/legalcode";
                                }
                            };
                            LicenseResolver.registerLicense(ccLicense);
                            new LicensesDialog.Builder(context)
                                    .setTitle(R.string.implemented_libraries)
                                    .setNotices(R.raw.notices)
                                    .setShowFullLicenseText(false)
                                    .setIncludeOwnLicense(false)
                                    .setDividerColorId(R.color.md_divider_black)
                                    .build()
                                    .show();
                            break;
                    }
                }
            });
        }
    }

    private class DetailedCreditsHolder extends RecyclerView.ViewHolder {

        final View view;
        private final TextView title;
        private final TextView content;
        private final ImageView photo;
        private final ImageView banner;
        private final SplitButtonsLayout buttons;

        public DetailedCreditsHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView) view.findViewById(R.id.title);
            content = (TextView) view.findViewById(R.id.content);
            photo = (ImageView) view.findViewById(R.id.photo);
            banner = (ImageView) view.findViewById(R.id.banner);
            buttons = (SplitButtonsLayout) view.findViewById(R.id.buttons);
        }
    }

}
