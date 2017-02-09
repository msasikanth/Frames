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

public class WallpaperGestureDetector implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private Object item;
    private WallpaperHolder holder;
    private OnWallpaperPressListener onPressListener;
    private OnWallpaperDoubleTapListener onDoubleTap;

    public WallpaperGestureDetector(WallpaperHolder holder, Object item) {
        this.holder = holder;
        this.item = item;
    }

    public WallpaperGestureDetector(WallpaperHolder holder, Object item, OnWallpaperPressListener
            onPressListener) {
        this(holder, item);
        this.onPressListener = onPressListener;
    }

    public WallpaperGestureDetector(WallpaperHolder holder, Object item, OnWallpaperPressListener
            onPressListener, OnWallpaperDoubleTapListener onDoubleTap) {
        this(holder, item, onPressListener);
        this.onDoubleTap = onDoubleTap;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (onPressListener != null && item != null)
            onPressListener.onPressed(item);
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (onDoubleTap != null && holder != null)
            onDoubleTap.onDoubleTap(holder);
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (onPressListener != null && item != null)
            onPressListener.onLongPressed(item);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public void setItem(Object item) {
        this.item = item;
    }

    public interface OnWallpaperDoubleTapListener {
        void onDoubleTap(WallpaperHolder holder);
    }

}