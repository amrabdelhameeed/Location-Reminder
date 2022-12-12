package com.udacity.project4.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T>(private val callback: ((item: T) -> Unit)? = null) :
    RecyclerView.Adapter<DataBindingViewHolder<T>>() {

    private var _items: MutableList<T> = mutableListOf()
    private val items: List<T>?
        get() = this._items
    override fun getItemCount() = _items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<T> {
        val lay_inf = LayoutInflater.from(parent.context)
        val bin = DataBindingUtil
            .inflate<ViewDataBinding>(lay_inf, getLayoutRes(viewType), parent, false)
        bin.lifecycleOwner = getLifecycleOwner()

        return DataBindingViewHolder(bin)
    }
    override fun onBindViewHolder(holder: DataBindingViewHolder<T>, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            callback?.invoke(item)
        }
    }

    fun addData(items: List<T>) {
        _items.addAll(items)
        notifyDataSetChanged()
    }
    open fun getLifecycleOwner(): LifecycleOwner? {
        return null
    }
    fun getItem(position: Int) = _items[position]
    fun clear() {
        _items.clear()
        notifyDataSetChanged()
    }
    @LayoutRes
    abstract fun getLayoutRes(viewType: Int): Int

}

