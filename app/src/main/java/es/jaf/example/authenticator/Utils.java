/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.jaf.example.authenticator;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.*;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static final int FILE_PICKER_CODE = 1;
    public static final int FOLDER_PICKER_CODE = 2;

    protected static SharedPreferences getEncryptedPrefs(Context appContext) throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

        // on below line initializing our encrypted shared preferences and passing our key to it.
        return  EncryptedSharedPreferences.create(
                "es.jaf.example.authenticator",
                masterKeyAlias,
                appContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
    }

    static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13 (API 33) y superiores
                packageInfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(),
                        PackageManager.PackageInfoFlags.of(0)
                );
            } else {
                // Versiones anteriores
                packageInfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(),
                        0
                );
            }

            if (packageInfo != null && packageInfo.versionName != null) {
                return packageInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "??";
    }
    static void saveException(String text, Exception ex) {
        File dir;
        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            dir = new File(root.getAbsolutePath() + "/Download/es.jaf/logs/");
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    return;
                }
            }
        } catch (Exception e) {
            return;
        }

        String now = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        File file = new File(dir, "es.jaf.example.authenticator" + ".txt");
        try (FileOutputStream fos = new FileOutputStream(file, true); PrintWriter pw = new PrintWriter(fos)) {
            pw.println(now + "\t" + text);
            if (ex != null) {
                pw.println(now + "\t" + "Message:" + ex.getMessage());
                ex.printStackTrace(pw);
            }
            pw.flush();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}