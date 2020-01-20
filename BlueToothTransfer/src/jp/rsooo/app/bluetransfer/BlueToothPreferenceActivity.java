package jp.rsooo.app.bluetransfer;


import jp.rsooo.app.lib.alert.AlertUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;

public class BlueToothPreferenceActivity extends PreferenceActivity 
	implements OnPreferenceChangeListener{

	EditTextPreference epName;
	EditTextPreference epPhone;
	EditTextPreference epMail;
	
	Context context = this;
	@Override 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
//		this.setPreferenceScreen(createPreferenceScreen());
		context = this;
		this.addPreferencesFromResource(R.xml.preferences);
//		EditTextPreference epDevice = (EditTextPreference)this.findPreference("DEVICE");
//		epDevice.setOnPreferenceChangeListener(this);
		epName = (EditTextPreference)this.findPreference("NAME");
		epName.setOnPreferenceChangeListener(this);
		epPhone = (EditTextPreference)this.findPreference("PHONE");
		epPhone.setOnPreferenceChangeListener(this);
		epMail = (EditTextPreference)this.findPreference("MAIL");
		epMail.setOnPreferenceChangeListener(this);
		
	
	}
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		preference.setSummary((String)newValue);
		return true;
	}

	@Override
	public void onStart(){
		super.onStart();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String name = sharedPreferences.getString("NAME", "no data");
		epName.setSummary(name);
		String phone = sharedPreferences.getString("PHONE", "no data");
		epPhone.setSummary(phone);
		String mail = sharedPreferences.getString("MAIL", "no data");
		epMail.setSummary(mail);
		
//		AlertUtil.showAlert(getPreferenceManager().getSharedPreferencesName(), context);
	}
	
	/*
	public static class PreferenceRegister implements OnPreferenceChangeListener{

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			preference.setSummary((String)newValue);
			return true;
		}

	}
	*/
}
