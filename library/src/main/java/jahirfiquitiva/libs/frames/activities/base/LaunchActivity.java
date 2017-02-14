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

package jahirfiquitiva.libs.frames.activities.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import jahirfiquitiva.libs.frames.activities.FavoritesActivity;
import jahirfiquitiva.libs.frames.activities.SearchActivity;
import jahirfiquitiva.libs.frames.activities.StudioActivity;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String shortcut = "";
        if (getIntent().getDataString() != null && getIntent().getDataString().contains
                ("_shortcut")) {
            shortcut = getIntent().getDataString();
        }
        Intent studio;
        if (shortcut.contains("search")) {
            studio = new Intent(this, SearchActivity.class);
        } else if (shortcut.contains("favorites") || shortcut.contains("favs")) {
            studio = new Intent(this, FavoritesActivity.class);
        } else {
            studio = new Intent(this, StudioActivity.class);
        }
        studio.putExtra("key", getKey());
        startActivity(studio);
        finish();
    }

    protected String getKey() {
        return "";
    }

}