package com.manish.chushylearn.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.manish.chushylearn.Model.ModuleModel;

import java.util.List;

import com.manish.chushylearn.R;

/**
 * Created by rbaisak on 3/16/17.
 */

public class ItemsAdapter extends BaseAdapter {

  private List<ModuleModel> moduleModelList;

  private Context context;

  private OnOptionClickedListener onOptionClickedListener;

  public ItemsAdapter(Context context, List<ModuleModel> moduleModelList) {
    this.context = context;
    this.moduleModelList = moduleModelList;
  }

  public void setOnOptionClickedListener(OnOptionClickedListener onOptionClickedListener) {
    this.onOptionClickedListener = onOptionClickedListener;
  }

  @Override
  public int getCount() {
    return moduleModelList.size();
  }

  @Override
  public Object getItem(int position) {
    return moduleModelList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {

    View view = LayoutInflater.from(context).inflate(R.layout.adapter_item, null);

    TextView titleTV = (TextView) view.findViewById(R.id.titleTV);
    ImageButton actionEdit = (ImageButton) view.findViewById(R.id.actionEdit);
    ImageButton actionDelete = (ImageButton) view.findViewById(R.id.actionDelete);


    actionEdit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (onOptionClickedListener != null)
          onOptionClickedListener.onEditOptionClicked(position);
      }
    });

    actionDelete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (onOptionClickedListener != null)
          onOptionClickedListener.onDeleteOptionClicked(position);
      }
    });

    titleTV.setText(moduleModelList.get(position).getTopicName());

    return view;
  }


  public interface OnOptionClickedListener {
    void onEditOptionClicked(int position);

    void onDeleteOptionClicked(int position);
  }


}
