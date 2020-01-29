package com.dam.daniel.playmatch;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dam.daniel.playmatch.enums.Gender;
import com.dam.daniel.playmatch.models.User;
import com.dam.daniel.playmatch.services.UserService;
import com.dam.daniel.playmatch.utils.ActivityUtils;
import com.dam.daniel.playmatch.utils.LocalSorage;
import com.dam.daniel.playmatch.utils.ToastUtils;
import com.dam.daniel.playmatch.utils.VolleySupport;
import com.dam.daniel.playmatch.utils.customXML.ViewPagerUtils;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import org.json.JSONException;
import org.json.JSONObject;


public class MatchProfileSettingsFragment extends Fragment {

//    private ImageView idImageViewCloseFragment;
    private TextView idTextViewFragmentName;

    private LinearLayout idLinearLayoutTelephoneNumber;
    private TextView idTextViewTelephoneNumber;
    private TextView idTextViewGenderPreferences;

    // SeekBar Single
    private RangeSeekBar idRangeSeekBarSingle;
    private TextView idTextViewKm;
    // SeekBar Range
    private RangeSeekBar idRangeSeekBarRange;
    private TextView idTextViewAge;

    private JSONObject jsonObjectUpdateUser;

    private User user;
    private static final int FRAGMENT_CODE = 200;

    private Context mContext;
    private RelativeLayout relativeLayoutContentViewPager;

    public MatchProfileSettingsFragment() {
        // Required empty public constructor
        jsonObjectUpdateUser = new JSONObject();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disable Swipe
        disableSwipeViewPager(true);
        // Get User Info
        user = LocalSorage.loadDataFromSharedPreferences(mContext).getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_profile_settings, container, false);

        // Toolbar
//        idImageViewCloseFragment = (ImageView) view.findViewById(R.id.idImageViewCloseFragment);
        idTextViewFragmentName = (TextView) view.findViewById(R.id.idTextViewFragmentName);

        // Views
        idLinearLayoutTelephoneNumber = (LinearLayout) view.findViewById(R.id.idLinearLayoutTelephoneNumber);
        idTextViewTelephoneNumber = (TextView) view.findViewById(R.id.idTextViewTelephoneNumber);
        idTextViewGenderPreferences = (TextView) view.findViewById(R.id.idTextViewGenderPreferences);
        idRangeSeekBarSingle = (RangeSeekBar) view.findViewById(R.id.idRangeSeekBarSingle);
        idTextViewKm = (TextView) view.findViewById(R.id.idTextViewKm);
        idRangeSeekBarRange = (RangeSeekBar) view.findViewById(R.id.idRangeSeekBarRange);
        idTextViewAge = (TextView) view.findViewById(R.id.idTextViewAge);

        // Load Image And Name of User To Chat
        setupToolbarData();
        // Button Close Fragment
//        closeFragment(view);

        // Load User Data
        loadUserData();

        // Event Handler
        eventHandlers();

        return view;
    }

    /**
     * Fill Fields With Values Of User And Default Configuration Of SeekBars
     */
    private void loadUserData(){
        // Telephone Number
        idTextViewTelephoneNumber.setText(user.getTelephoneNumber());
        // Gender Preferences
        setGenderPreferencesIntoTextView();
        // RangeSeekBar Configuration
        idRangeSeekBarSingle.getLeftSeekBar().setTypeface(Typeface.DEFAULT_BOLD);
        idRangeSeekBarSingle.setIndicatorTextDecimalFormat("0");
        idRangeSeekBarRange.getLeftSeekBar().setTypeface(Typeface.DEFAULT_BOLD);
        idRangeSeekBarRange.setIndicatorTextDecimalFormat("0");
        // SeekBar Single
        idRangeSeekBarSingle.setRange(0, 161);
        idRangeSeekBarSingle.setValue(user.getMaxDistancePreference());
        idTextViewKm.setText(user.getMaxDistancePreference() + "km.");
        idRangeSeekBarSingle.setProgressColor(getResources().getColor(android.R.color.holo_red_dark));
        // SeekBar Range
        idRangeSeekBarRange.setRange(18, 55);
        idRangeSeekBarRange.setValue(user.getMinAgePreference(), user.getMaxAgePreference());
        idTextViewAge.setText(user.getMinAgePreference() + " - " + user.getMaxAgePreference());
        idRangeSeekBarRange.setProgressColor(getResources().getColor(android.R.color.holo_red_dark));
    }

    /**
     * Set Text Of Gender Preferences Into TextView "idTextViewGenderPreferences"
     */
    private void setGenderPreferencesIntoTextView(){

        String genderPreferencesValue = "";
        for (int i = 0; i < user.getGenderPreferences().size(); i++){
            if(user.getGenderPreferences().get(i).getGender() == Gender.MALE.getValue()){
                genderPreferencesValue = genderPreferencesValue.concat("Male, ");
            }else if(user.getGenderPreferences().get(i).getGender() == Gender.FEMALE.getValue()){
                genderPreferencesValue = genderPreferencesValue.concat("Female, ");
            }else if(user.getGenderPreferences().get(i).getGender() == Gender.OTHER.getValue()){
                genderPreferencesValue = genderPreferencesValue.concat("Other, ");
            }
        }
        // Check If User Have GenderPreferences Or Not Before Cut String
        if(user.getGenderPreferences().size() > 0){
            genderPreferencesValue = genderPreferencesValue.substring(0, genderPreferencesValue.length() - 2);  // Delete the last comma and space of string
        }
        idTextViewGenderPreferences.setText(genderPreferencesValue);

    }

    /**
     * Event Handlers
     */
    private void eventHandlers() {
        // Gender Preferences
        idTextViewGenderPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGenderPreferencesFragment();            // TODO: Está abriendo infinitas instancias a saber donde, el "replace" del open fragment no esta funcionando bien
            }
        });
        // RangeSeekBar Single
        idRangeSeekBarSingle.setOnRangeChangedListener(new OnRangeChangedListener(){
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                //leftValue is left seekbar value, rightValue is right seekbar value
                //idRangeSeekBarSingle.setIndicatorText((int)leftValue+"");
                int max = (int)leftValue;
                String showDistance = max + "km.";

                idTextViewKm.setText(showDistance);
                // Update JsonObject
                try {
                    jsonObjectUpdateUser.put("user", new JSONObject().put("maxDistancePreference", max));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
                // It launches when start dragging
            }
            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                // It launches when stop dragging
                updateUser(jsonObjectUpdateUser);

            }
        });
        // RangeSeekBar Range
        idRangeSeekBarRange.setOnRangeChangedListener(new OnRangeChangedListener(){
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                //leftValue is left seekbar value, rightValue is right seekbar value
                int min = (int)leftValue;
                int max = (int)rightValue;

                String showAge;
                if(max != 55) showAge = min + " - " + max;
                else showAge = min + " - " + max + "+";

                idTextViewAge.setText(showAge);
                // Update JsonObject
                try {
                    jsonObjectUpdateUser.put("user", new JSONObject().put("minAgePreference", min).put("maxAgePreference", max));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
                // It launches when start dragging
            }
            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                // It launches when stop dragging
                updateUser(jsonObjectUpdateUser);
            }
        });

    }

    /**
     * Open Fragment To Choose Gender Preferences
     * Send FRAGMENT_CODE
     */
    private void openGenderPreferencesFragment(){
        MatchProfileSettingsGenderFragment matchProfileSettingsGenderFragment = new MatchProfileSettingsGenderFragment();
        matchProfileSettingsGenderFragment.setTargetFragment(MatchProfileSettingsFragment.this, FRAGMENT_CODE);
        FragmentManager fragmentManager = ((MatchActivity) mContext).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        fragmentTransaction.replace(R.id.fragment_container_MatchProfileSettingsGender, matchProfileSettingsGenderFragment, "fragment_container_MatchProfileSettingsGender");   // ( idFrameLayout, fragmentToShow, "tag" )
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Update User
     * @param jsonObject
     */
    private void updateUser(JSONObject jsonObject){
        UserService userService = new UserService(mContext);
        userService.updateUser(
                new VolleySupport.ServerCallbackUser() {
                    @Override
                    public void onSuccess(User newUser) {
                        try{
                            // Set New User
                            user = newUser;

                        }catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new VolleySupport.ServerCalbackError() {
                    @Override
                    public void onError(String errorMessage) {
                        System.out.println("ErrorMessage: " + errorMessage);
                        ToastUtils.showToastError(mContext, errorMessage);
                    }
                },
                jsonObject
        );
    }

    /**
     * If True Then The <ViewPager> Can't Swipe.
     * If False We Can Swipe The <ViewPager>
     *     TODO: Refactor - This functions is repeated in many sites
     *
     * @param value
     */
    private void disableSwipeViewPager(boolean value){
        ViewPagerUtils customViewPager = (ViewPagerUtils) ((MatchActivity) mContext).findViewById(R.id.pager);
        customViewPager.disableScroll(value);    // DISABLE SWIPE LEFT/RIGHT
    }

    /**
     * Set Title Fragment
     */
    private void setupToolbarData(){
        // Set Fragment name
        idTextViewFragmentName.setText("Account Settings");
    }

    /**
     * Close Fragment And go Back To Back Stack
     */
//    private void closeFragment(View view){
//        ImageView imageView = view.findViewById(R.id.idImageViewCloseFragment);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try{
//                    ((MatchActivity) mContext).onBackPressed();
//                    Fragment fragment = ((MatchActivity) mContext).getSupportFragmentManager().findFragmentByTag("PROFILE_SETTINGS_FRAGMENT");
//                    if(fragment != null){
//                        ((MatchActivity) mContext).getSupportFragmentManager().beginTransaction().remove(fragment).commit();
//                    }
//                }catch (NullPointerException ex){
//                    ex.printStackTrace();
//                    System.out.println("EXCEPCION CERRANDO EL FRAGMENT: " + ex.getMessage());
//                }
//            }
//        });
//    }

    /**
     * This method Overlaps the fragment above the global AppBarLayout(toolbar)
     *
     * @param overlap true overlaps, false undo overlap
     */
    private void setFragmentOverlapAppBarLayout(Boolean overlap){
        ActivityUtils.setFragmentOverlapAppBarLayout(relativeLayoutContentViewPager, overlap);
    }

    /**
     * "onActivityResult" is listening for determined actions like get returned data from another fragment, the result of Add/Remove GenderPreferences
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // Check ResultCode was successful
        if(resultCode == Activity.RESULT_OK){
            // Check If The Fragment Opened is "MatchProfileSettingsGenderFragment", and update TextView "idTextViewGenderPreferences" with the new values
            if(requestCode == FRAGMENT_CODE) {
                if(data != null) {
                    int updateText = data.getIntExtra("option", 0);
                    if(updateText != 0){
                        // Update "user" Propertie With Newly Added/Removed GenderPreferences And Set it To The TextView
                        user = LocalSorage.loadDataFromSharedPreferences(mContext).getUser();
                        setGenderPreferencesIntoTextView();
                    }
                }
            }
        }
    }

    /**
     * The onAttach method is used to pass the CONTEXT of Activity through fragments, we can also pass to nested fragments
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        // Get RelativeLayout that we use to overlaps this content above the global AppBarLayout
        relativeLayoutContentViewPager = ((MatchActivity) mContext).findViewById(R.id.contentViewPager);
        // Overlap the RelativeLayout above the global AppBarLayout(toolbar)
        setFragmentOverlapAppBarLayout(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // When Fragment Closes We Can Swipe Again
        disableSwipeViewPager(false);
        // Cancel the Overlap of the RelativeLayout to allow the AppBarLayout Overlap again
        setFragmentOverlapAppBarLayout(false);

    }

}
