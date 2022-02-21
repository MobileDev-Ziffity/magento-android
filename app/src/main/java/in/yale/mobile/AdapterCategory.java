package in.yale.mobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;



import androidx.recyclerview.widget.RecyclerView;

class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.ExampleViewHolder>{
    public ArrayList<ActivityList.mainaray> menulist;
    public ArrayList<ActivityList.mainaray> menulist1;
    public ArrayList<ActivityList.mainaray> menulist1_sort;
    public ArrayList<ActivityList.mainaray> menulist2;
    public ArrayList<ActivityList.mainaray> menulist2_sort;
    private Activity vcontext;
    private Context mContext;
    private int jj;

//    private OnTitleSelecteds titleSelecteds;
//
//
//    public interface OnTitleSelecteds {
//        void showTitle(String title);
//    }



//    void setListener(OnTitleSelecteds ch) {
//
//        this.titleSelecteds = ch;
//    }
    AdapterCategory(Activity context) {

        this.mContext = context;
    }


    void setListTitle() {
        jj--;
        if (jj == 0 || jj == 1 || jj == 2 ) {
            notifyDataSetChanged();
        }
        else
            ((ActivityList)mContext).finish();
    }
    @Override
    public int getItemCount() {

        if (jj == 0)
            return menulist.size();
        else if (jj == 1)
            return menulist1_sort.size();

        else {
            return menulist2_sort.size();
        }
    }
    public AdapterCategory(Context context, ArrayList<ActivityList.mainaray> exampleList,ArrayList<ActivityList.mainaray> exampleList1,ArrayList<ActivityList.mainaray> exampleList2) {
        this.mContext = context;
        menulist = exampleList;
        menulist1 = exampleList1;
        menulist2 = exampleList2;

        // Log.d("tag","these are the value in example list"+exampleList.toString());
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_list_category, parent, false);
        return new AdapterCategory.ExampleViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ExampleViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (jj == 0) {

            menulist1_sort = new ArrayList<>();
            ActivityList.mainaray currentItem = menulist.get(position);
            ActivityList.mainaray po = menulist.get(position);
            String labelname = currentItem.getlab();
            holder.txtCategory.setText(labelname);
            String chi = currentItem.getchild();
            if (chi.equals("true")) {
                holder.imgNav.setVisibility(View.VISIBLE);
            } else {
                holder.imgNav.setVisibility(View.GONE);
            }

//            if (this.titleSelecteds != null)
//                this.titleSelecteds.showTitle("Shop By Category");
            ((ActivityList)mContext).txtTitle.setText("Shop By Category");
        }else if (jj == 1) {
            menulist2_sort = new ArrayList<>();
            // ActivityList.mainaray currentItem = menulist1_sort.get(position);
            ActivityList.mainaray po = menulist1_sort.get(position);
            String labelname = po.getlab();
            holder.txtCategory.setText(labelname);
            String chi = po.getchild();
            if (chi.equals("true")) {
                holder.imgNav.setVisibility(View.VISIBLE);
            } else {
                holder.imgNav.setVisibility(View.GONE);
            }
            SharedPreferences sp = mContext.getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
            String secondTitle = sp.getString("SecondTitle", "Default");

//            if (titleSelecteds != null)
//                titleSelecteds.showTitle(secondTitle);
            ((ActivityList)mContext).txtTitle.setText(secondTitle);
        }else if (jj == 2) {

            // ActivityList.mainaray currentItem = menulist1_sort.get(position);
            ActivityList.mainaray po = menulist2_sort.get(position);
            String labelname = po.getlab();
            holder.txtCategory.setText(labelname);
            String chi = po.getchild();
            if (chi.equals("true")) {
                holder.imgNav.setVisibility(View.VISIBLE);
            } else {
                holder.imgNav.setVisibility(View.GONE);
            }
            SharedPreferences sp = mContext.getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
            String thirdTitle = sp.getString("ThreeTitle", "Default");
//            if (titleSelecteds != null)
//                titleSelecteds.showTitle(thirdTitle);
            ((ActivityList)mContext).txtTitle.setText(thirdTitle);
        }
        holder.imgNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jj++;
                if (jj == 1) {
                    String path = holder.txtCategory.getText().toString().trim();
//                    if (titleSelecteds != null)
//                        titleSelecteds.showTitle(path);

                    ((ActivityList)mContext).txtTitle.setText(path);
                    //  ((MainActivity) getActivity()).logoimg.setBackgroundResource(R.drawable.onboard_logo);
                    ActivityList.mainaray po = menulist.get(position);
                    String id = po.getid();
                    for(int i = 0; i < menulist1.size(); i++){
                        ActivityList.mainaray i1 = menulist1.get(i);
                        String pid = i1.getparentid();

                        if(id.equals(pid) ) {
                            String lab = i1.getlab();
                            String va = i1.getvalue();
                            String pva = i1.getparentid();
                            String gid = i1.getid();
                            String ch = i1.getchild();
                            menulist1_sort.add(new ActivityList.mainaray(lab, va, pva, gid, ch));
                        }
                    }
                    SharedPreferences preferences = mContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("SecondTitle", path);
                    editor.commit();

                    notifyDataSetChanged();
                }else if (jj == 2) {
                    String path = holder.txtCategory.getText().toString().trim();
//                    if (titleSelecteds != null)
//                        titleSelecteds.showTitle(path);
                    ((ActivityList)mContext).txtTitle.setText(path);
                    ActivityList.mainaray po = menulist1_sort.get(position);
                    String id = po.getid();
                    for(int i = 0; i < menulist2.size(); i++){
                        ActivityList.mainaray i1 = menulist2.get(i);
                        String pid = i1.getparentid();

                        if(id.equals(pid) ) {
                            String lab = i1.getlab();
                            String va = i1.getvalue();
                            String pva = i1.getparentid();
                            String gid = i1.getid();
                            String ch = i1.getchild();
                            menulist2_sort.add(new ActivityList.mainaray(lab, va, pva, gid, ch));
                        }
                    }
                    SharedPreferences preferences = mContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("ThreeTitle", path);
                    editor.commit();

                    notifyDataSetChanged();



                }
            }
        });
        holder.txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(jj == 0){
                    ActivityList.mainaray ss = menulist.get(position);
                    String val = ss.getvalue();

                    Intent returnIntent;
                    returnIntent = new Intent(mContext, MainActivity.class);
                    returnIntent.putExtra("result",val);
                    ((ActivityList)mContext).setResult(MainActivity.RESULT_OK,returnIntent);
                    ((ActivityList)mContext).finish();

                }else  if(jj == 1){
                    ActivityList.mainaray ss = menulist1_sort.get(position);
                    String val = ss.getvalue();

                    Intent returnIntent;
                    returnIntent = new Intent(mContext, MainActivity.class);
                    returnIntent.putExtra("result",val);
                    ((ActivityList)mContext).setResult(MainActivity.RESULT_OK,returnIntent);
                    ((ActivityList)mContext).finish();
                }else  if(jj == 2){
                    ActivityList.mainaray ss = menulist2_sort.get(position);
                    String val = ss.getvalue();

                    Intent returnIntent;
                    returnIntent = new Intent(mContext, MainActivity.class);
                    returnIntent.putExtra("result",val);
                    ((ActivityList)mContext).setResult(MainActivity.RESULT_OK,returnIntent);
                    ((ActivityList)mContext).finish();
                }

            }
        });
    }
    class ExampleViewHolder extends RecyclerView.ViewHolder {

        private TextView txtCategory;
        private ImageView imgNav;

        ExampleViewHolder(View itemView) {
            super(itemView);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            imgNav = itemView.findViewById(R.id.imgNav);
        }
    }
}
