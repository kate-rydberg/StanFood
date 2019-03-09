package stanford.cs194.stanfood.database;

import android.net.Uri;

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

    public UploadTask uploadImage(Uri imgUri){
        StorageReference uploadPathRef = storageRef.child("images/" + UUID.randomUUID().toString());
        return uploadPathRef.putFile(imgUri);
    }
}
