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

package jahirfiquitiva.libs.frames.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.List;
import java.util.Map;

import jahirfiquitiva.libs.frames.R;

public class NotificationUtils {

    public static void sendFirebaseNotification(Context context, Class mainActivity,
                                                Map<String, String> data, String title, String
                                                        content) {

        int ledColor = ThemeUtils.darkOrLight(context, R.color.dark_theme_accent,
                R.color.light_theme_accent);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setTicker(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setOngoing(false)
                .setColor(ledColor);

        Intent intent = new Intent();
        int flag = 0;
        intent = new Intent(context, mainActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (data != null) {
            if (data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    String[] dataValue = data.toString().replace("{", "").replace("}", "")
                            .split(",")[i].split("=");
                    intent.putExtra(dataValue[0], dataValue[1]);
                }
            }
        }
        flag = PendingIntent.FLAG_ONE_SHOT;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code
            */, intent, flag);
        notificationBuilder.setContentIntent(pendingIntent);

        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Resources resources = context.getResources(), systemResources = Resources.getSystem();

        notificationBuilder.setSound(ringtoneUri);
        notificationBuilder.setVibrate(new long[]{500, 500});

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = notificationBuilder.build();

        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.ledARGB = ledColor;
        notification.ledOnMS = resources.getInteger(systemResources.getIdentifier(
                "config_defaultNotificationLedOn", "integer", "android"));
        notification.ledOffMS = resources.getInteger(systemResources.getIdentifier(
                "config_defaultNotificationLedOff", "integer", "android"));

        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());
    }


    private static boolean hasNotificationExtraKey(Context context, Intent intent, String key,
                                                   Class service) {
        return context != null
                && isServiceAvailable(context, service)
                && intent != null && intent.getStringExtra(key) != null;
    }

    public static boolean isNotificationExtraKeyTrue(Context context, Intent intent, String key,
                                                     Class service) {
        return hasNotificationExtraKey(context, intent, key, service)
                && intent.getStringExtra(key).equals("true");
    }


    private static boolean isServiceAvailable(Context context, Class service) {
        if (context == null) return false;
        try {
            final PackageManager packageManager = context.getPackageManager();
            final Intent intent = new Intent(context, service);
            List resolveInfo =
                    packageManager.queryIntentServices(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return resolveInfo.size() > 0;
        } catch (Exception ex) {
            return false;
        }
    }

}