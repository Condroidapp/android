package cz.quinix.condroid.annotations;

import cz.quinix.condroid.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ShowAnnotation extends Activity {
	
	private Annotation annotation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.annotation = (Annotation) this.getIntent().getSerializableExtra("annotation");
		this.setContentView(R.layout.annotation);
		
		TextView title = (TextView) this.findViewById(R.id.annot_title);
		title.setText(this.annotation.title);
		
		TextView author = (TextView) this.findViewById(R.id.annot_author);
		author.setText(this.annotation.talker);
		
		TextView info = (TextView) this.findViewById(R.id.annot_info);
		info.setText(this.annotation.programLine+", "+this.annotation.pid+", "+this.annotation.length+", "+this.annotation.type);
		
		TextView text = (TextView) this.findViewById(R.id.annot_text);
		text.setText(this.annotation.annotation);
		
	}
}
