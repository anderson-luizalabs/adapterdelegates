/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4.paging

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate

/**
 * @author Hannes Dorfmann
 */
class SpyableAdapterDelegate<T>(
    @JvmField
    val viewType: Int,
) : AdapterDelegate<T>() {

    @JvmField
    var isForViewTypeReturnedYes = false

    @JvmField
    var onCreateViewHolderCalled = false

    @JvmField
    var onBindViewHolderCalled = false

    @JvmField
    var onViewDetachedFromWindowCalled = false

    @JvmField
    var onViewAtachedToWindowCalled = false

    @JvmField
    var onViewRecycledCalled = false

    @JvmField
    var onFailedToRecycleViewCalled = false

    @JvmField
    var onBindViewHolderPosition = -1

    @JvmField
    val viewHolder: RecyclerView.ViewHolder

    init {
        viewHolder = object : RecyclerView.ViewHolder(View(null)) {}

        try {
            val viewTypeField = RecyclerView.ViewHolder::class.java
                .getDeclaredField("mItemViewType")

            viewTypeField.isAccessible = true
            viewTypeField.set(viewHolder, viewType)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun reset() {
        isForViewTypeReturnedYes = false
        onCreateViewHolderCalled = false
        onBindViewHolderCalled = false
        onViewDetachedFromWindowCalled = false
        onViewAtachedToWindowCalled = false
        onViewRecycledCalled = false
        onFailedToRecycleViewCalled = false
        onBindViewHolderPosition = -1
    }

    override fun isForViewType(items: T, position: Int): Boolean {
        val isForThat = position == viewType
        if (isForThat) {
            isForViewTypeReturnedYes = true
        }
        return isForThat
    }

    override fun onCreateViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
        onCreateViewHolderCalled = true
        return viewHolder
    }

    override fun onBindViewHolder(
        items: T,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: List<Any>,
    ) {
        onBindViewHolderCalled = true
        onBindViewHolderPosition = position
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        onViewDetachedFromWindowCalled = true
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        onViewAtachedToWindowCalled = true
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        onViewRecycledCalled = true
    }

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        onFailedToRecycleViewCalled = true
        return super.onFailedToRecycleView(holder)
    }
}
