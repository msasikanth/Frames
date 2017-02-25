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

import com.afollestad.materialdialogs.MaterialDialog;

import org.sufficientlysecure.donations.google.util.IabHelper;
import org.sufficientlysecure.donations.google.util.IabResult;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.activities.base.ThemedActivity;
import jahirfiquitiva.libs.frames.fragments.DonationsFragment;
import jahirfiquitiva.libs.frames.utils.ColorUtils;
import jahirfiquitiva.libs.frames.utils.ThemeUtils;
import jahirfiquitiva.libs.frames.utils.ToolbarColorizer;

public class DonateActivity extends ThemedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
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
                createDonationsFragment(), "donate").commit();
    }

    protected DonationsFragment createDonationsFragment() {
        String key = getIntent().getStringExtra("key");
        boolean googleDonations = true;
        boolean paypalDonations = getIntent().getBooleanExtra("paypal", false);

        String installer = getPackageManager().getInstallerPackageName(getPackageName());
        if (installer == null) {
            googleDonations = false;
        } else {
            if ((installer.matches("com.google.android.feedback") || installer.matches("com" +
                    ".android.vending"))) {
                paypalDonations = false;
            }
        }

        final String[] catalogItems = getResources().getStringArray(R.array.google_donations_items);
        String[] catalogValues = getResources().getStringArray(R.array.google_donations_catalog);

        try {
            if (!(key.length() > 50) || (!(catalogItems.length > 0)) ||
                    (!(catalogItems.length == catalogValues.length))) {
                googleDonations = false;
            }
        } catch (Exception e) {
            googleDonations = false;
        }

        String paypalUser = "";
        String paypalCurrency = "";
        if (paypalDonations) {
            paypalUser = getResources().getString(R.string.paypal_email);
            paypalCurrency = getResources().getString(R.string.paypal_currency_code);
            if (!(paypalUser.length() > 5) || !(paypalCurrency.length() > 1)) {
                paypalDonations = false;
            }
        }

        if (googleDonations) {
            final IabHelper mHelper = new IabHelper(this, key);
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        new MaterialDialog.Builder(DonateActivity.this)
                                .title(R.string.donations_error_title)
                                .content(R.string.donations_error_content)
                                .positiveText(android.R.string.ok)
                                .show();

                    } else {
                        mHelper.queryInventoryAsync(false, null);
                    }
                }
            });
        }
        return DonationsFragment.newInstance(googleDonations,
                key,
                catalogItems,
                catalogValues,
                paypalDonations,
                paypalUser,
                paypalCurrency,
                getResources().getString(R.string.donate),
                false,
                false);
    }

}