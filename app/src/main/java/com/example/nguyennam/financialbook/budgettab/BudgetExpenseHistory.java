package com.example.nguyennam.financialbook.budgettab;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.nguyennam.financialbook.MainActivity;
import com.example.nguyennam.financialbook.R;
import com.example.nguyennam.financialbook.adapters.BudgetExpenseHistoryAdapter;
import com.example.nguyennam.financialbook.database.BudgetRecyclerViewDAO;
import com.example.nguyennam.financialbook.database.ExpenseDAO;
import com.example.nguyennam.financialbook.model.BudgetRecyclerView;
import com.example.nguyennam.financialbook.model.Expense;
import com.example.nguyennam.financialbook.recordtab.FinancialHistoryDetail;
import com.example.nguyennam.financialbook.utils.CalendarSupport;
import com.example.nguyennam.financialbook.utils.Constant;
import com.example.nguyennam.financialbook.utils.FileHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BudgetExpenseHistory extends Fragment implements View.OnClickListener,
        BudgetExpenseHistoryAdapter.BudgetExpenseOnClickListener {

    Context context;
    ImageView btnBack;
    TextView txtEdit;
    List<Expense> data = new ArrayList<>();
    BudgetRecyclerView budget;
    Date startDate;
    Date endDate;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.budget_expense_history, container, false);
        btnBack = (ImageView) view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        txtEdit = (TextView) view.findViewById(R.id.txtEdit);
        txtEdit.setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        // clear old data and load new
        data.clear();
        getDataExpense();
        BudgetExpenseHistoryAdapter myAdapter = new BudgetExpenseHistoryAdapter(context, data);
        myAdapter.setMyOnClickListener(this);
        recyclerView.setAdapter(myAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    private void getDataExpense() {
        BudgetRecyclerViewDAO budgetDAO = new BudgetRecyclerViewDAO(context);
        budget = budgetDAO.getBudgetById(Integer.parseInt(FileHelper.readFile(context, Constant.TEMP_BUDGET_ID)));
        getDateStartEnd();
        ExpenseDAO expenseDAO = new ExpenseDAO(context);
        List<String> expenseDates = expenseDAO.getDateExpenseByAccountID(budget.getAccountID());
        for (String expenseDate : expenseDates) {
            Date date = CalendarSupport.convertStringToDate(expenseDate);
            if (!date.before(startDate) && !date.after(endDate)) {
                List<Expense> expenseList = expenseDAO.getExpenseByDate(expenseDate);
                for (Expense expense : expenseList) {
                    if (budget.getCategory().equals(expense.get_category()) ||
                            budget.getCategory().equals(expense.get_categoryChild())) {
                        if (budget.getAccountID() == expense.get_accountID()) data.add(expense);
                    }
                }
            }
        }
    }

    private void getDateStartEnd() {
        // get date start and date end from view by
        String viewByDate = budget.getDate();
        String[] dateArray = viewByDate.split("-");
        for (int i = 0; i < dateArray.length; i++) {
            dateArray[i] = dateArray[i].trim();
        }
        startDate = CalendarSupport.convertStringToDate(dateArray[0]);
        endDate = CalendarSupport.convertStringToDate(dateArray[1]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                FileHelper.deleteFile(context, Constant.TEMP_BUDGET_ID);
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.txtEdit:
                ((MainActivity) context).replaceFragment(new EditBudget(), true);
                break;
        }
    }

    @Override
    public void onClick(int position) {
        //get detail expense here
        FileHelper.writeFile(context, Constant.TEMP_ISEXPENSE, "true");
        FileHelper.writeFile(context, Constant.TEMP_EXPENSE_ID, "" + position);
        ((MainActivity) context).replaceFragment(new FinancialHistoryDetail(), true);
    }
}