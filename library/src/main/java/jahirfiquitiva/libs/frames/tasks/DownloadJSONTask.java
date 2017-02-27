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

package jahirfiquitiva.libs.frames.tasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.models.Collection;
import jahirfiquitiva.libs.frames.models.Wallpaper;
import jahirfiquitiva.libs.frames.utils.JSONParser;

public class DownloadJSONTask extends AsyncTaskLoader<ArrayList<Collection>> {

    private InterestingConfigChanges mLastConfig = new InterestingConfigChanges();

    private ArrayList<Collection> collections;
    private boolean onlyCollections;

    private DownloadJSONTask(Context context) {
        super(context);
    }

    public DownloadJSONTask(Context context, boolean onlyCollections) {
        this(context);
        this.onlyCollections = onlyCollections;
    }

    /**
     * This is where the bulk of our work is done.  This function is called in a background thread
     * and should generate a new set of data to be published by the loader.
     */
    @Override
    public ArrayList<Collection> loadInBackground() {
        ArrayList<Collection> collections = new ArrayList<>();
        JSONObject json = JSONParser.getJSONFromURL(getContext(),
                getContext().getResources().getString(R.string.wallpapers_json_link));
        if (json != null) {
            try {
                JSONArray jsonCollections = json.getJSONArray("Collections");
                collections.add(new Collection("Featured", null, null));
                for (int i = 0; i < jsonCollections.length(); i++) {
                    JSONObject nCollection = jsonCollections.getJSONObject(i);
                    String name = nCollection.getString("name");
                    String preview = nCollection.getString("preview_url");
                    String previewThumbnail = nCollection.getString("preview_thumbnail_url");
                    collections.add(new Collection(name, preview, previewThumbnail));
                }

                if (!onlyCollections) {
                    JSONArray jsonWallpapers = json.getJSONArray("Wallpapers");
                    ArrayList<Wallpaper> wallpapers = new ArrayList<>();
                    for (int j = 0; j < jsonWallpapers.length(); j++) {
                        JSONObject nWallpaper = jsonWallpapers.getJSONObject(j);
                        String copyright = "";
                        try {
                            copyright = nWallpaper.getString("copyright");
                        } catch (Exception ignored) {
                            //
                        }
                        String dimensions = "";
                        try {
                            dimensions = nWallpaper.getString("dimensions");
                        } catch (Exception ignored) {
                            //
                        }
                        String thumbnail = null;
                        try {
                            thumbnail = nWallpaper.getString("thumbnail");
                        } catch (Exception ignored) {
                            //
                        }
                        boolean downloadable = true;
                        try {
                            downloadable = nWallpaper.getString("downloadable").equals("true");
                        } catch (Exception ignored) {
                            //
                        }
                        wallpapers.add(new Wallpaper(nWallpaper.getString("name"), nWallpaper
                                .getString("author"), copyright, dimensions, nWallpaper.getString
                                ("url"), thumbnail, nWallpaper.getString("collections"),
                                downloadable));
                    }

                    for (Wallpaper wallpaper : wallpapers) {
                        String[] collects = wallpaper.getCollections().split(",");
                        for (String collect : collects) {
                            for (Collection collection : collections) {
                                if (collection.getName().toLowerCase().equals(collect.toLowerCase
                                        ())) {
                                    collection.addWallpaper(wallpaper);
                                }
                            }
                        }
                    }
                }
                return collections;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Called when there is new data to deliver to the client.  The super class will take care of
     * delivering it; the implementation here just adds a little more logic.
     */
    @Override
    public void deliverResult(ArrayList<Collection> apps) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (apps != null) {
                onReleaseResources(apps);
            }
        }
        ArrayList<Collection> oldApps = collections;
        collections = apps;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(apps);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldApps != null) {
            onReleaseResources(oldApps);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (collections != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(collections);
        }

        // Has something interesting in the configuration changed since we
        // last built the app list?
        boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());

        if (takeContentChanged() || collections == null || configChange) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(ArrayList<Collection> apps) {
        super.onCanceled(apps);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (collections != null) {
            onReleaseResources(collections);
            collections = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated with an actively loaded data
     * set.
     */
    protected void onReleaseResources(ArrayList<Collection> apps) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}