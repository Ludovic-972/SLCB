package com.tchoutchou.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.tchoutchou.R;
import com.tchoutchou.model.Trip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfGenerator extends Thread{

    private final Context context;
    private Bitmap bitmap;
    private final Trip trip;
    private final View view;



    public PdfGenerator(Context context,View view, Bitmap bitmap, Trip trip){
        this.context = context;
        this.bitmap = bitmap;
        this.trip = trip;
        this.view = view;
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

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertedWidth,convertedHeight,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        bitmap = Bitmap.createScaledBitmap(bitmap,convertedWidth,convertedHeight,true);

        canvas.drawBitmap(bitmap,0,0,null);

        pdfDocument.finishPage(page);


        String dirName = context.getString(R.string.directory_name);
        File dir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                dirName);

        if (!dir.exists()){
            dir.mkdirs();
        }

        File file = new File(dir,
                String.join("-",tripDate)+
                        "_"+trip.getDepartureTown()+
                        "_"+trip.getDepartureTown()+
                        "_"+trip.getArrivalTown()+".pdf");


        try {
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            fos.close();
            pdfDocument.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
