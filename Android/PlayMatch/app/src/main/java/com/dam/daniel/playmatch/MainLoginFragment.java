package com.dam.daniel.playmatch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dam.daniel.playmatch.models.User;
import com.dam.daniel.playmatch.services.AuthService;
import com.dam.daniel.playmatch.services.UserService;
import com.dam.daniel.playmatch.utils.ToastUtils;
import com.dam.daniel.playmatch.utils.VolleySupport;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dam.daniel.playmatch.utils.AppSignatureHelper;
import com.dam.daniel.playmatch.MySMSBroadcastReceiver.OTPReceiveListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MainLoginFragment extends Fragment implements CountryCode.OnFragmentInteractionListener{   // implements OTPReceiveListener

    // Views
    EditText countryAndIsoCode;
    EditText idEditTextPhone;
    Button idButtonLogin;

    // SMS Retriever
    private GoogleApiClient mCredentialsApiClient;
    private final int RC_HINT = 2;

    //private final MySMSBroadcastReceiver smsBroadcast = new MySMSBroadcastReceiver();


    public MainLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_login, container, false);

        // Init
        countryAndIsoCode = (EditText) view.findViewById(R.id.idEditTextCountryAndIsoCode);
        idEditTextPhone = (EditText) view.findViewById(R.id.idEditTextPhone);
        idButtonLogin = (Button) view.findViewById(R.id.idButtonLogin);
        // Set Default Focus in "idEditTextPhone", because the "setOnFocusChangeListener" listener of EditText need to have Focus over EditText
        idEditTextPhone.requestFocus();

        // SMS Retriever API
        smsRetriever();
        // Event Handler
        eventHandlers();

        return view;
    }

    /**
     * Event Handlers
     *      1) Country and Iso Codes
     *      2) Register
     */
    private void eventHandlers(){
        // OnClick on EditText of Country and Iso Codes
        countryAndIsoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryCode fragmentCountryCode = new CountryCode();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container_ActivityMain, fragmentCountryCode);   // ( idFrameLayout, fragmentToShow, "tag" )
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        idButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If SMS Retriever Don't Work, or Device don't support it, we need to introduce number manually and click the button (ex: +34680731117)
                // TODO: Validar numero antes de mandar
                smsRetrieverSendRequest();
            }
        });
    }

    /**
     * SMS Retriever API
     *      1) Init Credentials for GoogleApiClient
     *      2) Show Modal Chooser to mobile number
     */
    private void smsRetriever(){
        // SMS Retriever
        this.mCredentialsApiClient = new GoogleApiClient.Builder(getActivity()).addApi(Auth.CREDENTIALS_API).build();
        this.requestHint();
//        this.startSMSListener();
//        this.smsBroadcast.initOTPListener(this);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
//        getActivity().getApplicationContext().registerReceiver(smsBroadcast, intentFilter);
    }

    /**
     * API CALL - Request to Node.js
     *      1) Get Hash from AppSignatureHelper
     *      2) Do Api Call
     */
    private void smsRetrieverSendRequest(){
        //Used to generate hash signature
        ArrayList<String> hashMap = new AppSignatureHelper(getActivity()).getAppSignatures();
        String hash = hashMap.get(0);
        if(hash != null){
            // SMS Request
            JSONObject postParams = new JSONObject();
            try {
                postParams.putOpt("hash", hash);
                postParams.putOpt("telephoneNumber", idEditTextPhone.getText());
                System.out.println("POSTPARAMS - hash:" + hash + ", movil: " + idEditTextPhone.getText());
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("Error Generating JSONObject With Post Params");
            }

            AuthService authService = new AuthService(getActivity());
            authService.smsAskOneTimeCode(
                    new VolleySupport.ServerCallbackEmpty(){
                        @Override
                        public void onSuccess() {
                            System.out.println("Petition of OneTimeCode to the Server Successfully, Waiting SMS...");
                            // Open MainLoginVerifyFragment To Put The OnTimeCode and send it to the Server to Validate
                            MainLoginVerifyFragment mainLoginVerifyFragment = new MainLoginVerifyFragment();
                            // Pass a Bundle with "telephoneNumber" just in case we need to click Resend and make a new OneTimeCode Request
                            Bundle bundle = new Bundle();
                            bundle.putString("telephoneNumber", idEditTextPhone.getText().toString());
                            mainLoginVerifyFragment.setArguments(bundle);
                            // Load Fragment
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container_ActivityMain, mainLoginVerifyFragment, "FRAGMENT_SMS_RETRIEVER_VERIFY");
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    },
                    new VolleySupport.ServerCalbackError() {
                        @Override
                        public void onError(String errorMessage) {
                            System.out.println(errorMessage);
                        }
                    },
                    postParams
            );
        }
    }

    /**
     * Construct a request for phone numbers and show the picker
     */
    private void requestHint() {
        // TODO: El SMS Retriever no funciona en todos los dispositivos, investigar como desactivar el Popup que abre y se cierra rápidamente donde debería mostrar los números de la SIM

        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(mCredentialsApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), RC_HINT, null, 0, 0, 0, null);
        } catch (Exception e) {
            Log.e("Error In getting Msg", e.getMessage());
        }
    }

    /**
     * Once the user selects the phone number, that phone number is returned to our app in the onActivityResult()
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_HINT) {

            if (resultCode == getActivity().RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);

                if (credential != null) {
                    final String unformattedPhone = credential.getId();

                    // TODO: PORBAR TELEFONO MAMA
                    System.out.print("-----------------> CREDENCIALES");
                    System.out.println("getId: " + credential.getId());
                    System.out.println("getAccountType: " + credential.getAccountType());
                    System.out.println("getFamilyName: " + credential.getFamilyName());
                    System.out.println("getGivenName: " + credential.getGivenName());
                    System.out.println("getIdTokens: " + credential.getIdTokens());
                    System.out.println("getName: " + credential.getName());
                    System.out.println("getPassword: " + credential.getPassword());
                    System.out.println("getProfilePictureUri: " + credential.getProfilePictureUri());

                    // TODO: Con el número de teléfono tenemos el prefijo (+34....), Buscar coincidencia de prefijo y setearla en el otro EditText
                    idEditTextPhone.setText(unformattedPhone);
                    idEditTextPhone.setEnabled(true);

                    // TODO: API CALL FindByTelephoneNumber
                    // TODO: Si no existe, entonces llamamos al método "smsRetrieverSendRequest()" para hacer confirmación de teléfono.


                    // When the "onActivityResult" gets the number, we send request to Node.js with the needed credentials (hash, telephoneNumber)
                    smsRetrieverSendRequest();
                }else{
                    System.out.println("Credentials NULL");
                }
            }
        }
    }

//    /**
//     * Start Listening for SMS
//     * When we are ready to verify the user’s phone number, get an instance of the SmsRetrieverClientobject.
//     * Will call startSmsRetriever and attach success and failure listeners to the SMS retrieval task
//     */
//    private void startSMSListener() {
//        // Get an instance of SmsRetrieverClient, used to start listening for a matching SMS message.
//        SmsRetrieverClient client = SmsRetriever.getClient(getActivity());
//
//        // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
//        // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
//        // action SmsRetriever#SMS_RETRIEVED_ACTION.
//        Task<Void> task = client.startSmsRetriever();
//
//        // Listen for success/failure of the start Task. If in a background thread, this
//        // can be made blocking using Tasks.await(task, [timeout]);
//        task.addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                // Successfully started retriever, expect broadcast intent
//                Log.d("Success","Successfully started retriever");
//            }
//        });
//
//        task.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(Exception e) {
//                // Failed to start retriever, inspect Exception for more details
//                Log.e("Error", "Failed to start retriever");
//            }
//        });
//    }
//
//    /**
//     * On SMS Received
//     * @param otp
//     */
//    public void onOTPReceived(String otp) {
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(smsBroadcast);
//        ToastUtils.showToastInfo(getActivity(), "Your OTP is: " + otp);
//    }
//
//    /**
//     * On Timeout Received
//     */
//    public void onOTPTimeOut() {
//        ToastUtils.showToastError(getActivity(), "Timeout - SMS retriever API Timeout");
//    }

    /**
     * The interface "implements CountryCode.OnFragmentInteractionListener" forces us to implement method "onFragmentInteraction"
     * We need it to comunnicate the ACTIVITY with the FRAGMENTS, and allow communicate between FRAGMENTS allowed in an ACTIVITY.
     *
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri){
    }
}
