package com.dam.daniel.playmatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.dam.daniel.playmatch.models.User;
import com.dam.daniel.playmatch.services.GenderPreferenceService;
import com.dam.daniel.playmatch.services.UserService;
import com.dam.daniel.playmatch.utils.ActivityUtils;
import com.dam.daniel.playmatch.utils.LocationTrack;
import com.dam.daniel.playmatch.utils.ToastUtils;
import com.dam.daniel.playmatch.utils.ValidationUtils;
import com.dam.daniel.playmatch.utils.VolleySupport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// {@link MainRequestUserDataFragment.OnFragmentInteractionListener} interface
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * Use the {@link MainRequestUserDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainRequestUserDataFragment extends Fragment {

    private String telephoneNumber;

    private static JSONObject userParams = new JSONObject();
    private static JSONObject genderPreference = new JSONObject();

    public MainRequestUserDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainRequestUserDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainRequestUserDataFragment newInstance() {
        MainRequestUserDataFragment fragment = new MainRequestUserDataFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get Bundle
        readBundle(getArguments());

        // Get Location
        LocationTrack locationTrack = new LocationTrack(getActivity());
        if(locationTrack.canGetLocation()){
            try{
                userParams.put("telephoneNumber", telephoneNumber);
                userParams.put("lat", locationTrack.getLatitude());
                userParams.put("lng", locationTrack.getLongitude());
                userParams.put("city", locationTrack.getCityName());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_request_user_data, container, false);

        // Setup Event Handler Buttons
        setupButtons(view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Once The Fragment Activity Has Been Created And The View Hierarchy Instantiated, We Load The First Relative Layout
        showNextRelativeLayout(-1, R.id.idRelativeLayoutEmail);
    }

    /**
     * Handle Button Clicks
     */
    private void setupButtons(final View view) {
        Button btnEmail = (Button) view.findViewById(R.id.idButtonEmail);
        Button btnPassword = (Button) view.findViewById(R.id.idButtonPassword);
        Button btnNick = (Button) view.findViewById(R.id.idButtonNick);
        Button btnBirthdate = (Button) view.findViewById(R.id.idButtonBirthdate);
        Button btnGender = (Button) view.findViewById(R.id.idButtonGender);
        Button btnPreferredGender = (Button) view.findViewById(R.id.idButtonPreferredGenders);
        Button btnDescription = (Button) view.findViewById(R.id.idButtonDescription);
        // Email
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextEmail = (EditText) view.findViewById(R.id.idEditTextEmail);
                String email = editTextEmail.getText().toString();

                // Save Email And Move To The Next Step
                if(ValidationUtils.regExpEmail(email)){
                    try {
                        userParams.put("email", email);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Change Relaltive Layout
                    showNextRelativeLayout(R.id.idRelativeLayoutEmail, R.id.idRelativeLayoutPassword);
                }else{
                    ValidationUtils.errorStyleValidation(getActivity(), editTextEmail);
                    ToastUtils.showToastError(getActivity(), "Introduce un email válido");
                }
            }
        });
        // Password
        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextPassword = (EditText) view.findViewById(R.id.idEditTextPassword);
                String password = editTextPassword.getText().toString();

                // Save Password And Move To The Next Step
                if(ValidationUtils.regExpPassword(password)){
                    try {
                        userParams.put("password", password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Change Relaltive Layout
                    showNextRelativeLayout(R.id.idRelativeLayoutPassword, R.id.idRelativeLayoutNick);
                }else{
                    ValidationUtils.errorStyleValidation(getActivity(), editTextPassword);
                    ToastUtils.showToastError(getActivity(), "La contraseña debe tener:\n1 Mayúscula\n1 Minúscula\n1 Letra\nEntre 3 y 32 caracteres");
                }

            }
        });
        // Nick
        btnNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextNick = (EditText) view.findViewById(R.id.idEditTextNick);
                String nick = editTextNick.getText().toString();

                // Save Nick And Move To The Next Step
                if(ValidationUtils.regExpNick(nick)){
                    try {
                        userParams.put("nick", nick);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Change Relaltive Layout
                    showNextRelativeLayout(R.id.idRelativeLayoutNick, R.id.idRelativeLayoutBirthdate);
                }else{
                    ValidationUtils.errorStyleValidation(getActivity(), editTextNick);
                    ToastUtils.showToastError(getActivity(), "Caracteres permitidos: [a-zA-Z0-9_-]\nEntre 3-14 caracteres");
                }
            }
        });
        // Birthdate
        btnBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextBirthdate = (EditText) view.findViewById(R.id.idEditTextBirthdate);
                String birthdate = editTextBirthdate.getText().toString();

                if(ValidationUtils.regExpBirthdate(birthdate)){
                    try {
                        userParams.put("birthdate", birthdate);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Change Relaltive Layout
                    showNextRelativeLayout(R.id.idRelativeLayoutBirthdate, R.id.idRelativeLayoutGender);
                }else{
                    ValidationUtils.errorStyleValidation(getActivity(), editTextBirthdate);
                    ToastUtils.showToastError(getActivity(), "Formato de fecha:\nYYYY/MM/DD\nYYYY-MM-DD");
                }
            }
        });
        // My Gender
        btnGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.idRadioGroupGender);
                // Get selected radio button from radioGroup
                int selectedId = radioGroup.getCheckedRadioButtonId();

                // If RadioButton is Selected
                if(selectedId != -1){
                    // Find the radiobutton by returned id
                    RadioButton radioButton = (RadioButton) view.findViewById(selectedId);
                    String gender = radioButton.getText().toString();
                    try {
                        if(gender.equals("Hombre")) userParams.put("gender", 1);
                        else if(gender.equals("Mujer"))  userParams.put("gender", 2);
                        else if(gender.equals("Otro")) userParams.put("gender", 3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Change Relaltive Layout
                    showNextRelativeLayout(R.id.idRelativeLayoutGender, R.id.idRelativeLayoutPreferredGenders);
                }else{
                    ToastUtils.showToastError(getActivity(), "Selecciona tu sexo");
                }
            }
        });
        // My Preferred Gender
        btnPreferredGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBoxMan = (CheckBox) view.findViewById(R.id.idCheckBoxPreferredMan);
                CheckBox checkBoxWoman = (CheckBox) view.findViewById(R.id.idCheckBoxPreferredWoman);
                CheckBox checkBoxOther = (CheckBox) view.findViewById(R.id.idCheckBoxPreferredOther);

                // If Some Checkbox is Selected
                if(checkBoxMan.isChecked() || checkBoxWoman.isChecked() || checkBoxOther.isChecked()){
                    JSONArray arr = new JSONArray();
                    try{
                        if(checkBoxMan.isChecked())
                            arr.put(new JSONObject().put("gender", 1)); // 1 - Man
                        if(checkBoxWoman.isChecked())
                            arr.put(new JSONObject().put("gender", 2)); // 2 - Woman
                        if(checkBoxOther.isChecked())
                            arr.put(new JSONObject().put("gender", 3)); // 3 - Other

                        genderPreference.put("genderPreferences", arr);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    // Change Relaltive Layout
                    showNextRelativeLayout(R.id.idRelativeLayoutPreferredGenders, R.id.idRelativeLayoutDescription);
                }else{
                    ToastUtils.showToastError(getActivity(), "Selecciona tus intereses");
                }
            }
        });
        // Description
        btnDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextDescription = (EditText) view.findViewById(R.id.idEditTextDescription);
                String description = editTextDescription.getText().toString();

                // Save Nick and Hide This Relative Layout
                // Send Request To The Server And Move to MatchActivity
                if(ValidationUtils.regExpDescription(description)){
                    try {
                        userParams.put("description", description);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Change Relaltive Layout
                    showNextRelativeLayout(R.id.idRelativeLayoutDescription, -1);

                    // Send Request To Server
                    JSONObject postParams = new JSONObject();
                    try {
                        userParams.put("maxDistancePreference", 75);
                        userParams.put("minAgePreference", 18);
                        userParams.put("maxAgePreference", 34);
                        userParams.put("active", true);
                        userParams.put("disabled", null);
                        postParams.put("user", userParams);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    updateUser(postParams);
                }else{
                    ValidationUtils.errorStyleValidation(getActivity(), editTextDescription);
                    ToastUtils.showToastError(getActivity(), "Caracteres permitidos: [a-zA-Z0-9_.-]\nEntre 0-500 caracteres");
                }
            }
        });
    }

    /**
     * Show Next Relative layout And Hide Preovious
     * Receive parameters as "R.id.your_relative_layout_id" that makes reference to an global int id
     * The 3rd Parameter "View" - The First Relative Layout Need To Use Their Own "View" from "onCreateView" becase The FragmentView It Isn't Created Yet
     * @param prevRelativeLayout
     * @param nextRelativeLayout
     */
    private void showNextRelativeLayout(int prevRelativeLayout, int nextRelativeLayout){
        // SHOW NEXT RELATIVE LAYOUT
        if(nextRelativeLayout != -1){
            RelativeLayout nextRL = (RelativeLayout) getView().findViewById(nextRelativeLayout);
            nextRL.setVisibility(View.VISIBLE);
            // Get The EditText And Set Focus To It, Only If Exists In This Relative Layout
            EditText editText = nextRL.findViewWithTag("register");
            if(editText != null)
                setFocusAndKeyboard(editText);
        }
        // HIDE PREVIOUS RELATIVE LAYOUT
        if(prevRelativeLayout != -1){
            RelativeLayout prevRL = (RelativeLayout) getView().findViewById(prevRelativeLayout);
            prevRL.setVisibility(View.GONE);
        }
    }

    /**
     * Show Focus by default in EditText and show the keyboard
     */
    private void setFocusAndKeyboard(EditText editText){
        // Set Focus And Show Keyboard
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        // This is for older versions
        try{
            final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    /**
     * Creates the User with the Values Given
     * @param postParams
     */
    private void updateUser(JSONObject postParams){
        UserService userService = new UserService(getActivity());
        userService.updateUser(
                new VolleySupport.ServerCallbackUser() {
                    @Override
                    public void onSuccess(User user) {
                        // When we receive updated user and the token, use this token to update our Gender Preferences
                        updateGenderPreference(genderPreference);

                        // Go Into App
                        try{
                            ActivityUtils.setRoot(getActivity(), MatchActivity.class);
                        }catch (NullPointerException e) {
                            e.printStackTrace();
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
     * Create Gender Preferences
     * @param postParams
     */
    private void updateGenderPreference(JSONObject postParams){
        GenderPreferenceService genderPreferenceService = new GenderPreferenceService(getActivity());
        genderPreferenceService.addGenderPreference(
                new VolleySupport.ServerCallbackEmpty() {
                    @Override
                    public void onSuccess() {
                        // Nothing to do when add a GenderPreference
                        //ToastUtils.showToastInfo(getActivity(), "OK");
                    }
                },
                new VolleySupport.ServerCalbackError() {
                    @Override
                    public void onError(String errorMessage) {
                        //ToastUtils.showToastError(getActivity(), errorMessage);
                    }
                },
                postParams
        );
    }

}