package com.pradip.cushylearn.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.pradip.cushylearn.Model.ItemNameList;
import com.pradip.cushylearn.R;

import java.util.ArrayList;

/**
 * Created by rbaisak on 15/2/17.
 */

public class ItemsListAdapter extends ArrayAdapter<ItemNameList> {

    private Context context;
    private ArrayList<ItemNameList> list=new ArrayList<ItemNameList>();
    int resource;

    private OnClickListener clickListener;
  //  private OnClickListener listener;



    public ItemsListAdapter(Context context, int resource, ArrayList<ItemNameList> list, OnClickListener clickListener) {
        super(context, resource, list);
        this.context = context;
        this.list = list;
        this.resource=resource;

        this.clickListener = clickListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemHolder holder;  // holder=null;

        if (row == null) {
            LayoutInflater infalInflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            //view = layoutInflater.inflate(R.layout.listview_item, parent,false);
            row = infalInflater.inflate(R.layout.custom_list_multiclick, null);
            //LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            //row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ItemHolder();
            holder.bt_do1=(ImageView)row.findViewById(R.id.iconEdit);
            holder.bt_do2=(ImageView)row.findViewById(R.id.iconDel);
            //bt_do.setOnClickListener(listener);
            holder.t1= (TextView)row.findViewById(R.id.textViewTitle);
            //holder.t1.setText(list.get(position).getItem_List());

            holder.bt_do1.setTag(position);
            holder.bt_do2.setTag(position);



//            holder.textName = (TextView) row.findViewById(R.id.textView1);
//            holder.textAddress = (TextView) row.findViewById(R.id.textView2);
//            holder.textLocation = (TextView) row.findViewById(R.id.textView3);
//            holder.btnEdit = (Button) row.findViewById(R.id.button1);
//            holder.btnDelete = (Button) row.findViewById(R.id.button2);
            row.setTag(holder);
        } else {
            holder = (ItemHolder) row.getTag();
        }
       // ItemNameList itm = list.get(position);
        ItemNameList itm = getItem(position);;
        holder.t1.setText(itm.getItem_List());



        setClickListeners(holder.bt_do1);  // arama imagebuttona tıklanma eventi ver
        setClickListeners(holder.bt_do2);   // mesaj gönderme imagebuttona tıklanma eventi ver

        setTagsToViews(holder.bt_do1, position);  // arama imagebuttona tag olarak position ver
        setTagsToViews(holder.bt_do2, position);  // mesaj gönderme imagebuttona tag olarak position ver



        holder.bt_do1.setTag(position);
        holder.bt_do2.setTag(position);
        //holder.txtName.setText(itm.getName());
        //holder.txtNumber.setText(itm.getPhoneNumber());


//        holder.textName.setText(itm.getName());
//        holder.textAddress.setText(user.getAddress());
//        holder.textLocation.setText(user.getLocation());
     /**********
        holder.bt_do1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("Edit Button Clicked", "**********");
                Toast.makeText(context, "Edit button Clicked"+ position,
                        Toast.LENGTH_LONG).show();
                Intent ieventreport = new Intent(context,ItemDetailsActivity.class);
                context.startActivity(ieventreport);





            }
        });
      *****************/
        /***************************************************************
        holder.bt_do2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("Delete Button Clicked", "**********");


                Toast.makeText(context, "Delete button Clicked"+ position,
                        Toast.LENGTH_LONG).show();

                list.remove(position);

                //Integer index = (Integer) v.getTag().toString();
               // list.remove(index.intValue());





                //


            }
        });

         ***********************************************************************************/
        return row;

    }


    private void setTagsToViews(View view, int position) {

        view.setTag(R.id.key_position, position);
    }

    private void setClickListeners(View view) {

        view.setOnClickListener(clickListener);
    }

     class ItemHolder {
        ImageView bt_do1;
        ImageView bt_do2;
        TextView  t1,quantity,sellingprice,marketprice,availableitem,quality,addedByKey;
        Spinner Category;




    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public ItemNameList getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void remove(int s) {
    }


}

