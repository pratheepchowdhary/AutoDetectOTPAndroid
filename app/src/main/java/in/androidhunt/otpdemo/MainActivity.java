package in.androidhunt.otpdemo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rilixtech.CountryCodePicker;

import in.androidhunt.otp.AutoDetectOTP;

public class MainActivity extends AppCompatActivity {
    AutoDetectOTP  autoDetectOTP;
    CountryCodePicker countryCodePicker;
    AppCompatEditText edtPhoneNumber;
    String testMessage="<#>Your AndroidHunt OTP is: 8686.  ";
    TextView test_message;
    Button copyMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        countryCodePicker = findViewById(R.id.ccp);
        edtPhoneNumber = findViewById(R.id.phone_number_edt);
        countryCodePicker.registerPhoneNumberTextView(edtPhoneNumber);
        autoDetectOTP=   new AutoDetectOTP(this);
        test_message=findViewById(R.id.message);
        copyMessage=findViewById(R.id.copy_message);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
     Intent  intent=new Intent(MainActivity.this,OtpActivity.class);
     intent.putExtra("NO",countryCodePicker.getFullNumberWithPlus());
     startActivity(intent);
            }
        });
        autoDetectOTP.requestPhoneNoHint();
        String message=testMessage+AutoDetectOTP.getHashCode(this);
        test_message.setText("Hash Code For This App is  "+AutoDetectOTP.getHashCode(this)+"\n"+message);
        copyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", testMessage+AutoDetectOTP.getHashCode(MainActivity.this));
                if (clipboard == null) return;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this,"Message Copied To ClipBoard", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AutoDetectOTP.RC_HINT) {
            if (resultCode == RESULT_OK) {
                countryCodePicker.setFullNumber(autoDetectOTP.getPhoneNo(data));

                Snackbar.make(findViewById(R.id.root_view), autoDetectOTP.getPhoneNo(data), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
            }
        }
    }
}
