package com.dam.daniel.playmatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MySMSBroadcastReceiver extends BroadcastReceiver {

    private MySMSBroadcastReceiver.OTPReceiveListener otpReceiver;

    /**
     * Init OTP Listenner
     *
     * @param receiver
     */
    public final void initOTPListener(MySMSBroadcastReceiver.OTPReceiveListener receiver) {
        this.otpReceiver = receiver;
    }

    /**
     * Broadcast Receiver
     *
     * @param context
     * @param intent
     */
    public void onReceive(Context context, Intent intent) {
        if(SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            MySMSBroadcastReceiver.OTPReceiveListener otp;
            switch (status.getStatusCode()) {
                // STATUS SUCCESS
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String message = (String)extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    // Extract one-time code as per the message

//                    Pattern pattern = Pattern.compile("(\\d{6})");
//                    Matcher matcher = pattern.matcher(message);

                    // Extract one-time code from the message and complete verification
                    String value = "";
                    try{
                        value = message.substring(30, 36);
                    }catch (NullPointerException e){
                        System.out.println("El mensaje no ha llegado!");
                    }

//                    if (matcher.find()) {
//                        System.out.println("Pasa 2 - Matcher Find: " + matcher.toString());
//                        System.out.println("Matcher group: " + matcher.group(1));
//                        value = matcher.group(1);
//                    }

                    // Send to Interface "onOTPReceived()" the code of message sended
                    otp = this.otpReceiver;
                    if (this.otpReceiver != null) {
                        otp.onOTPReceived(value);
                    }
                    break;
                // STATUS TIMEOUT
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    //Send your message to the respected activity
                    otp = this.otpReceiver;
                    if (this.otpReceiver != null) {
                        otp.onOTPTimeOut();
                    }
                    break;
            }
        }
    }

    /**
     * Interface to Implement It into MainLoginFragment Where we Wait [for Message Response / Timeout]
     */
    interface OTPReceiveListener {
        void onOTPReceived(String message);

        void onOTPTimeOut();
    }

}
