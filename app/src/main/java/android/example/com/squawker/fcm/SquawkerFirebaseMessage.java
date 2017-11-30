package android.example.com.squawker.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by gemeos_valdeci on 29/11/2017.
 */
// COMPLETED (1) Make a new Service in the fcm package that extends from FirebaseMessagingService.
public class SquawkerFirebaseMessage extends FirebaseMessagingService {

    private static final String LOG_TAG = SquawkerFirebaseMessage.class.getSimpleName();

    private static final String JSON_KEY_AUTHOR = SquawkContract.COLUMN_AUTHOR;
    private static final String JSON_KEY_AUTHOR_KEY = SquawkContract.COLUMN_AUTHOR_KEY;
    private static final String JSON_KEY_MESSAGE = SquawkContract.COLUMN_MESSAGE;
    private static final String JSON_KEY_DATE = SquawkContract.COLUMN_DATE;

    private static int NOTIFICATION_MAX_CHARACTERS = 30;


    // COMPLETED (2) As part of the new Service - Override onMessageReceived. This method will
    // be triggered whenever a squawk is received. You can get the data from the squawk
    // message using getData(). When you send a test message, this data will include the
    // following key/value pairs:
    // test: true
    // author: Ex. "TestAccount"
    // authorKey: Ex. "key_test"
    // message: Ex. "Hello world"
    // date: Ex. 1484358455343
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(LOG_TAG, "From: "+ remoteMessage.getFrom());

        Map<String, String> data = remoteMessage.getData();

        //Verifica se existe algum registro no data


        if(data.size() > 0){
            Log.d(LOG_TAG, "Message data payload: "+ data);

            //Envia uma notificação que obteve uma nova mensagem
            sendNotification(data);
            insertSquawk(data);
        }

    }



    // COMPLETED (3) As part of the new Service - If there is message data, get the data using
    // the keys and do two things with it :
    // 1. Display a notification with the first 30 character of the message
    // 2. Use the content provider to insert a new message into the local database
    // Hint: You shouldn't be doing content provider operations on the main thread.
    // If you don't know how to make notifications or interact with a content provider
    // look at the notes in the classroom for help.
    /**
     * Insere um simples squaw no database
     * @param data dados da mensagem
     */
    private void insertSquawk(final Map<String, String> data) {

        AsyncTask<Void, Void, Void> insertSquawkTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                ContentValues newMessage = new ContentValues();
                newMessage.put(SquawkContract.COLUMN_AUTHOR, data.get(JSON_KEY_AUTHOR));
                newMessage.put(SquawkContract.COLUMN_AUTHOR_KEY, data.get(JSON_KEY_AUTHOR_KEY));
                newMessage.put(SquawkContract.COLUMN_DATE, data.get(JSON_KEY_DATE));
                newMessage.put(SquawkContract.COLUMN_MESSAGE, data.get(JSON_KEY_MESSAGE));

                getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI, newMessage);

                return null;
            }
        };

        insertSquawkTask.execute();
    }

    /**
     * Cria e mostra uma simples notificação contendo o FCM recebido
     *
     * @param data dados que tem a mensagem enviada
     */
    private void sendNotification(Map<String, String> data) {
        Intent it = new Intent(this, MainActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Cria a Peding intent para lanças a activit
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,it, PendingIntent.FLAG_ONE_SHOT);

        String message = data.get(JSON_KEY_MESSAGE);
        String author = data.get(JSON_KEY_AUTHOR);

        //Verifica se a mensagem excede o número máximo de caracteres permitido na notificação
        if( message.length() > NOTIFICATION_MAX_CHARACTERS ){
            message = message.substring(0, NOTIFICATION_MAX_CHARACTERS) + "\u2026";
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                                             new NotificationCompat.Builder(this)
                                                                .setSmallIcon(R.drawable.ic_duck)
                                                                .setContentTitle(String.format(getString(R.string.notification_message), author))
                                                                .setContentText(message)
                                                                .setAutoCancel(true)
                                                                .setSound(defaultSoundUri)
                                                                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                             (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
