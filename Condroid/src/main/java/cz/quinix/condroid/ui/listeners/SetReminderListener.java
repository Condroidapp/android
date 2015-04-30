package cz.quinix.condroid.ui.listeners;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.service.ReminderManager;
import cz.quinix.condroid.ui.activities.ShowAnnotation;

public class SetReminderListener implements OnClickListener {

	Activity activity;

	int[] minutes = {0, 2, 5, 10, 15, 20, 30};

	private DataProvider provider;

	public SetReminderListener(Activity activity, DataProvider provider) {
		super();
		this.activity = activity;
		this.provider = provider;
	}

	public void onClick(View v) {
		this.invoke(((ShowAnnotation) activity).getAnnotation());
	}

	public void invoke(final Annotation annotation) {
		if (annotation.getStart() == null) {
			Toast.makeText(activity, "Program nemá zadaný čas - nelze nastavit připomenutí!", Toast.LENGTH_LONG).show();
			return;
		}
		AlertDialog.Builder ab = new AlertDialog.Builder(activity);
		ab.setTitle(R.string.remind);
		Integer exReminder = provider.getReminder(annotation.getPid());
		int selected = -1;
		if (exReminder != null) {
			for (int i = 0; i < minutes.length; i++) {
				if (exReminder == minutes[i]) {
					selected = i;
					break;
				}
			}
		}
		ab.setSingleChoiceItems(R.array.remindBefore, selected, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (provider.setReminder(annotation, minutes[which])) {
					ReminderManager.updateAlarmManager(activity);
					Toast.makeText(activity, "Upozornění bylo nastaveno.", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(activity, "Chyba - Nelze nastavit upozornění!", Toast.LENGTH_SHORT).show();
				}
			}
		});
		ab.create().show();
	}

}
