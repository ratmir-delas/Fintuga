package com.tugasoft.fintuga.asyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.AsyncTask;

import androidx.fragment.app.Fragment;

import com.tugasoft.fintuga.fragments.AddExpenseFragment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCompression extends AsyncTask<String, Void, String> {
    private static final float maxHeight = 1280.0f;
    private static final float maxWidth = 1280.0f;
    private final Fragment mFragment;
    private ProgressDialog dialog;
    private Activity mActivity;

    public ImageCompression(Fragment fragment) {
        this.mFragment = fragment;
        this.mActivity = fragment.getActivity();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int i, int i2) {
        int i3;
        int i4 = options.outHeight;
        int i5 = options.outWidth;
        if (i4 > i2 || i5 > i) {
            i3 = Math.round(((float) i4) / ((float) i2));
            int round = Math.round(((float) i5) / ((float) i));
            if (i3 >= round) {
                i3 = round;
            }
        } else {
            i3 = 1;
        }
        while (((float) (i5 * i4)) / ((float) (i3 * i3)) > ((float) (i * i2 * 2))) {
            i3++;
        }
        return i3;
    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
        showProgressDialog("Please wait ...");
    }

    @Override
    public String doInBackground(String... strArr) {
        if (strArr.length == 0 || strArr[0] == null) {
            return null;
        }
        return compressImage(strArr[0], strArr[1]);
    }

    @Override
    public void onPostExecute(String str) {
        super.onPostExecute(str);
        cancelProgressDialog();
        Fragment fragment = this.mFragment;
        if (fragment != null && (fragment instanceof AddExpenseFragment)) {
            ((AddExpenseFragment) fragment).uploadDetailsToServer(str);
        }
    }

    public String compressImage(String str, String str2) {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap decodeFile = BitmapFactory.decodeFile(str, options);
        int i = options.outHeight;
        int i2 = options.outWidth;
        float f = (float) i2;
        float f2 = (float) i;
        float f3 = f / f2;
        if (f2 > 1280.0f || f > 1280.0f) {
            if (f3 < 1.0f) {
                i2 = (int) ((1280.0f / f2) * f);
                i = 1280;
            } else {
                i = f3 > 1.0f ? (int) ((1280.0f / f) * f2) : 1280;
                i2 = 1280;
            }
        }
        options.inSampleSize = calculateInSampleSize(options, i2, i);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16384];
        try {
            decodeFile = BitmapFactory.decodeFile(str, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        try {
            bitmap = Bitmap.createBitmap(i2, i, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
            bitmap = null;
        }
        float f4 = (float) i2;
        float f5 = f4 / ((float) options.outWidth);
        float f6 = (float) i;
        float f7 = f6 / ((float) options.outHeight);
        float f8 = f4 / 2.0f;
        float f9 = f6 / 2.0f;
        Matrix matrix = new Matrix();
        matrix.setScale(f5, f7, f8, f9);
        Canvas canvas = new Canvas(bitmap);
        canvas.setMatrix(matrix);
        canvas.drawBitmap(decodeFile, f8 - ((float) (decodeFile.getWidth() / 2)), f9 - ((float) (decodeFile.getHeight() / 2)), new Paint(2));
        if (decodeFile != null) {
            decodeFile.recycle();
        }
        try {
            int attributeInt = new ExifInterface(str).getAttributeInt("Orientation", 0);
            Matrix matrix2 = new Matrix();
            if (attributeInt == 6) {
                matrix2.postRotate(90.0f);
            } else if (attributeInt == 3) {
                matrix2.postRotate(180.0f);
            } else if (attributeInt == 8) {
                matrix2.postRotate(270.0f);
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix2, true);
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(str2));
        } catch (FileNotFoundException e4) {
            e4.printStackTrace();
        }
        return bitmap != null ? str2 : "";
    }

    private void cancelProgressDialog() {
        ProgressDialog progressDialog = this.dialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
            this.dialog = null;
        }
    }

    private void showProgressDialog(String str) {
        this.dialog = ProgressDialog.show(this.mActivity, "", str, true, false, (DialogInterface.OnCancelListener) null);
    }
}
