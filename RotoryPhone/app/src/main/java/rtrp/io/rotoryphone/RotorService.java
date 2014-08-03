package rtrp.io.rotoryphone;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

import bolts.Task;

public abstract class RotorService extends Service {
    private final Rotor rotor;
    public RotorService() {
        rotor = new Rotor();
        rotor.setGetListener(new Rotor.RequestListener() {
            @Override
            public Task<JSONObject> onRequest(JSONObject requestObject) throws JSONException {
                return onGet(requestObject);
            }
        });
        rotor.setPostListener(new Rotor.RequestListener() {
            @Override
            public Task<JSONObject> onRequest(JSONObject requestObject) throws JSONException {
                return onPost(requestObject);
            }
        });
    }

    protected abstract String getAlias();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                rotor.connect();
                rotor.setAlias(getAlias());
                return null;
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    protected Task<JSONObject> onGet(JSONObject parameters) throws JSONException {
        return Task.forResult(new JSONObject());
    }

    protected Task<JSONObject> onPost(JSONObject parameters) throws JSONException {
        return Task.forResult(new JSONObject());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
