package com.dam.daniel.playmatch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CountryCode.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CountryCode#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CountryCode extends Fragment {

    private List<String> listCountries = new ArrayList<>();
    private List<String> listIsoCodes = new ArrayList<>();
    private List<String> larala = new ArrayList<>();
    private HashMap<String, String> hashMapCountryAndCodes = new HashMap<>();
    ListView listView ;

    private OnFragmentInteractionListener mListener;

    public CountryCode() {
        // Required empty public constructor
    }

    /**
     * Create a new instance of CountryCodeFragment, initialized to show the text at 'index'.
     *
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param index
     * @return A new instance of fragment CountryCode.
     */
    public static CountryCode newInstance(int index) {
        CountryCode fragment = new CountryCode();
        // Supply index input as an argument
        Bundle args = new Bundle();
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Give value to the variables
        listCountries.addAll( Arrays.asList(getResources().getStringArray(R.array.countries)) );
        listIsoCodes.addAll( Arrays.asList(getResources().getStringArray(R.array.isoCodes)) );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_country_code, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        /*IntStream.range(0, listCountries.size()).forEach(index -> hashMapCountryAndCodes.put(listCountries.get(index), listIsoCodes.get(index)));*/

        try{
            listView = (ListView) getActivity().findViewById(R.id.idListCountryCode);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listIsoCodes);
            listView.setAdapter(arrayAdapter);
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
         void onFragmentInteraction(Uri uri);
    }

    /**
     *  When user clicks on Item of ListView, we return these value to the MainActivity
     */
    /*@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
    }

    listView.setOnItemClickListener(new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
        int position, long id) {
            Toast.makeText(getApplicationContext(),
                    "Click ListItem Number " + position, Toast.LENGTH_LONG)
                    .show();
        }
    });*/


}
