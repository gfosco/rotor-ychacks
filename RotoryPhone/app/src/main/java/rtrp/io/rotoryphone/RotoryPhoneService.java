package rtrp.io.rotoryphone;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bolts.Continuation;
import bolts.Task;

public class RotoryPhoneService extends RotorService {
    private static final String TWIML_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "<Response>\n" +
            "%REPLACE%\n" +
            "</Response>";

    private static Map<String, Task<String>.TaskCompletionSource> awaitingResults =
            new HashMap<String, Task<java.lang.String>.TaskCompletionSource>();

    public RotoryPhoneService() {
    }

    @Override
    protected String getAlias() {
        return "RotoryPhone";
    }

    @Override
    protected Task<JSONObject> onPost(JSONObject parameters) throws JSONException {
        Task<String>.TaskCompletionSource tcs = Task.create();
        awaitingResults.put(parameters.getString("responseId"), tcs);
        Intent promptIntent = new Intent(this, PhonePrompt.class);
        promptIntent.setData(Uri.parse("request://" + parameters.getString("responseId")));
        promptIntent.putExtra("responseId", parameters.getString("responseId"));
        promptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String body = parameters.getString("body");
        Uri bodyUri = Uri.parse("foo://bar?" + body);

        promptIntent.putExtra("caller", bodyUri.getQueryParameter("Caller"));
        promptIntent.putExtra("callerName", bodyUri.getQueryParameter("CallerName"));

        startActivity(promptIntent);

        return tcs.getTask().onSuccess(new Continuation<String, JSONObject>() {
            @Override
            public JSONObject then(Task<String> stringTask) throws Exception {
                JSONObject obj = new JSONObject();
                obj.put("body", TWIML_TEMPLATE.replace("%REPLACE%", stringTask.getResult()));
                obj.put("contentType", "text/xml");
                return obj;
            }
        });
    }

    public static void complete(String requestId, String result) {
        awaitingResults.get(requestId).trySetResult(result);
    }
}
