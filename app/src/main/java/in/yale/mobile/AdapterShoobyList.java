package in.yale.mobile;

import android.app.Activity;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class AdapterShoobyList extends RecyclerView.Adapter<AdapterShoobyList.MyViewHolder> {


    private final Activity context;

    private List<ShopDetails> listCategory, listLevelOne = new ArrayList<>();

    private final List<ShopDetails> listOneData = new ArrayList<>();

    private final List<ShopDetails> listTwoData = new ArrayList<>();

    private final List<ShopDetails> listThreeData = new ArrayList<>();

    private final List<ShopDetails> listTwoCheck = new ArrayList<>();

    private final List<ShopDetails> listThreeCheck = new ArrayList<>();

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

                if (listTwoCheck.size() > 0) {
                    int i = 0;
                    while (i < listTwoCheck.size()) {
                        if (listTwoCheck.get(i).getParent().equals(parentID)) {
                            holder.imgNav.setVisibility(View.VISIBLE);
                            break;

                        } else {

                            holder.imgNav.setVisibility(View.GONE);
                        }

                        i++;
                    }
                }
                else{
                    holder.imgNav.setVisibility(View.GONE);
                }

            }
            else if (j == 2){
                holder.txtCategory.setText(listTwoData.get(position).getName());
                holder.imgNav.setVisibility(View.VISIBLE);
                String parentID = listTwoData.get(position).getId();

                if (listThreeCheck.size() > 0) {
                    int i = 0;
                    while (i < listThreeCheck.size()) {
                        if (listThreeCheck.get(i).getParent().equals(parentID)) {
                            holder.imgNav.setVisibility(View.VISIBLE);
                            break;
                        } else {
                            holder.imgNav.setVisibility(View.GONE);
                        }
                        i++;
                    }
                }
                else{
                    holder.imgNav.setVisibility(View.GONE);
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
                    context.setResult(3, returnIntent);
                    context.finish();
                }
                else if (j == 2) {
                    String callURL = listTwoData.get(position).getUrl();
                    Intent returnIntent;
                    returnIntent = new Intent(context, MainActivity.class);
                    returnIntent.putExtra("result",callURL);
                    context.setResult(MainActivity.RESULT_OK,returnIntent);
                    context.setResult(3, returnIntent);
                    context.finish();
                }
                else if (j == 3) {
                    String callURL = listThreeData.get(position).getUrl();
                    Intent returnIntent;
                    returnIntent = new Intent(context, MainActivity.class);
                    returnIntent.putExtra("result",callURL);
                    context.setResult(MainActivity.RESULT_OK,returnIntent);
                    context.setResult(3, returnIntent);
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
            else if (listLevelOne.get(i).getLevel().equals("2")) {
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

        private final TextView txtCategory;
        private final ImageView imgNav;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            imgNav = itemView.findViewById(R.id.imgNav);

        }
    }
}
