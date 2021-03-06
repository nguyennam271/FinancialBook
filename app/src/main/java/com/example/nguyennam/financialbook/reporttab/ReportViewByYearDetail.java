package com.example.nguyennam.financialbook.reporttab;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nguyennam.financialbook.R;
import com.example.nguyennam.financialbook.adapters.ReportExpenseIncomeHistoryAdapter;
import com.example.nguyennam.financialbook.database.AccountRecyclerViewDAO;
import com.example.nguyennam.financialbook.database.ExpenseDAO;
import com.example.nguyennam.financialbook.database.IncomeDAO;
import com.example.nguyennam.financialbook.model.AccountRecyclerView;
import com.example.nguyennam.financialbook.model.Expense;
import com.example.nguyennam.financialbook.model.FinancialHistoryChild;
import com.example.nguyennam.financialbook.model.Income;
import com.example.nguyennam.financialbook.utils.CalendarSupport;
import com.example.nguyennam.financialbook.utils.Constant;
import com.example.nguyennam.financialbook.utils.FileHelper;

import java.util.ArrayList;
import java.util.List;

public class ReportViewByYearDetail extends Fragment {

    Context context;
    ExpenseDAO expenseDAO;
    IncomeDAO incomeDAO;
    List<String> dateExpenseList;
    String[] mangId;
    RecyclerView recyclerView;
    List<FinancialHistoryChild> data;
    ImageView btnBack;
    TextView txtDateReport;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.report_expense_history, container, false);
        txtDateReport = (TextView) v.findViewById(R.id.txtDateReport);
        btnBack = (ImageView) v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileHelper.deleteFile(context, Constant.TEMP_BUDGET_DATE);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerviewReport);
        data = new ArrayList<>();
        getDateExpenseIncome();
        setDataForReport();
        txtDateReport.setText(FileHelper.readFile(context, Constant.TEMP_BUDGET_DATE));
        setAdapter();
        return v;
    }

    private void setAdapter() {
        ReportExpenseIncomeHistoryAdapter myAdapter = new ReportExpenseIncomeHistoryAdapter(context, data);
        recyclerView.setAdapter(myAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void getDateExpenseIncome() {
        // get date from income and expense
        expenseDAO = new ExpenseDAO(context);
        incomeDAO = new IncomeDAO(context);
        dateExpenseList = expenseDAO.getDateExpense();
        List<String> dateIncomeList = incomeDAO.getDateIncome();
        // sort date from now to past and avoid duplicate date
        CalendarSupport.sortDateList(dateExpenseList, dateIncomeList);
    }

    private void addData(String date) {
        for (int j = 0; j < mangId.length; j++) {
            AccountRecyclerView account = new AccountRecyclerViewDAO(context).
                    getAccountById(Integer.parseInt(mangId[j]));
            List<Expense> expenseList = expenseDAO.getExpenseByAccountID(Integer.parseInt(mangId[j]), date);
            for (Expense expense : expenseList) {
                if ("".equals(expense.get_categoryChild())) {
                    data.add(new FinancialHistoryChild(true, expense.get_amountMoney(),
                            account.getAccountName(), expense.get_category(), expense.get_date()));
                } else {
                    data.add(new FinancialHistoryChild(true, expense.get_amountMoney(),
                            account.getAccountName(), expense.get_categoryChild(), expense.get_date()));
                }
            }
            List<Income> incomeList = incomeDAO.getIncomeByAccountID(Integer.parseInt(mangId[j]), date);
            for (Income income : incomeList) {
                data.add(new FinancialHistoryChild(false, income.get_amountMoney(),
                        account.getAccountName(), income.get_category(), income.get_date()));
            }
        }
    }

    private void setDataForReport() {
        // get id account from account name form
        String idAccount = FileHelper.readFile(context, Constant.TEMP_ID);
        mangId = idAccount.split(";");
        String yearReport = FileHelper.readFile(context, Constant.TEMP_BUDGET_DATE);
        for (int i = 0; i < dateExpenseList.size(); i++) {
            String year = CalendarSupport.getYear(dateExpenseList.get(i));
            if (yearReport.equals(year)) {
                addData(dateExpenseList.get(i));
            }
        }
    }
}
