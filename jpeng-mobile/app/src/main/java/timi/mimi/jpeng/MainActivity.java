package timi.mimi.jpeng;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    GridLayout imadzes;
    Spinner img_format;
    TextView img_name;
    String formaty[] = {".png", ".jpg"};
    ImageView iv;
    File PicturesFolderPath;
    File CurrentDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imadzes = findViewById(R.id.imadzes);
        PicturesFolderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File directory = PicturesFolderPath;
        System.out.println(directory.toString());
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            // User may have declined earlier, ask Android if we should show him a reason
            if (shouldShowRequestPermissionRationale(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                ReloadDir();
            }
//            else if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    this, Manifest.permission.MANAGE_EXTERNAL_STORAGE
//            )) {
//            }
            else {
                ActivityCompat.requestPermissions(this,  new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 101);
            }
        } else {
            ReloadDir();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ReloadDir();
                } else {
                    // permission denied
                    // Disable the functionality that depends on this permission.
                    System.out.println("well fk.");
                }
                return;
            }

            default:
                break;
        }
    }

    private void ReloadDir() {
        System.out.println("Relowding");
        if (CurrentDir != null && CurrentDir.isDirectory()) {
            File[] files = CurrentDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        CreateCard();
                    } else {
                        CreateFolderCard();
                    }
                }
            }
        }
    }

    public void CreateCard() {
        LinearLayout imadz = (LinearLayout) getLayoutInflater().inflate(R.layout.zdjecie, null);
        img_format =  imadz.findViewWithTag("img_format");

        //adapter spinnera
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, formaty);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        img_format.setAdapter(spinnerAdapter);

        imadzes.addView(imadz);
    }

    public void CreateFolderCard() {
        LinearLayout imadz = (LinearLayout) getLayoutInflater().inflate(R.layout.folder, null);
        iv = imadz.findViewWithTag("img");
        iv.setImageResource(R.drawable.ic_launcher_foreground);

        imadzes.addView(imadz);
    }
}