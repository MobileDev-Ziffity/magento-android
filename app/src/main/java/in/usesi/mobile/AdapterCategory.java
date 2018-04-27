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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.MyViewHolder> {

    private Activity context;

    private List<String> listCategory, listDoubleData = new ArrayList<>(),
            labelData, separatorHyphen;

    private List<ThirdData> listThirdData = new ArrayList<>();

    private List<Values> listValues;

    private int j;

    void setListTitle() {
        j--;
        notifyDataSetChanged();

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
            holder.txtCategory.setText(listCategory.get(holder.getAdapterPosition()));
            if (titleSelected != null)
                titleSelected.showTitle(listCategory.get(holder.getAdapterPosition()), j);
        }
        if (j == 2) {
            holder.txtCategory.setText(listDoubleData.get(holder.getAdapterPosition()));
            if (titleSelected != null)
                titleSelected.showTitle(listDoubleData.get(holder.getAdapterPosition()), j);
        }
        if (j == 3) {
            holder.txtCategory.setText(listThirdData.get(holder.getAdapterPosition()).getThirdValue());
            holder.imgNav.setVisibility(View.GONE);
        }

        holder.txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                j++;
                String path = holder.txtCategory.getText().toString().trim();
                if (titleSelected != null)
                    titleSelected.showTitle(path, j);

                for (int i = 0; i < listValues.size(); i++) {
                    String pat = listValues.get(i).getPath();
                    String label = listValues.get(i).getLabel();

                    if (pat.contains("/")) {
                        List<String> separatorData = Arrays.asList(pat.split("/"));
                        if (separatorData.size() == 2 && j == 2) {
                            if (separatorData.get(0).equalsIgnoreCase(path)) {
                                listDoubleData.add(separatorData.get(1));
                                notifyDataSetChanged();
                            }
                        } else if (separatorData.size() == 3 && j == 3) {
                            if (separatorData.get(1).equalsIgnoreCase(path)) {
                                listThirdData.add(new ThirdData(separatorData.get(2), label));
                                notifyDataSetChanged();
                            }
                        } else if (j == 4) {
                            String thirdLabel = listThirdData.get(holder.getAdapterPosition()).getThirdLabel();
                            List<String> labelSeparotor = Arrays.asList(thirdLabel.split("\\|"));
                            String URL  = labelSeparotor.get(0);

                            if (path.equals(URL.trim())){
                                Intent returnIntent;
                                returnIntent = new Intent(context, MainActivity.class);
                                returnIntent.putExtra("result",labelSeparotor.get(1));
                                context.setResult(MainActivity.RESULT_OK,returnIntent);
                                context.finish();
                            }
                        }
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
            return listThirdData.size();
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