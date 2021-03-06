package com.example.nguyennam.financialbook.recordtab;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.nguyennam.financialbook.R;
import com.example.nguyennam.financialbook.adapters.ListCategoryAdapter;
import com.example.nguyennam.financialbook.model.CategoryChild;
import com.example.nguyennam.financialbook.model.CategoryGroup;
import com.example.nguyennam.financialbook.utils.Constant;
import com.example.nguyennam.financialbook.utils.FileHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ExpenseCategory extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, View.OnClickListener {

    Context context;
    private ListCategoryAdapter listAdapter;
    private ExpandableListView myList;
    private ArrayList<CategoryGroup> categoryGroupList = new ArrayList<>();

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
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_expense_category, container, false);
        ImageView btnPrevious = (ImageView) view.findViewById(R.id.btnPrevious);
        btnPrevious.setOnClickListener(this);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) view.findViewById(R.id.searchCategory);
        search.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        search.setIconifiedByDefault(false);
        search.setOnQueryTextListener(this);
        search.setOnCloseListener(this);
        //display the list, expand all groups
        loadSomeData();
        //get reference to the ExpandableListView
        myList = (ExpandableListView) view.findViewById(R.id.expandableCategory);
        //create the adapter by passing your ArrayList data
        listAdapter = new ListCategoryAdapter(context, categoryGroupList);
        //attach the adapter to the list
        myList.setAdapter(listAdapter);
        //expand all Groups
        expandAll();
        myList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                TextView textView = (TextView) v.findViewById(R.id.groupname);
                String groupname = (String) textView.getText();
                FileHelper.deleteFile(context, Constant.TEMP_CATEGORY_CHILD);
                FileHelper.writeFile(context, Constant.TEMP_CATEGORY, groupname);
                getActivity().getSupportFragmentManager().popBackStack();
                return false;
            }
        });
        myList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                FileHelper.writeFile(context, Constant.TEMP_CATEGORY, categoryGroupList.get(groupPosition).getName());
                TextView textView = (TextView) v.findViewById(R.id.childrow);
                String childrow = (String) textView.getText();
                FileHelper.writeFile(context, Constant.TEMP_CATEGORY_CHILD, childrow);
                getActivity().getSupportFragmentManager().popBackStack();
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

    private void loadSomeData() {
        List<String> myGroupList = Arrays.asList(getResources().getStringArray(R.array.group_row_category));
        List<String> listAnUong = Arrays.asList(getResources().getStringArray(R.array.child_row_anuong));
        List<String> listConCai = Arrays.asList(getResources().getStringArray(R.array.child_row_concai));
        List<String> listDichVuSH = Arrays.asList(getResources().getStringArray(R.array.child_row_dichvusinhhoat));
        List<String> listDiLai = Arrays.asList(getResources().getStringArray(R.array.child_row_dilai));
        List<String> listHieuHi = Arrays.asList(getResources().getStringArray(R.array.child_row_hieuhi));
        List<String> listHuongThu = Arrays.asList(getResources().getStringArray(R.array.child_row_huongthu));
        List<String> listNhaCua = Arrays.asList(getResources().getStringArray(R.array.child_row_nhacua));
        List<String> listPhatTrien = Arrays.asList(getResources().getStringArray(R.array.child_row_phattrienbanthan));
        List<String> listSucKhoe = Arrays.asList(getResources().getStringArray(R.array.child_row_suckhoe));
        List<String> listTrangPhuc = Arrays.asList(getResources().getStringArray(R.array.child_row_trangphuc));

        CategoryGroup categoryGroup;

        categoryGroup = new CategoryGroup(myGroupList.get(0), getChildList(listAnUong));
        categoryGroupList.add(categoryGroup);
        categoryGroup = new CategoryGroup(myGroupList.get(1), getChildList(listDiLai));
        categoryGroupList.add(categoryGroup);
        categoryGroup = new CategoryGroup(myGroupList.get(2), getChildList(listDichVuSH));
        categoryGroupList.add(categoryGroup);
        categoryGroup = new CategoryGroup(myGroupList.get(3), getChildList(listConCai));
        categoryGroupList.add(categoryGroup);
        categoryGroup = new CategoryGroup(myGroupList.get(4), getChildList(listTrangPhuc));
        categoryGroupList.add(categoryGroup);
        categoryGroup = new CategoryGroup(myGroupList.get(5), getChildList(listHieuHi));
        categoryGroupList.add(categoryGroup);
        categoryGroup = new CategoryGroup(myGroupList.get(6), getChildList(listNhaCua));
        categoryGroupList.add(categoryGroup);
        categoryGroup = new CategoryGroup(myGroupList.get(7), getChildList(listHuongThu));
        categoryGroupList.add(categoryGroup);
        categoryGroup = new CategoryGroup(myGroupList.get(8), getChildList(listSucKhoe));
        categoryGroupList.add(categoryGroup);
        categoryGroup = new CategoryGroup(myGroupList.get(9), getChildList(listPhatTrien));
        categoryGroupList.add(categoryGroup);
    }

    private ArrayList<CategoryChild> getChildList(List<String> list){
        ArrayList<CategoryChild> categoryChildList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CategoryChild categoryChild = new CategoryChild(list.get(i));
            categoryChildList.add(categoryChild);
        }
        return categoryChildList;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPrevious:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }
}