package com.example.nguyennam.financialbook.recordtab;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import com.example.nguyennam.financialbook.R;
import com.example.nguyennam.financialbook.adapters.FinancialHistoryAdapter;
import com.example.nguyennam.financialbook.database.ExpenseDAO;
import com.example.nguyennam.financialbook.database.IncomeDAO;
import com.example.nguyennam.financialbook.model.Expense;
import com.example.nguyennam.financialbook.model.FinancialHistoryChild;
import com.example.nguyennam.financialbook.model.FinancialHistoryGroup;
import com.example.nguyennam.financialbook.model.Income;
import com.example.nguyennam.financialbook.utils.CalculatorSupport;
import com.example.nguyennam.financialbook.utils.CalendarSupport;
import com.example.nguyennam.financialbook.utils.Constant;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FinancialHistory extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    Context context;
    private FinancialHistoryAdapter listAdapter;
    private ExpandableListView myList;
    private ArrayList<FinancialHistoryGroup> financialGroupList = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.financial_history, container, false);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) view.findViewById(R.id.searchHistory);
        search.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        search.setIconifiedByDefault(false);
        search.setOnQueryTextListener(this);
        search.setOnCloseListener(this);
        //display the list, expand all groups
        loadHistoryData();
        //get reference to the ExpandableListView
        myList = (ExpandableListView) view.findViewById(R.id.expandableHistory);
        //create the adapter by passing your ArrayList data
        listAdapter = new FinancialHistoryAdapter(context, financialGroupList);
        //attach the adapter to the list
        myList.setAdapter(listAdapter);
        //expand all Groups
        expandAll();
        myList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                TextView textView = (TextView) v.findViewById(R.id.groupname);
//                String groupname = (String) textView.getText();
//                Toast.makeText(getActivity().getApplicationContext(), "child clicked " + groupname , Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent();
//                intent.putExtra(Constant.KEY_CATEGORY, groupname);
//                setResult(RESULT_OK, intent);
//                finish();
                return false;
            }
        });
        myList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                TextView textView = (TextView) v.findViewById(R.id.childrow);
//                String childrow = (String) textView.getText();
//                Toast.makeText(getApplicationContext(), "child clicked " + childrow , Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent();
//                intent.putExtra(Constant.KEY_CATEGORY, childrow);
//                setResult(RESULT_OK, intent);
//                finish();
                return false;
            }
        });
        return view;
    }

    //method to expand all groups
    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            myList.expandGroup(i);
        }
    }

    private void loadHistoryData() {
        String dateOfWeek;
        String dateOfMonth;
        String month;
        String moneyExpense;
        String moneyIncome;
        FinancialHistoryGroup financialHistoryGroup;
        ExpenseDAO expenseDAO = new ExpenseDAO(context);
        Log.d(Constant.TAG, "loadHistoryData: " + expenseDAO.getAllExpense());
        IncomeDAO incomeDAO = new IncomeDAO(context);
        Log.d(Constant.TAG, "loadHistoryData: " + incomeDAO.getAllIncome());
        // get date from income and expense
        List<String> dateExpenseList = expenseDAO.getDateExpense();
        List<String> dateIncomeList = incomeDAO.getDateIncome();
        // sort date from now to past and avoid duplicate date
        sortDateList(dateExpenseList, dateIncomeList);
        for (String date : dateExpenseList) {
            dateOfWeek = CalendarSupport.getDateOfWeek(context, date);
            dateOfMonth = CalendarSupport.getDateOfMonth(date);
            month = CalendarSupport.getMonth(date);
            List<String> moneyExpenseList = expenseDAO.getMoneyByDate(date);
            moneyExpense = getMoneyOneDate(moneyExpenseList);
            List<String> moneyIncomeList = incomeDAO.getMoneyByDate(date);
            moneyIncome = getMoneyOneDate(moneyIncomeList);
            Log.d(Constant.TAG, "loadHistoryData: " + date + "    " + moneyExpense);
            Log.d(Constant.TAG, "loadData: " + date + "    " + moneyIncome);
            financialHistoryGroup = new FinancialHistoryGroup(dateOfWeek, dateOfMonth, month, moneyExpense, moneyIncome, getChildList(date));
            financialGroupList.add(financialHistoryGroup);
        }
    }

    private ArrayList<FinancialHistoryChild> getChildList(String date) {
        //TODO
        String description;
        String moneyAmount;
        String account;
        String category;
        FinancialHistoryChild financialChild;
        ArrayList<FinancialHistoryChild> financialChildList = new ArrayList<>();
        List<Income> incomeList;
        IncomeDAO incomeDAO = new IncomeDAO(context);
        incomeList = incomeDAO.getIncomeByDate(date);
        for (Income income : incomeList) {
            moneyAmount = income.get_amountMoney();
            account = income.get_accountName();
            category = income.get_category();
            description = income.get_description();
            financialChild = new FinancialHistoryChild(false, moneyAmount, account, category, description);
            financialChildList.add(financialChild);
        }
        List<Expense> expenseList;
        ExpenseDAO expenseDAO = new ExpenseDAO(context);
        expenseList = expenseDAO.getExpenseByDate(date);
        for (Expense expense : expenseList) {
            moneyAmount = expense.get_amountMoney();
            account = expense.get_accountName();
            category = expense.get_category();
            description = expense.get_description();
            financialChild = new FinancialHistoryChild(true, moneyAmount, account, category, description);
            financialChildList.add(financialChild);
        }
        return financialChildList;
    }

    public void sortDateList(List<String> dateExpenseList, List<String> dateIncomeList) {
        // remove date duplicate
        dateExpenseList.removeAll(dateIncomeList);
        // add date income to expense
        dateExpenseList.addAll(dateIncomeList);
        // sort date from now to past
        Collections.sort(dateExpenseList, new Comparator<String>() {
            @Override
            public int compare(String arg1, String arg0) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                int compareResult = 0;
                try {
                    Date arg0Date = format.parse(arg0);
                    Date arg1Date = format.parse(arg1);
                    compareResult = arg0Date.compareTo(arg1Date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    compareResult = arg0.compareTo(arg1);
                }
                return compareResult;
            }
        });
    }

    public String getMoneyOneDate(List<String> moneyExpenseList) {
        String moneyOnedate;
        double moneyNumber = 0; //money expense format to calculate and format to display
        for (String money : moneyExpenseList) {
            moneyNumber += Double.parseDouble(CalculatorSupport.formatExpression(money));
        }
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
        moneyOnedate = nf.format(moneyNumber);
        return moneyOnedate;
    }

    @Override
    public boolean onClose() {
        listAdapter.filterData("");
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        listAdapter.filterData(query);
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        listAdapter.filterData(query);
        expandAll();
        return false;
    }
}