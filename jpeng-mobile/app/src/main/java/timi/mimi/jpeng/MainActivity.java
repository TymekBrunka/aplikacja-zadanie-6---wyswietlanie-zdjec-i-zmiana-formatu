package timi.mimi.jpeng;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

        ActivityResultLauncher<Intent> peakImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    reload(data);
                }
            }
        );

        ((Button)findViewById(R.id.addbtn)).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            peakImageLauncher.launch(intent);
        });
    }

    private void reload(Intent data) {
            if (data != null) {
                Uri imageUri = data.getData();
                CreateCard(imageUri);
            }
    }

    public String getFileNameFromUri(Uri uri) {
        String fileName = null;
        String[] projection = { MediaStore.Images.Media.DISPLAY_NAME };

        // Use the ContentResolver to query the MediaStore
        ContentResolver contentResolver = MainActivity.this.getContentResolver();
        try (Cursor cursor = contentResolver.query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                fileName = cursor.getString(nameIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileName;
    }
    private void saveFileToUri(File tempFile, Uri uri) {
        try (InputStream inputStream = new FileInputStream(tempFile);
             OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void CreateCard(Uri imageUri) {
        LinearLayout imadz = (LinearLayout) getLayoutInflater().inflate(R.layout.zdjecie, null);
        img_format =  imadz.findViewWithTag("img_format");

        //adapter spinnera
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, formaty);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        img_format.setAdapter(spinnerAdapter);

        img_name = imadz.findViewWithTag("img_name");
        String fileName = getFileNameFromUri(imageUri);
        int lastDotIndex = fileName.lastIndexOf(".");
        String format = fileName.substring(lastDotIndex, fileName.length());
        img_name.setText(fileName.substring(0, lastDotIndex));

        int positionToSelect = -1;
        for (int i = 0; i < spinnerAdapter.getCount(); i++) {
            if (spinnerAdapter.getItem(i).endsWith(format)) {
                positionToSelect = i;
                break; // Exit the loop once the item is found
            }
        }
        if (positionToSelect != -1) {
            img_format.setSelection(positionToSelect, false);
        }
        ImageView imgViewAnchor = imadz.findViewWithTag("img");

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), imageUri);
            iv = imgViewAnchor;
            iv.setImageBitmap(bitmap);
        } catch (IOException e) {
            Toast.makeText(this, "Wystąpił błąd wczytywania obrazka", Toast.LENGTH_LONG).show();
        }

        img_format.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String format = (String)parent.getSelectedItem();
                String name = ((TextView)(((LinearLayout)(parent.getParent())).findViewWithTag("img_name"))).getText().toString();
                File tempFile;
                try {
                    tempFile = File.createTempFile("converted_image", format, MainActivity.this.getCacheDir());
                    FileOutputStream out = new FileOutputStream(tempFile);
                    // Compress the resized bitmap to the desired format (e.g., PNG)
                    ImageView img = (ImageView)(((LinearLayout)(parent.getParent())).findViewWithTag("img"));
                    Bitmap bitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();
                    switch (format) {
                        case ".png":
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            break;
                        case ".jpg":
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            break;
                    }
                    out.close();

                    ActivityResultLauncher<Intent> peakSaveImageLauncher = registerForActivityResult(
                            new ActivityResultContracts.StartActivityForResult(), result -> {
                                if (result.getResultCode() == RESULT_OK) {
                                    if (result.getData() != null) {
                                        Uri uri = result.getData().getData();
                                        saveFileToUri(tempFile, uri);
                                    }
                                }
                            }
                    );

                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.putExtra(Intent.EXTRA_TITLE, name + format);
                    peakSaveImageLauncher.launch(intent);
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "Wystąpił błąd konwertowania obrazka", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imadzes.addView(imadz);
    }

    public void CreateFolderCard() {
        LinearLayout imadz = (LinearLayout) getLayoutInflater().inflate(R.layout.folder, null);
        iv = imadz.findViewWithTag("img");
        iv.setImageResource(R.drawable.ic_launcher_foreground);

        imadzes.addView(imadz);
    }
}