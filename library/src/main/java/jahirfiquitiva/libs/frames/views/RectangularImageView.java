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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import jahirfiquitiva.libs.frames.R;

public class RectangularImageView extends ImageView {

    private int heightDivider;

    public RectangularImageView(Context context) {
        super(context);
    }

    public RectangularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHeightDivider(context, attrs);
    }

    public RectangularImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setHeightDivider(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RectangularImageView(Context context, AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setHeightDivider(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //noinspection SuspiciousNameCombination
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth() / heightDivider);
    }

    private void setHeightDivider(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RectangularImageView,
                0, 0);
        try {
            heightDivider = ta.getInteger(R.styleable.RectangularImageView_heightDivider, 3);
        } finally {
            ta.recycle();
        }
    }

}