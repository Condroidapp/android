/**
 * 
 */
package cz.quinix.condroid.annotations;

import cz.quinix.condroid.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Honza
 *
 */
public class AnnotationActivity extends Activity {
	public static String INTENT_BUNDLE_TEXT = "text";
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);                
            setContentView(R.layout.annotations);                
            final EditText et = (EditText)findViewById(R.id.editText1);
            Button b = (Button) findViewById(R.id.button1);                
            b.setOnClickListener(new View.OnClickListener() {                        
                    public void onClick(View v) {
                            Intent appResultIntent = new Intent();
                            appResultIntent.putExtra(INTENT_BUNDLE_TEXT, et.getText().toString());
                            setResult(RESULT_OK, appResultIntent);
                            finish();
                    }
            });                                          
    }   
}
