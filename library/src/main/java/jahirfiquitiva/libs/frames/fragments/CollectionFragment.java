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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.adapters.CollectionsAdapter;
import jahirfiquitiva.libs.frames.adapters.WallpapersAdapter;
import jahirfiquitiva.libs.frames.holders.lists.FullListHolder;
import jahirfiquitiva.libs.frames.utils.IconUtils;
import jahirfiquitiva.libs.frames.utils.Preferences;
import jahirfiquitiva.libs.frames.utils.ThemeUtils;
import jahirfiquitiva.libs.frames.utils.Utils;
import jahirfiquitiva.libs.frames.views.GridSpacingItemDecoration;

public class CollectionFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerFastScroller fastScroller;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar progress;
    private ImageView noConnection;
    private RecyclerView.ItemDecoration decoration;
    private boolean isCollections;
    private String collectionName;

    public static CollectionFragment newInstance(boolean isCollections, String collectionName) {
        CollectionFragment fragment = new CollectionFragment();
        Bundle args = new Bundle();
        args.putBoolean("collection", isCollections);
        args.putString("collectionName", collectionName);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.isCollections = getArguments().getBoolean("collection");
            this.collectionName = getArguments().getString("collectionName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        View layout = inflater.inflate(R.layout.wallpapers_section, container, false);

        noConnection = (ImageView) layout.findViewById(R.id.no_connected_icon);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.wallsGrid);
        fastScroller = (RecyclerFastScroller) layout.findViewById(R.id.rvFastScroller);
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);

        progress = (ProgressBar) layout.findViewById(R.id.progress);

        // noConnection.setImageDrawable(IconUtils.getTintedDrawable(context, "ic_no_connection"));

        setupRecyclerView(false, 0);

        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(
                ThemeUtils.darkOrLight(getActivity(), R.color.drawable_tint_light,
                        R.color.drawable_tint_dark));

        // int accent = ThemeUtils.darkOrLight(R.color.dark_theme_accent, R.color
        // .light_theme_accent);

        // mSwipeRefreshLayout.setColorSchemeResources(accent);

        mSwipeRefreshLayout.setEnabled(false);

        setupContent();

        return layout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // inflater.inflate(R.menu.wallpapers, menu);
    }

    public void setupContent() {
        if (Utils.hasNetwork(getActivity())) {
            showProgressBar();

            RecyclerView.Adapter mAdapter = null;
            if (isCollections) {
                if (!(FullListHolder.get().getCollections().getList().isEmpty())) {
                    mAdapter = new CollectionsAdapter(getActivity(),
                            FullListHolder.get().getCollections().getList());
                }
            } else {
                int index = FullListHolder.get().getCollections().getIndexForCollectionWithName
                        (collectionName.toLowerCase());
                if (index >= 0) {
                    mAdapter = new WallpapersAdapter(getActivity(), FullListHolder.get()
                            .getCollections().getList().get(index).getWallpapers());
                }
            }

            if (mAdapter != null) {
                mRecyclerView.setAdapter(mAdapter);
                fastScroller.attachRecyclerView(mRecyclerView);

                mRecyclerView.setVisibility(View.VISIBLE);
                fastScroller.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                noConnection.setVisibility(View.GONE);

                mSwipeRefreshLayout.setEnabled(false);
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
                return;
            }
        }
        noConnection.setImageDrawable(IconUtils.getTintedDrawable(getActivity(),
                "ic_no_connection"));
        showNoConnectionPicture();
    }

    private void showNoConnectionPicture() {
        noConnection.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        fastScroller.setVisibility(View.GONE);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void showProgressBar() {
        noConnection.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        fastScroller.setVisibility(View.GONE);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void setupRecyclerView(boolean updating, int newColumns) {
        Preferences mPrefs = new Preferences(getActivity());
        if (updating && decoration != null) {
            mPrefs.setWallsColumnsNumber(newColumns);
            mRecyclerView.removeItemDecoration(decoration);
        }

        int columnsNumber = mPrefs.getWallsColumnsNumber();
        if (isCollections) columnsNumber /= 2;
        if (getActivity().getResources().getConfiguration().orientation == 2) {
            columnsNumber *= 1.5f;
        }

        if (isCollections) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL, false));
            // decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration
            // .VERTICAL);
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columnsNumber));
            decoration = new GridSpacingItemDecoration(columnsNumber, getActivity().getResources()
                    .getDimensionPixelSize(R.dimen.item_margin), true);
        }

        if (decoration != null)
            mRecyclerView.addItemDecoration(decoration);

        mRecyclerView.setHasFixedSize(true);

        if (mRecyclerView.getVisibility() != View.VISIBLE) {
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        if (mRecyclerView.getAdapter() != null) {
            fastScroller.attachRecyclerView(mRecyclerView);
            if (fastScroller.getVisibility() != View.VISIBLE) {
                fastScroller.setVisibility(View.VISIBLE);
            }
        }
    }

    public void updateRecyclerView(int newColumns) {
        mRecyclerView.setVisibility(View.GONE);
        fastScroller.setVisibility(View.GONE);
        setupRecyclerView(true, newColumns);
    }

    public void refreshContent() {
        mRecyclerView.setVisibility(View.GONE);
        fastScroller.setVisibility(View.GONE);
        /*
        int stringId;
        if (Utils.hasNetwork(getActivity())) {
            stringId = R.string.refreshing_walls;
        } else {
            stringId = R.string.no_conn_title;
        }
        snackbar(new SnackbarEvent(stringId).setDuration(Snackbar.LENGTH_SHORT)
                .setColor(ThemeUtils.darkOrLight(context, R.color.snackbar_dark, R.color
                        .snackbar_light)));
        */
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }
}