/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * An implementation of an Adapter that already uses [AdapterDelegatesManager] and calls
 * the corresponding methods from Adapter's methods.
 *
 * @param T The type of the datasource / items
 * @author Hannes Dorfmann
 */
abstract class AbsDelegationAdapter<T>(
    @JvmField
    protected val delegatesManager: AdapterDelegatesManager<T> = AdapterDelegatesManager(),
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: T? = null

    /**
     * Adds a list of [AdapterDelegate]s
     *
     * @param delegates Items to add
     * @since 4.1.0
     */
    constructor(vararg delegates: AdapterDelegate<T>) : this(AdapterDelegatesManager(*delegates))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegatesManager.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegatesManager.onBindViewHolder(items, position, holder, null)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        delegatesManager.onBindViewHolder(items, position, holder, payloads)
    }

    override fun getItemViewType(position: Int): Int {
        return delegatesManager.getItemViewType(items, position)
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
