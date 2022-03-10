package com.tchoutchou.util;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.tchoutchou.model.Trip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfGenerator implements Runnable{

    private Context context;
    private Bitmap bitmap;
    private Trip trip;



    public PdfGenerator(Context context, Bitmap bitmap, Trip trip){
        this.context = context;
        this.bitmap = bitmap;
        this.trip = trip;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        String[] tripDate = trip.getTripDay().split("-");
        String tmp = tripDate[0];
        tripDate[0] = tripDate[2];
        tripDate[2] = tmp;


        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float width = displayMetrics.widthPixels;
        float height = displayMetrics.heightPixels;

        int convertedHeight = (int) height;
        int convertedWidth = (int) width;

        Log.d("PdfGenerator","width = "+ convertedWidth);
        Log.d("PdfGenerator","height = "+convertedHeight);
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertedWidth,convertedHeight,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap,convertedWidth,convertedHeight,true);
        Log.d("PdfGenerator","bitmap = "+bitmap.toString());
        canvas.drawBitmap(bitmap,0,0,null);

        pdfDocument.finishPage(page);


        File dir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Mes Billets TchouTchou");

        if (!dir.exists()){
            dir.mkdirs();
            Log.d("PdfGenerator","Le répertoire à été crée");
        }else{
            Log.d("PdfGenerator","Le répertoire existe");
        }

        Log.d("PdfGenerator","répertoire = "+dir.getAbsolutePath());

        File file = new File(dir,String.join("-",tripDate)+"_"+trip.getDepartureTown()+"_"+trip.getArrivalTown()+".pdf");

        Log.d("PdfGenerator","fichier = "+file.getAbsolutePath());

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(context, "Votre billet à été enregistré dans "+dir.getCanonicalPath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
    }
}
