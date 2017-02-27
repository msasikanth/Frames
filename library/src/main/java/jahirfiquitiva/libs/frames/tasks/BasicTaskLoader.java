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

package jahirfiquitiva.libs.frames.tasks;

import android.content.AsyncTaskLoader;
import android.content.Context;

abstract class BasicTaskLoader<T> extends AsyncTaskLoader<T> {

    private T data;
    private boolean hasResult = false;

    BasicTaskLoader(final Context context) {
        super(context);
        onContentChanged();
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged())
            forceLoad();
        else if (hasResult)
            deliverResult(data);
    }

    @Override
    public void deliverResult(final T data) {
        this.data = data;
        this.hasResult = true;
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (hasResult) {
            this.data = null;
            this.hasResult = false;
        }
    }

    public T getResult() {
        return data;
    }

    interface TaskListener<T> {
        void onTaskStarted(BasicTaskLoader<T> task);

        void onTaskFinished(BasicTaskLoader<T> task, T data);
    }

}