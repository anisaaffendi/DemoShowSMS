package sg.edu.rp.c347.id19030019.demoshowsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView tvSms;
    Button btnRetrieve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSms = findViewById(R.id.tv);
        btnRetrieve = findViewById(R.id.btnRetrieve);

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permissionCheck = PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS);

                if(permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, 0);
                    //stops the action from proceeding further as permission not granted yet
                    return;
                }
                //create all messages URI
                Uri uri = Uri.parse("content://sms");
                //the columns we want date is when the message took place
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                //get Content resolver object
                ContentResolver cr = getContentResolver();
                //the filter string
                String filter ="body LIKE ? AND body LIKE ?";
                //the matches for the ?
                String[] filterArgs = {"%late%", "%min%"};
                //fetch SMS message from built-in Content provider
                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at" + date + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvSms.setText(smsBody);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0: {
                //if request is cancelled, the result arrays are empty
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission was granted.
                    btnRetrieve.performClick();
                }else{
                    //permission denied..notify user
                    Toast.makeText(MainActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
