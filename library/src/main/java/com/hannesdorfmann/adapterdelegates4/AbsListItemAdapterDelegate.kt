/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * A simplified [AdapterDelegate] when the underlying adapter's dataset is a [List].
 * This class helps to reduce writing boilerplate code like casting list item and casting ViewHolder.
 *
 * @param I The type of the item that is managed by this AdapterDelegate. Must be a subtype of T
 * @param T The generic type of the list
 * @param VH The type of the ViewHolder
 * @author Hannes Dorfmann
 * @since 1.2
 */
abstract class AbsListItemAdapterDelegate<I : T, T, VH : RecyclerView.ViewHolder> :
    AdapterDelegate<List<T>>() {
    final override fun isForViewType(items: List<T>, position: Int): Boolean {
        return isForViewType(items[position], items, position)
    }

    final override fun onBindViewHolder(
        items: List<T>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: List<Any>,
    ) {
        @Suppress("UNCHECKED_CAST")
        onBindViewHolder(items[position] as I, holder as VH, payloads)
    }

    /**
     * Called to determine whether this AdapterDelegate is responsible for the given item.
     *
     * @param item The item from the list at the given position
     * @param items The items from adapter's dataset
     * @param position The item's position in the dataset
     * @return true if this AdapterDelegate is responsible, otherwise false
     */
    protected abstract fun isForViewType(item: T, items: List<T>, position: Int): Boolean

    /**
     * Creates the [RecyclerView.ViewHolder] for the given data source item.
     *
     * @param parent The ViewGroup parent
     * @return ViewHolder
     */
    abstract override fun onCreateViewHolder(parent: ViewGroup): VH

    /**
     * Called to bind the [RecyclerView.ViewHolder] to the item of the dataset.
     *
     * @param item The data item
     * @param holder The ViewHolder
     * @param payloads The payloads
     */
    protected abstract fun onBindViewHolder(item: I, holder: VH, payloads: List<Any>)
}
