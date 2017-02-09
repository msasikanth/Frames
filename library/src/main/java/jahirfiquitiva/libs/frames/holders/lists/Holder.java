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

import jahirfiquitiva.libs.frames.models.Collection;

/**
 * Created by Allan Wang on 2016-09-10.
 */
public class Holder {

    private final CollectionsList mCollections = new CollectionsList();

    public CollectionsList getCollections() {
        return mCollections;
    }

    public class CollectionsList extends ListHolderFrame<Collection> {

        public int getIndexForCollectionWithName(String name) {
            if ((getList() == null) || getList().isEmpty()) return -1;
            for (int i = 0; i < getList().size(); i++) {
                if (getList().get(i).getName().toLowerCase().equals(name.toLowerCase())) {
                    return i;
                }
            }
            return -1;
        }

    }

}