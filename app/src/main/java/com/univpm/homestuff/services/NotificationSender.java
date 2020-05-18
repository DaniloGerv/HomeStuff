package com.univpm.homestuff.services;

import android.app.Activity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.univpm.homestuff.utilities.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.HashMap;
import java.util.Map;


public class NotificationSender {

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA9gpW13M:APA91bFdwc176-4Ipp0fziAIdJpidrhGEShk_xDj88pMyS-rQFXzV--ygA_-KgaqQC0yTTgo9OP6ThfWR7Jo512p4fpzdP9IV9SH0Kvv9bssyrnWmtcSDNQ1iDzfDZDF-guXDcp59JdE";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    public void send(Activity sender,String title,String message,String tokenReceiver)
    {
        TOPIC = tokenReceiver; //topic must match with what the receiver subscribed to
        NOTIFICATION_TITLE = title;
        NOTIFICATION_MESSAGE = message;

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);

            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        Log.d("USER","OK");
        sendNotification(notification,sender);
    }

    private void sendNotification(JSONObject notification, Activity sender) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                            Log.d("USER", response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("USER",error.getMessage());

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(sender.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
