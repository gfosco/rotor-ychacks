package rtrp.io.simplehttpdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

import bolts.Task;


public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        final EditText response = (EditText)findViewById(R.id.responseField);
        final TextView url = (TextView)findViewById(R.id.urlField);

        Task.callInBackground(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Rotor r = new Rotor();
                r.setAlias("SimpleHttpDemo");
                r.setGetListener(new Rotor.RequestListener() {
                    @Override
                    public Task<JSONObject> onRequest(JSONObject requestObject) throws JSONException {
                        JSONObject result = new JSONObject();
                        result.put("body", response.getText().toString());
                        return Task.forResult(result);
                    }
                });
                r.connect();

                url.setText("http://rtrp.io/client/SimpleHttpDemo");
                return null;
            }
        });
    }
}
