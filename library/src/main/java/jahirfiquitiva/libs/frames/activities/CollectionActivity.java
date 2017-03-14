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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.activities.base.ThemedActivity;
import jahirfiquitiva.libs.frames.adapters.WallpapersAdapter;
import jahirfiquitiva.libs.frames.fragments.CollectionFragment;
import jahirfiquitiva.libs.frames.models.Collection;
import jahirfiquitiva.libs.frames.utils.FavoritesUtils;
import jahirfiquitiva.libs.frames.utils.ToolbarColorizer;
import jahirfiquitiva.libs.frames.utils.Utils;
import jahirfiquitiva.libs.frames.views.FixedElevationAppBarLayout;
import jahirfiquitiva.libs.frames.views.RectangularImageView;

public class CollectionActivity extends ThemedActivity {

    private CollectionFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FavoritesUtils.init(this);

        Collection collection = getIntent().getParcelableExtra("collection");

        setContentView(R.layout.activity_collection);

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            Utils.setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            Utils.setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FixedElevationAppBarLayout appBar = (FixedElevationAppBarLayout) findViewById(R.id.appBar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id
                .collapsingToolbar);

        if (collection != null)
            collapsingToolbar.setTitle(collection.getName());

        TextView title = getToolbarTextView(toolbar);
        if (title != null)
            ViewCompat.setTransitionName(title, getIntent().getStringExtra("nameTransition"));

        RectangularImageView toolbarHeader = (RectangularImageView) findViewById(R.id
                .toolbarHeader);
        ViewCompat.setTransitionName(toolbarHeader, getIntent().getStringExtra("wallTransition"));

        setupCollapsingToolbarPicture(collection, toolbarHeader);

        ToolbarColorizer.setupCollapsingToolbarIconsAndTextsColors(this, appBar, toolbar);
        ToolbarColorizer.setupCollapsingToolbarTextColors(this, collapsingToolbar);
        ToolbarColorizer.tintStatusBar(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FavoritesUtils.destroy(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAndSendData();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finishAndSendData();
    }

    private TextView getToolbarTextView(Toolbar toolbar) {
        try {
            Class<?> toolbarClass = Toolbar.class;
            Field titleField = toolbarClass.getDeclaredField("mTitleTextView");
            titleField.setAccessible(true);
            return (TextView) titleField.get(toolbar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void finishAndSendData() {
        Intent intent = new Intent();
        StringBuilder s = new StringBuilder("");
        if (listFragment != null) {
            if (listFragment.getRVAdapter() != null) {
                ArrayList<String> list = ((WallpapersAdapter) listFragment.getRVAdapter())
                        .getModifiedWallpapers();
                for (int i = 0; i < list.size(); i++) {
                    s.append(list.get(i));
                    if (list.size() > 1 && i < (list.size() - 1)) {
                        s.append(",");
                    }
                }
            }
        }
        intent.putExtra("modified", s.toString());
        setResult(11, intent);
        try {
            ActivityCompat.finishAfterTransition(this);
        } catch (Exception e) {
            finish();
        }
    }

    private void setupCollapsingToolbarPicture(Collection collection, ImageView toolbarHeader) {

        final Bitmap[][] bitmap = {{null}};
        final String filename = getIntent().getStringExtra("image");
        final Drawable[] d = new Drawable[1];

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (filename != null) {
                    try {
                        FileInputStream is = openFileInput(filename);
                        bitmap[0][0] = BitmapFactory.decodeStream(is);
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                if (bitmap[0][0] != null) {
                    d[0] = new GlideBitmapDrawable(getResources(), bitmap[0][0]);
                } else {
                    d[0] = new ColorDrawable(ContextCompat.getColor(CollectionActivity.this, android.R.color.transparent));
                }

            }
        }).start();

        if (collection != null) {
            if (collection.getPreviewThumbnailURL() != null) {
                Glide.with(this)
                        .load(collection.getPreviewURL())
                        .priority(Priority.HIGH)
                        .placeholder(d[0])
                        .error(d[0])
                        .dontTransform()
                        .dontAnimate()
                        .thumbnail(Glide.with(this)
                                .load(collection.getPreviewThumbnailURL())
                                .priority(Priority.IMMEDIATE)
                                .placeholder(d[0])
                                .error(d[0])
                                .dontTransform()
                                .dontAnimate()
                                .thumbnail(0.5f))
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(toolbarHeader);
            } else {
                Glide.with(this)
                        .load(collection.getPreviewURL())
                        .priority(Priority.HIGH)
                        .placeholder(d[0])
                        .error(d[0])
                        .dontTransform()
                        .dontAnimate()
                        .thumbnail(0.5f)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(toolbarHeader);
            }
            listFragment = CollectionFragment.newInstance(collection.getWallpapers(), false, false);
            getSupportFragmentManager().beginTransaction().replace(R.id.content, listFragment,
                    collection.getName()).commit();
        } else {
            Glide.with(this)
                    .load(d[0])
                    .priority(Priority.IMMEDIATE)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(toolbarHeader);
        }
    }

}