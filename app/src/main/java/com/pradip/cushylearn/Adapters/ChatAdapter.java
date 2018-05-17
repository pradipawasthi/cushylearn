package com.pradip.cushylearn.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pradip.cushylearn.R;
import com.pradip.cushylearn.Model.Chat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import shortroid.com.shortroid.ShortRoidPreferences.FileNameException;
import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;



public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>
{   Context context;
    List<Chat> chats;
    int temp=-1;
    ShortRoidPreferences shortRoidPreferences;
    public ChatAdapter(Activity context,List<Chat> chats)
    {
        this.context=context;
        this.chats = chats;
        try {
            shortRoidPreferences=new ShortRoidPreferences(context,"chat",context.MODE_PRIVATE);
        } catch (FileNameException e) {
            e.printStackTrace();
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if(viewType==temp) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_activity_send_adapter_layout, parent, false);
        }
        else
        {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_activity_receive_adapter_layout, parent, false);
        }
        return new ViewHolder(v);

    }

    @Override
    public int getItemViewType(int position) {
       Chat chat = chats.get(position);
        if (chat.getPhone().contentEquals(shortRoidPreferences.getPrefString("phone")))
        {
            return temp;
        }
        else
            return position;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {

            Chat chat = chats.get(position);
            holder.text.setText(chat.getChatText());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(chat.getTimestamp());
            holder.time.setText(simpleDateFormat.format(calendar.getTime()));
            holder.name.setText(chat.getName());
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView time;
        TextView text;
        TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            time=(TextView)itemView.findViewById(R.id.chat_message_time);
            text=(TextView)itemView.findViewById(R.id.chat_message_text);
            name=(TextView)itemView.findViewById(R.id.chat_message_sender_name);

        }
    }
}
