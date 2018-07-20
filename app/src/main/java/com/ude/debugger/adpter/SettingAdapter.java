package com.ude.debugger.adpter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ude.debugger.R;
import com.ude.debugger.adpter.listener.OnItemClickListener;

import java.util.List;
import java.util.Map;

/**
 * Created by ude on 2017-10-12.
 */

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.MyViewHolder>{
    public static final int NOSWICH = 0;//无选项
    public static final int NOCHOOSE = 1;//有选项但未选中
    public static final int CHOOSE = 2;//有选项且选中

    private List<String> settingNameList;
    private List<Integer> settingType;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private SharedPreferences sp;

    public SettingAdapter(Context context, List<String> settingNameList, List<Integer> settingType){
        this.context = context;
        this.settingNameList = settingNameList;
        this.settingType = settingType;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_setting,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tv_setting.setText(settingNameList.get(position));
        if (settingType != null){
        switch (settingType.get(position)){
            case NOSWICH:
                holder.switch_setting.setVisibility(View.GONE);
                break;
            case NOCHOOSE:
                holder.switch_setting.setVisibility(View.VISIBLE);
                holder.switch_setting.setChecked(false);
                break;
            case CHOOSE:
                holder.switch_setting.setVisibility(View.VISIBLE);
                holder.switch_setting.setChecked(true);
        }
        }else {
            holder.switch_setting.setVisibility(View.GONE);
        }

        if (onItemClickListener != null){
            holder.ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (settingType != null && settingType.get(holder.getLayoutPosition()) != NOSWICH) {
                        holder.switch_setting.setChecked(!holder.switch_setting.isChecked());
                        if (holder.switch_setting.isChecked()){
                            settingType.set(holder.getLayoutPosition(),CHOOSE);
                        }else {
                            settingType.set(holder.getLayoutPosition(),NOCHOOSE);
                        }
                        saveSetting(settingNameList.get(holder.getLayoutPosition()),settingType.get(holder.getLayoutPosition()));
                    }
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
            holder.switch_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.switch_setting.isChecked()){
                        settingType.set(holder.getLayoutPosition(),CHOOSE);
                    }else {
                        settingType.set(holder.getLayoutPosition(),NOCHOOSE);
                    }
                    saveSetting(settingNameList.get(holder.getLayoutPosition()),settingType.get(holder.getLayoutPosition()));
                    onItemClickListener.OnClick(view,holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return settingNameList.size();
    }

    /**
     * 保存选项
     * @param name
     * @param type
     */
    public void saveSetting(String name,int type){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(name,type);
        editor.apply();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout ll_item;
        TextView tv_setting;
        Switch switch_setting;

        public MyViewHolder(View itemView) {
            super(itemView);
            ll_item = (LinearLayout)itemView.findViewById(R.id.ll_item);
            tv_setting = (TextView)itemView.findViewById(R.id.tv_setting);
            switch_setting = (Switch)itemView.findViewById(R.id.switch_setting);
        }
    }
}
