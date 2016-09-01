package com.aloh.YOU.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aloh.YOU.util.C;
import com.aloh.YOU.util.ContactParser;
import com.aloh.YOU.R;
public class HistoryAdapter extends ArrayAdapter<String> {

    private final Context context;
    private ArrayList<String> desc;
    //private ArrayList<String> amount;
    private final int rowResourceId;

    
    public HistoryAdapter(Context context, int textViewResourceId, ArrayList<String> desc, ArrayList<String> amount) {

        super(context, textViewResourceId, desc);

        this.context = context;
        this.desc = desc;
        //this.amount = amount;
        this.rowResourceId = textViewResourceId;

    }
    
    class ViewHolder{
    	
    	TextView callName;
    	TextView callPhone;
        TextView callTime;
        ImageView callType;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(rowResourceId, parent, false);
        
        ViewHolder holder = new ViewHolder();

        holder.callName = (TextView) rowView.findViewById(R.id.callName);
        holder.callPhone = (TextView) rowView.findViewById(R.id.callPhone);
        holder.callTime = (TextView) rowView.findViewById(R.id.callTime);
        holder.callType = (ImageView) rowView.findViewById(R.id.callType);
        holder.callName.setText(this.desc.get(position));        
      	holder.callType = (ImageView) rowView.findViewById(R.id.callType);
      	switch(History.type.get(position)){
      	
      		case 3: holder.callType.setImageResource(R.drawable.ic_arrow_blue);holder.callPhone.setText(History.from.get(position)+" -> "+History.to.get(position));break;
	      	//case 3: holder.callType.setImageResource(R.drawable.ic_arrow_blue);holder.callPhone.setText(History.from.get(position)+" -> "+History.to.get(position));break;
	      	case 4: holder.callType.setImageResource(R.drawable.ic_arrow_green);holder.callPhone.setText(History.from.get(position));break;
	      	//case 4: holder.callType.setImageResource(R.drawable.ic_arrow_green);holder.callPhone.setText(History.to.get(position)+" -> "+History.from.get(position));break;
	      	case 5: holder.callType.setImageResource(R.drawable.ic_arrow_callback);holder.callPhone.setText(History.to.get(position)+" -> "+History.from.get(position)); break;
	      	case 6: holder.callType.setImageResource(R.drawable.ic_arrow_missed);holder.callPhone.setText(History.from.get(position)+" -> "+History.to.get(position));	break;
      	}
      	holder.callName.setText(History.name.get(position));
      	
      	
      	holder.callTime.setText(ContactParser.date(History.time.get(position)+C.gmt, getContext()));
      	
      	/*
        if(this.amount.get(position).equals("0")){
        	
        	holder.callType.setImageResource(R.drawable.ic_arrow_green);	
        }else if(this.amount.get(position).equals("1")){
        	
        	holder.callType.setImageResource(R.drawable.ic_arrow_green);	
        }else if(this.amount.get(position).equals("2")){
        	
        	holder.callType.setImageResource(R.drawable.ic_arrow_blue);	
        }
        */
        //holder.amount.setText(String.valueOf(this.amount.get(position)));
   
        return rowView;

    }

}