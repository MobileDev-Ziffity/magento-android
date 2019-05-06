package in.usesi.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import static in.usesi.mobile.R.string.SHOP_BY_CATEGORY;


public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.MyViewHolder> {

    private Activity context;

    private List<String> listCategory, listDoubleData, listThreeData = new ArrayList<>();

    private List<ThirdData> listThirdData = new ArrayList<>();

    private List<SecondData> listSecondData = new ArrayList<>();

    private List<Values> listValues;

    private int j;

    void setListTitle() {
        j--;
        if (j > 0) {
            notifyDataSetChanged();
        }
        else
            ((ActivityList)context).finish();
    }

    public interface OnTitleSelected {
        void showTitle(String title, int value);
    }

    private OnTitleSelected titleSelected;

    void setListener(OnTitleSelected onTitleSelected) {
        this.titleSelected = onTitleSelected;
    }


    AdapterCategory(Activity context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_list_category, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (j == 1) {
            listDoubleData = new ArrayList<>();
            listSecondData = new ArrayList<>();
            holder.txtCategory.setText(listCategory.get(holder.getAdapterPosition()));
            holder.imgNav.setVisibility(View.VISIBLE);
            if (titleSelected != null)
                 titleSelected.showTitle("Shop By Category", j);
        }
        if (j == 2) {
            listThreeData = new ArrayList<>();
            holder.txtCategory.setText(listDoubleData.get(holder.getAdapterPosition()));
            holder.imgNav.setVisibility(View.VISIBLE);
            SharedPreferences sp = context.getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
            String secondTitle = sp.getString("SecondTitle", "Default");
            if (titleSelected != null)
               titleSelected.showTitle(secondTitle, j);
        }
        if (j == 3) {

            holder.txtCategory.setText(listThreeData.get(holder.getAdapterPosition()));
            holder.imgNav.setVisibility(View.GONE);
        }

        holder.imgNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                j++;
                String path = holder.txtCategory.getText().toString().trim();

                for (int i = 0; i < listValues.size(); i++) {
                    String pat = listValues.get(i).getPath();
                    String label = listValues.get(i).getLabel();

                    if (pat.contains("/")) {
                        List<String> separatorData = Arrays.asList(pat.split("/"));
                        if (separatorData.size() == 2 && j == 2) {
                            if (separatorData.get(0).equalsIgnoreCase(path)) {
                                listSecondData.add(new SecondData(separatorData.get(1), label));
                                listDoubleData.add(separatorData.get(1));
                                titleSelected.showTitle(path, j);
                                SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("SecondTitle", path);
                                editor.commit();

                                notifyDataSetChanged();
                            }
                        } else if (separatorData.size() == 3 && j == 3) {
                            if (separatorData.get(1).equalsIgnoreCase(path)) {
                                listThirdData.add(new ThirdData(separatorData.get(2), label));
                                listThreeData.add(separatorData.get(2));
                                titleSelected.showTitle(path, j);
                                notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });

        holder.txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             if (j == 3) {
                    String thirdLabel = listThirdData.get(holder.getAdapterPosition()).getThirdLabel();

                    StringTokenizer tokens = new StringTokenizer(thirdLabel, "|");
                    String first = tokens.nextToken();
                    String loadURL = tokens.nextToken();


                    if ((loadURL.length() > 0)){
                        Intent returnIntent;
                        returnIntent = new Intent(context, MainActivity.class);
                        returnIntent.putExtra("result",loadURL);
                        context.setResult(MainActivity.RESULT_OK,returnIntent);
                        context.finish();
                    }
                }
                else if (j == 2) {
                    String thirdLabel = listSecondData.get(holder.getAdapterPosition()).getSecondLabel();
                     Log.i("Message Link", thirdLabel);
                    StringTokenizer tokens = new StringTokenizer(thirdLabel, "|");
                    String first = tokens.nextToken();
                    String loadURL = tokens.nextToken();


                    if ((loadURL.length() > 0)){
                        Intent returnIntent;
                        returnIntent = new Intent(context, MainActivity.class);
                        returnIntent.putExtra("result",loadURL);
                        context.setResult(MainActivity.RESULT_OK,returnIntent);
                        context.finish();
                    }
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        if (j == 1)
            return listCategory.size();
        else if (j == 2)
            return listDoubleData.size();
        else {
            return listThreeData.size();
        }
    }

    public void setData(List<String> listValues) {
        j = 1;
        Collections.sort(listValues);
        this.listCategory = listValues;
    }

    void setAllValues(List<Values> listValues) {
        this.listValues = listValues;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView txtCategory;
        private ImageView imgNav;

        MyViewHolder(View itemView) {
            super(itemView);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            imgNav = itemView.findViewById(R.id.imgNav);
        }
    }
}
