/*
 * Copyright (C) 2012 The Android Open Source Project
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

	private static final String CENTER_CLOCK_STATUS_BAR_PROP = "pref_center_clock_status_bar";

    private static final String BACK_BUTTON_ENDS_CALL_PROP = "pref_back_button_ends_call";

    private static final String DISABLE_BOOTANIMATION_PROP = "pref_disable_bootanimation";
    private static final String DISABLE_BOOTANIMATION_PERSIST_PROP = "persist.sys.nobootanimation";

	private CheckBoxPreference mCenterClockStatusBar;

    private CheckBoxPreference mBackButtonEndsCall;

    private CheckBoxPreference mDisableBootanimPref;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
		
		addPreferencesFromResource(R.xml.popfansettings_prefs);

        mCenterClockStatusBar = (CheckBoxPreference) findPreference(CENTER_CLOCK_STATUS_BAR_PROP);
        
        mBackButtonEndsCall = (CheckBoxPreference) findPreference(BACK_BUTTON_ENDS_CALL_PROP);

        mDisableBootanimPref = (CheckBoxPreference) findPreference(DISABLE_BOOTANIMATION_PROP);

        Log.i(TAG, "\n\nWelcome in Daveee10's world!!! :D\n\n");
    }

    @Override
    public void onResume() {
        super.onResume();

        final ContentResolver cr = getActivity().getContentResolver();

		updateCenterClockStatusBar();

        updateBackButtonEndsCall();

        updateDisableBootAnimation();
    }

    /* Update functions */
    private void updateCenterClockStatusBar() {
        mCenterClockStatusBar.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.CENTER_CLOCK_STATUS_BAR, 0) == 1);
            if (DEBUG) Log.i(TAG, UPD + "CenterClock");
    }

    private void updateBackButtonEndsCall() {
        mBackButtonEndsCall.setChecked(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.BACK_BUTTON_ENDS_CALL, 0) == 1);
            if (DEBUG) Log.i(TAG, UPD + "BackButtonEndsCall");
    }

    private void updateDisableBootAnimation() {
        String disableBootanimation = SystemProperties.get(DISABLE_BOOTANIMATION_PERSIST_PROP, 0);
        mDisableBootanimPref.setChecked("1".equals(disableBootanimation));
            if (DEBUG) Log.i(TAG, UPD + "BootAnimation");
    }

    /* Write functions */
	private void writeCenterClockStatusBar() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.CENTER_CLOCK_STATUS_BAR, mCenterClockStatusBar.isChecked() ? 1 : 0);
        	if (DEBUG) Log.i(TAG, WRT + "CenterClock");
        Helpers.restartSystemUI();
        	if (DEBUG) Log.i(TAG, "Restarting SystemUI");
    }

    private void writeBackButtonEndsCall() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.BACK_BUTTON_ENDS_CALL, mBackButtonEndsCall.isChecked() ? 1 : 0);
            if (DEBUG) Log.i(TAG, WRT + "BackButtonEndsCall");
    }

    private void writeDisableBootAnimation() {
        SystemProperties.set(DISABLE_BOOTANIMATION_PERSIST_PROP, mDisableBootanimPref.isChecked() ? "1" : "0");
        if (DEBUG) Log.i(TAG, WRT + "BootAnimation");
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if (Utils.isMonkeyRunning()) {
            return false;
        }

        if (preference == mBackButtonEndsCall) {
            writeBackButtonEndsCall();
        } else if (preference == mCenterClockStatusBar) {
            writeCenterClockStatusBar();
        } else if (preference == mDisableBootanimPref) {
            writeDisableBootAnimation();
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
