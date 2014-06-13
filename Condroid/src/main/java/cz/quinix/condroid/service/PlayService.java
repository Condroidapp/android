package cz.quinix.condroid.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by Jan on 13. 6. 2014.
 */
public class PlayService implements ServiceConnection {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
