package net.prahasiwi.laporankeuangan.helper;

import android.graphics.Bitmap;

/**
 * Created by PRAHASIWI on 31/01/2018.
 */

public class BitmapHelper {
    private Bitmap bitmap = null;
    private static final BitmapHelper instance = new BitmapHelper();

    public BitmapHelper(){

    }
    public static BitmapHelper getInstance(){
        return instance;
    }
    public Bitmap getBitmap(){
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }
}
