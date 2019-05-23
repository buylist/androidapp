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

import ru.buylist.KeyboardUtils;
import ru.buylist.R;
import ru.buylist.models.BuyList;
import ru.buylist.models.ProductLab;
import ru.buylist.swipe_helper.ITouchHelperAdapter;
import ru.buylist.swipe_helper.ITouchHelperHolder;
import ru.buylist.swipe_helper.TouchHelperCallback;

import static ru.buylist.data.BuyListDbSchema.*;

public class BuyListFragment extends Fragment {

    private RecyclerView list_collection_recycler;

    private Button collection;
    private Button templates;
    private Button recipe;

    private ImageButton newCollection;
    private ImageButton newTemplates;
    private ImageButton newRecipe;

    private LinearLayout createCollectionLayout;
    private EditText nameCollection;
    private ImageButton createCollection;

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
        initUi(view);
        return view;
    }

    private void initUi(View view) {
        collection = view.findViewById(R.id.list_collection);
        templates = view.findViewById(R.id.list_templates);
        recipe = view.findViewById(R.id.recipe_list);

        newCollection = view.findViewById(R.id.new_list_collection);
        newTemplates = view.findViewById(R.id.new_list_templates);
        newRecipe = view.findViewById(R.id.new_recipe_list);

        createCollectionLayout = view.findViewById(R.id.new_list_collection_layout);
        nameCollection = view.findViewById(R.id.name_new_list_collection);
        createCollection = view.findViewById(R.id.create_new_list_collection);

        onNewCollectionButtonClick();
        onCreateCollectionButtonClick();

        list_collection_recycler = view.findViewById(R.id.list_collection_recycler_view);
        list_collection_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        ItemTouchHelper.Callback callback = new TouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(list_collection_recycler);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_buy_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateUI() {
        ProductLab productLab = ProductLab.get(getActivity());
        List<BuyList> lists = productLab.getBuyLists();

        if (adapter == null) {
            adapter = new BuyListAdapter(lists);
            list_collection_recycler.setAdapter(adapter);
        } else {
            adapter.setLists(lists);
            adapter.notifyDataSetChanged();
        }
    }

    private void onNewCollectionButtonClick() {
        newCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCollectionLayout.setVisibility(View.VISIBLE);
                KeyboardUtils.showKeyboard(nameCollection, getActivity());
            }
        });
    }

    private void onCreateCollectionButtonClick() {
        createCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuyList buyList = new BuyList();
                buyList.setTitle(nameCollection.getText().toString());
                ProductLab.get(getActivity()).addBuyList(buyList);
                updateUI();
                nameCollection.setText("");
                createCollectionLayout.setVisibility(View.GONE);
                KeyboardUtils.hideKeyboard(nameCollection, getActivity());
            }
        });
    }


    private void updateBuyTable(final List<BuyList> lists) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ProductLab productLab = ProductLab.get(getActivity());
                productLab.updateBuyTable(lists);
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

            updateBuyTable(lists);
        }

        @Override
        public void onItemDismiss(int position) {
            Log.d("TAG", "onItemDismiss: ");
            ProductLab productLab = ProductLab.get(getActivity());
            productLab.deleteFromDb(lists.get(position).getId().toString(), BuyTable.NAME, BuyTable.Cols.UUID);
            notifyItemRemoved(position);
            updateUI();
        }
    }
}
