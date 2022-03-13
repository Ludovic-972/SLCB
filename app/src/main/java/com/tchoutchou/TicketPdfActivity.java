package com.tchoutchou;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.tchoutchou.model.Trip;
import com.tchoutchou.util.PdfGenerator;

import java.util.Objects;

/**
 *Page affichant le billet de l'utilisateur
 */

public class TicketPdfActivity extends AppCompatActivity {

    private View pdf_layout;
    private Bitmap bitmap;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pdf_layout = getLayoutInflater().inflate(R.layout.ticket_pdf_layout,null);
        setContentView(pdf_layout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        Trip trip = (Trip) getIntent().getSerializableExtra("Trip");
        initView(trip);

        bitmap = LoadBitmap();

        Button print = findViewById(R.id.printButton);

        print.setOnClickListener(view -> {
            // Demande la permission en écriture et en lecture si l'application ne l'a pas
            if (!checkPermission()) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 200);
            }
            PdfGenerator pdfGenerator = new PdfGenerator(this,pdf_layout,bitmap,trip);
            pdfGenerator.start();
            try {
                pdfGenerator.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.pdf_saved_text)+" Download/"+getString(R.string.directory_name),
                    Toast.LENGTH_SHORT).show();
        });

    }

    private Bitmap LoadBitmap(){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(1200, 2000);
        pdf_layout.setLayoutParams(layoutParams);
        Bitmap bitmap = Bitmap.createBitmap(pdf_layout.getLayoutParams().width,pdf_layout.getLayoutParams().height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        pdf_layout.draw(canvas);
        return bitmap;
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    private void  initView(Trip trip){

        String[] tripDay = trip.getTripDay().split("-");
        String tmp = tripDay[0];
        tripDay[0] = tripDay[2];
        tripDay[2] = tmp;

        TextView tripDate = findViewById(R.id.tripDate);
        tripDate.setText(String.join("/",tripDay));

        TextView tripInfos = findViewById(R.id.trip);
        tripInfos.setText(trip.getDepartureTown()+" -> "+trip.getArrivalTown());



        SharedPreferences preferences = getSharedPreferences("userInfos", Context.MODE_PRIVATE);
        TextView traveler = findViewById(R.id.traveler);
        String travelerString = "";
        travelerString += preferences.getString("lastname","");
        travelerString += " ";
        travelerString += preferences.getString("firstname","");
        traveler.setText(travelerString);

        TextView departure = findViewById(R.id.departure);
        String departureString = "";
        departureString += trip.getDepartureTown()+" ";
        departureString += getString(R.string.at)+" ";
        departureString += trip.getDepartureHour();
        departure.setText(departureString);

        TextView arrival = findViewById(R.id.arrival);
        String arrivalString = "";
        arrivalString += trip.getArrivalTown()+" ";
        arrivalString += getString(R.string.at)+" ";
        arrivalString += trip.getArrivalHour();
        arrival.setText(arrivalString);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = checkSelfPermission(WRITE_EXTERNAL_STORAGE);
        int permission2 = checkSelfPermission(READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }
}