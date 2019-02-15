package stanford.cs194.stanfood.authentication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import stanford.cs194.stanfood.database.Database;

public class Authentication {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private Database db = new Database();

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void addCurrentUserToDatabase() {
        FirebaseUser user = getCurrentUser();
        db.createUser(user.getUid(), user.getEmail(), user.getDisplayName());
    }
}
