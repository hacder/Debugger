package com.ude.debuggerlibrary.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ude.debuggerlibrary.R;
import com.ude.debuggerlibrary.adpter.listener.OnItemClickListener;
import com.ude.debuggerlibrary.data.CheckData;

import java.util.List;

/**
 * Created by ude on 2017-10-18.
 */

public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.MyViewHolder>{
    private List<CheckData> checkDatas;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private boolean isCheckMode =false;//是否为选择模式

    public CheckAdapter(Context context,List<CheckData> checkDatas){
        this.context = context;
        this.checkDatas = checkDatas;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_check,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tv_check.setText(checkDatas.get(position).getContent());
        if (isCheckMode){
            holder.checkBox.setVisibility(View.VISIBLE);
            if (checkDatas.get(position).isCheck()){
                holder.checkBox.setChecked(true);
            }else {
                holder.checkBox.setChecked(false);
            }
        }else {
            holder.checkBox.setVisibility(View.GONE);
        }

        if (onItemClickListener != null){
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

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDatas.get(holder.getLayoutPosition()).setCheck(holder.checkBox.isChecked());
                notifyItemChanged(holder.getLayoutPosition());
            }
        });
    }

    public boolean isCheckMode() {
        return isCheckMode;
    }

    public void setCheckMode(boolean checkMode) {
        isCheckMode = checkMode;
    }

    @Override
    public int getItemCount() {
        return checkDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout ll_item;
        CheckBox checkBox;
        TextView tv_check;
        public MyViewHolder(View itemView) {
            super(itemView);
            ll_item = (LinearLayout)itemView.findViewById(R.id.ll_item);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkbox);
            tv_check = (TextView)itemView.findViewById(R.id.tv_check);
        }
    }
}
