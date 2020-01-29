package com.dam.daniel.playmatch;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dam.daniel.playmatch.utils.ActivityUtils;
import com.dam.daniel.playmatch.utils.LocalSorage;
import com.dam.daniel.playmatch.utils.customXML.ViewPagerUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class MatchProfileEditModalFragment extends Fragment {

    private Context mContext;
    private FrameLayout galleryFragmentToOverlap;

    // Views
    private Button idButtonCamera;
    private Button idButtonGallery;

    // Toolbar
//    private ImageView idImageViewCloseFragment;
    private TextView idTextViewFragmentName;

    public MatchProfileEditModalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disable Swipe
        disableSwipeViewPager(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_profile_edit_modal, container, false);

        // Views
        idButtonCamera = (Button) view.findViewById(R.id.idButtonCamera);
        idButtonGallery = (Button) view.findViewById(R.id.idButtonGallery);
        // Toolbar
//        idImageViewCloseFragment = (ImageView) view.findViewById(R.id.idImageViewCloseFragment);
        idTextViewFragmentName = (TextView) view.findViewById(R.id.idTextViewFragmentName);

        // Load Fragment Name
        setupToolbarData();
        // Button Close Fragment
//        closeFragment(view);
        // Events Handler
        eventHandlers();

        return view;
    }

    /**
     * Event Handlers
     */
    private void eventHandlers() {
        // Camera
        idButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToParentFragment(1);
            }
        });
        // Gallery
        idButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToParentFragment(2);
            }
        });
    }

    /**
     * Go Back To Parent Fragment And Send The Choosed Option (Camera/Gallery)
     * @param value
     */
    private void goBackToParentFragment(int value){
        // If We Select Camenra/Gallery, then we must call onActivityResult and send it the info, in other case, only close the fragment
//        if(value != 0){
            Intent intent = new Intent();
            intent.putExtra("option", value);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            getFragmentManager().popBackStack();
//        }
//        else{
//            // Remove Fragment
//            Fragment fragment = ((MatchActivity) mContext).getSupportFragmentManager().findFragmentByTag("fragment_container_MatchProfileEditGender");
//            if(fragment != null) {
//                ((MatchActivity) mContext).getSupportFragmentManager().beginTransaction().remove(fragment).commit();
//            }
//        }
    }

    /**
     * Set Title Fragment
     */
    private void setupToolbarData(){
        // Set Fragment name
        idTextViewFragmentName.setText("Take Photo");
    }

    /**
     * Close Fragment And go Back To Back Stack
     */
    private void closeFragment(View view){
        ImageView imageView = view.findViewById(R.id.idImageViewCloseFragment);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToParentFragment(0);
            }
        });
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
     * This method Overlaps the fragment above the Previous Fragment
     *
     * @param overlap true overlaps, false undo overlap
     */
    private void setFragmentOverlapAppBarLayout(Boolean overlap){
        ActivityUtils.setFragmentOverlapAppBarLayout(galleryFragmentToOverlap, overlap);
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
        galleryFragmentToOverlap = ((MatchActivity) mContext).findViewById(R.id.fragment_container_matchProfileEditFragment);
        // Overlap the FragmentLayout above the AppBarLayout(toolbar) of Parent Fragment
        setFragmentOverlapAppBarLayout(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // When Fragment Closes We Can Swipe Again
        //disableSwipeViewPager(false);
        // Cancel the Overlap of the FragmentLayout to allow the AppBarLayout of Parent Fragment Overlap again
        setFragmentOverlapAppBarLayout(false);

        // Returns To The Previous Fragment
        //goBackToParentFragment(0);  // TODO: Go Back From Header Arrow
    }

}
