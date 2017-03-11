package com.example.appdaddy.moviemash.DataService;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.appdaddy.moviemash.Model.User;
import com.example.appdaddy.moviemash.POJO.UploadFileEvent;
import com.example.appdaddy.moviemash.POJO.UploadProgressEvent;
import com.example.appdaddy.moviemash.POJO.UserUpdateEvent;
import com.example.appdaddy.moviemash.util.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 1/25/2017.
 */

public class FBDataService {


    private static final FBDataService _instance = new FBDataService();
    private static final FirebaseDatabase  mDatabase = FirebaseDatabase.getInstance();
    private static final StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

    public static FBDataService getInstance() {
        return _instance;
    }

    //-----------------Database References------------------//

    public DatabaseReference mainRef() {
        return mDatabase.getReference();
    }

    public DatabaseReference usersRef() {
        return mDatabase.getReference(Constants.FIR_CHILD_USERS);
    }

    //-----------------End Database References------------------//

    //-----------------Storage References--------------------//

    public StorageReference mainStorageRef() {
        return mStorageReference;
    }

    public StorageReference profilePicsStorageRef() {
        return mStorageReference.child(Constants.FIR_STORAGE_CHILD_USER_PROFILE_PICS);
    }

    //-----------------End Storage References--------------------//


    public void saveUser(final User user){
        Map<String, Object> properties = user.toMap();
        usersRef().child(user.getUUID()).setValue(properties, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null){
                    EventBus.getDefault().post(new UserUpdateEvent(null));
                }else{
                    EventBus.getDefault().post(new UserUpdateEvent(databaseError.getMessage()));
                }
            }
        });
    }

    public void updateUser(final User user){
        Map<String, Object> properties = user.toMap();
        usersRef().child(user.getUUID()).updateChildren(properties, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null){
                    EventBus.getDefault().post(new UserUpdateEvent(null));
                }else{
                    EventBus.getDefault().post(new UserUpdateEvent(databaseError.getMessage()));
                }
            }
        });
    }

    public void uploadFile(StorageReference filePath, final File file, StorageMetadata metadata){

        Uri fileURI = Uri.fromFile(file);

        UploadTask uploadTask = filePath.putFile(fileURI, metadata);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                EventBus.getDefault().post(new UploadProgressEvent(progress));
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                EventBus.getDefault().post(new UploadFileEvent("Image Upload Paused. Please Check Network State" , null, null));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                EventBus.getDefault().post(new UploadFileEvent("Failed to Upload Image" + exception.getMessage(), null, null));
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                EventBus.getDefault().post(new UploadFileEvent(null, taskSnapshot, file));
            }
        });

    }



    public FBDataService(){
    }

}
