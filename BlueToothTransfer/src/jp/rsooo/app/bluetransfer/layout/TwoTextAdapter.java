package jp.rsooo.app.bluetransfer.layout;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TwoLineListItem;

public class TwoTextAdapter extends ArrayAdapter<TwoTextItem>{

//	String primaryText;
//	String secondaryText;
	
	//XMLからビューつくる
	private LayoutInflater inflater;
	private final int textViewResource;
	private List<TwoTextItem> items;
	
	public TwoTextAdapter(Context context, int textViewResourceId,
			List<TwoTextItem> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.textViewResource =textViewResourceId;
		items = objects;
		
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parent){
		View view = null;
		//if(convertView != null){
		//	view = convertView;					//再利用するとうまく動かない．原因究明しないと
		//}else{
			view = this.inflater.inflate(this.textViewResource, null);
		
//		TextView textview = (TextView)view.findViewWithTag("main");
//		TwoTextItem item = items.get(pos);
//		textview.setText(item.getPrimaryText());
//		EditText editText = (EditText)view.findViewWithTag("edit");
//		editText.setHint(item.getSecondaryText());
		
			TwoTextItem item = items.get(pos);
			TwoLineListItem twolinelistText = (TwoLineListItem) view
					.findViewWithTag("twolinelist");
			TextView primaryView = twolinelistText.getText1();
			primaryView.setText(item.getPrimaryText());

			//TwoLineListItem secondaryText = (TwoLineListItem) view
			//		.findViewWithTag("twolinelist");
			TextView secondaryView = twolinelistText.getText2();
			secondaryView.setText(item.getSecondaryText());
		
		//}
		return view;
	}
}
