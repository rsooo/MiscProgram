package hakomusu.app;

import android.app.Activity;
import android.os.Bundle;

public class Hakomusu extends Activity {
    /** Called when the activity is first created. */
    HakoMusuView view;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new HakoMusuView(this);
        setContentView(view);
        //String s = this.findViewById(R.string.Rule2);
    }
    
	@Override
	public void onPause(){
//		try {
			view.timerEnable(false);
//			Thread.sleep(1000);
//			view.timerThread.join();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		super.onPause();
	}

	@Override
	public void onResume(){
		super.onResume();
		view.timerEnable(true);
	}

	
	@Override
	public void onDestroy(){
		super.onDestroy();
		view.saveTime();
	}

}