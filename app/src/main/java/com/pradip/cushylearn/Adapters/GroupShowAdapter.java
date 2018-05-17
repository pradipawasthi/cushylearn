package com.pradip.cushylearn.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pradip.cushylearn.Activities.RealTimeChatScreenActivity;
import com.pradip.cushylearn.ApplicationConfig.MyApplication;
import com.pradip.cushylearn.Model.ChatUser;
import com.pradip.cushylearn.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shortroid.com.shortroid.ShortRoidPreferences.FileNameException;
import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;

//import com.appiva.rb.ezmap.ApplicationConfig.RealTimeChatScreenActivity;
//.MyApplication;


/**
 * Created by JohnConnor on 06-Sep-16.
 */
public class GroupShowAdapter extends RecyclerView.Adapter<GroupShowAdapter.ViewHolder>{
    Activity context;
    List<String> groupNames;
    List<String> groupKeys;
    List<Boolean> groupTypes;
        public GroupShowAdapter(Activity context, List<String> groupNames, List<String> groupKeys, List<Boolean> groupTypes)
        {
            this.context=context;
            this.groupKeys=groupKeys;
            this.groupNames=groupNames;
            this.groupTypes=groupTypes;
        }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_activity_group_show_adapter, parent, false);

    return new ViewHolder(v);

}

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String groupName=groupNames.get(position);
        final Boolean grouptype=groupTypes.get(position);
        final String groupKey=groupKeys.get(position);
        holder.groupName.setText(groupName);
        holder.groupType.setImageDrawable(context.getResources().getDrawable(R.drawable.privates));
        if(grouptype)
        {
            holder.groupType.setImageDrawable(context.getResources().getDrawable(R.drawable.publics));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, RealTimeChatScreenActivity.class);
                intent.putExtra("key",groupKey);
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Delete the Group")
                        .setNegativeButton("Cancel",null);
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                            delete(groupKey, new OnChange() {
                                                @Override
                                                public void onChange() {
                                                    groupNames.remove(position);
                                                    groupKeys.remove(position);
                                                    groupTypes.remove(position);
                                                    notifyItemRemoved(position);
                                                    notifyItemRangeRemoved(0,groupKeys.size());
                                                    notifyDataSetChanged();
                                                }
                                            });
                                    }
                                });

                                AlertDialog dialog = builder.create();
                dialog.show();

                return false;
            }
        });
    }
    interface OnChange
    {
        void onChange();
    }
    @Override
    public int getItemCount() {
        return groupKeys.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
            TextView groupName;
            ImageView groupType;
        public ViewHolder(View itemView) {
            super(itemView);
            groupName=(TextView)itemView.findViewById(R.id.group_name);
            groupType=(ImageView)itemView.findViewById(R.id.group_type);

        }
    }
    ShortRoidPreferences shortRoidPreferences;
    void delete(final String groupKey, final OnChange on)
    {
        try {
            shortRoidPreferences=new ShortRoidPreferences(context,"chat", Context.MODE_PRIVATE);
        } catch (FileNameException e) {
            e.printStackTrace();
        }
        FirebaseDatabase database = MyApplication.getFireBaseInstance();
        final DatabaseReference myRef = database.getReference("users");



        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for (final DataSnapshot ds : dataSnapshots) {
                    ChatUser chatUser = ds.getValue(ChatUser.class);
                    if (chatUser.getPhone().contentEquals(shortRoidPreferences.getPrefString("phone"))) {
                        String userKey = ds.getKey();
                        Log.d("firebase", ds.getKey());
                        //ds.getRef().child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        ds.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ChatUser chatUser = dataSnapshot.getValue(ChatUser.class);
                                List<String> temp = chatUser.getGroups();
                                int h=0;
                                for(int i=0;i<temp.size();i++)
                                {
                                    if(temp.get(i).contentEquals(groupKey))
                                    {
                                        h=i;
                                        break;
                                    }
                                }
                                temp.remove(h);
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("groups", temp);
                                ds.getRef().updateChildren(map);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Log.d("firebase", ds.getRef().getKey());
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final DatabaseReference databaseReference=database.getReference("groups");
        databaseReference.child(groupKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("datasnap",dataSnapshot.toString() + databaseReference.toString());
                try{
                List<String> temp=(List<String>)dataSnapshot.child("gMembers").getValue();
                String g=shortRoidPreferences.getPrefString("phone");
                int h=0;
                for(int i=0;i<temp.size();i++)
                {
                    if(temp.get(i).contentEquals(g))
                    {   h=i;
                        break;
                    }
                }
                temp.remove(h);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("gMembers", temp);
                dataSnapshot.getRef().updateChildren(map);
                on.onChange();
            }
                catch (Exception e){
                    Log.v("dataChange",e.toString());
                }

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
