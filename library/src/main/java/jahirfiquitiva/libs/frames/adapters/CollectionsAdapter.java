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
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.util.ArrayList;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.activities.CollectionActivity;
import jahirfiquitiva.libs.frames.callbacks.OnWallpaperClickListener;
import jahirfiquitiva.libs.frames.holders.WallpaperHolder;
import jahirfiquitiva.libs.frames.models.Collection;
import jahirfiquitiva.libs.frames.utils.GlideConfiguration;

public class CollectionsAdapter extends RecyclerView.Adapter<WallpaperHolder> {

    private FragmentActivity activity;
    private ArrayList<Collection> collections;

    public CollectionsAdapter(FragmentActivity activity, ArrayList<Collection> nCollections) {
        this.activity = activity;
        this.collections = new ArrayList<>();
        if (nCollections != null) {
            for (Collection collection : nCollections) {
                if (!(collection.getName().toLowerCase().equals("featured"))) {
                    collections.add(collection);
                }
            }
        }
    }

    @Override
    public WallpaperHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        return new WallpaperHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_wallpaper, parent, false),
                new OnWallpaperClickListener() {
                    @Override
                    public void onClick(Object item, Bitmap picture, ImageView wall,
                                        ImageView heart, TextView name, TextView author) {
                        doOnPressed(item, picture, wall, name);
                    }
                }, null, true);
    }

    @Override
    public void onBindViewHolder(final WallpaperHolder holder, int position) {
        holder.setItem(collections.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return collections != null ? collections.size() : 0;
    }

    private Handler handler(Context context) {
        return new Handler(context.getMainLooper());
    }

    private void runOnUIThread(Context context, Runnable r) {
        handler(context).post(r);
    }

    public void changeCollections(ArrayList<Collection> nCollections) {
        if (collections == null)
            collections = new ArrayList<>();
        collections.clear();
        if (nCollections != null)
            collections.addAll(nCollections);
        notifyDataSetChanged();
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
    private void doOnPressed(Object item, final Bitmap bitmap, final ImageView wall, final TextView name) {

        final Intent collectionDetails = new Intent(activity, CollectionActivity.class);
        collectionDetails.putExtra("collection", (Collection) item);
        collectionDetails.putExtra("wallTransition", ViewCompat.getTransitionName(wall));
        collectionDetails.putExtra("nameTransition", ViewCompat.getTransitionName(name));

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    if (bitmap != null) {
                        String filename = "thumb.png";
                        FileOutputStream stream = activity.openFileOutput(filename, Context
                                .MODE_PRIVATE);
                        bitmap.compress(Bitmap.CompressFormat.PNG,
                                GlideConfiguration.getPictureMaxRes(activity), stream);
                        stream.flush();
                        stream.close();
                        collectionDetails.putExtra("image", filename);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ActivityCompat.startActivityForResult(activity, collectionDetails, 11, null);
                }

            }
        }).start();

        ActivityCompat.startActivityForResult(activity, collectionDetails, 11, null);

    }

}