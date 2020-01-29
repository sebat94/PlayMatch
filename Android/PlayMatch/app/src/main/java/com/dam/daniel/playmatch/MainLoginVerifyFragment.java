package com.dam.daniel.playmatch;


import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dam.daniel.playmatch.models.User;
import com.dam.daniel.playmatch.services.AuthService;
import com.dam.daniel.playmatch.services.UserService;
import com.dam.daniel.playmatch.utils.ActivityUtils;
import com.dam.daniel.playmatch.utils.ToastUtils;
import com.dam.daniel.playmatch.utils.VolleySupport;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainLoginVerifyFragment extends Fragment implements MySMSBroadcastReceiver.OTPReceiveListener {

    // Variables
    private String telephoneNumber;

    // Views
    TextView idTextViewTelephoneNumber;
    EditText idEditTextOneTimeCode;
    Button idButtonVerify;

    // SMS Retriever
    private final MySMSBroadcastReceiver smsBroadcast = new MySMSBroadcastReceiver();


    public MainLoginVerifyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // "getArguments()" is a method that belongs to Fragment class to get parameters sended to it
        readBundle(getArguments());
    }

    /**
     * READ BUNDLE
     * @param bundle
     */
    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            telephoneNumber = bundle.getString("telephoneNumber");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_login_verify, container, false);

        // Init
        idTextViewTelephoneNumber = (TextView) view.findViewById(R.id.idTextViewTitle);
        idEditTextOneTimeCode = (EditText) view.findViewById(R.id.idEditTextOneTimeCode);
        idButtonVerify = (Button) view.findViewById(R.id.idButtonVerify);

        // Show TelephoneNumber
        idTextViewTelephoneNumber.setText("Introduce el código que te hemos enviado al " + telephoneNumber);

        // SMS Retriever API
        smsRetriever();

        // Event Handler
        eventHandlers();

        return view;
    }

    /**
     * Event Handlers   -   Login/Register
     */
    private void eventHandlers(){
        // OnClick Button Verify OneTimeCode
        idButtonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CREATE USER
                postAuthService();
            }
        });
    }

    /**
     * Authentication Of TelephoneNumber With a OneTimeCode given by message of SMS Retriever
     */
    private void postAuthService(){
        final AuthService authService = new AuthService(getActivity());
        // CREATE USER
        JSONObject postParams = new JSONObject();
        try {
            postParams.put("telephoneNumber", telephoneNumber);                     // TODO: Está llegando NULL a Node.js, comprobar enviar usuario existente
            postParams.put("oneTimeCode", idEditTextOneTimeCode.getText());
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error Generating JSONObject With Post Params");
        }
        authService.smsVerifyOneTimeCode(
                new VolleySupport.ServerCallbackEmpty() {
                    @Override
                    public void onSuccess() {
                        System.out.println("Teléfono Verificado!");

                        JSONObject postParams = new JSONObject();
                        JSONObject userParams = new JSONObject();
                        try {
                            userParams.put("telephoneNumber", telephoneNumber);
                            postParams.put("user", userParams);
                            postRegisterUser(postParams);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("Error Login/Register User");
                        }
                    }
                },
                new VolleySupport.ServerCalbackError() {
                    @Override
                    public void onError(String errorMessage) {
                        System.out.println("ErrorMessage: " + errorMessage);
                    }
                },
                postParams
        );
    }

    /**
     * SMS Retriever API
     *      1) Start SMS Listenner
     *      2) Init OTP ( One Time Password )
     */
    private void smsRetriever(){
        // SMS Retriever
        this.startSMSListener();
        this.smsBroadcast.initOTPListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        getActivity().getApplicationContext().registerReceiver(smsBroadcast, intentFilter);
    }

    /**
     * Creates the User with the Values Given
     * @param postParams
     */
    private void postRegisterUser(JSONObject postParams){
        UserService userService = new UserService(getActivity());
        userService.postRegisterUser(
                new VolleySupport.ServerCallbackUser() {
                    @Override
                    public void onSuccess(User user) {
                        // If User Is Active And Is Not Disabled, it means that the user is making Login
                        if(user.isActive() && user.getDisabled().equals("null")){       // TODO: When Response is parsed, the null it's transformed to "null" becaus the propertie Disabled is a String, Need to be @Nullable, but don't work as expected
                            // Go Into App
                            try{
                                ActivityUtils.setRoot(getActivity(), MatchActivity.class);
                            }catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        // If user Is Disabled, it means that is New User or is Coming Back after Unsubscribe
                        }else{
                            // Open MainRequestUserDataFragment To Set New User Details
                            MainRequestUserDataFragment mainRequestUserDataFragment = new MainRequestUserDataFragment();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container_ActivityMain, mainRequestUserDataFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    }
                },
                new VolleySupport.ServerCalbackError() {
                    @Override
                    public void onError(String errorMessage) {
                        System.out.println("ErrorMessage: " + errorMessage);
                        ToastUtils.showToastError(getActivity(), errorMessage);
                    }
                },
                postParams
        );
    }

    /**
     * Start Listening for SMS
     * When we are ready to verify the user’s phone number, get an instance of the SmsRetrieverClientobject.
     * Will call startSmsRetriever and attach success and failure listeners to the SMS retrieval task
     */
    private void startSMSListener() {
        // Get an instance of SmsRetrieverClient, used to start listening for a matching SMS message.
        SmsRetrieverClient client = SmsRetriever.getClient(getActivity());

        // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
        // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
        // action SmsRetriever#SMS_RETRIEVED_ACTION.
        Task<Void> task = client.startSmsRetriever();

        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent
                Log.d("Success","Successfully started retriever");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // Failed to start retriever, inspect Exception for more details
                Log.e("Error", "Failed to start retriever");
            }
        });
    }

    /**
     * On SMS Received
     * @param otp
     */
    public void onOTPReceived(String otp) {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(smsBroadcast);
        // If the OneTimeCode was received successfully then set it into EditText And Do the petition automatically to check if this OneTimeCode with this telephoneNumber are corrects
        if(otp != null && otp != ""){
            // Set The OneTimeCode in EditText
            idEditTextOneTimeCode.setText(otp);
            // Check if OneTimeCode is Valid
            postAuthService();
        }
    }

    /**
     * On Timeout Received
     */
    public void onOTPTimeOut() {
        System.out.println("Timeout - SMS retriever API Timeout");
    }

}
