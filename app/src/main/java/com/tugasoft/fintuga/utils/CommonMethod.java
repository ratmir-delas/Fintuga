package com.tugasoft.fintuga.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tugasoft.fintuga.activity.ExpenseManagerLoadingActivity;
import com.tugasoft.fintuga.activity.FirebaseAuthenticationActivity;
import com.tugasoft.fintuga.activity.ShowPDFActivity;
import com.tugasoft.fintuga.application.AppCore;
import com.tugasoft.fintuga.BuildConfig;
import com.tugasoft.fintuga.models.Expense;
import com.tugasoft.fintuga.models.MasterExpenseModel;
import com.tugasoft.fintuga.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommonMethod {

    public static Dialog mDialog;
    private final String TAG = "Common Class";

    public static boolean isNetworkConnected(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static boolean isDeviceSupportCamera(Activity activity) {
        return activity.getPackageManager().hasSystemFeature("android.hardware.camera");
    }

    public static int getFileSize(File file) {
        int i = 0;
        try {
            i = Integer.parseInt(String.valueOf(file.length() / PlaybackState.ACTION_PLAY_FROM_MEDIA_ID));
            Log.e("fileSize", "" + i);
            return i;
        } catch (Exception e) {
            e.printStackTrace();
            return i;
        }
    }

    public static void displayNetworkImage(Activity activity, String str, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        try {
            ((RequestBuilder) ((RequestBuilder) Glide.with(AppCore.getAppContext()).load(str).apply(requestOptions).thumbnail(0.1f).error(ContextCompat.getDrawable(activity, R.drawable.error_downloading_icon))).placeholder(ContextCompat.getDrawable(activity, R.drawable.loading_transparent)).diskCacheStrategy(DiskCacheStrategy.ALL)).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap StringToBitMap(String str) {
        try {
            byte[] decode = Base64.decode(str, 0);
            return BitmapFactory.decodeByteArray(decode, 0, decode.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static Map<String, Double> sortByComparator(Map<String, Double> map, final boolean z) {
        LinkedList<Map.Entry<String, Double>> linkedList = new LinkedList<>(map.entrySet());
        Collections.sort(linkedList, (entry, entry2) -> {
            if (z) {
                return entry.getValue().compareTo(entry2.getValue());
            }
            return entry2.getValue().compareTo(entry.getValue());
        });
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Map.Entry entry : linkedList) {
            linkedHashMap.put(entry.getKey(), entry.getValue());
        }
        return linkedHashMap;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model.toUpperCase();
        }
        return manufacturer.toUpperCase() + " " + model;
    }

    public static void hideKeyboard(Activity activity) {
        try {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAlertForChangeDate(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle((CharSequence) activity.getString(R.string.title_alert_date));
        builder.setCancelable(false);
        builder.setMessage((CharSequence) activity.getString(R.string.msg_alert_change_date)).setCancelable(true).setPositiveButton((CharSequence) activity.getString(R.string.action_ok), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.startActivityForResult(new Intent("android.settings.SETTINGS"), 0);
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    public static boolean isAutoDateTimeEnabled(Context context) {
        try {
            return !Settings.System.getString(context.getContentResolver(), "auto_time").contentEquals("0");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setWindowFlag(Activity activity, int i, boolean z) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (z) {
            attributes.flags = i | attributes.flags;
        } else {
            attributes.flags = (~i) & attributes.flags;
        }
        window.setAttributes(attributes);
    }

    public static void showAlertForLogout(final Activity activity, final FirebaseAuth firebaseAuth) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle((CharSequence) activity.getString(R.string.title_alert_confirm_logout));
        builder.setMessage((CharSequence) activity.getString(R.string.msg_alert_confirm_logout)).setCancelable(true).setPositiveButton((CharSequence) activity.getString(R.string.action_yes), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                if (firebaseAuth != null) {
                    firebaseAuth.signOut();
                }
                MySharedPreferences.clearSP();
                activity.startActivity(new Intent(activity, FirebaseAuthenticationActivity.class));
                activity.finish();
            }
        }).setNegativeButton((CharSequence) activity.getString(R.string.action_cancel), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    public static void showAlertWithOk(Activity activity, String str, String str2, String str3) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(str);
        builder.setMessage(str2).setCancelable(true).setPositiveButton((CharSequence) str3, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    public static String formatPrice(double d) {
        return Constant.decimalFormat.format(d);
    }

    public static void showConnectionAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle((CharSequence) context.getString(R.string.title_alert_connection));
        builder.setMessage((CharSequence) context.getString(R.string.msg_alert_connection)).setPositiveButton((CharSequence) context.getResources().getString(R.string.action_ok), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog create = builder.create();
        create.setCancelable(false);
        create.show();
    }

    public static void showDialogForFileStorePath(final Activity activity, String str, final String str2, String str3, String str4) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle((CharSequence) "Data downloaded success");
        builder.setMessage((CharSequence) str).setPositiveButton((CharSequence) str3, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).setNegativeButton((CharSequence) str4, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                CommonMethod.openFile(activity, str2);
            }
        });
        AlertDialog create = builder.create();
        create.setCancelable(false);
        create.setCanceledOnTouchOutside(false);
        create.show();
    }

    static void openFile(Activity activity, String str) {
        try {
            Uri uriForFile = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", new File(str));
            Intent intent = new Intent("android.intent.action.VIEW");
            if (!str.contains(".doc")) {
                if (!str.contains(".docx")) {
                    if (str.contains(".pdf")) {
                        intent.setDataAndType(uriForFile, "application/pdf");
                    } else {
                        if (!str.contains(".ppt")) {
                            if (!str.contains(".pptx")) {
                                if (!str.contains(".xls")) {
                                    if (!str.contains(".xlsx")) {
                                        if (str.contains(".zip")) {
                                            intent.setDataAndType(uriForFile, "application/zip");
                                        } else if (str.contains(".rar")) {
                                            intent.setDataAndType(uriForFile, "application/x-rar-compressed");
                                        } else if (str.contains(".rtf")) {
                                            intent.setDataAndType(uriForFile, "application/rtf");
                                        } else {
                                            if (!str.contains(".wav")) {
                                                if (!str.contains(".mp3")) {
                                                    if (str.contains(".gif")) {
                                                        intent.setDataAndType(uriForFile, "image/gif");
                                                    } else {
                                                        if (!str.contains(".jpg") && !str.contains(".jpeg")) {
                                                            if (!str.contains(".png")) {
                                                                if (str.contains(".txt")) {
                                                                    intent.setDataAndType(uriForFile, "text/plain");
                                                                } else {
                                                                    if (!str.contains(".3gp") && !str.contains(".mpg") && !str.contains(".mpeg") && !str.contains(".mpe") && !str.contains(".mp4")) {
                                                                        if (!str.contains(".avi")) {
                                                                            intent.setDataAndType(uriForFile, "*/*");
                                                                        }
                                                                    }
                                                                    intent.setDataAndType(uriForFile, "video/*");
                                                                }
                                                            }
                                                        }
                                                        intent.setDataAndType(uriForFile, "image/jpeg");
                                                    }
                                                }
                                            }
                                            intent.setDataAndType(uriForFile, "audio/x-wav");
                                        }
                                    }
                                }
                                intent.setDataAndType(uriForFile, "application/vnd.ms-excel");
                            }
                        }
                        intent.setDataAndType(uriForFile, "application/vnd.ms-powerpoint");
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    activity.startActivity(intent);
                }
            }
            intent.setDataAndType(uriForFile, "application/msword");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(activity, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }

    public static void showConnectionAlertAndRetry(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.title_alert_connection));
        builder.setMessage(context.getString(R.string.msg_alert_connection)).setPositiveButton((CharSequence) context.getResources().getString(R.string.action_retry), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

                if (context instanceof ExpenseManagerLoadingActivity) {
                    ((ExpenseManagerLoadingActivity) context).reloadFirebase();
                }
            }
        }).setNegativeButton((CharSequence) context.getResources().getString(R.string.action_cancel), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

                if (context instanceof ExpenseManagerLoadingActivity) {
                    ((ExpenseManagerLoadingActivity) context).destroyActivity();
                }
            }
        });
        AlertDialog create = builder.create();
        create.setCancelable(false);
        create.show();
    }

    public static void cancelProgressDialog() {
        Dialog dialog = mDialog;
        if (dialog != null && dialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public static void showProgressDialog(Activity activity) {
        if (activity != null && mDialog == null) {
            Dialog dialog = new Dialog(activity);
            mDialog = dialog;
            dialog.requestWindowFeature(1);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setContentView(R.layout.dialog);


            mDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(activity, android.R.color.transparent));
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(mDialog.getWindow().getAttributes());
            layoutParams.width = -1;
            layoutParams.height = -1;
            mDialog.getWindow().setAttributes(layoutParams);
            mDialog.show();
        }
    }

    public static void showProgressDialogForDownloading(Activity activity) {
        if (activity != null && mDialog == null) {
            Dialog dialog = new Dialog(activity);
            mDialog = dialog;
            dialog.requestWindowFeature(1);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setContentView(R.layout.dialog_download);
            mDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(activity, android.R.color.transparent));
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(mDialog.getWindow().getAttributes());
            layoutParams.width = -1;
            layoutParams.height = -1;
            mDialog.getWindow().setAttributes(layoutParams);
            mDialog.show();
        }
    }

    public static File getApplicationDirectory(SubDirectory subDirectory, Context context, boolean z) {
        File file;
        if (!"mounted".equals(Environment.getExternalStorageState())) {
            File file2 = new File(getMountedPaths(), context.getResources().getString(R.string.app_name));
            if (!file2.exists()) {
                file2.mkdirs();
            }
            file2.setReadable(true);
            file2.setWritable(true);
            return file2;
        }
        if (z) {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + context.getResources().getString(R.string.app_name));
        } else {
            file = context.getFilesDir();
        }
        if (file == null || (!file.exists() && !file.mkdirs())) {
            return null;
        }
        if (subDirectory == null) {
            return file;
        }
        File file3 = new File(file, subDirectory.toString());
        if (file3.exists() || file3.mkdirs()) {
            return file3;
        }
        return null;
    }

    private static String getMountedPaths() {
        Process process;
        String[] split;
        try {
            process = Runtime.getRuntime().exec("mount");
        } catch (IOException e) {
            e.printStackTrace();
            process = null;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String str = "";
        while (true) {
            try {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                } else if (!readLine.contains("secure")) {
                    if (!readLine.contains("asec")) {
                        if (readLine.contains("fat")) {
                            String[] split2 = readLine.split(" ");
                            if (split2 != null && split2.length > 1) {
                                str = split2[1];
                            }
                        } else if (readLine.contains("fuse") && (split = readLine.split(" ")) != null && split.length > 1) {
                            str = split[1];
                        }
                    }
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return str;
    }

    public static void startSound(Activity activity, String str) {
        AssetFileDescriptor assetFileDescriptor;
        try {
            assetFileDescriptor = activity.getResources().getAssets().openFd(str);
        } catch (IOException e) {
            e.printStackTrace();
            assetFileDescriptor = null;
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
    }

    public static ArrayList<File> getImagesListFiles(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            Arrays.sort(listFiles, new Comparator() {
                public int compare(Object obj, Object obj2) {
                    File file = (File) obj;
                    File file2 = (File) obj2;
                    if (file.lastModified() > file2.lastModified()) {
                        return -1;
                    }
                    return file.lastModified() < file2.lastModified() ? 1 : 0;
                }
            });
            for (File file2 : listFiles) {
                if (file2.getName().endsWith(".png")) {
                    if (!arrayList.contains(file2)) {
                        arrayList.add(file2);
                    }
                } else if (file2.getName().endsWith(".pdf") && !arrayList.contains(file2)) {
                    arrayList.add(file2);
                }
            }
        }
        return arrayList;
    }

    public static void ShareImageFile(Activity activity, File file) {
        try {
            activity.startActivity(ShareCompat.IntentBuilder.from(activity).setType("image/jpg").setStream(FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file)).createChooserIntent().addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void viewImage(Activity activity, File file) {
        Uri uriForFile = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uriForFile, "image/*");
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void viewPDF(Activity activity, File file) {
        Toast.makeText(activity, "Saved!", Toast.LENGTH_SHORT).show();
        activity.startActivity(new Intent(activity, ShowPDFActivity.class).putExtra("pdfFile", file.getAbsolutePath()));
    }

    public static void addEmptyLine(Paragraph paragraph, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            paragraph.add((Element) new Paragraph(" "));
        }
    }

    public void downloadSelectedMonthDataIntoPDF(final Activity activity, final String str, final ArrayList<MasterExpenseModel> arrayList) {
        showProgressDialogForDownloading(activity);
        new Handler().postDelayed(() -> AsyncTask.execute(() -> {
            Throwable th;
            Document document;

            Exception exc;
            Exception e;
            int i;
            Document document2;

            Font font = new Font(Font.FontFamily.TIMES_ROMAN, 18.0f, 1);
            Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 14.0f, 1);
            final File file = new File(CommonMethod.getApplicationDirectory(SubDirectory.APP_PDF_DATA, activity, true), str + ".pdf");
            Document document3 = new Document();
            try {
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (Exception e2) {
                        exc = e2;

                    } catch (Throwable th2) {
                        th = th2;
                        document = document3;
                        document.close();
                        throw th;
                    }
                }
                PdfWriter.getInstance(document3, new FileOutputStream(file));
                document3.open();
                Paragraph paragraph = new Paragraph(new Chunk("TRANSACTION HISTORY", font));
                paragraph.setAlignment(1);
                document3.add(paragraph);
                Paragraph paragraph2 = new Paragraph(new Chunk(str, font2));
                paragraph2.setAlignment(1);
                document3.add(paragraph2);
                Paragraph paragraph3 = new Paragraph();
                CommonMethod.addEmptyLine(paragraph3, 1);
                document3.add(paragraph3);
                PdfPTable pdfPTable = new PdfPTable(5);
                pdfPTable.setWidthPercentage(100.0f);
                pdfPTable.setWidths(new float[]{16.0f, 21.0f, 21.0f, 18.0f, 24.0f});
                PdfPCell pdfPCell = new PdfPCell(new Paragraph(new Chunk("Date", font2)));
                pdfPCell.setHorizontalAlignment(1);
                pdfPCell.setVerticalAlignment(1);
                pdfPCell.setPadding(5.0f);
                pdfPTable.addCell(pdfPCell);
                PdfPCell pdfPCell2 = new PdfPCell(new Paragraph(new Chunk("Income", font2)));
                pdfPCell2.setHorizontalAlignment(1);
                pdfPCell2.setVerticalAlignment(1);
                pdfPCell2.setPadding(5.0f);
                pdfPTable.addCell(pdfPCell2);
                PdfPCell pdfPCell3 = new PdfPCell(new Paragraph(new Chunk("Expense", font2)));
                pdfPCell3.setHorizontalAlignment(1);
                pdfPCell3.setVerticalAlignment(1);
                pdfPCell3.setPadding(5.0f);
                pdfPTable.addCell(pdfPCell3);
                PdfPCell pdfPCell4 = new PdfPCell(new Paragraph(new Chunk("Category", font2)));
                pdfPCell4.setHorizontalAlignment(1);
                pdfPCell4.setVerticalAlignment(1);
                pdfPCell4.setPadding(5.0f);
                pdfPTable.addCell(pdfPCell4);
                PdfPCell pdfPCell5 = new PdfPCell(new Paragraph(new Chunk("Description", font2)));
                pdfPCell5.setHorizontalAlignment(1);
                pdfPCell5.setVerticalAlignment(1);
                pdfPCell5.setPadding(5.0f);
                pdfPTable.addCell(pdfPCell5);
                pdfPTable.setHeaderRows(1);
                int size = arrayList.size();


                Log.e(TAG, "arrayList: ====   " + size);
                double j = 0;
                long j2 = 0;
                int i2 = 0;
                while (i2 < size) {

                    Log.e(TAG, "arrayList: ==I@@@==   " + i2);
                    try {
                        ArrayList<Expense> detailList = ((MasterExpenseModel) arrayList.get(i2)).getExpenses();
                        int size2 = detailList.size();
                        int i3 = 0;
                        while (i3 < size2) {
                            Expense expense = detailList.get(i3);
                            PdfPCell pdfPCell6 = new PdfPCell(new Phrase(expense.getDate()));
                            pdfPCell6.setHorizontalAlignment(1);
                            pdfPCell6.setVerticalAlignment(1);
                            pdfPCell6.setPadding(5.0f);
                            pdfPTable.addCell(pdfPCell6);
                            if (!expense.isExpense()) {
                                double amount = j + expense.getAmount();

                                Log.e(TAG, "amount: ====   " + amount);
                                String str1 = MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "");
                                StringBuilder sb = new StringBuilder();
                                sb.append(" ");
                                i = i3;
                                document2 = document3;
                                try {
                                    sb.append(CommonMethod.formatPrice((double) expense.getAmount()));
                                    PdfPCell pdfPCell7 = new PdfPCell(new Phrase(str1.concat(sb.toString())));
                                    pdfPCell7.setHorizontalAlignment(1);
                                    pdfPCell7.setVerticalAlignment(1);
                                    pdfPCell7.setPadding(5.0f);
                                    pdfPTable.addCell(pdfPCell7);
                                    PdfPCell pdfPCell8 = new PdfPCell(new Phrase(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(String.valueOf(0))));
                                    pdfPCell8.setHorizontalAlignment(1);
                                    pdfPCell8.setVerticalAlignment(1);
                                    pdfPCell8.setPadding(5.0f);
                                    pdfPTable.addCell(pdfPCell8);
                                    j = amount;
                                } catch (Exception e3) {

                                    exc = e3;
                                    document = document2;
                                    try {
                                        exc.printStackTrace();
                                        document.close();
                                        activity.runOnUiThread(new Runnable() {


                                            public void run() {
                                                CommonMethod.cancelProgressDialog();
                                            }
                                        });
                                    } catch (Throwable th3) {
                                        th = th3;
                                        th = th;
                                        document.close();
                                        throw th;
                                    }
                                } catch (Throwable th4) {
                                    th = th4;
                                    document = document2;
                                    document.close();
                                    throw th;
                                }
                            } else {
                                i = i3;
                                document2 = document3;
                                if (expense.isExpense()) {
                                    j2 += expense.getAmount();
                                    PdfPCell pdfPCell9 = new PdfPCell(new Phrase(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(String.valueOf(0))));
                                    pdfPCell9.setHorizontalAlignment(1);
                                    pdfPCell9.setVerticalAlignment(1);
                                    pdfPCell9.setPadding(5.0f);
                                    pdfPTable.addCell(pdfPCell9);
                                    PdfPCell pdfPCell10 = new PdfPCell(new Phrase(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" " + CommonMethod.formatPrice((double) expense.getAmount()))));
                                    pdfPCell10.setHorizontalAlignment(1);
                                    pdfPCell10.setVerticalAlignment(1);
                                    pdfPCell10.setPadding(5.0f);
                                    pdfPTable.addCell(pdfPCell10);
                                }
                            }
                            PdfPCell pdfPCell11 = new PdfPCell(new Phrase(expense.getCategory().toUpperCase()));
                            pdfPCell11.setHorizontalAlignment(1);
                            pdfPCell11.setVerticalAlignment(1);
                            pdfPCell11.setPadding(5.0f);
                            pdfPTable.addCell(pdfPCell11);
                            PdfPCell pdfPCell12 = new PdfPCell(new Phrase(expense.getDescription()));
                            pdfPCell12.setHorizontalAlignment(1);
                            pdfPCell12.setVerticalAlignment(1);
                            pdfPCell12.setPadding(5.0f);
                            pdfPTable.addCell(pdfPCell12);
                            i3 = i + 1;
                            size2 = size2;
                            detailList = detailList;
                            document3 = document2;
                        }
                        i2++;

                        size = size;

                    } catch (Exception e4) {

                        exc = e4;
                        document = document3;
                        exc.printStackTrace();
                        document.close();
                        activity.runOnUiThread(() -> CommonMethod.cancelProgressDialog());
                    } catch (Throwable th5) {
                        th = th5;
                        document = document3;
                        document.close();
                        throw th;
                    }
                }
                try {
                    PdfPCell pdfPCell13 = new PdfPCell(new Paragraph(new Chunk("Total", font2)));
                    pdfPCell13.setHorizontalAlignment(1);
                    pdfPCell13.setPadding(5.0f);
                    pdfPTable.addCell(pdfPCell13);
                    PdfPCell pdfPCell14 = new PdfPCell(new Paragraph(new Chunk(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" " + CommonMethod.formatPrice((double) j)), font2)));
                    pdfPCell14.setHorizontalAlignment(1);
                    pdfPCell14.setPadding(5.0f);
                    pdfPTable.addCell(pdfPCell14);
                    PdfPCell pdfPCell15 = new PdfPCell(new Paragraph(new Chunk(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" " + CommonMethod.formatPrice((double) j2)), font2)));
                    pdfPCell15.setHorizontalAlignment(1);
                    pdfPCell15.setPadding(5.0f);
                    pdfPTable.addCell(pdfPCell15);
                    PdfPCell pdfPCell16 = new PdfPCell(new Paragraph(""));
                    pdfPCell16.setHorizontalAlignment(1);
                    pdfPCell16.setColspan(2);
                    pdfPCell16.setPadding(5.0f);
                    pdfPTable.addCell(pdfPCell16);
                    document = document3;
                    try {
                        document.add(pdfPTable);

                        try {
                            activity.runOnUiThread(() -> {
                                Log.e(TAG, "Exception: =====successfully ");
                                CommonMethod.showDialogForFileStorePath(activity, "Data downloaded successfully for the month ' " + str + " ' at below mobile location.\n\n " + file.toString(), file.toString(), activity.getString(R.string.action_ok), "Open File");
                            });
                        } catch (Exception e5) {
                            e = e5;
                        }
                    } catch (Exception e6) {
                        e = e6;
                        Log.e(TAG, "Exception: ===== " + e6);
                        exc = e;
                        exc.printStackTrace();
                        document.close();
                        activity.runOnUiThread(() -> CommonMethod.cancelProgressDialog());
                    } catch (Throwable th6) {
                        th = th6;
                        th = th;
                        document.close();
                        throw th;
                    }
                } catch (Exception e7) {
                    e = e7;
                    Log.e(TAG, "Exception: ===== " + e7);
                    document = document3;
                    exc = e;
                    exc.printStackTrace();
                    document.close();
                    activity.runOnUiThread(() -> CommonMethod.cancelProgressDialog());
                } catch (Throwable th7) {
                    th = th7;
                    document = document3;
                    th = th;
                    document.close();
                    throw th;
                }
            } catch (Exception e8) {
                e = e8;
                Log.e(TAG, "Exception: ===== " + e8.toString());
                document = document3;
                exc = e;
                exc.printStackTrace();
                document.close();
                activity.runOnUiThread(new Runnable() {


                    public void run() {
                        CommonMethod.cancelProgressDialog();
                    }
                });
            } catch (Throwable th8) {


                Log.e(TAG, "Exception: ===== " + th8);
                th = th8;
                document = document3;
                th = th;
                document.close();
                try {
                    throw th;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
            document.close();
            activity.runOnUiThread(() -> CommonMethod.cancelProgressDialog());
        }), 3000);
    }


    public enum SubDirectory {
        APP_PDF_DATA(PdfObject.TEXT_PDFDOCENCODING),
        APP_XLS_DATA("XLS"),
        APP_LOG_DIRECTORY("Log"),
        APP_DOWNLOAD_IMAGE("Download");

        private final String subDirectoryName;

        private SubDirectory(String str) {
            this.subDirectoryName = str;
        }

        public String toString() {
            return this.subDirectoryName;
        }
    }


    public void saveImage(Activity activity, String folderName) {
        OutputStream outputStream = null;

        File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File dir = new File(filePath.getAbsolutePath() + "/" + folderName);
        dir.mkdir();

        String fName = String.format("%d.jpg", System.currentTimeMillis());
        File outFile = new File(dir, fName);
        try {
            outputStream = new FileOutputStream(outFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(activity, "Image Saved\nInternal Storage/" + folderName + "/", Toast.LENGTH_LONG).show();
    }


    public static int getNumberOfShortMonth(Context context, String monthStr) {

        String JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER;

        // Initialize month strings
        JANUARY = context.getString(R.string.jan);
        FEBRUARY = context.getString(R.string.feb);
        MARCH = context.getString(R.string.mar);
        APRIL = context.getString(R.string.apr);
        MAY = context.getString(R.string.may);
        JUNE = context.getString(R.string.jun);
        JULY = context.getString(R.string.jul);
        AUGUST = context.getString(R.string.aug);
        SEPTEMBER = context.getString(R.string.sep);
        OCTOBER = context.getString(R.string.oct);
        NOVEMBER = context.getString(R.string.nov);
        DECEMBER = context.getString(R.string.dec);

        if (JANUARY.equals(monthStr)) {
            return 1;
        } else if (FEBRUARY.equals(monthStr)) {
            return 2;
        } else if (MARCH.equals(monthStr)) {
            return 3;
        } else if (APRIL.equals(monthStr)) {
            return 4;
        } else if (MAY.equals(monthStr)) {
            return 5;
        } else if (JUNE.equals(monthStr)) {
            return 6;
        } else if (JULY.equals(monthStr)) {
            return 7;
        } else if (AUGUST.equals(monthStr)) {
            return 8;
        } else if (SEPTEMBER.equals(monthStr)) {
            return 9;
        } else if (OCTOBER.equals(monthStr)) {
            return 10;
        } else if (NOVEMBER.equals(monthStr)) {
            return 11;
        } else if (DECEMBER.equals(monthStr)) {
            return 12;
        } else {
            return 0;
        }
    }

    public static String getShortMonthByNumber(Context context, int monthNumber) {

        // Initialize month strings
        String JANUARY = context.getString(R.string.jan);
        String FEBRUARY = context.getString(R.string.feb);
        String MARCH = context.getString(R.string.mar);
        String APRIL = context.getString(R.string.apr);
        String MAY = context.getString(R.string.may);
        String JUNE = context.getString(R.string.jun);
        String JULY = context.getString(R.string.jul);
        String AUGUST = context.getString(R.string.aug);
        String SEPTEMBER = context.getString(R.string.sep);
        String OCTOBER = context.getString(R.string.oct);
        String NOVEMBER = context.getString(R.string.nov);
        String DECEMBER = context.getString(R.string.dec);

        switch (monthNumber) {
            case 1:
                return JANUARY;
            case 2:
                return FEBRUARY;
            case 3:
                return MARCH;
            case 4:
                return APRIL;
            case 5:
                return MAY;
            case 6:
                return JUNE;
            case 7:
                return JULY;
            case 8:
                return AUGUST;
            case 9:
                return SEPTEMBER;
            case 10:
                return OCTOBER;
            case 11:
                return NOVEMBER;
            case 12:
                return DECEMBER;
            default:
                return null;
        }
    }
}