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

package jahirfiquitiva.libs.frames.callbacks;

import android.view.GestureDetector;
import android.view.MotionEvent;

import jahirfiquitiva.libs.frames.holders.WallpaperHolder;

public class WallpaperDoubleTapDetector extends GestureDetector.SimpleOnGestureListener {

    private WallpaperHolder holder;
    private OnWallpaperDoubleTapListener onDoubleTap;

    public WallpaperDoubleTapDetector(WallpaperHolder holder,
                                       OnWallpaperDoubleTapListener onDoubleTap) {
        this.holder = holder;
        this.onDoubleTap = onDoubleTap;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (onDoubleTap != null && holder != null)
            onDoubleTap.onDoubleTap(holder);
        return super.onDoubleTap(e);
    }

    public interface OnWallpaperDoubleTapListener {
        void onDoubleTap(WallpaperHolder holder);
    }

}