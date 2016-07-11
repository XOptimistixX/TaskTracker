package com.jeffsieu.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeffsieu.tasktracker.activity.LoginActivity;

/**
 * Created by Jeff on 7/7/2016.
 */
public class SettingsFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		if (preference.getKey().equals(getString(R.string.key_settings_signin))) {
			startActivity(new Intent(getActivity(), LoginActivity.class));
		}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}
