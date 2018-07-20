package com.ude.debuggerlibrary.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ude.debuggerlibrary.R;
import com.ude.debuggerlibrary.adpter.listener.OnItemClickListener;

import java.util.List;

/**
 * Created by ude on 2017-10-12.
 */

public class FunctionWindowAdapter extends RecyclerView.Adapter<FunctionWindowAdapter.MyViewHolder>{
    private List<String> strings;
    private List<Integer> integers;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public FunctionWindowAdapter(Context context,List<String> strings,List<Integer> integers){
        this.context = context;
        this.strings = strings;
        this.integers = integers;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_function,parent,false));
    }

    @Override
    public int getItemCount() {
        return integers.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.img_function.setImageResource(integers.get(position));
        holder.tv_function.setText(strings.get(position));
        if (onItemClickListener != null){
            holder.ll_function_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.OnClick(view,holder.getLayoutPosition());
                }
            });
            holder.ll_function_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemClickListener.OnLongClick(view,holder.getLayoutPosition());
                    return false;
                }
            });
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout ll_function_item;
        ImageView img_function;
        TextView tv_function;
        public MyViewHolder(View itemView) {
            super(itemView);
            ll_function_item = (LinearLayout) itemView.findViewById(R.id.ll_function_item);
            img_function = (ImageView) itemView.findViewById(R.id.img_function);
            tv_function = (TextView) itemView.findViewById(R.id.tv_function);
        }
    }
}
