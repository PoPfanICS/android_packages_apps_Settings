/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.android.settings;

import com.android.settings.util.CMDProcessor;
import com.android.settings.util.Helpers;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.backup.IBackupManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.VerifierDeviceIdentity;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.IWindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class PopfanSettings extends PreferenceFragment
    implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener, OnPreferenceChangeListener {

    private static final String TAG = "PoPfanSettings";
    private static final String WRT = "Setting changed: ";
    private static final String UPD = "Setting updated: ";

    private static final boolean DEBUG = true;

    private static final String BACK_BUTTON_ENDS_CALL_PROP = "pref_back_button_ends_call";

    private CheckBoxPreference mBackButtonEndsCall;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mBackButtonEndsCall = (CheckBoxPreference) findPreference(BACK_BUTTON_ENDS_CALL_PROP);

        Log.i(TAG, "\n\nWelcome in Daveee10's world!!! :D\n\n");
    }

    @Override
    public void onResume() {
        super.onResume();

        final ContentResolver cr = getActivity().getContentResolver();

        updateBackButtonEndsCall();
    }

    /* Update functions */
    private void updateBackButtonEndsCall() {
        mBackButtonEndsCall.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.BACK_BUTTON_ENDS_CALL, 0) == 1);
            if (DEBUG) Log.i(TAG, UPD + "BackButtonEndsCall");
    }

    /* Write functions */
    private void writeBackButtonEndsCall() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.BACK_BUTTON_ENDS_CALL, mBackButtonEndsCall.isChecked() ? 1 : 0);
            if (DEBUG) Log.i(TAG, WRT + "BackButtonEndsCall");
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if (Utils.isMonkeyRunning()) {
            return false;
        }

        if (preference == mBackButtonEndsCall) {
            writeBackButtonEndsCall();
        }

        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean handled = false;
    }

    private void dismissDialog() {
        if (mOkDialog == null) return;
        mOkDialog.dismiss();
        mOkDialog = null;
    }

    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            mOkClicked = true;
        }
    }

    public void onDismiss(DialogInterface dialog) {
        // Assuming that onClick gets called first
        if (!mOkClicked) {
        }
    }

    @Override
    public void onDestroy() {
        dismissDialog();
        super.onDestroy();
    }
}
