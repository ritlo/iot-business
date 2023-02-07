import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class init{
    void initialiseFirebase() throws IOException {
        FileInputStream serviceAccount = new FileInputStream(
                "/home/pi/Desktop/javaprojectopenstore/serviceAccountKey.json");

        // FirebaseOptions options = new FirebaseOptions.Builder()
        // .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        // .setDatabaseUrl("https://fireproject-ee4f5.firebaseio.com").build();

        FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://fireproject-ee4f5.firebaseio.com").build();

        FirebaseApp.initializeApp(options);
    }

}