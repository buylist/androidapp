package ru.buylist.lists;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.*;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.List;

import ru.buylist.KeyboardUtils;
import ru.buylist.R;
import ru.buylist.data.BuyList;
import ru.buylist.data.ProductLab;

import static ru.buylist.data.db.BuyListDbSchema.*;

public class BuyListFragment extends Fragment {

    private RecyclerView list_collection_recycler;

    private CardView collection;

    private CardView cardView_list;
    private CardView cardView_templates;
    private CardView cardView_recipe;

    private ImageButton newCollection;
    private ImageButton newTemplates;
    private ImageButton newRecipe;

    private LinearLayout createCollectionLayout;
    private EditText nameCollection;
    private ImageButton createCollection;

    private BuyListAdapter adapter;
    private Callbacks callbacks;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.action_home:
                    callbacks.showHome();
                    return true;
                case R.id.action_lists:

                    return true;
                case R.id.action_templates:

                    return true;
                case R.id.action_recipe:

                    return true;
                case R.id.action_settings:

                    return true;
            }
            return false;
        }
    };

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
        collection = view.findViewById(R.id.card_list_view);

        newCollection = view.findViewById(R.id.new_list);
        newTemplates = view.findViewById(R.id.new_pattern);
        newRecipe = view.findViewById(R.id.new_recipe);
        createCollectionLayout = view.findViewById(R.id.new_list_collection_layout);
        nameCollection = view.findViewById(R.id.name_new_list_collection);
        createCollection = view.findViewById(R.id.create_new_list_collection);
        list_collection_recycler = view.findViewById(R.id.list_collection_recycler_view);
        list_collection_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        collection.setBackgroundColor(0);
        cardView_templates = view.findViewById(R.id.card_pattern_view);
        cardView_templates.setBackgroundColor(0);
        cardView_recipe = view.findViewById(R.id.card_recipe_view);
        cardView_recipe.setBackgroundColor(0);

        BottomNavigationView navigation = view.findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        updateUI();
        onNewCollectionButtonClick();
        onCreateCollectionButtonClick();
        onCardListViewClick();
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
                list_collection_recycler.setVisibility(View.VISIBLE);
                adapter.closeAllItems();
                KeyboardUtils.showKeyboard(nameCollection, getActivity());
            }
        });
    }

    private void onCardListViewClick() {
        collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.closeAllItems();
                nameCollection.setText("");
                createCollectionLayout.setVisibility(View.GONE);
                KeyboardUtils.hideKeyboard(nameCollection, getActivity());
                if (list_collection_recycler.getVisibility() == View.GONE) {
                    list_collection_recycler.setVisibility(View.VISIBLE);
                } else {
                    list_collection_recycler.setVisibility(View.GONE);
                }

            }
        });
    }

    private void onCreateCollectionButtonClick() {
        createCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameCollection.getText().length() != 0) {
                    BuyList buyList = new BuyList();
                    buyList.setTitle(nameCollection.getText().toString());
                    ProductLab.get(getActivity()).addBuyList(buyList);
                }
                updateUI();
                nameCollection.setText("");
                createCollectionLayout.setVisibility(View.GONE);
                KeyboardUtils.hideKeyboard(nameCollection, getActivity());
            }
        });
    }

    public interface Callbacks {
        void onBuyListSelected(BuyList buyList);

        void showHome();
    }

    private class BuyListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView titleTextView;
        private BuyList buyList;
        private FrameLayout Surface_list;
        private FrameLayout Button_list;
        private ImageButton edit_list;
        private ImageButton delete_list;
        private SwipeLayout swipeLayout;
        private CardView cardView;

        BuyListHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list, parent, false));
            titleTextView = itemView.findViewById(R.id.buyListName);
            Surface_list = itemView.findViewById(R.id.surface_list);
            delete_list = itemView.findViewById(R.id.delete_list);
            edit_list = itemView.findViewById(R.id.edit_list);
            swipeLayout = itemView.findViewById(R.id.swipe_layout_list);
            Surface_list.setOnClickListener(this);
            onDeleteButtonClick();
            onEditButtonClick();

            Button_list = itemView.findViewById(R.id.bottom_list);
            cardView = itemView.findViewById(R.id.card_lists);
        }

        void bind(BuyList buyList) {
            this.buyList = buyList;
            titleTextView.setText(buyList.getTitle());
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.bottom_list));
            cardView.setBackgroundColor(0);
        }

        private void onDeleteButtonClick() {
            delete_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardView.setBackgroundColor(0);
                    ProductLab productLab = ProductLab.get(getActivity());
                    productLab.deleteFromDb(buyList.getId().toString(),
                            BuyTable.NAME, BuyTable.Cols.UUID);
                    productLab.deleteFromDb(buyList.getId().toString(), ProductTable.NAME, ProductTable.Cols.BUYLIST_ID);
                    adapter.closeAllItems();
                    updateUI();
                }
            });
        }

        private void onEditButtonClick() {
            edit_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createCollectionLayout.setVisibility(View.VISIBLE);
                    adapter.closeAllItems();
                    nameCollection.setText(buyList.getTitle());
                    KeyboardUtils.showKeyboard(nameCollection, getActivity());
                    ProductLab productLab = ProductLab.get(getActivity());
                    productLab.deleteFromDb(buyList.getId().toString(),
                            BuyTable.NAME,
                            BuyTable.Cols.UUID);
                }
            });
        }

        @Override
        public void onClick(View v) {
            callbacks.onBuyListSelected(buyList);
        }
    }

    private class BuyListAdapter extends RecyclerSwipeAdapter<BuyListHolder> {
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
        public void onBindViewHolder(@NonNull final BuyListHolder holder, int i) {
            BuyList buyList = lists.get(i);
            holder.bind(buyList);

            mItemManger.bindView(holder.itemView, i);
            holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {
                    mItemManger.closeAllExcept(layout);
                    holder.cardView.setBackgroundColor(Color.LTGRAY);
                }

                @Override
                public void onOpen(SwipeLayout layout) {
                }

                @Override
                public void onStartClose(SwipeLayout layout) {
                }

                @Override
                public void onClose(SwipeLayout layout) {
                    holder.cardView.setBackgroundColor(Color.WHITE);
                    holder.Button_list.setBackgroundColor(Color.WHITE);
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                }
            });
        }

        @Override
        public int getItemCount() {
            return lists.size();
        }


        void setLists(List<BuyList> lists) {
            this.lists = lists;
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe_layout_list;
        }
    }
}
