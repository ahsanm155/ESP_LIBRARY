package com.esp.library.utilities.common;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.Log;

import com.esp.library.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GoogleFontsLibrary {


    public static int getFontStyle(int fontName)
    {
        if (fontName == R.raw.caveat_regular) {
            return R.raw.caveat_regular;
        }
        else if (fontName == R.raw.dancingscript) {
            return R.raw.dancingscript;
        }
        else if (fontName == R.raw.gloriagallelujah_regular) {
            return R.raw.gloriagallelujah_regular;
        }
        else if (fontName == R.raw.greatvibes_regular) {
            return R.raw.greatvibes_regular;
        }
        else if (fontName == R.raw.indieflower_regular) {
            return R.raw.indieflower_regular;
        }
        else if (fontName == R.raw.kaushanscript_regular) {
            return R.raw.kaushanscript_regular;
        }
        else if (fontName == R.raw.pacifico_regular) {
            return R.raw.pacifico_regular;
        }
        else if (fontName == R.raw.pangolin_regular) {
            return R.raw.pangolin_regular;
        }else if (fontName == R.raw.parisienne_regular) {
            return R.raw.parisienne_regular;
        }
        else if (fontName == R.raw.rocksalt_regular) {
            return R.raw.rocksalt_regular;
        }
        else if (fontName == R.raw.sacramento_regular) {
            return R.raw.sacramento_regular;
        }

        return 0;
    }


    public static Typeface setGoogleFont(Context context, int selectedValueFont)
    {
        return getTypefaceFromRes(context,selectedValueFont);
    }

    public static Typeface Caveat(Context context){
        return getTypefaceFromRes(context,R.raw.caveat_regular);
    }

    public static Typeface Dancingscript(Context context){
        return getTypefaceFromRes(context,R.raw.dancingscript);
    }

    public static Typeface Gloriagallelujah(Context context){
        return getTypefaceFromRes(context,R.raw.gloriagallelujah_regular);
    }

    public static Typeface Greatvibes(Context context){
        return getTypefaceFromRes(context,R.raw.greatvibes_regular);
    }

    public static Typeface Indieflower(Context context){
        return getTypefaceFromRes(context,R.raw.indieflower_regular);
    }

    public static Typeface Kaushanscript(Context context){
        return getTypefaceFromRes(context,R.raw.kaushanscript_regular);
    }

    public static Typeface Pacifico(Context context){
        return getTypefaceFromRes(context,R.raw.pacifico_regular);
    }

    public static Typeface Pangolin(Context context){
        return getTypefaceFromRes(context,R.raw.pangolin_regular);
    }

    public static Typeface Parisienne(Context context){
        return getTypefaceFromRes(context,R.raw.parisienne_regular);
    }

    public static Typeface Rocksalt(Context context){
        return getTypefaceFromRes(context,R.raw.rocksalt_regular);
    }

    public static Typeface Sacramento(Context context){
        return getTypefaceFromRes(context,R.raw.sacramento_regular);
    }



    public static Typeface getTypefaceFromRes(Context context, int resource)
    {
        Typeface typeFace = null;
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(resource);
        }
        catch(Resources.NotFoundException e) {
            Log.e("Typeface", "Could not find font in resources!");
        }

        String outPath = context.getCacheDir() + "/tmp" + System.currentTimeMillis() + ".raw";

        try
        {
            byte[] buffer = new byte[is.available()];
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outPath));

            int l = 0;
            while((l = is.read(buffer)) > 0)
                bos.write(buffer, 0, l);

            bos.close();

            typeFace = Typeface.createFromFile(outPath);

            // clean up
            new File(outPath).delete();
        }
        catch (IOException e)
        {
            Log.e("Typeface", "Error reading in font!");
            return null;
        }

        Log.d("Typeface", "Successfully loaded font.");

        return typeFace;
    }
}
