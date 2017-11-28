package android.example.com.squawker.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by gemeos_valdeci on 28/11/2017.
 */

public class SquawkFirebaseInstanceIdService extends FirebaseInstanceIdService {
    // COMPLETED (1) Make a new package for your FCM service classes called "fcm"
    // COMPLETED (2) Create a new Service class that extends FirebaseInstanceIdService.
    // You'll need to implement the onTokenRefresh method. Simply have it print out
    // the new token.

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
    }
}
