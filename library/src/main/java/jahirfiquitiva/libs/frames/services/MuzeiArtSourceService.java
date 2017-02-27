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

package jahirfiquitiva.libs.frames.services;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import com.google.android.apps.muzei.api.UserCommand;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.models.Collection;
import jahirfiquitiva.libs.frames.models.Wallpaper;
import jahirfiquitiva.libs.frames.utils.JSONParser;
import jahirfiquitiva.libs.frames.utils.Preferences;
import jahirfiquitiva.libs.frames.utils.Utils;

public class MuzeiArtSourceService extends RemoteMuzeiArtSource {

    private static final String ARTSOURCE_NAME = "Frames - MuzeiExtension";
    private static final int COMMAND_ID_SHARE = 1337;
    private Preferences mPrefs;

    public MuzeiArtSourceService() {
        super(ARTSOURCE_NAME);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String command = intent.getExtras().getString("service");
        if (command != null) {
            try {
                onTryUpdate(UPDATE_REASON_USER_NEXT);
            } catch (RetryException e) {
                Log.d(Utils.LOG_TAG, "Error updating Muzei: " + e.getMessage());
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPrefs = new Preferences(this);
        ArrayList<UserCommand> commands = new ArrayList<>();
        commands.add(new UserCommand(BUILTIN_COMMAND_ID_NEXT_ARTWORK));
        commands.add(new UserCommand(COMMAND_ID_SHARE, getString(R.string.share)));
        setUserCommands(commands);
    }

    @Override
    public void onCustomCommand(int id) {
        super.onCustomCommand(id);
        if (id == COMMAND_ID_SHARE) {
            Artwork currentArtwork = getCurrentArtwork();
            Intent shareWall = new Intent(Intent.ACTION_SEND);
            shareWall.setType("text/plain");
            String wallName = currentArtwork.getTitle();
            String authorName = currentArtwork.getByline();
            String storeUrl = "https://play.google.com/store/apps/details?id=" + getPackageName();
            String iconPackName = getString(R.string.app_name);
            shareWall.putExtra(Intent.EXTRA_TEXT,
                    getString(R.string.share_text, wallName, authorName, iconPackName, storeUrl));
            shareWall = Intent.createChooser(shareWall, getString(R.string.share_title));
            shareWall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(shareWall);
        }
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {
        if (mPrefs.isDashboardWorking()) {
            if (mPrefs.getMuzeiRefreshOnWiFiOnly()) {
                if (Utils.isConnectedToWiFi(this)) {
                    executeMuzeiUpdate();
                }
            } else if (Utils.isConnected(this)) {
                executeMuzeiUpdate();
            }
        }
    }

    private void executeMuzeiUpdate() throws RetryException {
        try {
            AsyncTask<Void, Void, ArrayList<Collection>> downloadJSON =
                    new AsyncTask<Void, Void, ArrayList<Collection>>() {
                        @Override
                        protected ArrayList<Collection> doInBackground(Void... voids) {
                            ArrayList<Collection> collections = new ArrayList<>();
                            JSONObject json = JSONParser.getJSONFromURL(getApplicationContext(),
                                    getApplicationContext().getResources().getString(R.string
                                            .wallpapers_json_link));

                            if (json != null) {
                                try {
                                    JSONArray jsonCollections = json.getJSONArray("Collections");
                                    collections.add(new Collection("Featured", null, null));
                                    for (int i = 0; i < jsonCollections.length(); i++) {
                                        JSONObject nCollection = jsonCollections.getJSONObject(i);
                                        String name = nCollection.getString("name");
                                        String preview = nCollection.getString("preview_url");
                                        String previewThumbnail = nCollection.getString
                                                ("preview_thumbnail_url");
                                        collections.add(new Collection(name, preview,
                                                previewThumbnail));
                                    }

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
                                            downloadable = nWallpaper.getString("downloadable")
                                                    .equals("true");
                                        } catch (Exception ignored) {
                                            //
                                        }
                                        wallpapers.add(new Wallpaper(nWallpaper.getString("name")
                                                , nWallpaper.getString("author"), copyright,
                                                dimensions, nWallpaper.getString("url"),
                                                thumbnail, nWallpaper.getString("collections"),
                                                downloadable));
                                    }

                                    for (Wallpaper wallpaper : wallpapers) {
                                        String[] collects = wallpaper.getCollections().split(",");
                                        for (String collect : collects) {
                                            for (Collection collection : collections) {
                                                if (collection.getName().toLowerCase().equals
                                                        (collect.toLowerCase())) {
                                                    collection.addWallpaper(wallpaper);
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

                        @Override
                        protected void onPostExecute(ArrayList<Collection> collections) {
                            if (collections == null) return;
                            super.onPostExecute(collections);
                            ArrayList<Wallpaper> wallpapers = new ArrayList<>();
                            if (mPrefs.getMuzeiCollections().length() <= 0) {
                                for (Collection collection : collections) {
                                    for (Wallpaper wallpaper : collection.getWallpapers()) {
                                        if (!(wallpapers.contains(wallpaper)))
                                            wallpapers.add(wallpaper);
                                    }
                                }
                            } else {
                                String[] collects = mPrefs.getMuzeiCollections().split(",");
                                for (String collect : collects) {
                                    for (Collection collection : collections) {
                                        if (collection.getName().toLowerCase().equals(collect
                                                .toLowerCase())) {
                                            for (Wallpaper wallpaper : collection.getWallpapers()) {
                                                if (!(wallpapers.contains(wallpaper)))
                                                    wallpapers.add(wallpaper);
                                            }
                                        }
                                    }
                                }
                            }
                            if (wallpapers.size() <= 0) return;
                            int i = new Random().nextInt(wallpapers.size());
                            Wallpaper randomWallpaper = wallpapers.get(i);
                            setImageForMuzei(randomWallpaper.getName(), randomWallpaper.getAuthor(),
                                    randomWallpaper.getURL());
                            Log.d(Utils.LOG_TAG, "Setting picture: " + randomWallpaper.getName());
                        }
                    };
            try {
                downloadJSON.execute();
            } catch (Exception e) {
                Log.d(Utils.LOG_TAG, "Muzei error: " + e.getMessage());
            }
        } catch (Exception e) {
            Log.d(Utils.LOG_TAG, "Error updating Muzei: " + e.getMessage());
            throw new RetryException();
        }
    }

    private void setImageForMuzei(String name, String author, String url) {
        publishArtwork(new Artwork.Builder()
                .title(name)
                .byline(author)
                .imageUri(Uri.parse(url))
                .viewIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                .build());
        scheduleUpdate(System.currentTimeMillis() + convertRefreshIntervalToMillis(mPrefs
                .getMuzeiRefreshInterval()));
    }

    private long convertRefreshIntervalToMillis(int interval) {
        switch (interval) {
            case 0:
                return TimeUnit.MINUTES.toMillis(15);
            case 1:
                return TimeUnit.MINUTES.toMillis(30);
            case 2:
                return TimeUnit.MINUTES.toMillis(45);
            case 3:
                return TimeUnit.HOURS.toMillis(1);
            case 4:
                return TimeUnit.HOURS.toMillis(2);
            case 5:
                return TimeUnit.HOURS.toMillis(3);
            case 6:
                return TimeUnit.HOURS.toMillis(6);
            case 7:
                return TimeUnit.HOURS.toMillis(9);
            case 8:
                return TimeUnit.HOURS.toMillis(12);
            case 9:
                return TimeUnit.HOURS.toMillis(18);
            case 10:
                return TimeUnit.DAYS.toMillis(1);
            case 11:
                return TimeUnit.DAYS.toMillis(3);
            case 12:
                return TimeUnit.DAYS.toMillis(7);
            case 13:
                return TimeUnit.DAYS.toMillis(14);
        }
        return 0;
    }

}