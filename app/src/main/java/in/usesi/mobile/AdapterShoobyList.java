package in.usesi.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class AdapterShoobyList extends RecyclerView.Adapter<AdapterShoobyList.MyViewHolder> {


    private Activity context;

    private List<ShopDetails> listCategory, listLevelOne = new ArrayList<>();

    private List<ShopDetails> listOneData = new ArrayList<>();

    private List<ShopDetails> listTwoData = new ArrayList<>();

    private List<ShopDetails> listThreeData = new ArrayList<>();

    private List<ShopDetails> listTwoCheck = new ArrayList<>();

    private List<ShopDetails> listThreeCheck = new ArrayList<>();

    private int j;


    AdapterShoobyList(Activity context) {
        this.context = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_list_category, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
            ShopDetails data = listLevelOne.get(position);
            if (j == 1) {
                holder.txtCategory.setText(listOneData.get(position).getName());
                String parentID = listOneData.get(position).getId();
                for (int i=0; i<listTwoCheck.size(); i++) {
                    if (listTwoCheck.get(i).getParent().equals(parentID)) {

                    }
                    else
                    {
                        holder.imgNav.setVisibility(View.GONE);
                    }

                }

            }
            else if (j == 2){
                holder.txtCategory.setText(listTwoData.get(position).getName());
                holder.imgNav.setVisibility(View.VISIBLE);
                String parentID = listTwoData.get(position).getId();
                for (int i=0; i<listThreeCheck.size(); i++) {
                    if (listThreeCheck.get(i).getParent().equals(parentID)) {


                    }
                    else
                    {
                        holder.imgNav.setVisibility(View.GONE);
                    }

                }
            }
            else if (j == 3){
                holder.txtCategory.setText(listThreeData.get(position).getName());
                holder.imgNav.setVisibility(View.GONE);
            }

        holder.txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (j == 1) {
                    String callURL = listOneData.get(position).getUrl();
                    Intent returnIntent;
                    returnIntent = new Intent(context, MainActivity.class);
                    returnIntent.putExtra("result",callURL);
                    context.setResult(MainActivity.RESULT_OK,returnIntent);
                    context.finish();
                }
                else if (j == 2) {
                    String callURL = listTwoData.get(position).getUrl();
                    Intent returnIntent;
                    returnIntent = new Intent(context, MainActivity.class);
                    returnIntent.putExtra("result",callURL);
                    context.setResult(MainActivity.RESULT_OK,returnIntent);
                    context.finish();
                }
                else if (j == 3) {
                    String callURL = listThreeData.get(position).getUrl();
                    Intent returnIntent;
                    returnIntent = new Intent(context, MainActivity.class);
                    returnIntent.putExtra("result",callURL);
                    context.setResult(MainActivity.RESULT_OK,returnIntent);
                    context.finish();
                }
            }
        });

        holder.imgNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (j == 1)
                {
                    j=2;
                    for (int i=0; i<listLevelOne.size(); i++) {
                        if (listLevelOne.get(i).getLevel().equals("2") && (listLevelOne.get(i).getParent().equals(listOneData.get(position).getId()))) {
                            Log.i("Message ", "onClickEvent: " + listOneData.get(position).getId());
                            listTwoData.add(new ShopDetails(listLevelOne.get(i).getId(), listLevelOne.get(i).getUrl(), listLevelOne.get(i).getName(), listLevelOne.get(i).getLevel(), listLevelOne.get(i).getParent()));
                        }
                        if (listLevelOne.get(i).getLevel().equals("3")) {
                            listThreeCheck.add(new ShopDetails(listLevelOne.get(i).getId(), listLevelOne.get(i).getUrl(), listLevelOne.get(i).getName(), listLevelOne.get(i).getLevel(), listLevelOne.get(i).getParent()));

                        }
                    }
                    notifyDataSetChanged();
                }
                else if (j == 2){
                    j=3;
                    for (int i=0; i<listLevelOne.size(); i++) {
                        if (listLevelOne.get(i).getLevel().equals("3") && (listLevelOne.get(i).getParent().equals(listTwoData.get(position).getId()))) {
                            Log.i("Message ", "onClickEvent: " + listOneData.get(position).getId());
                            listThreeData.add(new ShopDetails(listLevelOne.get(i).getId(), listLevelOne.get(i).getUrl(), listLevelOne.get(i).getName(), listLevelOne.get(i).getLevel(), listLevelOne.get(i).getParent()));
                        }
                    }
                    notifyDataSetChanged();
                }

            }
        });
    }

    public void setData(ArrayList<ShopDetails> listValues) {

        this.listLevelOne = listValues;
        j = 1;
        for (int i=0; i<listLevelOne.size(); i++) {
            if (listLevelOne.get(i).getLevel().equals("1")) {
                listOneData.add(new ShopDetails(listLevelOne.get(i).getId(), listLevelOne.get(i).getUrl(), listLevelOne.get(i).getName(), listLevelOne.get(i).getLevel(), listLevelOne.get(i).getParent()));
            }
            if (listLevelOne.get(i).getLevel().equals("2")) {
                listTwoCheck.add(new ShopDetails(listLevelOne.get(i).getId(), listLevelOne.get(i).getUrl(), listLevelOne.get(i).getName(), listLevelOne.get(i).getLevel(), listLevelOne.get(i).getParent()));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (j == 1) {
            return listOneData.size();
        }
        else if (j == 2){
            return listTwoData.size();
        }
        else if (j == 3){
            return listThreeData.size();
        }
        else
            return 0;
    }

    class MyViewHolder  extends RecyclerView.ViewHolder{

        private TextView txtCategory;
        private ImageView imgNav;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            imgNav = itemView.findViewById(R.id.imgNav);

        }
    }
}
