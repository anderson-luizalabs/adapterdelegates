/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test

/**
 * @author Hannes Dorfmann
 */
class AsyncListDifferDelegationAdapterTest {

    private val callback = object : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any) = false
        override fun areContentsTheSame(oldItem: Any, newItem: Any) = false
    }

    @Test
    fun itemCallbackIsNull() {
        try {
            object : AsyncListDifferDelegationAdapter<Any>(null as DiffUtil.ItemCallback<Any>?) {
                override fun getItemCount() = 0
            }
            fail("Expected NullPointerException")
        } catch (e: NullPointerException) {
            assertTrue(e.message?.contains("Parameter specified as non-null is null") == true)
        }
    }

    @Test
    fun adapterDelegateManagerIsNull() {
        try {
            object : AsyncListDifferDelegationAdapter<Any>(
                callback,
                null as AdapterDelegatesManager<List<Any>>?
            ) {
                override fun getItemCount() = 0
            }
            fail("Expected NullPointerException")
        } catch (e: NullPointerException) {
            assertEquals("AdapterDelegatesManager is null", e.message)
        }
    }

    @Test
    fun checkDelegatesManagerInstance() {
        val manager = AdapterDelegatesManager<List<Any>>()
        val config: AsyncDifferConfig<Any> = mockk(relaxed = true)
        
        val adapter = object : AsyncListDifferDelegationAdapter<Any>(config, manager) {
            override fun getItemCount(): Int {
                // Hacky but does the job
                assertTrue(manager === this.delegatesManager)
                return 0
            }
        }

        adapter.itemCount
    }

    @Test
    fun callAllMethods() {
        val delegate1 = SpyableAdapterDelegate<List<Any>>(0)
        val delegate2 = SpyableAdapterDelegate<List<Any>>(1)

        val manager = AdapterDelegatesManager<List<Any>>()
            .addDelegate(delegate1)
            .addDelegate(delegate2)

        val config: AsyncDifferConfig<Any> = mockk(relaxed = true)
        val adapter = AsyncListDifferDelegationAdapter(config, manager)

        val parent: ViewGroup = mockk(relaxed = true)

        // CreateViewHolder
        adapter.onCreateViewHolder(parent, 0)
        assertTrue(delegate1.onCreateViewHolderCalled)
        assertFalse(delegate2.onCreateViewHolderCalled)

        // BindViewHolder
        adapter.onBindViewHolder(delegate1.viewHolder, 1)
        assertTrue(delegate1.onBindViewHolderCalled)
        assertFalse(delegate2.onBindViewHolderCalled)

        // bind with payload
        delegate1.onBindViewHolderCalled = false // reset
        adapter.onBindViewHolder(delegate1.viewHolder, 1, emptyList())
        assertTrue(delegate1.onBindViewHolderCalled)
        assertFalse(delegate2.onBindViewHolderCalled)

        // On view AttachedToWindow
        adapter.onViewAttachedToWindow(delegate1.viewHolder)
        assertTrue(delegate1.onViewAtachedToWindowCalled)
        assertFalse(delegate2.onViewAtachedToWindowCalled)

        // On view Detached from window
        adapter.onViewDetachedFromWindow(delegate1.viewHolder)
        assertTrue(delegate1.onViewDetachedFromWindowCalled)
        assertFalse(delegate2.onViewDetachedFromWindowCalled)

        // failed to recycle view holder
        assertFalse(adapter.onFailedToRecycleView(delegate1.viewHolder))
        assertTrue(delegate1.onFailedToRecycleViewCalled)
        assertFalse(delegate2.onFailedToRecycleViewCalled)

        // view recycle view holder
        adapter.onViewRecycled(delegate1.viewHolder)
        assertTrue(delegate1.onViewRecycledCalled)
        assertFalse(delegate2.onViewRecycledCalled)
    }
}
