package rtrp.io.rotoryphone;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;


public class PhonePrompt extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_prompt);

        Intent serviceIntent = new Intent(this, RotoryPhoneService.class);
        startService(serviceIntent);

        if (getIntent().getData() == null) {
            finish();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Button acceptButton = (Button) findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String phoneNumber = tMgr.getLine1Number();
                String responseId = getIntent().getStringExtra("responseId");
                RotoryPhoneService.complete(responseId, "<Say>Please wait while we forward your call.</Say><Dial>" + phoneNumber + "</Dial>");
                finish();
            }
        });

        Button declineButton = (Button) findViewById(R.id.decline_button);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String responseId = getIntent().getStringExtra("responseId");
                PopupMenu menu = new PopupMenu(PhonePrompt.this, v);
                menu.getMenuInflater().inflate(R.menu.decline_menu, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.busy) {
                            RotoryPhoneService.complete(responseId, "<Reject reason=\"busy\"></Reject>");
                        } else if (item.getItemId() == R.id.ignore) {
                            RotoryPhoneService.complete(responseId, "<Reject reason\"rejected\" />");
                        } else {
                            RotoryPhoneService.complete(responseId, "<Say>" + item.getTitle() + "</Say>");
                        }

                        finish();
                        return true;
                    }
                });
                menu.show();
            }
        });

        String phoneNumber = getIntent().getStringExtra("caller");
        if (phoneNumber != null) {
            TextView contactPhone = (TextView) findViewById(R.id.contact_phone);
            contactPhone.setText(PhoneNumberUtils.formatNumber(phoneNumber));
        }
        TextView contactInfo = (TextView) findViewById(R.id.contact_info);
        Map<String, String> contactDisplay = getContactDisplayByNumber(phoneNumber);
        String name;
        if (contactDisplay.get("name") != null) {
            name = contactDisplay.get("name");
        } else {
            name = getIntent().getStringExtra("callerName");
        }
        contactInfo.setText(name);
        final CircularImageView contactImage = (CircularImageView) findViewById(R.id.contact_image);
        contactImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.call_background));
        contactImage.setBorderWidth(5);
        contactImage.setShadowWidth(50);
        getImage(phoneNumber, contactDisplay).onSuccess(new Continuation<Bitmap, Object>() {
            @Override
            public Object then(Task<Bitmap> bitmapTask) throws Exception {
                contactImage.setImageBitmap(bitmapTask.getResult());
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    public Map<String, String> getContactDisplayByNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "?";

        ContentResolver contentResolver = getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[]{BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.PHOTO_URI}, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
                Map<String, String> result = new HashMap<String, String>();
                result.put("contactId", contactId);
                result.put("name", name);
                return result;
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return new HashMap<String, String>();
    }

    public Task<Bitmap> getImage(String phoneNumber, final Map<String, String> contactDisplay) {
        if (phoneNumber == null) {
            return Task.forResult(null);
        }
        final String md5 = MD5(phoneNumber);
        return Task.callInBackground(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                if (contactDisplay.get("contactId") != null) {
                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactDisplay.get("contactId")));
                    InputStream stream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), uri);
                    if (stream != null) {
                        return BitmapFactory.decodeStream(stream);
                    }
                }
                URL url = new URL("http://www.gravatar.com/avatar/" + md5 + "?d=wavatar");
                return BitmapFactory.decodeStream(url.openStream());
            }
        });
    }

    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.phone_prompt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
