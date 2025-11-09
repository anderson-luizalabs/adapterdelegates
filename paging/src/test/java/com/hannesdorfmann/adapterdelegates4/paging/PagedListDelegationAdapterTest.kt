/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4.paging

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test

class PagedListDelegationAdapterTest {

    private val callback = object : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any) = false
        override fun areContentsTheSame(oldItem: Any, newItem: Any) = false
    }

    @Test
    fun adapterDelegateManagerIsNull() {
        try {
            PagedListDelegationAdapter(
                null as AdapterDelegatesManager<List<Any>>?,
                callback
            )
            fail("Expected NullPointerException")
        } catch (e: NullPointerException) {
            assertEquals("AdapterDelegatesManager is null", e.message)
        }
    }

    @Test
    @Ignore("Needs Android runtime - AsyncDifferConfig initialization fails in unit tests")
    fun checkDelegatesManagerInstance() {
        val manager = AdapterDelegatesManager<List<Any>>()
        val config: AsyncDifferConfig<Any> = mockk(relaxed = true)

        val adapter = object : PagedListDelegationAdapter<Any>(manager, config) {
            override fun getItemCount(): Int {
                // Hacky but does the job
                assertTrue(manager === this.delegatesManager)
                return 0
            }
        }

        adapter.itemCount
    }

    @Test
    @Ignore("Needs Android runtime - AsyncDifferConfig initialization fails in unit tests")
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
        adapter.onBindViewHolder(delegate1.viewHolder, 1, mutableListOf())
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
