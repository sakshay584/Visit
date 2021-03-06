package com.example.sonu_pc.visit.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.sonu_pc.visit.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NonDisclosureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NonDisclosureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NonDisclosureFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = NonDisclosureFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TERMS_AND_CONDITIONS = "tnc";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String terms_and_cond;
    private String mParam2;

    private CheckBox mCheckBoxAgree;
    private TextView mTextViewTnc;
    private OnFragmentInteractionListener mListener;

    public NonDisclosureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NonDisclosureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NonDisclosureFragment newInstance(String param1, String param2) {
        NonDisclosureFragment fragment = new NonDisclosureFragment();
        Bundle args = new Bundle();
        args.putString(TERMS_AND_CONDITIONS, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            terms_and_cond = getArguments().getString(TERMS_AND_CONDITIONS);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_non_disclosure, container, false);
        mCheckBoxAgree = (CheckBox) view.findViewById(R.id.checkBox_agree);
        mCheckBoxAgree.setOnClickListener(this);
        mTextViewTnc = view.findViewById(R.id.text_tnc);
        mTextViewTnc.setText(terms_and_cond);
        return view;
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case(R.id.checkBox_agree):
                CheckBox checkBox = (CheckBox) v;
                if(checkBox.isChecked()){
                    if (mListener != null) {
                        mListener.onFragmentInteraction(1, 6);
                    }
                }
        }
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
        // TODO: Update argument wipe and name
        void onFragmentInteraction(int direction, int stageNo);
    }
}
