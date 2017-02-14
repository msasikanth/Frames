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
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import jahirfiquitiva.libs.frames.R;
import jahirfiquitiva.libs.frames.utils.ThemeUtils;

public class FramesCardView extends CardView {

    private Context context;

    public FramesCardView(Context context) {
        super(context);
        setupRightCardColor(context);
    }

    public FramesCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupRightCardColor(context);
    }

    public FramesCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupRightCardColor(context);
    }

    @Override
    public void setCardBackgroundColor(int ignoredColor) {
        /*
        super.setCardBackgroundColor(ThemeUtils.darkLightOrTransparent(context, R.color
                .card_dark_background, R.color.card_light_background, R.color
                .card_clear_background));
                */
    }

    private void setupRightCardColor(Context context) {
        this.context = context;
        setCardBackgroundColor(0);
    }

}