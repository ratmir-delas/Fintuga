package com.tugasoft.fintuga.fragments;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.polyak.iconswitch.IconSwitch;
import com.tugasoft.fintuga.activity.DashboardActivity;
import com.tugasoft.fintuga.adapters.CategorySpinnerAdapter;
import com.tugasoft.fintuga.models.Category;
import com.tugasoft.fintuga.models.Expense;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.utils.Constant;
import com.tugasoft.fintuga.utils.MySharedPreferences;
import com.tugasoft.fintuga.ads.AdsProvider;
import com.tugasoft.fintuga.asyncTask.ImageCompression;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;



public class AddExpenseFragment extends DialogFragment implements IconSwitch.CheckedChangeListener, TextWatcher {

    public String TAG = AddExpenseFragment.class.getCanonicalName();
    public SimpleDateFormat dateFormatter;
    public File file = null;
    public Uri imageCaptureUri;
    public String imageExtension = "jpg";
    public TextView mTvDate;
    public SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.US);
    public SharedPreferences sharedPreferences;
    public File sourceFile = null;
    DatabaseReference expensesRef;
    EditText mEtAmount, mEtDescription;
    DatabaseReference ref;
    private FrameLayout adView;
    private IconSwitch iconSwitch;
    private FirebaseAuth mAuth;
    private Expense expense = null;
    private ImageView mIvUserProfile;
    private Toolbar mToolbar;
    private View mView;
    private Spinner spCategory;
    private int[] statusBarColors;
    private Window window;

    public static AddExpenseFragment newInstance(Bundle bundle) {
        AddExpenseFragment addExpenseFragment = new AddExpenseFragment();
        addExpenseFragment.setArguments(bundle);
        return addExpenseFragment;
    }

    private static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable error) {
                throw new RuntimeException(error);
            }
        }
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(STYLE_NORMAL, R.style.CustomDialog);
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(-1, -1);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        mView = layoutInflater.inflate(R.layout.fragment_add_transaction, viewGroup, false);
        if (getArguments() != null) {
            expense = (Expense) getArguments().getSerializable("ExpenseDetailModel");
        }
        sharedPreferences = getActivity().getSharedPreferences("mypref", 0);
        mView.setBackgroundColor(ContextCompat.getColor(requireActivity(), android.R.color.white));
        mView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                view.removeOnLayoutChangeListener(this);
                assert getArguments() != null;
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(view, getArguments().getInt("cx"), getArguments().getInt("cy"), 0.0f, (float) ((int) Math.hypot(i3, i4)));
                createCircularReveal.setInterpolator(new DecelerateInterpolator(2.0f));
                createCircularReveal.setDuration(500);
                createCircularReveal.start();
            }
        });
        ref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        return mView;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getString(R.string.app_name));
        file = file2;
        if (!file2.exists()) {
            file.mkdirs();
        }
        dateFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.US);
        setToolbar(mView);
        loadAllViews(mView);
    }

    private void setToolbar(View view) {
        Dialog dialog = getDialog();
        Objects.requireNonNull(dialog);
        Window window2 = dialog.getWindow();
        Objects.requireNonNull(window2);
        window = window2;
        window.addFlags(Integer.MIN_VALUE);
        initColors();
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        mToolbar = toolbar;
        toolbar.setTitle(getString(R.string.add_expense));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));
        mToolbar.setNavigationIcon((int) R.drawable.ic_cancle);
        mToolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null && (getActivity() instanceof DashboardActivity)) {
                ((DashboardActivity) getActivity()).dismissDialog();
            }
        });
        IconSwitch iconSwitch2 = view.findViewById(R.id.icon_switch);
        iconSwitch = iconSwitch2;
        iconSwitch2.setCheckedChangeListener(current -> {

            updateColors(true);

            switch (current) {
                case LEFT:
                    mToolbar.setTitle(getString(R.string.add_expense));
                    break;
                case RIGHT:
                    mToolbar.setTitle(getString(R.string.add_income));
                    break;
            }
        });
        updateColors(false);
    }

    private void loadAllViews(View view) {
        LinearLayout mLlHeader = view.findViewById(R.id.ll_header);
        mTvDate = view.findViewById(R.id.tv_date);
        mEtDescription = view.findViewById(R.id.et_description);
        mEtAmount = view.findViewById(R.id.et_amount);
        adView = view.findViewById(R.id.ad_view_container);
        spCategory = view.findViewById(R.id.sp_category);
        mIvUserProfile = view.findViewById(R.id.iv_proof);
        AppCompatButton buttonSubmit = view.findViewById(R.id.fab_submit);
        CardView imageProof = view.findViewById(R.id.cv_proof);
        mEtAmount.addTextChangedListener(this);
        mEtAmount.setOnFocusChangeListener((view1, z) -> {
            if (z) {
                EditText editText = (EditText) view1;
                editText.setSelection(editText.getText().length());
            }
        });

        //Expense expense = this.expense;
        if (expense != null) {
            mTvDate.setText(expense.getDate());
            mTvDate.setClickable(false);
            mEtAmount.setText(String.valueOf(expense.getAmount()));
            mEtDescription.setText(expense.getDescription());
            if (expense.getProofUri().trim().length() > 0) {
                CommonMethod.displayNetworkImage(getActivity(), expense.getProofUri(), mIvUserProfile);
            }
            if (iconSwitch.getChecked() == IconSwitch.Checked.LEFT && !expense.isExpense()) {
                iconSwitch.setChecked(IconSwitch.Checked.RIGHT);
            } else if (iconSwitch.getChecked() == IconSwitch.Checked.LEFT && expense.isExpense()) {
                displayCategory();
            }
        } else {
            displayCategory();
            mTvDate.setText(sdf.format(Calendar.getInstance().getTime()));
        }
        FrameLayout frameLayout = mView.findViewById(R.id.ad_view_container);
        frameLayout.post(this::loadBanner);

        mTvDate.setOnClickListener(v -> setDate());

        buttonSubmit.setOnClickListener(v -> submitData());

        imageProof.setOnClickListener(v -> uploadProof());
    }

    private void uploadProof() {
        if (!CommonMethod.isDeviceSupportCamera(getActivity())) {
            Toast.makeText(getActivity(), "Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();
        } else {
            captureImages();
        }
    }

    private void captureImages() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Upload Photo");
        builder.setMessage("How do you want to take proof ?");
        builder.setPositiveButton("Gallery", (dialogInterface, i) -> {
            Intent intent = new Intent("android.intent.action.PICK");
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });
        builder.setNegativeButton("Camera", (dialogInterface, i) -> {
            AddExpenseFragment addExpenseFragment = AddExpenseFragment.this;
            File access$500 = file;
            File unused = addExpenseFragment.sourceFile = new File(access$500, "camera_" + dateFormatter.format(new Date()) + "." + imageExtension);
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            if (Build.VERSION.SDK_INT < 24) {
                AddExpenseFragment addExpenseFragment2 = AddExpenseFragment.this;
                addExpenseFragment2.imageCaptureUri = Uri.fromFile(addExpenseFragment2.sourceFile);
                intent.putExtra("output", imageCaptureUri);
            } else {
                AddExpenseFragment addExpenseFragment3 = AddExpenseFragment.this;
                FragmentActivity activity = addExpenseFragment3.getActivity();
                addExpenseFragment3.imageCaptureUri = FileProvider.getUriForFile(activity, getActivity().getPackageName() + ".provider", sourceFile);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("output", imageCaptureUri);
            }
            startActivityForResult(intent, 2);
        });
        builder.show();
    }

    private void setDate() {
        Date date = null;
        try {
            date = sdf.parse(mTvDate.getText().toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (datePicker, i, i2, i3) -> {
            Calendar instance1 = Calendar.getInstance();
            instance1.set(1, i);
            instance1.set(2, i2);
            instance1.set(5, i3);
            mTvDate.setText(sdf.format(instance1.getTime()));
        }, instance.get(1), instance.get(2), instance.get(5));
        datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.show();
    }

    private void submitData() {
        try {
            String date = mTvDate.getText().toString().trim();
            String replaceAll = mEtAmount.getText().toString().trim().replaceAll(",", "");
            if (date.trim().length() == 0) {
                Toast.makeText(getActivity(), "Select date", Toast.LENGTH_LONG).show();
                return;
            } else
            if (replaceAll.length() != 0) {
                if (Float.parseFloat(replaceAll) > 0.0f) {
                    if (spCategory.getSelectedItemPosition() < 0) {
                        Toast.makeText(getActivity(), "Select category", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        if (sourceFile != null && sourceFile.exists() && CommonMethod.getFileSize(sourceFile) < 10) {
                            Toast.makeText(getActivity(), "Captured image is corrupted or not valid/exists. Please try again.", Toast.LENGTH_LONG).show();
                            return;
                        } else if (CommonMethod.isNetworkConnected(requireActivity())) {
                            File file3 = sourceFile;
                            if (file3 != null) {
                                if (file3.exists()) {
                                    File file4 = file;
                                    File file5 = new File(file4, "img_compressed." + imageExtension);
                                    new ImageCompression(this).execute(new String[]{sourceFile.getAbsolutePath(), file5.getAbsolutePath()});
                                    return;
                                }
                            }
                            //uploadDetailsToFirebase(expense.getProofName(), expense.getProofUri());
                            uploadDetailsToFirebase("", "");
                            return;
                        } else {
                            CommonMethod.showConnectionAlert(getActivity());
                            return;
                        }
                    }
                }
            }
            Toast.makeText(getActivity(), "Enter Expense Amount", Toast.LENGTH_LONG).show();
        } catch (NumberFormatException exceptionAmount) {
            Toast.makeText(getActivity(), getString(R.string.enter_valid_amount), Toast.LENGTH_LONG).show();
        } catch (Exception exceptionValues) {
            //enter valid values exception toast
            Toast.makeText(getActivity(), getString(R.string.enter_valid_values) + exceptionValues, Toast.LENGTH_LONG).show();
            Log.e("Exception", "Exception" + exceptionValues);
        }
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    public void onDetach() {
        super.onDetach();
        System.gc();
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 != -1) {
            return;
        }
        if (i == 1) {
            Uri data = intent.getData();
            mIvUserProfile.setImageURI(data);
            sourceFile = new File(getPathFromGooglePhotosUri(data));
        } else if (i == 2) {
            mIvUserProfile.setImageURI(imageCaptureUri);
        }
    }

    private int color(int i) {
        return ContextCompat.getColor(getActivity(), i);
    }

    public void onCheckChanged(IconSwitch.Checked checked) {
        updateColors(true);
        int i = iconSwitch.getChecked().ordinal();
        if (i == 1) {
            mToolbar.setTitle(getString(R.string.add_expense));
        } else if (i == 2) {
            mToolbar.setTitle(getString(R.string.add_income));
        }
    }

    public void afterTextChanged(Editable editable) {
        if (editable.toString().trim().length() > 0) {
            String format = Constant.decimalFormat.format(Double.valueOf(editable.toString().replaceAll(",", "")));
            mEtAmount.removeTextChangedListener(this);
            mEtAmount.setText(format);
            mEtAmount.addTextChangedListener(this);
            EditText editText = mEtAmount;
            editText.setSelection(editText.getText().length());
        }
    }

    private void displayCategory() {
        if (iconSwitch.getChecked() == IconSwitch.Checked.LEFT) {
            ArrayList arrayList = new ArrayList();
            String str = MySharedPreferences.getStr(MySharedPreferences.KEY_EXPENSE_CATEGORY, "");
            if (str.trim().length() > 0) {
                try {
                    arrayList = new Gson().fromJson(str, new TypeToken<List<Category>>() {
                    }.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            spCategory.setAdapter(new CategorySpinnerAdapter(getActivity(), R.layout.dropdown_category_item, arrayList));
            //Expense expense = this.expense;
            if (expense != null && expense.getCategory() != null) {
                Spinner spinner = spCategory;
                spinner.setSelection(getIndexForCategory(spinner, expense.getCategory()));
            }
        } else if (iconSwitch.getChecked() == IconSwitch.Checked.RIGHT) {
            ArrayList arrayList2 = new ArrayList<>();
            String str2 = MySharedPreferences.getStr(MySharedPreferences.KEY_INCOME_CATEGORY, "");
            if (str2.trim().length() > 0) {
                try {
                    arrayList2 = new Gson().fromJson(str2, new TypeToken<List<Category>>() {
                    }.getType());
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            spCategory.setAdapter(new CategorySpinnerAdapter(getActivity(), R.layout.dropdown_category_item, arrayList2));
            Expense expense2 = expense;
            if (expense2 != null && expense2.getCategory() != null) {
                Spinner spinner2 = spCategory;
                spinner2.setSelection(getIndexForCategory(spinner2, expense.getCategory()));
            }
        }
    }

    private void initColors() {
        int[] colors = new int[IconSwitch.Checked.values().length];
        statusBarColors = new int[colors.length];
        colors[IconSwitch.Checked.LEFT.ordinal()] = color(R.color.light_pink_bg);
        statusBarColors[IconSwitch.Checked.LEFT.ordinal()] = color(R.color.light_pink_bg);
        colors[IconSwitch.Checked.RIGHT.ordinal()] = color(R.color.light_pink_bg);
        statusBarColors[IconSwitch.Checked.RIGHT.ordinal()] = color(R.color.light_pink_bg);
    }

    private void updateColors(boolean update) {
        int ordinal = iconSwitch.getChecked().ordinal();
        mToolbar.setBackgroundColor(Color.parseColor("#F9E9FF"));
        if (update) {
            displayCategory();
        }
        window.setStatusBarColor(statusBarColors[ordinal]);
    }

    public void uploadDetailsToFirebase(String proofName, String proofUri) {
        Expense expenseAux;
        double amount = Double.parseDouble(mEtAmount.getText().toString().trim().replaceAll(",", ""));
        String description = mEtDescription.getText().toString().trim();
        String trim = mTvDate.getText().toString().trim();
        if (proofUri.trim().length() != 0) {
            expenseAux = new Expense(amount, description, iconSwitch.getChecked() == IconSwitch.Checked.LEFT, ((Category) spCategory.getSelectedItem()).getCategory(), ((Category) spCategory.getSelectedItem()).getColor(), proofName, proofUri);
        } else if (expense != null) {
            expenseAux = new Expense(amount, description, iconSwitch.getChecked() == IconSwitch.Checked.LEFT, ((Category) spCategory.getSelectedItem()).getCategory(), ((Category) spCategory.getSelectedItem()).getColor(), proofName, proofUri);
        } else {
            expenseAux = new Expense(amount, description, iconSwitch.getChecked() == IconSwitch.Checked.LEFT, ((Category) spCategory.getSelectedItem()).getCategory(), ((Category) spCategory.getSelectedItem()).getColor(), proofName, proofUri);
        }
        Date date = null;
        try {
            date = sdf.parse(trim);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        expensesRef = ref.child(Constant.FIREBASE_NODE_EXPENSE).child(this.mAuth.getCurrentUser().getUid()).child(new SimpleDateFormat(Constant.MONTH_YEAR_FORMAT, Locale.US).format(date)).child(trim);
        CommonMethod.showProgressDialog(getActivity());
        Expense expense2 = expense;
        if (expense2 != null) {
            expensesRef.child(expense2.getId()).setValue(expenseAux).addOnSuccessListener(voidR -> {
                CommonMethod.cancelProgressDialog();
                if (getActivity() != null && (getActivity() instanceof DashboardActivity)) {
                    ((DashboardActivity) getActivity()).insertTransactionEntry();
                }
            }).addOnFailureListener(exc -> {
                CommonMethod.cancelProgressDialog();
                String access$900 = TAG;
                Log.d(access$900, "Entry not sync : " + exc.getMessage());
                Toast.makeText(getActivity(), exc.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            assert expensesRef.push().getKey() != null;
            expensesRef.child(Objects.requireNonNull(expensesRef.push().getKey())).setValue(expenseAux).addOnSuccessListener(voidR -> {
                CommonMethod.cancelProgressDialog();
                if (getActivity() != null && (getActivity() instanceof DashboardActivity)) {
                    ((DashboardActivity) getActivity()).insertTransactionEntry();
                }
            }).addOnFailureListener(exc -> {
                CommonMethod.cancelProgressDialog();
                String access$900 = TAG;
                Log.d(access$900, "Entry not sync : " + exc.getMessage());
                Toast.makeText(getActivity(), exc.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    public String getPathFromGooglePhotosUri(Uri uri) {
        FileOutputStream fileOutputStream;
        FileInputStream fileInputStream;
        String tempFilename = null;
        FileInputStream fileInputStream2 = null;
        if (uri == null) {
            return null;
        }
        try {
            fileInputStream = new FileInputStream(getActivity().getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor());
            try {
                tempFilename = getTempFilename();
                fileOutputStream = new FileOutputStream(tempFilename);
            } catch (IOException unused) {
                closeSilently(fileInputStream);
                return null;
            } catch (Throwable th) {
                fileInputStream2 = fileInputStream;
                closeSilently(fileInputStream2);
                throw th;
            }
            try {
                byte[] bArr = new byte[4096];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read != -1) {
                        fileOutputStream.write(bArr, 0, read);
                    } else {
                        closeSilently(fileInputStream);
                        closeSilently(fileOutputStream);
                        return tempFilename;
                    }
                }
            } catch (IOException unused2) {
                closeSilently(fileInputStream);
                closeSilently(fileOutputStream);
                return null;
            } catch (Throwable th2) {
                fileInputStream2 = fileInputStream;
                closeSilently(fileInputStream2);
                closeSilently(fileOutputStream);
                throw th2;
            }
        } catch (IOException unused3) {
            return null;
        } catch (Throwable th3) {
            closeSilently(fileInputStream2);
            try {
                throw th3;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return tempFilename;
    }

    private String getTempFilename() throws IOException {
        File file2 = file;
        File file3 = new File(file2, "gallery_" + dateFormatter.format(new Date()) + "." + imageExtension);
        sourceFile = file3;
        return file3.getAbsolutePath();
    }

    private int getIndexForCategory(Spinner spinner, String str) {
        int count = spinner.getCount();
        int i = 0;
        while (i < count) {
            try {
                if (((Category) spinner.getItemAtPosition(i)).getCategory().equalsIgnoreCase(str)) {
                    return i;
                }
                i++;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public void uploadDetailsToServer(String str) {
        Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();

        try {
            Date date;
            final String str2;
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            String uid = mAuth.getCurrentUser().getUid();
            try {
                date = sdf.parse(mTvDate.getText().toString().trim());
            } catch (ParseException e) {
                e.printStackTrace();
                date = null;
            }
            String format = new SimpleDateFormat(Constant.MONTH_YEAR_FORMAT, Locale.US).format(date);
            //Expense expense = this.expense;
            if (expense == null || expense.getProofName() == null || expense.getProofName().trim().length() <= 0) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH_mm_ss", Locale.US);
                str2 = simpleDateFormat.format(new Date()).toString() + "." + imageExtension;
            } else {
                str2 = expense.getProofName();
            }
            final StorageReference child = FirebaseStorage.getInstance().getReference(uid + "/" + Constant.CONST_IMAGE_STORE_CONTAINER + "/" + format + "/" + mTvDate.getText().toString().trim()).child(str2);
            child.putFile(Uri.fromFile(new File(str))).addOnSuccessListener((OnSuccessListener) taskSnapshot -> {
                Toast.makeText(getActivity(), "Proof Uploaded Successfully.", Toast.LENGTH_LONG).show();
                child.getDownloadUrl().addOnSuccessListener(uri -> {
                    progressDialog.dismiss();
                    uploadDetailsToFirebase(str2, uri.toString());
                });
            }).addOnFailureListener(exc -> {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), exc.getMessage(), Toast.LENGTH_LONG).show();
            }).addOnProgressListener((OnProgressListener<UploadTask.TaskSnapshot>) taskSnapshot -> {
                double bytesTransferred = (((double) taskSnapshot.getBytesTransferred()) * 100.0d) / ((double) taskSnapshot.getTotalByteCount());

                progressDialog.setMessage("Uploaded " + ((int) bytesTransferred) + "%...");
            });
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void loadBanner() {
        AdsProvider.getInstance().addBanner(getActivity(), adView);
    }
}