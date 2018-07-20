package com.ude.debugger.adpter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ude.debugger.R;
import com.ude.debugger.adpter.listener.OnItemClickListener;
import com.ude.debugger.utils.LogUtil;

import java.util.List;

/**
 * Created by ude on 2017-10-13.
 */

public class InfoShowAdapter extends RecyclerView.Adapter<InfoShowAdapter.MyViewHolder>{
    public static final int V = 0;
    public static final int D = 1;
    public static final int I = 2;
    public static final int W = 3;
    public static final int E = 4;

    private List<String> infoList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public InfoShowAdapter(Context context,List<String> infoList){
        this.context = context;
        this.infoList = infoList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_info,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        int level = V;
        String content = infoList.get(position);
        holder.tv_info.setText(content);
            if (content.contains(" V ")) {//白,默认
                holder.tv_info.setTextColor(Color.WHITE);
                level = V;
            } else if (content.contains(" I ")) {//蓝
                holder.tv_info.setTextColor(Color.BLUE);
                level = I;
            } else if (content.contains(" D ")) {//绿
                holder.tv_info.setTextColor(Color.GREEN);
                level = D;
            } else if (content.contains(" W ")) {//橙
                holder.tv_info.setTextColor(Color.YELLOW);
                level = W;
            } else if (content.contains(" E ")) {//红
                holder.tv_info.setTextColor(Color.RED);
                level = E;
            }

        if (onItemClickListener!=null){
            holder.ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.OnClick(view,holder.getLayoutPosition());
                }
            });
            holder.ll_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemClickListener.OnLongClick(view,holder.getLayoutPosition());
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout ll_item;
        TextView tv_info;
        public MyViewHolder(View itemView) {
            super(itemView);
            ll_item = (LinearLayout)itemView.findViewById(R.id.ll_item);
            tv_info = (TextView)itemView.findViewById(R.id.tv_info);
        }
    }
}
