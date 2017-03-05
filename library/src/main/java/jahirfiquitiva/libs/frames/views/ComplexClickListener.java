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

import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewConfiguration;

public abstract class ComplexClickListener implements View.OnClickListener {

    private static final long DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout() + 100;
    private static final long TAP_TIMEOUT = ViewConfiguration.getTapTimeout();

    private long lastClickTime = 0;

    private Handler handler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            onSimpleClick();
        }
    };

    @Override
    public void onClick(View v) {
        onSimpleClick();
        /*
        long newClickTime = SystemClock.elapsedRealtime();
        if (lastClickTime == 0) lastClickTime = newClickTime;
        long elapsedTime = (newClickTime - lastClickTime);
        handler.removeCallbacks(mRunnable);
        if (elapsedTime <= DOUBLE_TAP_TIMEOUT) {
            onDoubleTap();
        } else {
            handler.postDelayed(mRunnable, TAP_TIMEOUT);
        }
        lastClickTime = newClickTime; */
    }

    public abstract void onSimpleClick();

    public abstract void onDoubleTap();
}