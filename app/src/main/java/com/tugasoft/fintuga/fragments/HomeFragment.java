package com.tugasoft.fintuga.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.tugasoft.fintuga.activity.DashboardActivity;
import com.tugasoft.fintuga.adapters.ExpenseChildAdapter;
import com.tugasoft.fintuga.adapters.ExpenseHeaderAdapter;
import com.tugasoft.fintuga.application.AppCore;
import com.tugasoft.fintuga.models.Expense;
import com.tugasoft.fintuga.models.MasterExpenseModel;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.utils.Constant;

import java.text.DecimalFormat;
import java.util.Calendar;

import static com.tugasoft.fintuga.adapters.ExpenseHeaderAdapter.expenseChildAdapter0;

public class HomeFragment extends Fragment {
    public ExpenseHeaderAdapter mAdapter = null;
    public FirebaseAuth mAuth;
    FloatingActionButton addExp;
    DecimalFormat df = new DecimalFormat("###.#");
    String uid;
    private RecyclerView expList;
    private LinearLayout mLlDataFound;
    private LinearLayout mLlNoDataFound;
    private DatabaseReference ref;
    private View view;
    private String TAG = "HomeFragment";

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_home, (ViewGroup) null);
        this.view = inflate;
        return inflate;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (getActivity() != null && (getActivity() instanceof DashboardActivity)) {
            this.ref = ((DashboardActivity) getActivity()).ref;
            this.mAuth = ((DashboardActivity) getActivity()).mAuth;
        }
        this.expList = (RecyclerView) this.view.findViewById(R.id.expensesListView);
        this.expList.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.mLlDataFound = (LinearLayout) this.view.findViewById(R.id.ll_data_found);
        this.mLlNoDataFound = (LinearLayout) this.view.findViewById(R.id.ll_no_data_found);
        showExpenseTable();
    }

    public void displayData() {
        showExpenseTable();
    }

    public void resetData() {
        showExpenseTable();
    }

    // For Create Header Item Deleted Dialog...
    private void showExpenseTable() {
        if (getActivity() != null) {
            if (((DashboardActivity) requireActivity()).mTransactionList.size() == 0) {
                this.mLlNoDataFound.setVisibility(View.VISIBLE);
                this.mLlDataFound.setVisibility(View.GONE);
                return;
            }
            this.mLlNoDataFound.setVisibility(View.GONE);
            this.mLlDataFound.setVisibility(View.VISIBLE);
            ExpenseHeaderAdapter expenseHeaderAdapter = this.mAdapter;
            if (expenseHeaderAdapter == null) {
                ExpenseHeaderAdapter expenseHeaderAdapter2 = new ExpenseHeaderAdapter(this, R.layout.row_layout, ((DashboardActivity) getActivity()).mTransactionList, new ExpenseHeaderAdapter.OnItemClickListener() {
                    public void onItemLongClick(final MasterExpenseModel masterExpenseModel, final int i2) {
                        if (CommonMethod.isNetworkConnected(HomeFragment.this.requireActivity())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeFragment.this.getActivity());
                            builder.setTitle((CharSequence) HomeFragment.this.getString(R.string.alert));
                            builder.setMessage((CharSequence) "Sure to delete all transaction entries for selected '" + masterExpenseModel.getId() + "' date?").setPositiveButton((CharSequence) HomeFragment.this.getString(R.string.action_yes), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.e(TAG + " Line 94: for Header: ", "onClick: Dialog int i: " + i);
                                    dialogInterface.cancel();
                                    HomeFragment.this.deleteDateWiseTransactionEntries(masterExpenseModel, i2);
                                }
                            }).setNegativeButton((CharSequence) HomeFragment.this.getString(R.string.action_no), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            AlertDialog create = builder.create();
                            create.setCancelable(false);
                            create.show();
                            return;
                        }
                        CommonMethod.showConnectionAlert(HomeFragment.this.getActivity());
                    }
                });
                this.mAdapter = expenseHeaderAdapter2;
                this.expList.setAdapter(expenseHeaderAdapter2);
                return;
            }
            expenseHeaderAdapter.notifyDataSetChanged();
        }
    }


    // For Header Item delete....
    public void deleteDateWiseTransactionEntries(final MasterExpenseModel masterExpenseModel, final int i) {
        Log.e(TAG + " Line 120: for Header: ", "delete Date Wise Transaction: " + i);
        DatabaseReference databaseReference = ((DashboardActivity) requireActivity()).expensesRef;
        if (databaseReference != null) {
            CommonMethod.showProgressDialog(getActivity());
            databaseReference.child(masterExpenseModel.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                public void onSuccess(Void voidR) {
                    CommonMethod.cancelProgressDialog();
                    ((DashboardActivity) HomeFragment.this.requireActivity()).totalIncome = ((DashboardActivity) HomeFragment.this.getActivity()).totalIncome - masterExpenseModel.getTotalIncome();
                    ((DashboardActivity) HomeFragment.this.getActivity()).totalExpense -= masterExpenseModel.getTotalExpense();
                    ((DashboardActivity) HomeFragment.this.getActivity()).displayAmount();
                    ((DashboardActivity) HomeFragment.this.getActivity()).mTransactionList.remove(i);
                    HomeFragment.this.mAdapter.notifyDataSetChanged();
                    Toast.makeText(HomeFragment.this.getActivity(), "Transaction removed successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                public void onFailure(Exception exc) {
                    CommonMethod.cancelProgressDialog();
                    Toast.makeText(HomeFragment.this.getActivity(), exc.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //For Create Child Item Deleted Dialog...
    public void deleteTransactionEntry(final Expense expense, final int i, final int i2, ExpenseChildAdapter expenseChildAdapter) {
        Log.e(TAG + " Line 144: for Child: ", "delete Transaction int i: " + i);
        Log.e(TAG + " Line 145: for Child: ", "delete Transaction int i2: " + i2);
        if (CommonMethod.isNetworkConnected(requireActivity())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle((CharSequence) getString(R.string.alert));
            builder.setMessage((CharSequence) "Sure to delete transaction entry?").setPositiveButton((CharSequence) getString(R.string.action_yes), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int j) {
                    dialogInterface.cancel();
                    Log.e(TAG + " Line 152: for Child: ", "onClick Dialog int i : " + i);
                    Log.e(TAG + " Line 152: for Child: ", "onClick Dialog int i2 : " + i2);
                    HomeFragment.this.deleteSingleTransactionEntry(expense, i, i2, expenseChildAdapter);
                }
            }).setNegativeButton((CharSequence) getString(R.string.action_no), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog create = builder.create();
            create.setCancelable(false);
            create.show();
            return;
        }
        CommonMethod.showConnectionAlert(getActivity());
    }

    // For Delete Child Item....
    public void deleteSingleTransactionEntry(final Expense expense, final int i, final int i2, ExpenseChildAdapter expenseChildAdapter) {
        Log.e(TAG + " Line 170: for Child: ", "delete Single Transaction int i: " + i);
        Log.e(TAG + " Line 171: for Child: ", "delete Single Transaction int i2: " + i2);
        DatabaseReference databaseReference = ((DashboardActivity) requireActivity()).expensesRef;
        if (databaseReference != null) {
            CommonMethod.showProgressDialog(getActivity());
            databaseReference.child(expense.getDate()).child(expense.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                public void onSuccess(Void voidR) {
                    CommonMethod.cancelProgressDialog();
                    if (expense.getProofUri() != null && expense.getProofUri().trim().length() > 0) {
                        Calendar.getInstance();
                        String trim = ((DashboardActivity) HomeFragment.this.getActivity()).mTvSelectDate.getText().toString().trim();
                        FirebaseStorage.getInstance().getReference(HomeFragment.this.mAuth.getCurrentUser().getUid() + "/" + Constant.CONST_IMAGE_STORE_CONTAINER + "/" + trim + "/" + expense.getDate()).child(expense.getProofName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            public void onSuccess(Void voidR) {
                                Toast.makeText(AppCore.getAppContext(), "Proof deleted successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            public void onFailure(Exception exc) {
                                Context appContext = AppCore.getAppContext();
                                Toast.makeText(appContext, "Issue found while removing Proof : " + exc.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    MasterExpenseModel masterExpenseModel = ((DashboardActivity) HomeFragment.this.requireActivity()).mTransactionList.get(i);
                    if (!expense.isExpense()) {
                        ((DashboardActivity) HomeFragment.this.getActivity()).totalIncome -= expense.getAmount();
                        masterExpenseModel.setTotalIncome(masterExpenseModel.getTotalIncome() - expense.getAmount());
                    } else if (expense.isExpense()) {
                        ((DashboardActivity) HomeFragment.this.getActivity()).totalExpense -= expense.getAmount();
                        masterExpenseModel.setTotalExpense(masterExpenseModel.getTotalExpense() - expense.getAmount());
                    }
                    ((DashboardActivity) HomeFragment.this.getActivity()).displayAmount();


                    //masterExpenseModel.getDetailList().remove(i2);

                    if (masterExpenseModel.getExpenses().size() > 0) {
                        //For Header Update...
                        //mAdapter is Header Adapter...
                        HomeFragment.this.mAdapter.notifyItemChanged(i);
                        expenseChildAdapter.notifyItemChanged(i2);
                        ((DashboardActivity) HomeFragment.this.getActivity()).mTransactionList.get(i).getExpenses().remove(i2);
                    } else {
                        try {
                            ((DashboardActivity) HomeFragment.this.getActivity()).mTransactionList.get(i).getExpenses().remove(i2);
                            masterExpenseModel.getExpenses().remove(i2);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    expenseChildAdapter0.notifyItemRemoved(i2);
                    Toast.makeText(HomeFragment.this.getActivity(), "Transaction removed successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                public void onFailure(Exception exc) {
                    CommonMethod.cancelProgressDialog();
                    Toast.makeText(HomeFragment.this.getActivity(), exc.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
