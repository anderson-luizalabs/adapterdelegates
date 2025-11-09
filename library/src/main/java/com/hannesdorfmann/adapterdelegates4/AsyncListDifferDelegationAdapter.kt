package com.hannesdorfmann.adapterdelegates4

import android.view.ViewGroup
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * An implementation of an Adapter that uses [AdapterDelegatesManager] and [AsyncListDiffer]
 * for calculating diffs between old and new collections on background thread.
 *
 * @param T The type of the datasource items
 * @author Sergey Opivalov
 * @author Hannes Dorfmann
 */
open class AsyncListDifferDelegationAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected val delegatesManager: AdapterDelegatesManager<List<T>>
    protected val differ: AsyncListDiffer<T>

    constructor(diffCallback: DiffUtil.ItemCallback<T>) : this(
        diffCallback,
        AdapterDelegatesManager()
    )

    constructor(
        diffCallback: DiffUtil.ItemCallback<T>,
        delegatesManager: AdapterDelegatesManager<List<T>>
    ) {
        this.differ = AsyncListDiffer(this, diffCallback)
        this.delegatesManager = delegatesManager
    }

    constructor(
        differConfig: AsyncDifferConfig<T>,
        delegatesManager: AdapterDelegatesManager<List<T>>
    ) {
        this.differ = AsyncListDiffer(AdapterListUpdateCallback(this), differConfig)
        this.delegatesManager = delegatesManager
    }

    /**
     * Adds a list of [AdapterDelegate]s
     *
     * @since 4.2.0
     */
    constructor(
        diffCallback: DiffUtil.ItemCallback<T>,
        vararg delegates: AdapterDelegate<List<T>>
    ) {
        this.differ = AsyncListDiffer(this, diffCallback)
        this.delegatesManager = AdapterDelegatesManager(*delegates)
    }

    constructor(
        differConfig: AsyncDifferConfig<T>,
        vararg delegates: AdapterDelegate<List<T>>
    ) {
        this.differ = AsyncListDiffer(AdapterListUpdateCallback(this), differConfig)
        this.delegatesManager = AdapterDelegatesManager(*delegates)
    }

    /**
     * Submit a new list to be diffed and displayed.
     */
    fun submitList(list: List<T>?) {
        differ.submitList(list)
    }

    /**
     * Get the current list being displayed.
     */
    fun getCurrentList(): List<T> = differ.currentList

    override fun getItemViewType(position: Int): Int {
        return delegatesManager.getItemViewType(differ.currentList, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegatesManager.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegatesManager.onBindViewHolder(differ.currentList, position, holder, null)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        delegatesManager.onBindViewHolder(differ.currentList, position, holder, payloads)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        delegatesManager.onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return delegatesManager.onFailedToRecycleView(holder)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        delegatesManager.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        delegatesManager.onViewDetachedFromWindow(holder)
    }
}
