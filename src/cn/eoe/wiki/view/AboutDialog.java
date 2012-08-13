package cn.eoe.wiki.view;

import cn.eoe.wiki.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

public class AboutDialog extends Dialog {
	
	private Context con;
	private Button btnCancel;

	public AboutDialog(Context context) {
		super(context,R.style.dialog);
		setContentView(R.layout.about_dialog);
		this.con = context;
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(cancelListener);
	}
	
	private View.OnClickListener cancelListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dismiss();
		}
	};

}
