package stanford.cs194.stanfood.helpers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import stanford.cs194.stanfood.authentication.Authentication;
import stanford.cs194.stanfood.database.Database;

public class FirebaseInstanceIdAccessor {
    private static final String TAG = "FirebaseInstanceId";
    private Database db;
    private Authentication auth;

    public FirebaseInstanceIdAccessor(Database db, Authentication auth) {
        this.db = db;
        this.auth = auth;
    }

    public void uploadInstanceId() {
        if (auth.getCurrentUser() == null) return;
        com.google.firebase.iid.FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        String userId = auth.getCurrentUser().getUid();
                        db.updateUserInstanceId(userId, token);
                    }
                });
    }
}
