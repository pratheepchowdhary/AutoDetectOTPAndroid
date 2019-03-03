package in.androidhunt.otp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by Pratheep Chowdhary on 02,March,2019
 */
public class AutoDetectOTP {
    public static final int RC_HINT = 1000;
    private SmsCallback smsCallback;
    private GoogleApiClient googleApiClient;
    private Context context;
    private BroadcastReceiver chargerReceiver;
    private AppCompatActivity appCompatActivity;
    private IntentFilter intentFilter;
    public AutoDetectOTP(Context context) {
        this.appCompatActivity = (AppCompatActivity) context;
        this.context = appCompatActivity.getApplicationContext();
    }

    public void requestPhoneNoHint() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(appCompatActivity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .addApi(Auth.CREDENTIALS_API)
                .build();
        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent =
                Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest);
        try {
            appCompatActivity.startIntentSenderForResult(intent.getIntentSender(), RC_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e("PHONE_HINT", "Could not start hint picker Intent", e);
        }
    }

    public void requestPhoneNoHint(final Callback callback) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(appCompatActivity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .addApi(Auth.CREDENTIALS_API)
                .build();
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        callback.connectionSuccess(bundle);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        callback.connectionSuspend(i);
                    }
                })
                .enableAutoManage(appCompatActivity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        callback.connectionfailed(connectionResult);
                    }
                })
                .addApi(Auth.CREDENTIALS_API)
                .build();
        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                .setPhoneNumberIdentifierSupported(true)
                .build();


        PendingIntent intent =
                Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest);
        try {
            appCompatActivity.startIntentSenderForResult(intent.getIntentSender(), RC_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e("PHONE_HINT", "Could not start hint picker Intent", e);
        }
    }

    public void startSmsRetriver(final SmsCallback smsCallback) {
        registerReceiver();
        this.smsCallback = smsCallback;
        // Get an instance of SmsRetrieverClient, used to start listening for a matching
// SMS message.
        SmsRetrieverClient client = SmsRetriever.getClient(context);

// Starts SmsRetriever, which waits for ONE matching SMS message until timeout
// (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
// action SmsRetriever#SMS_RETRIEVED_ACTION.
        Task<Void> task = client.startSmsRetriever();
// Listen for success/failure of the start Task. If in a background thread, this
// can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("SMSRE","success");
                smsCallback.connectionSuccess(aVoid);
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                smsCallback.connectionfailed();
            }
        });

    }

    public String getPhoneNo(Intent data) {
        Credential cred = data.getParcelableExtra(Credential.EXTRA_KEY);
        return cred.getId();

    }

    private void registerReceiver() {
//        filter to receive SMS
        intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);

//        receiver to receive and to get otp from SMS
        chargerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                    Bundle extras = intent.getExtras();
                    Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                    switch (status.getStatusCode()) {
                        case CommonStatusCodes.SUCCESS:
                            // Get SMS message contents
                            String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                            // Extract one-time code from the message and complete verification
                            // by sending the code back to your server for SMS authenticity.
                            smsCallback.smsCallback(message);
                            stopSmsReciever();
                            break;
                        case CommonStatusCodes.TIMEOUT:
                            // Waiting for SMS timed out (5 minutes)
                            smsCallback.connectionfailed();
                            break;

                    }
                }
            }
        };
        appCompatActivity.getApplication().registerReceiver(chargerReceiver, intentFilter);
    }

    public void stopSmsReciever() {
        try {
            appCompatActivity.getApplicationContext().unregisterReceiver(chargerReceiver);
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    ;

    public interface Callback {
        void connectionfailed(ConnectionResult connectionResult);
        void connectionSuspend(int i);
        void connectionSuccess(Bundle bundle);
    }

    public interface SmsCallback {
        void connectionfailed();
        void connectionSuccess(Void aVoid);
        void smsCallback(String sms);
    }
    public static String getHashCode(Context context){
        AppSignatureHelper appSignature = new AppSignatureHelper(context);
        Log.e(" getAppSignatures ",""+appSignature.getAppSignatures());
        return appSignature.getAppSignatures().get(0);

    }


}
