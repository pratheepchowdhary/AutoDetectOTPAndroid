AutoDetectOTPAndroid
===============

 [ ![Download](https://api.bintray.com/packages/pratheepchowdhary/maven/AutoDetectOTPAndroid/images/download.svg) ](https://bintray.com/pratheepchowdhary/maven/AutoDetectOTPAndroid/_latestVersion)
 [![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat)](https://bintray.com/pratheepchowdhary/maven/AutoDetectOTPAndroid/_latestVersion)

[AutoDetectOTPAndroid](https://www.androidhunt.in)  is an Android libary to Integrate Auto Detect OTP With Out Sms Permissions Required To Your Android Appliaction Essaily With less Stuff.


Usage
-----

**1.** Add the following to your **build.gradle**.
```groovy
dependencies {
  implementation 'in.androidhunt.otp:AutoDetectOTPAndroid:1.0.0'
}
```
**2.** Example Message Format and Generating Hash Code
```java
       //Example Message

        <#>Your AndroidHunt OTP is: 8686. ynfd/rIwy/+

        ynfd/rIwy/+  is the hash code of your Application

```
```java
       //There are Two  Ways To Genearate Hash Code One Is By Using Command Line
        keytool -exportcert -alias YOUR_KEYSTORE_ALIAS -keystore YOUR_KEYSTORE_FILE | xxd -p | tr -d "[:space:]" | echo -n com.example.myapp `cat` | sha256sum | tr -d "[:space:]-" | xxd -r -p | base64 | cut -c1-11

```
```java
      //Other Way  By Doing Programatically Inside The Application.
      //Here You get Hash code by Using With Our Library
      String hashCode = AutoDetectOTP.getHashCode(this);
      // above string like this "ynfd/rIwy/+"

```
<div align="center">
        <img width="30%" src="https://github.com/pratheepchowdhary/AutoDetectOTPAndroid/blob/master/screenshots/Screenshot_1.png" alt="Obtain the user's phone number" title="Obtain the user's phone number"</img>
</div>

**3.** Obtain the user's phone number Using hint picker with prompt
```java
        AutoDetectOTP autoDetectOTP=new AutoDetectOTP(this);
        //To display List Of Phone No Linked with Google Account  By showing Dialog as Shown Above
        autoDetectOTP.requestPhoneNoHint();

        //we can get phone number onActivity Result when User Picked From prompt
           @Override
           protected void onActivityResult(int requestCode, int resultCode, Intent data) {
               super.onActivityResult(requestCode, resultCode, data);
               if (requestCode == AutoDetectOTP.RC_HINT) {
                   if (resultCode == RESULT_OK) {
                   String phoneNo=autoDetectOTP.getPhoneNo(data);

                   }
                   else {
                   //nothing selected
                   }

               }
           }

```
<div align="center">
        <img width="40%" src="https://github.com/pratheepchowdhary/AutoDetectOTPAndroid/blob/master/screenshots/Screenshot_2.png" alt="" title=""></img>
        <img height="0" width="8px">
         <img width="40%" src="https://github.com/pratheepchowdhary/AutoDetectOTPAndroid/blob/master/screenshots/Screenshot_3.png" alt="Verification" title="OTP Verification"></img>
</div>

**4.** Auto Detect OTP Usage
```java
        AutoDetectOTP autoDetectOTP=new AutoDetectOTP(this);
        autoDetectOTP.startSmsRetriver(new AutoDetectOTP.SmsCallback() {
                  @Override
                  public void connectionfailed() {

                      //do something here on failure
                      Toast.makeText(OtpActivity.this,"Failed", Toast.LENGTH_SHORT).show();
                  }

                  @Override
                  public void connectionSuccess(Void aVoid) {
                      //do something here on success
                      Toast.makeText(OtpActivity.this,"Success", Toast.LENGTH_SHORT).show();
                  }

                  @Override
                  public void smsCallback(String sms) {
                  //  Get Sms Message Here pick what want from message here
                      if(sms.contains(":") && sms.contains(".")) {
                          String otp = sms.substring( sms.indexOf(":")+1 , sms.indexOf(".") ).trim();
                          //otpTextView.setOTP(otp);
                          Toast.makeText(OtpActivity.this,"The OTP is " + otp, Toast.LENGTH_SHORT).show();
                      }
                  }
              });
              -----------------
              // Finally Stop Sms Retriver BY Calling  autoDetectOTP.stopSmsReciever() On Stop As  Shown Below
               @Override
                  protected void onStop() {
                      super.onStop();
                      autoDetectOTP.stopSmsReciever();
                  }

```
```
Licences
--------
    Copyright 2019 Pratheep Chowdhary.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```