package rtrp.io.simplehttpdemo;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by david on 8/2/14.
 */
public class Rotor {
    public static interface RequestListener {
        public Task<JSONObject> onRequest(JSONObject requestObject) throws JSONException;
    }

    private final Socket socket;
    private RequestListener getListener;
    private RequestListener postListener;
    private String alias;

    public Rotor() {
        try {
            socket = IO.socket("http://rtrp.io");
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("RotoryPhone", "I'm here!");
                }
            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("RotoryPhone", "error");
                }
            }).on("id", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("RotoryPhone", "id: " + args[0]);
                    applyAlias();
                }
            }).on("get", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("RotoryPhone", "GET: ");
                    for (Object arg : args) {
                        Log.d("RotoryPhone", "" + arg);
                    }

                    try {
                        JSONObject obj = (JSONObject) args[0];
                        final String responseId = obj.getString("responseId");
                        Task<JSONObject> response;
                        if (getListener != null) {
                            response = getListener.onRequest(obj);
                        } else {
                            response = Task.forResult(new JSONObject());
                        }
                        response.onSuccess(new Continuation<JSONObject, Void>() {
                            @Override
                            public Void then(Task<JSONObject> jsonObjectTask) throws Exception {
                                jsonObjectTask.getResult().put("responseId", responseId);
                                socket.emit("response", jsonObjectTask.getResult());
                                return null;
                            }
                        });
                    } catch (JSONException e) {

                    }
                }
            }).on("post", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("RotoryPhone", "POST: " + args);
                    for (Object arg : args) {
                        Log.d("RotoryPhone", "" + arg);
                    }

                    try {
                        JSONObject obj = (JSONObject) args[0];
                        final String responseId = obj.getString("responseId");
                        Task<JSONObject> response;
                        if (postListener != null) {
                            response = postListener.onRequest(obj);
                        } else {
                            response = Task.forResult(new JSONObject());
                        }
                        response.onSuccess(new Continuation<JSONObject, Void>() {
                            @Override
                            public Void then(Task<JSONObject> jsonObjectTask) throws Exception {
                                jsonObjectTask.getResult().put("responseId", responseId);
                                socket.emit("response", jsonObjectTask.getResult());
                                return null;
                            }
                        });
                    } catch (JSONException e) {

                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void connect() {
        socket.connect();
    }

    public void setGetListener(RequestListener listener) {
        this.getListener = listener;
    }

    public void setPostListener(RequestListener listener) {
        this.postListener = listener;
    }

    private void applyAlias() {
        if (alias != null) {
            socket.emit("alias", alias);
        }
    }

    public void setAlias(String alias) {
        this.alias = alias;
        applyAlias();
    }
}
