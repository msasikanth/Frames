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

package jahirfiquitiva.libs.frames.models;

public class DetailedCreditsItem {

    private final String bannerLink, photoLink, title, content, btnTexts[], btnLinks[];

    public DetailedCreditsItem(String bannerLink, String photoLink, String title, String content,
                               String[] btnTexts, String[] btnLinks) {
        this.bannerLink = bannerLink;
        this.photoLink = photoLink;
        this.title = title;
        this.content = content;
        this.btnTexts = btnTexts;
        this.btnLinks = btnLinks;
    }

    public String getBannerLink() {
        return bannerLink;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String[] getBtnTexts() {
        return btnTexts;
    }

    public String[] getBtnLinks() {
        return btnLinks;
    }
}