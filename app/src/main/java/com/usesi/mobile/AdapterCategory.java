package com.usesi.mobile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.MyViewHolder> {

    private Context context;

    private List<String> listCategory, listDoubleData = new ArrayList<>(),
            labelData, separatorHyphen;

    private List<ThirdData> listThirdData = new ArrayList<>();

    private List<Values> listValues;

    private int j;

    public void setListTitle() {
        j--;
        notifyDataSetChanged();
    }

    public interface OnTitleSelected {
        void showTitle(String title, int value);
    }

    public OnTitleSelected titleSelected;

    public void setListener(OnTitleSelected onTitleSelected) {
        this.titleSelected = onTitleSelected;
    }

    public AdapterCategory(Context context) {
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

                //listDoubleData = new ArrayList<>();

                for (int i = 0; i < listValues.size(); i++) {
                    String pat = listValues.get(i).getPath();
                    String label = listValues.get(i).getLabel();

                    if (pat.contains("/")) {
                        Log.d("tag", "CONTAINS /");
                        List<String> separatorData = Arrays.asList(pat.split("/"));
                        Log.d("tag", "SEPARATOR SIZE " + separatorData.size());
                        if (separatorData.size() == 2 && j == 2) {
                            Log.d("tag", "onClick: reaches if first");
                            if (separatorData.get(0).equalsIgnoreCase(path)) {
                                listDoubleData.add(separatorData.get(1));
                                Log.d("tag", "SIZE OD DOUBLE DATA    " + listDoubleData.size());
                            }
                        } else if (separatorData.size() == 3 && j == 3) {
                            // String label = listValues.get(i).getLabel();
                            //  separatorHyphen = Arrays.asList(label.split("|"));

                            //  Log.d("tag", "onClick:  hypen separotor ==" +separatorHyphen.get(0) + label) ;
                            if (separatorData.get(1).equalsIgnoreCase(path)) {
                                listThirdData.add(new ThirdData(separatorData.get(2), label));
                            }

                        } else if (j == 4) {
                            Log.d("tag", "J VALUE ID+S 4 ");
                            String thirdLabel = listThirdData.get(holder.getAdapterPosition()).getThirdLabel().trim();

                            thirdLabel= thirdLabel.replaceAll(" ", "");
                            Log.d("tag", "thirdLabel  === " +thirdLabel);
                            List<String> labelSeparotor = Arrays.asList(thirdLabel.split("\\|"));
                            Log.d("tag", "PATH== " +path  + "label value 00000 == " +
                                labelSeparotor.get(1) + "size == " +labelSeparotor.size());

                            if (path.equalsIgnoreCase(labelSeparotor.get(0))) {
                               // for (int h = 0; h < labelSeparotor.size(); h++) {
                                    Log.d("tag", "labelSeparator  == " + labelSeparotor.get(0));
                               // }
                            }

                        }
                    }
                }
                notifyDataSetChanged();
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
        this.listCategory = listValues;
    }

    public void setAllValues(List<Values> listValues) {
        this.listValues = listValues;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView txtCategory;

        private ImageView imgNav;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtCategory = (TextView) itemView.findViewById(R.id.txtCategory);
            imgNav = (ImageView) itemView.findViewById(R.id.imgNav);
        }
    }


}
