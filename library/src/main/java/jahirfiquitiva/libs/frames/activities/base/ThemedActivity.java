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

package jahirfiquitiva.libs.frames.activities.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import jahirfiquitiva.libs.frames.callbacks.JSONDownloadCallback;
import jahirfiquitiva.libs.frames.models.Collection;
import jahirfiquitiva.libs.frames.tasks.DownloadJSONTask;
import jahirfiquitiva.libs.frames.utils.ThemeUtils;

public class ThemedActivity extends AppCompatActivity {

    private boolean mLastTheme;
    private boolean mLastNavBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLastTheme != ThemeUtils.isDarkTheme() ||
                mLastNavBar != ThemeUtils.hasColoredNavbar()) {
            ThemeUtils.restartActivity(this);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mLastTheme = ThemeUtils.isDarkTheme();
        mLastNavBar = ThemeUtils.hasColoredNavbar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            getSupportLoaderManager().getLoader(0).cancelLoad();
            getSupportLoaderManager().destroyLoader(0);
        } catch (Exception ignored) {
        }
    }

    protected JSONDownloadCallback getCallback() {
        return null;
    }

    public void executeJsonTask(final boolean onlyCollections) {
        final Context c = this;
        try {
            getSupportLoaderManager().getLoader(0).cancelLoad();
            getSupportLoaderManager().destroyLoader(0);
        } catch (Exception ignored) {
        }
        if (getCallback() != null) getCallback().onPreExecute();
        getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<ArrayList
                <Collection>>() {
            @Override
            public Loader<ArrayList<Collection>> onCreateLoader(int id, Bundle args) {
                return new DownloadJSONTask(c, onlyCollections);
            }

            @Override
            @SuppressWarnings("unchecked")
            public void onLoadFinished(Loader<ArrayList<Collection>> loader,
                                       ArrayList<Collection> data) {
                if ((data != null) && (getCallback() != null)) {
                    getCallback().onSuccess(data);
                }
            }

            @Override
            public void onLoaderReset(Loader<ArrayList<Collection>> loader) {
                // Do nothing
            }
        });
    }

    public void executeJsonTask() {
        executeJsonTask(false);
    }
}