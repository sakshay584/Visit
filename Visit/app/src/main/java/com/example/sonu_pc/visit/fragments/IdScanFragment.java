package com.example.sonu_pc.visit.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sonu_pc.visit.R;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IdScanFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IdScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IdScanFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = IdScanFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton mImageButtonCamera;
    private CameraView mCameraView;

    private Bitmap mBitmapIdPhotoColor ;

    private OnFragmentInteractionListener mListener;
    private OnIdPhotoTakenListener mIdListener;

    public IdScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IdScanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IdScanFragment newInstance(String param1, String param2) {
        IdScanFragment fragment = new IdScanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_id_scan, container, false);
        mImageButtonCamera = (ImageButton) view.findViewById(R.id.imageButton_camera);
        mCameraView = (CameraView) view.findViewById(R.id.camera);
        mCameraView.addCameraListener(cameraListener);
        mImageButtonCamera.setOnClickListener(this);
        return  view;
    }

    CameraListener cameraListener = new CameraListener() {
        @Override
        public void onPictureTaken(byte[] picture) {
            Log.i(TAG, "picture taken");
            // convert the byte array to a bitmap
            mBitmapIdPhotoColor = BitmapFactory.decodeByteArray(picture, 0, picture.length);

            if(mIdListener != null){
                mIdListener.onIdPhotoTaken(mBitmapIdPhotoColor);
            }

            moveToNext();

        }
    };

    private void moveToNext(){
        if(mListener != null){
            mListener.onFragmentInteraction(1, 5);
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
        if (context instanceof OnIdPhotoTakenListener) {
            mIdListener = (OnIdPhotoTakenListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnIdPhotoTakenListener");
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
            case R.id.imageButton_camera:
                Toast.makeText(getActivity(), "Click!", Toast.LENGTH_SHORT).show();
                mCameraView.capturePicture();
                //TODO: move to the next fragment only when the image capture is complete
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraView.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCameraView.destroy();
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

    // TODO: Transfer the photos to the Activity for passing it to the printer module and uploading to firebase
    public interface OnIdPhotoTakenListener{
        void onIdPhotoTaken(Bitmap IdPhoto);
    }
}
