package com.example.sonu_pc.visit.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.Rating;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.example.sonu_pc.visit.FragmentCancelListener;
import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.fragments.FaceIdFragment;
import com.example.sonu_pc.visit.fragments.IdScanFragment;
import com.example.sonu_pc.visit.fragments.NonDisclosureFragment;
import com.example.sonu_pc.visit.fragments.RatingFragment;
import com.example.sonu_pc.visit.fragments.SuggestionFragment;
import com.example.sonu_pc.visit.fragments.SurveyFragment;
import com.example.sonu_pc.visit.fragments.ThankYouFragment;
import com.example.sonu_pc.visit.fragments.VisiteeInfoFragment;
import com.example.sonu_pc.visit.fragments.VisitorInfoFragment;
import com.example.sonu_pc.visit.fragments.WelcomeFragment;
import com.example.sonu_pc.visit.model.data_model.CameraModel;
import com.example.sonu_pc.visit.model.data_model.Model;
import com.example.sonu_pc.visit.model.data_model.NewDataModel;
import com.example.sonu_pc.visit.model.data_model.RatingModel;
import com.example.sonu_pc.visit.model.data_model.SuggestionModel;
import com.example.sonu_pc.visit.model.data_model.SurveyModel;
import com.example.sonu_pc.visit.model.data_model.TextInputModel;
import com.example.sonu_pc.visit.model.preference_model.CameraPreference;
import com.example.sonu_pc.visit.model.preference_model.MasterWorkflow;
import com.example.sonu_pc.visit.model.preference_model.Preference;
import com.example.sonu_pc.visit.model.preference_model.PreferencesModel;
import com.example.sonu_pc.visit.model.preference_model.RatingPreferenceModel;
import com.example.sonu_pc.visit.model.preference_model.SuggestionPreference;
import com.example.sonu_pc.visit.model.preference_model.SurveyPreferenceModel;
import com.example.sonu_pc.visit.model.preference_model.TextInputPreferenceModel;
import com.example.sonu_pc.visit.model.preference_model.ThankYouPreference;
import com.example.sonu_pc.visit.utils.AndroidBug5497Workaround;
import com.example.sonu_pc.visit.utils.GsonUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StageActivity extends AppCompatActivity implements WelcomeFragment.OnWelcomeFragmentInteractionListener,
        FaceIdFragment.OnFragmentInteractionListener, NonDisclosureFragment.OnFragmentInteractionListener,
        VisitorInfoFragment.OnVisitorInteractionListener,
        VisiteeInfoFragment.OnFragmentInteractionListener,
        SurveyFragment.OnSurveyInteractionListener,
        ThankYouFragment.OnThankYouFragmentInteractionListener, IdScanFragment.OnIdPhotoTakenListener, FragmentCancelListener,
        RatingFragment.RatingFragmentInterface, SuggestionFragment.SuggestionFragmentInterface{

    private static final String TAG = StageActivity.class.getSimpleName();

    private NewDataModel mVisitorDataModel;

    private FirebaseFirestore mFirestore;

    private MasterWorkflow masterWorkflow;

    private ConstraintLayout mConstraintLayout;

    private String workflow_name;

    private PreferencesModel mSelectedWorkflow;
    private Preference mCurrentPreference;

    private ArrayList<Preference> mOrderOfScreens;
    private Map<String, Model> mDataModelsMap;

    private List<Pair<String, Uri>> photoUriPairList;

    private String visitor_id;
    private int curr_stage = 0;

    //######################################################################################
    private List<Pair<String,String>> quesAnsPairList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        final View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            // TODO: The system bars are visible. Make any desired
                            decorView.setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
// again hide it
                        } else {
                            // TODO: The system bars are NOT visible. Make any desired
                            // adjustments to your UI, such as hiding the action bar or
                            // other navigational controls.
                        }
                    }
                });


        setContentView(R.layout.activity_stage);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            AndroidBug5497Workaround.assistActivity(this);
        }

        // Initialize the views
        mConstraintLayout = findViewById(R.id.constraint_container_signup);

        initFirestore();

        // Show the fragment with list of screens.
        initWelcomeFragment();

        // Obtain MasterWorkflow
        initMasterWorkflow();

    }

    private void initFirestore(){
        Log.d(TAG, "initFirestore()");
        mFirestore = FirebaseFirestore.getInstance();

        // Enable offline
    }

    private void refreshStageActivity(){
        // replacement for coupon model
        mVisitorDataModel = new NewDataModel();
        mDataModelsMap = new HashMap<>();

        //############################################################################
        quesAnsPairList = new ArrayList<>();
        photoUriPairList = new ArrayList<>();

        //############################################################################

        visitor_id = System.currentTimeMillis() + "";
    }

    private void initWelcomeFragment(){

        refreshStageActivity();

        Log.d(TAG, "initWelcomeFragment()");
        // Set the curr_stage to 0
        curr_stage = 0;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WelcomeFragment.newInstance("",""))
                .commit();
    }

    private void initMasterWorkflow(){

        Log.d(TAG, "initMasterWorkflow()");
        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE_MASTERWORKFLOW), Context.MODE_PRIVATE);
        Gson gson2 = GsonUtils.getGsonParser();
        String workflow_json = preferences.getString(getString(R.string.PREF_KEY_MASTERWORKFLOW), "NOPREF");
        Log.d(TAG, "obtained_workflow_json = " + workflow_json);
        if(workflow_json.equals("NOPREF")){
            Log.e(TAG, "Could not fetch the workflow json from sharedpreferences");
        }
        else{
            masterWorkflow = gson2.fromJson(workflow_json, MasterWorkflow.class);
        }
    }

    /*Runs after obtaining workflow key from welcome fragment*/
    private void initSessionWorkflow(String selected_workflow_key){

        workflow_name = selected_workflow_key;
        Log.d(TAG, "initSessionWorkflow()");

        mSelectedWorkflow = masterWorkflow.getWorkflows_map().get(workflow_name);
        if(mSelectedWorkflow instanceof PreferencesModel){
            mOrderOfScreens = mSelectedWorkflow.getOrder_of_screens();
            Log.d(TAG, "obtained the selected workflow model");
        }
        else{
            Log.e(TAG, "Did not obtain the selected workflow model");
        }

        //Start handling fragments after initializing session workflow given from welcome
        // fragment
        handleFragments();

    }

    private void handleFragments(){

        Log.d(TAG, "handleFragments()");

        FragmentManager fragmentManager = getSupportFragmentManager();

        if(curr_stage < mOrderOfScreens.size()) { // if the curr screen no is within req no of screens

            Log.d(TAG, "curr stage = " + curr_stage);
            Gson gson = GsonUtils.getGsonParser();
            String pref_obj_json;

            final ImageView sharedImage = getCurrentFragmentBrandImage();
            mCurrentPreference = mOrderOfScreens.get(curr_stage++);      // get the pref object of the fragment

            if(mCurrentPreference instanceof TextInputPreferenceModel){
                Log.d(TAG, "moving to text input fragment");
                //final ImageView sharedImage = ((VisitorInfoFragment) fragment).getSharedImageView();
                pref_obj_json = gson.toJson((TextInputPreferenceModel) mCurrentPreference);
                fragmentManager.beginTransaction()
                        .addSharedElement(sharedImage, ViewCompat.getTransitionName(sharedImage))
                        .replace(R.id.fragment_container, VisitorInfoFragment.newInstance(pref_obj_json, ""))
                        .commit();
            }
            else if(mCurrentPreference instanceof SurveyPreferenceModel){
                Log.d(TAG, "moving to survey input fragment");
                //final ImageView sharedImage = ((SurveyFragment) fragment).getSharedImageView();
                pref_obj_json = gson.toJson((SurveyPreferenceModel) mCurrentPreference);
                fragmentManager.beginTransaction()
                        .addSharedElement(sharedImage, ViewCompat.getTransitionName(sharedImage))
                        .replace(R.id.fragment_container, SurveyFragment.newInstance(pref_obj_json, ""))
                        .commit();
            }
            else if(mCurrentPreference instanceof CameraPreference){
                Log.d(TAG, "moving to camera preference fragment");
                //final ImageView sharedImage = ((IdScanFragment) fragment).getSharedImageView();
                pref_obj_json = gson.toJson((CameraPreference) mCurrentPreference);
                fragmentManager.beginTransaction()
                        .addSharedElement(sharedImage, ViewCompat.getTransitionName(sharedImage))
                        .replace(R.id.fragment_container, IdScanFragment.newInstance(pref_obj_json, visitor_id))
                        .commit();
            }
            else if(mCurrentPreference instanceof RatingPreferenceModel){
                Log.d(TAG, "moving to rating preference fragment");
                pref_obj_json = gson.toJson((RatingPreferenceModel) mCurrentPreference);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, RatingFragment.newInstance(pref_obj_json))
                        .commit();
            }

            else if(mCurrentPreference instanceof SuggestionPreference){
                Log.d(TAG, "moving to suggestion preference fragment");
                pref_obj_json = gson.toJson((SuggestionPreference) mCurrentPreference);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SuggestionFragment.newInstance(pref_obj_json))
                        .commit();
            }

            else if(mCurrentPreference instanceof ThankYouPreference){
                Log.d(TAG, "moving to thank you fragment");

                pref_obj_json = gson.toJson((ThankYouPreference) mCurrentPreference);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ThankYouFragment.newInstance(pref_obj_json))
                        .commit();
                Log.d(TAG, "uploading new visitor data model");

                //#################################################################
                uploadWorkflow(workflow_name);
            }

        }
        else{ // Exhausted the screens, upload the data
            Log.d(TAG, "exhausted all the screens");

        }

    }

    private ImageView getCurrentFragmentBrandImage(){

        ImageView brandImage = null;

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

       if(fragment instanceof WelcomeFragment){
           brandImage = ((WelcomeFragment) fragment).getSharedImageView();
       }
       else if(fragment instanceof VisitorInfoFragment){
           brandImage = ((VisitorInfoFragment) fragment).getSharedImageView();
       }
       else if(fragment instanceof SurveyFragment){
           brandImage = ((SurveyFragment) fragment).getSharedImageView();
       }
       else if(fragment instanceof IdScanFragment){
           brandImage = ((IdScanFragment) fragment).getSharedImageView();
       }
       else if(fragment instanceof ThankYouFragment){
           Log.e(TAG, "this log should not appear if thank you fragment is the last fragment!");
           brandImage = ((ThankYouFragment) fragment).getSharedImageView();
       }
       if(brandImage == null){
           Log.e(TAG, "some error in retrieving the brand image check getCurrentFragmentBrandImage()");
       }
       return brandImage;
    }

    //############################################################################3
    private void uploadWorkflow(final String workflow){

        CollectionReference workflowCollectionRef;
        CollectionReference visitorsCollectionRef;
        CollectionReference photosCollectionRef;

        workflowCollectionRef = mFirestore.collection(getString(R.string.collection_ref_institutes))
                .document(FirebaseAuth.getInstance().getUid()).collection(getString(R.string.collection_ref_workflows));
        visitorsCollectionRef = workflowCollectionRef.document(workflow)
                .collection(getString(R.string.collection_ref_visitors));
        photosCollectionRef = visitorsCollectionRef.document(visitor_id)
                .collection(getString(R.string.collection_ref_photos));

        /*ArrayList<String> questions = new ArrayList<>(quesAnsPairList.keySet());
        Map<String, List<String>> questionsMap = new HashMap<>();
        questionsMap.put("questions", questions);*/

        // If the workflow is meant for SignOut purposes, add the signIn and the signIn status to the visitor info
        if(mSelectedWorkflow != null && mSelectedWorkflow.isWorkflowForSignOut()){
            Pair<String, String> pair = new Pair<>(getString(R.string.KEY_WORKFLOW_PREF_SIGNIN_TIME), visitor_id);
            quesAnsPairList.add(pair);
            pair = new Pair<>(getString(R.string.KEY_WORKFLOW_PREF_IS_SIGNEDOUT), "0");
            quesAnsPairList.add(pair);
            pair = new Pair<>(getString(R.string.KEY_WORKFLOW_PREF_SIGNOUT_TIME), "0");
            quesAnsPairList.add(pair);
            /*// Add a field to identify the SignOut enabled workflow in the firestore
            Map<String, Boolean> map = new HashMap<>();
            map.put(getString(R.string.KEY_WORKFLOW_DATA_IS_WORKFLOW_SIGNOUT), true);
            workflowCollectionRef.document(workflow).set(map);*/
        }


        // Add questions list to the workflow field
        /*List<String> questionsList = new ArrayList<>();*/
        Map<String, String> quesAnsMap = new LinkedHashMap<>();
        for(Pair<String, String> pair : quesAnsPairList){
            Log.d(TAG, "adding key : "  + pair.first);
            quesAnsMap.put(pair.first, pair.second);    // Add ques and answers for visitor doc
        }



        visitorsCollectionRef.document(visitor_id).set(quesAnsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Uploaded the visitor ques Ans map");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to upload visitor data to firestore");
            }
        });

        if(!photoUriPairList.isEmpty()) {
            uploadPhotos(photosCollectionRef, photoUriPairList);
        }
    }


    private void uploadPhotos(CollectionReference photoCollectionRef, List<Pair<String,Uri>> photosList){

        // TODO: IMPORTANT https://stackoverflow.com/questions/38822982/firebase-uploading-images-when-internet-is-offline

        Map<String, String> photoKeyNameMap = new HashMap<>();
        for(Pair<String, Uri> pair : photosList){
            photoKeyNameMap.put(pair.first, visitor_id);
        }
        photoCollectionRef.document(visitor_id).set(photoKeyNameMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Successfully uplaoded photo details on firestore");
            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        StorageReference insitRef = storageRef.child(FirebaseAuth.getInstance().getUid());
        StorageReference workflowRef = insitRef.child(workflow_name);

        // upload all photo under the folder name same as the key
        for(Pair<String, Uri> pair : photosList){
            Uri file = pair.second;
            final String foldername = pair.first;

            //upload the photos to firestore storage

            StorageReference imageRef = workflowRef.child(foldername).child(visitor_id);
            UploadTask uploadTask = imageRef.putFile(file);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "Uploaded image: " + foldername);
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if(downloadUrl != null) {
                        Log.d(TAG, "image url = " + downloadUrl.toString());
                    }
                    else{
                        Log.e(TAG, "Image download url obtained is null");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Unsuccessful in uploading image: " + foldername);
                }
            });
        }
    }

    //##########################################################################################

    private void moveToNextFragment(){
        Log.d(TAG, "moving to next fragment");
        handleFragments();
    }

    //[Implemented Methods]
    @Override
    public void onWelcomeFragmentInteraction(String selected_workflow_key) {
        // set the selected workflow
        initSessionWorkflow(selected_workflow_key);
    }

    @Override
    public void onFragmentInteraction(int direction, int stageNo) {
        Log.d(TAG, "onFragmentInteraction()");
        handleFragments();
    }


    @Override
    public void onTextInputInteraction(TextInputModel textInputModel) {
        Log.d(TAG, "obtained textinput model");
        if(mCurrentPreference instanceof TextInputPreferenceModel){
            mDataModelsMap.put(((TextInputPreferenceModel) mCurrentPreference).getPage_title(), textInputModel);

            //#######################################################################
            for(Pair<String, String> pair : textInputModel.getText_input_data()){
                quesAnsPairList.add(pair);
            }
            //quesAnsPairList.putAll(textInputModel.getText_input_data());

            moveToNextFragment();
        }
        else{
            Log.e(TAG, "current preference object did not match the required object, check onTextInputInteraction");
        }

    }

    @Override
    public void onSurveyInteraction(SurveyModel surveyModel) {
        Log.d(TAG, "obtained survey info");
        if(mCurrentPreference instanceof SurveyPreferenceModel){
            mDataModelsMap.put(((SurveyPreferenceModel) mCurrentPreference).getSurvey_title(), surveyModel);

            //########################################################################
            for(Pair<String, String> pair : surveyModel.getSurvey_results()){
                quesAnsPairList.add(pair);
            }
            //quesAnsPairList.putAll(surveyModel.getRating_answers());

            moveToNextFragment();
        }
        else{
            Log.e(TAG, "Current preference object did not match the required object, check onSurveyInteraction");
        }
    }

    @Override
    public void OnThankYouFragmentInteraction() {
        initWelcomeFragment();
    }


    @Override
    public void onPhotoTaken(CameraModel cameraModel) {
        photoUriPairList.add(cameraModel.getCameraKeyUriPair());
        moveToNextFragment();
    }

    @Override
    public void onRatingSubmit(RatingModel ratingModel) {
        Log.d(TAG, "obtained ratings ");
        if(mCurrentPreference instanceof RatingPreferenceModel){
            mDataModelsMap.put("Ratings", ratingModel);

            //########################################################################

            for (Map.Entry<String, String> item : ratingModel.getRating_answers().entrySet()) {
                String key = item.getKey();
                String value = item.getValue();
                Pair<String, String> pair = new Pair<>(key, value);
                quesAnsPairList.add(pair);
            }

            moveToNextFragment();
        }
        else{
            Log.e(TAG, "Current preference object did not match the required object, check onRatingSubmit");
        }
    }

    @Override
    public void onSuggestionSubmit(SuggestionModel suggestionModel) {
        Log.d(TAG, "obtained suggestions");
        if(mCurrentPreference instanceof SuggestionPreference){
            for(Map.Entry<String, String> item: suggestionModel.getSuggestions_map().entrySet()){
                Pair<String, String> pair = new Pair<>(item.getKey(), item.getValue());
                quesAnsPairList.add(pair);
            }
            moveToNextFragment();
        }
        else{
            Log.e(TAG, "Current preference object did not match the required object, check onSuggestionSubmit");
        }
    }

    //[Disable back button]
    private boolean shouldAllowBack(){
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!shouldAllowBack()) {

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onCancelPressed() {
        Log.d(TAG, "cancel pressed");
        initWelcomeFragment();
    }

}
