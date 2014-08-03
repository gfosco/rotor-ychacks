package rtrp.io.authrotor;

import android.content.Intent;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bolts.Continuation;
import bolts.Task;

public class AuthRotorService extends RotorService {
    private static Map<String, Task<Boolean>.TaskCompletionSource> awaitingResults =
            new HashMap<String, Task<Boolean>.TaskCompletionSource>();

    public AuthRotorService() {
    }

    @Override
    protected String getAlias() {
        return "AuthRotor";
    }

    @Override
    protected Task<JSONObject> onGet(JSONObject parameters) throws JSONException {
        Task<Boolean>.TaskCompletionSource tcs = Task.create();
        awaitingResults.put(parameters.getString("responseId"), tcs);
        Intent promptIntent = new Intent(this, AuthActivity.class);
        promptIntent.setData(Uri.parse("request://" + parameters.getString("responseId")));
        promptIntent.putExtra("responseId", parameters.getString("responseId"));
        promptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(promptIntent);

        return tcs.getTask().onSuccess(new Continuation<Boolean, JSONObject>() {
            @Override
            public JSONObject then(Task<Boolean> booleanTask) throws Exception {
                JSONObject obj = new JSONObject();
                obj.put("body", "" + booleanTask.getResult());
                obj.put("contentType", "text/xml");
                return obj;
            }
        });
    }

    public static void complete(String requestId, boolean result) {
        awaitingResults.get(requestId).trySetResult(result);
    }
}
