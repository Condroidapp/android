package cz.quinix.condroid;

import cz.quinix.condroid.abstracts.CondroidActivity;
import android.os.Bundle;

public class AboutActivity extends CondroidActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.about);
	}
}
