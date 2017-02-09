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
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.holders.WallpaperHolder;
import jahirfiquitiva.libs.frames.models.Collection;

public class CollectionsAdapter extends RecyclerView.Adapter<WallpaperHolder> {

    private final FragmentActivity activity;
    private final ArrayList<Collection> collections;

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
        return new WallpaperHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .item_wallpaper, parent, false), null, null, null, true);
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

    private void doOnPressed(final Object item) {
        runOnUIThread(activity, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "Pressed collection: " + ((Collection) item).getName(),
                        Toast
                                .LENGTH_SHORT).show();
            }
        });
    }

}