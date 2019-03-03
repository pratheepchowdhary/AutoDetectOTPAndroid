package in.androidhunt.otpdemo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;
import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;
import in.androidhunt.otp.AutoDetectOTP;

public class OtpActivity extends AppCompatActivity {
   private String otpnN0;
    TextView timer;
    AutoDetectOTP autoDetectOTP;
   private OtpTextView otpTextView;
   CountDownTimer countDownTimer=   new CountDownTimer(180000, 1000) {
       @Override
       public void onTick(long millisUntilFinished) {
           timer.setText(millisecondsToTime(millisUntilFinished));
       }
       @Override
       public void onFinish() {
           timer.setText("");
       }
   };
    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(otpnN0!=null&&otpnN0.equals("1234")){
                otpTextView.showSuccess();
                countDownTimer.cancel();
            }
            else {
                otpTextView.showError();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        autoDetectOTP=new AutoDetectOTP(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView phoneview=findViewById(R.id.phone_);
        timer=  findViewById(R.id.timer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(null);
        AppBarLayout app= findViewById(R.id.appbar);
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
            app.setOutlineProvider(null);
        }
        findViewById(R.id.fab_previos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        String no=getIntent().getStringExtra("NO");
        if(no!=null){
            phoneview.append(no);
        }
        otpTextView = findViewById(R.id.otp_view);
        otpTextView.requestFocusOTP();
        otpTextView.setOtpListener(new OTPListener() {;

            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                otpnN0=otp;
                Toast.makeText(OtpActivity.this,"The OTP is " + otp, Toast.LENGTH_SHORT).show();
                handler.postDelayed(runnable,100);

            }
        });
      countDownTimer.start();
      autoDetectOTP.startSmsRetriver(new AutoDetectOTP.SmsCallback() {
          @Override
          public void connectionfailed() {
              Toast.makeText(OtpActivity.this,"Failed", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void connectionSuccess(Void aVoid) {
              Toast.makeText(OtpActivity.this,"Success", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void smsCallback(String sms) {
              if(sms.contains(":") && sms.contains(".")) {
                  String otp = sms.substring( sms.indexOf(":")+1 , sms.indexOf(".") ).trim();
                  otpTextView.setOTP(otp);
                  Toast.makeText(OtpActivity.this,"The OTP is " + otp, Toast.LENGTH_SHORT).show();
              }
          }
      });
      findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
              ClipData clip = ClipData.newPlainText("label", AutoDetectOTP.getHashCode(OtpActivity.this));
              if (clipboard == null) return;
              clipboard.setPrimaryClip(clip);
              Toast.makeText(OtpActivity.this,AutoDetectOTP.getHashCode(OtpActivity.this), Toast.LENGTH_SHORT).show();
          }
      });
    }
    @Override
    protected void onDestroy() {

        if (countDownTimer != null) {
            countDownTimer.onFinish();
            countDownTimer.cancel();
        }
        super.onDestroy();
    }
    @Override
    protected void onStop() {
        super.onStop();
        autoDetectOTP.stopSmsReciever();
        if (countDownTimer != null) {
            countDownTimer.onFinish();
            countDownTimer.cancel();
        }
    }
    private String millisecondsToTime(long milliseconds) {

        return "Time remaining " + String.format("%d : %d ",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }
}
