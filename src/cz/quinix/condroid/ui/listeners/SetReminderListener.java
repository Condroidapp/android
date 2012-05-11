package cz.quinix.condroid.ui.listeners;

import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.service.ReminderManager;
import cz.quinix.condroid.ui.ShowAnnotation;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class SetReminderListener implements OnClickListener {

	Activity activity;
	int[] minutes = {0,2,5,10,15,20,30};
	
	
	
	public SetReminderListener(Activity activity) {
		super();
		this.activity = activity;
	}



	public void onClick(View v) {
		this.invoke(((ShowAnnotation) activity).getAnnotation());
	}
	
	public void invoke(final Annotation annotation) {
		AlertDialog.Builder ab = new AlertDialog.Builder(activity);
		ab.setTitle(R.string.remind);
		Integer exReminder = DataProvider.getInstance(activity).getReminder(annotation.getPid());
		int selected = -1;
		if(exReminder != null) {
			for (int i = 0; i<minutes.length; i++) {
				if(exReminder == minutes[i]) {
					selected = i;
					break;
				}
			}
		}
		ab.setSingleChoiceItems(R.array.remindBefore, selected, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				DataProvider provider = DataProvider.getInstance(activity);
				if(provider.setReminder(annotation, minutes[which])) {
                    ReminderManager.updateAlarmManager(activity);
					Toast.makeText(activity, "Upozornění bylo nastaveno.", Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(activity, "Chyba - Upozornění nebylo nastaveno.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		ab.create().show();
	}

}
