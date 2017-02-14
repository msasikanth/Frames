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

package jahirfiquitiva.libs.frames.views;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Jahir Fiquitiva
 */
public class EmptyViewRecyclerView extends RecyclerView {
    @Nullable
    View emptyView;
    @Nullable
    View notConnectedView;

    @IntDef({STATE_NORMAL, STATE_NOT_CONNECTED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public static final int STATE_NORMAL = 0;
    public static final int STATE_NOT_CONNECTED = 1;

    @State
    private int state = STATE_NORMAL;

    public EmptyViewRecyclerView(Context context) {
        super(context);
    }

    public EmptyViewRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyViewRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void checkIfEmptyOrNotConnected() {
        if (state == STATE_NORMAL) {
            if (getAdapter() != null) {
                if (emptyView != null) {
                    emptyView.setVisibility(getAdapter().getItemCount() > 0 ? GONE : VISIBLE);
                }
                if (notConnectedView != null) {
                    notConnectedView.setVisibility(GONE);
                }
                setVisibility(getAdapter().getItemCount() > 0 ? VISIBLE : GONE);
            } else {
                if (emptyView != null) {
                    emptyView.setVisibility(VISIBLE);
                }
            }
        } else {
            if (emptyView != null) {
                emptyView.setVisibility(GONE);
            }
            if (notConnectedView != null) {
                notConnectedView.setVisibility(VISIBLE);
            }
            setVisibility(GONE);
        }
    }

    final
    @NonNull
    AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmptyOrNotConnected();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            checkIfEmptyOrNotConnected();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            checkIfEmptyOrNotConnected();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            checkIfEmptyOrNotConnected();
        }
    };

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        checkIfEmptyOrNotConnected();
    }

    @State
    public int getState() {
        return state;
    }

    public void setState(@State int state) {
        this.state = state;
        checkIfEmptyOrNotConnected();
    }

    public void setEmptyViews(@Nullable View emptyView, @Nullable View notConnectedView) {
        this.emptyView = emptyView;
        this.notConnectedView = notConnectedView;
        checkIfEmptyOrNotConnected();
    }

}