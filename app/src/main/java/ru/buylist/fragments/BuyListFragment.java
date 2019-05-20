package ru.buylist.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.*;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.Collections;
import java.util.List;

import ru.buylist.R;
import ru.buylist.models.BuyList;
import ru.buylist.models.ProductLab;
import ru.buylist.swipe_helper.ITouchHelperAdapter;
import ru.buylist.swipe_helper.ITouchHelperHolder;
import ru.buylist.swipe_helper.TouchHelperCallback;

public class BuyListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private BuyListAdapter adapter;
    private Callbacks callbacks;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_list, container, false);
        emptyTextView = view.findViewById(R.id.list_is_empty);
        recyclerView = view.findViewById(R.id.buy_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        ItemTouchHelper.Callback callback = new TouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void checkList(List<BuyList> lists) {
        if (lists.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.GONE);
        }
    }

    public void updateUI() {
        ProductLab productLab = ProductLab.get(getActivity());
        List<BuyList> lists = productLab.getBuyLists();
        checkList(lists);

        if (adapter == null) {
            adapter = new BuyListAdapter(lists);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setLists(lists);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_buy_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_buylist:
                BuyList buyList = new BuyList();
                ProductLab.get(getActivity()).addBuyList(buyList);
                updateUI();
                callbacks.onProductSelected(buyList);
                return true;
            case R.id.settings:
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updateDatabase(final List<BuyList> lists) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ProductLab productLab = ProductLab.get(getActivity());
                productLab.updateDB(lists);
            }
        }).start();
    }

    public interface Callbacks {
        void onProductSelected(BuyList buyList);
    }

    private class BuyListHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ITouchHelperHolder {
        private TextView titleTextView;
        private BuyList buyList;

        BuyListHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_buy, parent, false));
            titleTextView = itemView.findViewById(R.id.list_title);
            itemView.setOnClickListener(this);
        }

        void bind(BuyList buyList) {
            this.buyList = buyList;
            titleTextView.setText(buyList.getTitle());
        }

        @Override
        public void onClick(View v) {
            callbacks.onProductSelected(buyList);
        }

        @Override
        public void onItemSelected() {
            Log.d("TAG", "onItemSelected: ");
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            Log.d("TAG", "onItemClear: ");
            itemView.setBackgroundColor(0);
        }
    }

    private class BuyListAdapter extends RecyclerView.Adapter<BuyListHolder> implements ITouchHelperAdapter {
        private List<BuyList> lists;

        BuyListAdapter(List<BuyList> lists) {
            this.lists = lists;
        }

        @NonNull
        @Override
        public BuyListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new BuyListHolder(inflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull BuyListHolder holder, int i) {
            BuyList buyList = lists.get(i);
            holder.bind(buyList);
        }

        @Override
        public int getItemCount() {
            return lists.size();
        }


        void setLists(List<BuyList> lists) {
            this.lists = lists;
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            Log.d("TAG", "onItemMove: ");

            Collections.swap(lists, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);

            updateDatabase(lists);
        }

        @Override
        public void onItemDismiss(int position) {
            Log.d("TAG", "onItemDismiss: ");
            ProductLab productLab = ProductLab.get(getActivity());
            productLab.deleteBuyList(lists.get(position).getId());
            notifyItemRemoved(position);
            updateUI();
        }
    }
}
