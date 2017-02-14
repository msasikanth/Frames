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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.util.ArrayList;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.adapters.CollectionsAdapter;
import jahirfiquitiva.libs.frames.adapters.WallpapersAdapter;
import jahirfiquitiva.libs.frames.holders.lists.FullListHolder;
import jahirfiquitiva.libs.frames.models.Collection;
import jahirfiquitiva.libs.frames.models.Wallpaper;
import jahirfiquitiva.libs.frames.utils.IconUtils;
import jahirfiquitiva.libs.frames.utils.Preferences;
import jahirfiquitiva.libs.frames.utils.ThemeUtils;
import jahirfiquitiva.libs.frames.utils.Utils;
import jahirfiquitiva.libs.frames.views.EmptyViewRecyclerView;
import jahirfiquitiva.libs.frames.views.GridSpacingItemDecoration;

public class CollectionFragment extends Fragment {

    private EmptyViewRecyclerView mRecyclerView;
    private RecyclerFastScroller fastScroller;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.Adapter mAdapter;
    private boolean isCollections;
    private boolean isFavorites;
    private boolean isSearch;
    private String collectionName;
    private ArrayList<Wallpaper> wallpapers;
    private ArrayList<Collection> collections;
    private RecyclerView.ItemDecoration decoration;
    private int lastColumns = -1;

    public static CollectionFragment newInstance(boolean isCollections, String collectionName) {
        CollectionFragment fragment = new CollectionFragment();
        Bundle args = new Bundle();
        args.putString("collectionName", collectionName);
        args.putParcelableArrayList("wallpapers", null);
        args.putParcelableArrayList("collections", null);
        args.putBoolean("isCollections", isCollections);
        args.putBoolean("isFavorites", false);
        args.putBoolean("isSearch", false);
        fragment.setArguments(args);
        return fragment;
    }

    public static CollectionFragment newInstance(ArrayList<Wallpaper> wallpapers, boolean
            isFavorites, boolean isSearch) {
        CollectionFragment fragment = new CollectionFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("wallpapers", wallpapers);
        args.putParcelableArrayList("collections", null);
        args.putBoolean("isCollections", false);
        args.putBoolean("isFavorites", isFavorites);
        args.putBoolean("isSearch", isSearch);
        fragment.setArguments(args);
        return fragment;
    }

    public static CollectionFragment newInstance(ArrayList<Collection> collections, boolean
            isSearch) {
        CollectionFragment fragment = new CollectionFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("collections", collections);
        args.putBoolean("isCollections", true);
        args.putBoolean("isFavorites", false);
        args.putBoolean("isSearch", isSearch);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.collectionName = getArguments().getString("collectionName");
            this.wallpapers = getArguments().getParcelableArrayList("wallpapers");
            this.collections = getArguments().getParcelableArrayList("collections");
            this.isCollections = getArguments().getBoolean("isCollections", false);
            this.isFavorites = getArguments().getBoolean("isFavorites", false);
            this.isSearch = getArguments().getBoolean("isSearch", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        View layout = inflater.inflate(R.layout.fragment_content, container, false);

        mRecyclerView = (EmptyViewRecyclerView) layout.findViewById(R.id.wallsGrid);
        fastScroller = (RecyclerFastScroller) layout.findViewById(R.id.rvFastScroller);
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);

        // TODO: Change ic_ drawables with beautiful illustrations

        ImageView noConnection = (ImageView) layout.findViewById(R.id.no_connected_view);
        noConnection.setImageDrawable(IconUtils.getTintedDrawable(getActivity(),
                "ic_no_connection"));
        noConnection.setScaleX(0.65f);
        noConnection.setScaleY(0.65f);

        ImageView noFavorites = (ImageView) layout.findViewById(R.id.no_favorites_view);
        noFavorites.setImageDrawable(IconUtils.getTintedDrawable(getActivity(), "ic_no_favorites"));
        noFavorites.setScaleX(0.65f);
        noFavorites.setScaleY(0.65f);

        ImageView noResults = (ImageView) layout.findViewById(R.id.no_results_view);
        noResults.setImageDrawable(IconUtils.getTintedDrawable(getActivity(), "ic_search"));
        noResults.setScaleX(0.65f);
        noResults.setScaleY(0.65f);

        ProgressBar progress = (ProgressBar) layout.findViewById(R.id.progress);
        mRecyclerView.setEmptyViews(isSearch ? noResults : isFavorites ? noFavorites : progress,
                noConnection);

        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(
                ThemeUtils.darkOrLight(getActivity(), R.color.drawable_tint_light,
                        R.color.drawable_tint_dark));
        int accent = ThemeUtils.darkOrLight(R.color.dark_theme_accent, R.color.light_theme_accent);
        mSwipeRefreshLayout.setColorSchemeResources(accent);
        mSwipeRefreshLayout.setEnabled(false);

        setupRecyclerView();
        setupContent();

        return layout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // inflater.inflate(R.menu.wallpapers, menu);
    }

    public void setupContent() {
        mRecyclerView.setState(Utils.isConnected(getActivity()) ? EmptyViewRecyclerView
                .STATE_NORMAL : EmptyViewRecyclerView.STATE_NOT_CONNECTED);

        if (mRecyclerView.getState() != EmptyViewRecyclerView.STATE_NOT_CONNECTED) {
            mAdapter = null;
            if (isSearch) {
                if (wallpapers != null) {
                    mAdapter = new WallpapersAdapter(getActivity(), wallpapers, isFavorites);
                } else if (collections != null) {
                    mAdapter = new CollectionsAdapter(getActivity(), collections);
                }
            } else {
                if (isCollections) {
                    if (!(FullListHolder.get().getCollections().getList().isEmpty())) {
                        mAdapter = new CollectionsAdapter(getActivity(),
                                FullListHolder.get().getCollections().getList());
                    }
                } else if (isFavorites || wallpapers != null) {
                    mAdapter = new WallpapersAdapter(getActivity(), wallpapers, isFavorites);
                } else {
                    if (collectionName != null) {
                        int index = FullListHolder.get().getCollections()
                                .getIndexForCollectionWithName(collectionName);
                        if (index >= 0) {
                            mAdapter = new WallpapersAdapter(getActivity(), FullListHolder
                                    .get().getCollections().getList().get(index)
                                    .getWallpapers(), isFavorites);
                        }
                    }
                }
            }
            if (mAdapter != null) {
                mRecyclerView.setAdapter(mAdapter);
                fastScroller.attachRecyclerView(mRecyclerView);
                fastScroller.setVisibility(View.VISIBLE);
            }
        }
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void setupRecyclerView() {
        Preferences mPrefs = new Preferences(getActivity());
        if (lastColumns == mPrefs.getWallsColumnsNumber()) return;
        int columnsNumber = mPrefs.getWallsColumnsNumber();
        lastColumns = columnsNumber;
        if ((isCollections) || (isSearch && collections != null)) columnsNumber /= 2;
        if (getActivity().getResources().getConfiguration().orientation == 2) {
            columnsNumber *= isCollections ? 2 : 1.5f;
        }
        if (mRecyclerView == null) return;
        if (decoration != null)
            mRecyclerView.removeItemDecoration(decoration);
        decoration = new GridSpacingItemDecoration(columnsNumber,
                ((isCollections) || (isSearch && collections != null)) ? 0 : getActivity()
                        .getResources().getDimensionPixelSize(R.dimen.cards_margin), true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columnsNumber));
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setHasFixedSize(true);
    }

    public int getLastColumns() {
        return lastColumns;
    }

    public void refreshContent() {
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        setupContent();
    }

    public RecyclerView.Adapter getRVAdapter() {
        return mAdapter;
    }
}