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
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.Checkable;

import jahirfiquitiva.libs.frames.callbacks.OnWallpaperFavedListener;
import jahirfiquitiva.libs.frames.models.Wallpaper;

/**
 * A {@link Checkable} {@link AppCompatImageView} which can be offset vertically.
 */
public class CheckableImageView extends AppCompatImageView implements Checkable {

    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    private boolean isChecked = false;

    public CheckableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean check) {
        if (isChecked != check) {
            isChecked = check;
            refreshDrawableState();
        }
    }

    public void toggle() {
        setChecked(!isChecked);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

}