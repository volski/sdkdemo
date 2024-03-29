package com.zhj.bluetooth.sdkdemo.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.zhj.bluetooth.sdkdemo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 */

public abstract class BaseAdapter<T, MVH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    protected List<T> mList;
    protected Context mContext;
    protected LayoutInflater inflater;
    protected OnItemClickListener mOnItemClickListener;
    protected OnRetryClickListener mRetryClickListener;
    protected OnCustomClickListener customClickListener;
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_NO_DATA = 1; //No data
    private static final int TYPE_NET_ERROR = 2; //network error

    private static int currentType = 0; //Current category

    public BaseAdapter(Context mContext, List<T> mList) {
        this.mList = mList;
        if(mList != null && mList.size() > 0){
            showNormalData();
        }else{
            showNoData();
        }
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_NORMAL:
                viewHolder = onCreateViewHolder(parent);
                break;
            case TYPE_NO_DATA:
                viewHolder = new NoDataViewHolder(inflater.inflate(R.layout.refresh_load_no_date, parent, false));
                break;
            case TYPE_NET_ERROR:
                viewHolder = new NetErrorViewHolder(inflater.inflate(R.layout.refresh_load_net_error, parent, false));
                break;
            default:
                final RecyclerView.ViewHolder holder = onCreateViewHolder(parent, viewType);
                if (holder != null) {
                    holder.itemView.setTag(holder);
                    holder.itemView.setOnClickListener(view -> mOnItemClickListener.onItemClick(view,viewType));
                }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NoDataViewHolder) {

        } else if (holder instanceof NetErrorViewHolder) {
//            ((NetErrorViewHolder) holder).tvNetRetry.setOnClickListener(v -> {
//                if (mRetryClickListener != null) {
//                    mRetryClickListener.onRetryClick(v);
//                }
//            });
        } else {
            MVH mvh = (MVH) holder;
            onNormalBindViewHolder(mvh, mList.get(position), position);
        }
    }

    /**
     * Normal BindViewHolder
     *
     * @param holder
     * @param position
     */
    protected abstract void onNormalBindViewHolder(MVH holder, T itemBean, int position);

    /**
     * Return to ViewHolder under normal conditions
     *
     * @param parent
     * @return
     */
    protected abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent);

    @Override
    public int getItemCount() {
        if (currentType == 0) {
            return mList.size();
        } else {
            return 1;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnCustomClickListener {
        void onCustomClick(View view, int position);
    }

    public void setCustomClickListener(OnCustomClickListener customClickListener) {
        this.customClickListener = customClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnRetryClickListener {
        void onRetryClick(View view);
    }

    public void setRetryClickListener(OnRetryClickListener mRetryClickListener) {
        this.mRetryClickListener = mRetryClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return currentType;
    }

    //For pull-up loading
    public void addMoreItem(List<T> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    //For general update data
    public void setList(List<T> list) {
        if (list == null) {
            currentType = 1;
            mList = new ArrayList<>();
        } else {
            currentType = 0;
            mList = list;
        }
        notifyDataSetChanged();
    }

    //Get set
    public List<T> getList() {
        return mList;
    }

    //Add data
    public void addItem(int position, T data) {
        mList.add(position, data);
        notifyItemInserted(position);//Notification Demo Insert Animation
        notifyItemRangeChanged(position, mList.size() - position);//Notification data and interface rebinding
    }

    public T getItem(int position){
        return mList.get(position);
    }

    public void showNormalData() {
        currentType = 0;
        notifyDataSetChanged();
    }
    public void showNoData() {
        currentType = 1;
        notifyDataSetChanged();
    }

    public void showNetError() {
        currentType = 2;
        notifyDataSetChanged();
    }

    //ViewHolder with no data temporarily
    public static class NoDataViewHolder extends BaseViewHolder {
        @BindView(R.id.module_base_id_empty_img)
        ImageView moduleBaseIdEmptyImg;
        @BindView(R.id.module_base_empty_text)
        TextView moduleBaseEmptyText;
        @BindView(R.id.ll_load_no_date)
        LinearLayout llLoadNoDate;

        public NoDataViewHolder(View itemView) {
            super(itemView);
            llLoadNoDate.setVisibility(View.VISIBLE);
        }
    }

    //ViewHolder with failed network request
    public static class NetErrorViewHolder extends BaseViewHolder {
        @BindView(R.id.ll_net_error)
        LinearLayout llNetError;

        public NetErrorViewHolder(View itemView) {
            super(itemView);
            llNetError.setVisibility(View.VISIBLE);
        }
    }
}
