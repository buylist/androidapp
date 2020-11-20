package ru.buylist.presentation.product_dictionary

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.data.entity.GlobalItem

object ProductDictionaryBindings {

    @BindingAdapter("app:product_dictionary")
    @JvmStatic
    fun setProducts(recycler: RecyclerView, products: List<GlobalItem>?) {
        products?.let {
            (recycler.adapter as ProductDictionaryAdapter).submitList(products)
        }
    }

    @BindingAdapter("app:onScrolled")
    @JvmStatic
    fun setScrollListener(recycler: RecyclerView, viewModel: ProductDictionaryViewModel) {
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.showHideFab(dy)
            }
        })
    }
}