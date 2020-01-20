package jp.stressfreesoft.app.chat;

import jp.rsooo.app.lib.alert.AlertUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;

public class InviteNearUserActivity extends Activity{
	
	Context context;
	Button searchButton;
	RadioButton maleSelectButton;
	RadioButton femaleSelectButton;
	RadioButton noneSelectButton;
	
//	private String selectedSex;
	private int selectedRange;
	//スピナーの選択肢と統一すること
	private int[] rangeValue = new int[]{500,1000,3000,5000};
	 
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.invitenearuserlayout);
		context = this;
		searchButton = (Button)this.findViewById(R.id.btn_invitenear);
		maleSelectButton = (RadioButton)this.findViewById(R.id.radiobtn_invitenear_male);
		femaleSelectButton = (RadioButton)this.findViewById(R.id.radiobtn_invitenear_female);
		noneSelectButton = (RadioButton)this.findViewById(R.id.radiobtn_invitenear_unknown);
		//スピナーの設定
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("500m");
        adapter.add("1km");
        adapter.add("3km");
        adapter.add("5km");
        Spinner spinner = (Spinner)this.findViewById(R.id.spinner_changerange);
        spinner.setPrompt("検索範囲を選択してください。");

        spinner.setAdapter(adapter);
     // スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
                Spinner spinner = (Spinner) parent;
                // 選択されたアイテムを取得します
                String item = (String) spinner.getSelectedItem();
                if(GlobalData.D){AlertUtil.showToast(item + " " + rangeValue[position], context);}
                selectedRange = rangeValue[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
        });
        
        searchButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent retIntent = new Intent();
				String selectedSex = "none";
				if(maleSelectButton.isChecked()){
					selectedSex = "male";
				}else if(femaleSelectButton.isChecked()){
					selectedSex = "female";
				}
				retIntent.putExtra("SELECTEDRANGE", selectedRange);
				retIntent.putExtra("SELECTEDSEX", selectedSex);
				setResult(InviteNearUserActivity.RESULT_OK, retIntent);
				finish();
			}
        	
        });
	}
	
	
} 
