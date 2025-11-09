/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * This class ties [RecyclerView.Adapter] together with [AdapterDelegate].
 *
 * @param T The type of the datasource of the adapter
 * @author Hannes Dorfmann
 */
open class AdapterDelegatesManager<T> {
    companion object {
        /**
         * ViewType for the fallback delegate
         */
        const val FALLBACK_DELEGATE_VIEW_TYPE = Int.MAX_VALUE - 1

        /**
         * Empty payload list used internally
         */
        private val PAYLOADS_EMPTY_LIST = emptyList<Any>()
    }

    /**
     * Map for ViewType to AdapterDelegate
     */
    private val delegates = SparseArrayCompat<AdapterDelegate<T>>()

    var fallbackDelegate: AdapterDelegate<T>? = null
        private set

    /**
     * Creates an AdapterDelegatesManager without any delegates.
     */
    constructor()

    /**
     * Creates an AdapterDelegatesManager which already has the given delegates added to it.
     */
    constructor(vararg delegates: AdapterDelegate<T>) {
        delegates.forEach { addDelegate(it) }
    }

    /**
     * Adds an [AdapterDelegate].
     * This method automatically assigns the view type integer by using the next unused value.
     */
    fun addDelegate(delegate: AdapterDelegate<T>): AdapterDelegatesManager<T> {
        return addDelegate(delegate, false)
    }

    /**
     * Adds an [AdapterDelegate] with option to allow replacing existing delegate.
     */
    fun addDelegate(
        delegate: AdapterDelegate<T>,
        allowReplacingDelegate: Boolean,
    ): AdapterDelegatesManager<T> {
        var viewType = delegates.size()
        while (delegates.get(viewType) != null) {
            viewType++
            if (viewType == FALLBACK_DELEGATE_VIEW_TYPE) {
                throw IllegalArgumentException(
                    "Oops, we are very close to Integer.MAX_VALUE. " +
                        "It seems that there are no more free and unused view type integers left " +
                        "to add another AdapterDelegate.",
                )
            }
        }
        return addDelegate(viewType, allowReplacingDelegate, delegate)
    }

    /**
     * Adds an [AdapterDelegate] with a specific view type.
     */
    fun addDelegate(
        viewType: Int,
        delegate: AdapterDelegate<T>,
    ): AdapterDelegatesManager<T> {
        return addDelegate(viewType, false, delegate)
    }

    /**
     * Adds an [AdapterDelegate] with a specific view type and option to replace existing delegate.
     */
    fun addDelegate(
        viewType: Int,
        allowReplacingDelegate: Boolean,
        delegate: AdapterDelegate<T>,
    ): AdapterDelegatesManager<T> {
        if (viewType == FALLBACK_DELEGATE_VIEW_TYPE) {
            throw IllegalArgumentException(
                "ViewType $viewType is reserved for fallback adapter delegate " +
                    "(see setFallbackDelegate() ). Please use another view type.",
            )
        }

        if (!allowReplacingDelegate && delegates.get(viewType) != null) {
            throw IllegalArgumentException(
                "An AdapterDelegate is already registered for the viewType = $viewType. " +
                    "Already registered AdapterDelegate is ${delegates.get(viewType)}",
            )
        }

        delegates.put(viewType, delegate)
        return this
    }

    /**
     * Remove a previously registered delegate.
     */
    fun removeDelegate(delegate: AdapterDelegate<T>): AdapterDelegatesManager<T> {
        val indexToRemove = delegates.indexOfValue(delegate)
        if (indexToRemove >= 0) {
            delegates.removeAt(indexToRemove)
        }
        return this
    }

    /**
     * Remove a previously registered delegate by view type.
     */
    fun removeDelegate(viewType: Int): AdapterDelegatesManager<T> {
        delegates.remove(viewType)
        return this
    }

    /**
     * Get the view type for the given position.
     * Must be called from [RecyclerView.Adapter.getItemViewType]
     */
    fun getItemViewType(
        items: T?,
        position: Int,
    ): Int {
        if (items == null) {
            throw NullPointerException("Items is null")
        }

        val delegatesCount = delegates.size()
        for (i in 0 until delegatesCount) {
            val delegate = delegates.valueAt(i)
            if (delegate.isForViewType(items, position)) {
                return delegates.keyAt(i)
            }
        }

        fallbackDelegate?.let {
            return FALLBACK_DELEGATE_VIEW_TYPE
        }

        throw NullPointerException(
            "No AdapterDelegate added that matches position=$position in data source",
        )
    }

    /**
     * Create ViewHolder.
     * Must be called from [RecyclerView.Adapter.onCreateViewHolder]
     */
    fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val delegate =
            getDelegateForViewType(viewType)
                ?: throw NullPointerException("No AdapterDelegate added for ViewType $viewType")

        val vh =
            delegate.onCreateViewHolder(parent)
                ?: throw NullPointerException(
                    "ViewHolder returned from AdapterDelegate $delegate " +
                        "for ViewType $viewType is null!",
                )

        return vh
    }

    /**
     * Bind ViewHolder.
     * Must be called from [RecyclerView.Adapter.onBindViewHolder]
     */
    fun onBindViewHolder(
        items: T?,
        position: Int,
        viewHolder: RecyclerView.ViewHolder,
        payloads: List<Any>?,
    ) {
        if (items == null) {
            throw NullPointerException("Items is null")
        }

        val delegate =
            getDelegateForViewType(viewHolder.itemViewType)
                ?: throw NullPointerException(
                    "No delegate found for item at position = $position for viewType = ${viewHolder.itemViewType}",
                )

        delegate.onBindViewHolder(items, position, viewHolder, payloads ?: PAYLOADS_EMPTY_LIST)
    }

    /**
     * Called when ViewHolder is recycled.
     */
    fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        val delegate = getDelegateForViewType(holder.itemViewType)
        delegate?.onViewRecycled(holder)
    }

    /**
     * Called when ViewHolder failed to recycle.
     */
    fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        val delegate = getDelegateForViewType(holder.itemViewType)
        return delegate?.onFailedToRecycleView(holder) ?: false
    }

    /**
     * Called when ViewHolder is attached to window.
     */
    fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        val delegate = getDelegateForViewType(holder.itemViewType)
        delegate?.onViewAttachedToWindow(holder)
    }

    /**
     * Called when ViewHolder is detached from window.
     */
    fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        val delegate = getDelegateForViewType(holder.itemViewType)
        delegate?.onViewDetachedFromWindow(holder)
    }

    /**
     * Set a fallback delegate that will be used if no other delegate matches.
     */
    fun setFallbackDelegate(fallbackDelegate: AdapterDelegate<T>?): AdapterDelegatesManager<T> {
        this.fallbackDelegate = fallbackDelegate
        return this
    }

    /**
     * Get the view type for a given delegate.
     */
    fun getViewType(delegate: AdapterDelegate<T>): Int {
        val index = delegates.indexOfValue(delegate)
        return if (index == -1) {
            if (fallbackDelegate === delegate) {
                FALLBACK_DELEGATE_VIEW_TYPE
            } else {
                throw IllegalArgumentException(
                    "No view type found for delegate $delegate. " +
                        "Delegate is not registered",
                )
            }
        } else {
            delegates.keyAt(index)
        }
    }

    /**
     * Get the delegate for a given view type.
     */
    fun getDelegateForViewType(viewType: Int): AdapterDelegate<T>? {
        return if (viewType == FALLBACK_DELEGATE_VIEW_TYPE) {
            fallbackDelegate
        } else {
            delegates.get(viewType)
        }
    }
}
