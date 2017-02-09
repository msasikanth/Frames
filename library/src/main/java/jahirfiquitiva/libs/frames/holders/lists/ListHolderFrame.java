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

package jahirfiquitiva.libs.frames.holders.lists;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by Allan Wang on 2016-09-10.
 */
public abstract class ListHolderFrame<T> {

    private ArrayList<T> mList = new ArrayList<>();

    public void createList(@NonNull ArrayList<T> list) {
        mList = list;
    }

    public ArrayList<T> getList() {
        return mList;
    }

    public void clearList() {
        mList = null;
    }

    public boolean hasList() {
        return mList != null && !mList.isEmpty();
    }

    public boolean isEmpty() {
        return !hasList();
    }

    public boolean isNull() {
        return mList == null;
    }
}
