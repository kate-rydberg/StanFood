package stanford.cs194.stanfood.database;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class Storage {
    final private String storagePath = "gs://stanfood-e7255.appspot.com";
    private FirebaseStorage storage;
    private StorageReference storageRef;

    public Storage(){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(storagePath);
    }

    public Task uploadImage(Uri imgUri){
        final StorageReference uploadPathRef = storageRef.child("images/" + UUID.randomUUID().toString());
        return uploadPathRef.putFile(imgUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()) {
                    throw task.getException();
                }
                return uploadPathRef.getDownloadUrl();
            }
        });
    }

}
