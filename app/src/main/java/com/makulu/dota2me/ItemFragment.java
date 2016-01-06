package com.makulu.dota2me;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.makulu.dota2api.Dota2;
import com.makulu.dota2api.UrlGenerator;
import com.makulu.dota2api.model.item.Item;
import com.trello.rxlifecycle.components.RxFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujintian on 2015/8/17.
 */
public class ItemFragment extends RxFragment {
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter = new HistoryAdapter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.items);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(historyAdapter);
        historyAdapter.refresh(Dota2.getItems());
    }

    class HistoryAdapter extends RecyclerView.Adapter<ItemHolder> {
        private List<Item> items = new ArrayList<>();

        public void refresh(List<Item> historyList) {
            this.items.clear();
            notifyDataSetChanged();
            this.items.addAll(historyList);
            notifyDataSetChanged();
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ItemHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_thumbnail, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(ItemHolder historyHolder, int i) {
            historyHolder.refreshContent(items.get(i));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }


    class ItemHolder extends RecyclerView.ViewHolder {
        TextView tv;
        SimpleDraweeView iv;

        public ItemHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.item_name);
            iv = (SimpleDraweeView) itemView.findViewById(R.id.item_thumbnail);
        }

        void refreshContent(Item item) {
            tv.setText(item.getLocalized_name() + "");
            iv.setImageURI(UrlGenerator.generateItemImage(item.getName()));
        }
    }
}
