/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4.paging

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager

/**
 * A [PagedListAdapter] that uses [AdapterDelegatesManager] and [AdapterDelegate].
 *
 * @param T The type of items in the PagedList
 */
open class PagedListDelegationAdapter<T : Any> : PagedListAdapter<T, RecyclerView.ViewHolder> {
    @JvmField
    protected val delegatesManager: AdapterDelegatesManager<List<T>>

    /**
     * @param diffCallback The callback
     * @param delegates The [AdapterDelegate]s that should be added
     * @since 4.1.0
     */
    constructor(
        diffCallback: DiffUtil.ItemCallback<T>,
        vararg delegates: AdapterDelegate<List<T>>,
    ) : this(AdapterDelegatesManager(), diffCallback) {
        delegates.forEach { delegatesManager.addDelegate(it) }
    }

    constructor(diffCallback: DiffUtil.ItemCallback<T>) : this(
        AdapterDelegatesManager(),
        diffCallback,
    )

    constructor(
        delegatesManager: AdapterDelegatesManager<List<T>>?,
        diffCallback: DiffUtil.ItemCallback<T>,
    ) : super(diffCallback) {
        if (delegatesManager == null) {
            throw NullPointerException("AdapterDelegatesManager is null")
        }
        this.delegatesManager = delegatesManager
    }

    constructor(config: AsyncDifferConfig<T>) : this(
        AdapterDelegatesManager(),
        config,
    )

    constructor(
        delegatesManager: AdapterDelegatesManager<List<T>>?,
        config: AsyncDifferConfig<T>,
    ) : super(config) {
        if (delegatesManager == null) {
            throw NullPointerException("AdapterDelegatesManager is null")
        }
        this.delegatesManager = delegatesManager
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegatesManager.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position) // Internally triggers loading items around the given position
        delegatesManager.onBindViewHolder(currentList, position, holder, null)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        getItem(position) // Internally triggers loading items around the given position
        delegatesManager.onBindViewHolder(currentList, position, holder, payloads)
    }

    override fun getItemViewType(position: Int): Int {
        return delegatesManager.getItemViewType(currentList, position)
    }

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
