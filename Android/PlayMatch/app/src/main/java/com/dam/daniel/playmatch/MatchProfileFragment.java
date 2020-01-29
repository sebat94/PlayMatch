package com.dam.daniel.playmatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dam.daniel.playmatch.models.Image;
import com.dam.daniel.playmatch.models.User;
import com.dam.daniel.playmatch.utils.LocalSorage;
import com.dam.daniel.playmatch.utils.ServiceParserUtils;

import java.util.List;

public class MatchProfileFragment extends Fragment {

    // Views
    private ImageButton idImageButtonSettings;
    private ImageButton idImageButtonEditProfile;
    private ImageView idImageProfile;
    private TextView idTextViewNameAndAge;
    private TextView idTextViewCity;

    private Context mContext;

    public MatchProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_profile, container, false);

        idImageButtonSettings = view.findViewById(R.id.idImageButtonSettings);
        idImageButtonEditProfile = view.findViewById(R.id.idImageButtonEditProfile);
        idImageProfile = view.findViewById(R.id.idImageProfile);
        idTextViewNameAndAge = view.findViewById(R.id.idTextViewNameAndAge);
        idTextViewCity = view.findViewById(R.id.idTextViewCity);

        // Round Profile Image
        List<Image> images = LocalSorage.loadDataFromSharedPreferences(((MatchActivity) mContext)).getUser().getImages();
        String image = ServiceParserUtils.getProfileImage(images);
        Glide.with(((MatchActivity) mContext))
                .load(image)
                .apply(RequestOptions.circleCropTransform())
                .into(idImageProfile);

        // Set Nick, age and City
        User user = LocalSorage.loadDataFromSharedPreferences(((MatchActivity) mContext)).getUser();
        String nameAndAge = user.getNick() + ", " + user.getBirthdate();
        idTextViewNameAndAge.setText(nameAndAge);
        idTextViewCity.setText(user.getCity());

        // Event Handlers
        eventHandlers();

        return view;
    }

    /**
     * Event Handlers
     */
    private void eventHandlers(){
        // Settings
        idImageButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatchProfileSettingsFragment matchProfileSettingsFragment = new MatchProfileSettingsFragment();
                FragmentManager fragmentManager = ((MatchActivity) mContext).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                fragmentTransaction.replace(R.id.fragment_container_MatchProfileFragment, matchProfileSettingsFragment, "PROFILE_SETTINGS_FRAGMENT");   // ( idFrameLayout, fragmentToShow, "tag" )
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        // Edit Profile
        idImageButtonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatchProfileEditFragment matchProfileEditFragment = new MatchProfileEditFragment();
                FragmentManager fragmentManager = ((MatchActivity) mContext).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                fragmentTransaction.replace(R.id.fragment_container_MatchProfileFragment, matchProfileEditFragment,"PROFILE_EDIT_FRAGMENT");   // ( idFrameLayout, fragmentToShow, "tag" )
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    /**
     * Get the "onActivityResult" from the Activity Class and then, pass to the next fragment calling super
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * The onAttach method is used to pass the CONTEXT of Activity through fragments, we can also pass to nested fragments
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
