package timi.mimi.jpeng;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    GridLayout imadzes;
    Spinner img_format;
    TextView img_name;
    String formaty[] = {".png", ".jpg"};
    ImageView iv;
    File PicturesFolderPath;

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

        ReloadDir();
    }

    private void ReloadDir() {

        File[] files = PicturesFolderPath.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    CreateCard();
                    System.out.println(file.getName());
                } else {
                    CreateFolderCard();
                    System.out.println(file.getName());
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