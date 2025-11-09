/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.robolectric.RuntimeEnvironment

/**
 * Test spy for AdapterDelegate
 * @author Hannes Dorfmann
 */
class SpyableAdapterDelegate<T>(
    val viewType: Int
) : AdapterDelegate<T>() {

    var isForViewTypeReturnedYes = false
    var onCreateViewHolderCalled = false
    var onBindViewHolderCalled = false
    var onViewDetachedFromWindowCalled = false
    var onViewAttachedToWindowCalled = false
    var onViewRecycledCalled = false
    var onFailedToRecycleViewCalled = false
    var onBindViewHolderPosition = -1

    val viewHolder: RecyclerView.ViewHolder = object : RecyclerView.ViewHolder(
        View(RuntimeEnvironment.getApplication())
    ) {}.apply {
        // Set viewType using reflection
        try {
            val viewTypeField = RecyclerView.ViewHolder::class.java
                .getDeclaredField("mItemViewType")
            viewTypeField.isAccessible = true
            viewTypeField.set(this, viewType)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun reset() {
        isForViewTypeReturnedYes = false
        onCreateViewHolderCalled = false
        onBindViewHolderCalled = false
        onViewDetachedFromWindowCalled = false
        onViewAttachedToWindowCalled = false
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

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        onCreateViewHolderCalled = true
        return viewHolder
    }

    override fun onBindViewHolder(
        items: T,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: List<Any>?
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
        onViewAttachedToWindowCalled = true
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
