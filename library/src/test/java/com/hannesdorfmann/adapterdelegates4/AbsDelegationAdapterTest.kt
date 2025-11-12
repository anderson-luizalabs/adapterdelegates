/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4

import android.view.ViewGroup
import android.widget.FrameLayout
import io.mockk.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * @author Hannes Dorfmann
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AbsDelegationAdapterTest {


    @Test
    fun checkDelegatesManagerInstance() {
        val manager = AdapterDelegatesManager<Any>()

        val adapter = object : AbsDelegationAdapter<Any>(manager) {
            override fun getItemCount(): Int {
                // Hacky but does the job
                assertTrue(manager === this.delegatesManager)
                return 0
            }
        }

        adapter.itemCount
    }

    @Test
    fun checkNewAdapterDelegatesManagerInstanceNotNull() {
        // Empty constructor should produce a new instance of AdapterDelegatesManager
        val adapter = object : AbsDelegationAdapter<Any>() {
            override fun getItemCount(): Int {
                // Hacky but does the job
                assertNotNull(this.delegatesManager)
                return 0
            }
        }

        adapter.itemCount
    }

    @Test
    fun callAllMethods() {
        val delegate1 = SpyableAdapterDelegate<Any>(0)
        val delegate2 = SpyableAdapterDelegate<Any>(1)
        val manager = AdapterDelegatesManager<Any>()
            .addDelegate(0, delegate1)
            .addDelegate(1, delegate2)

        val adapter = object : AbsDelegationAdapter<Any>(manager) {
            override fun getItemCount() = (items as List<*>?)?.size ?: 0
        }
        adapter.items = listOf("foo", "bar")

        val parent: ViewGroup = FrameLayout(RuntimeEnvironment.getApplication())

        // CreateViewHolder
        adapter.onCreateViewHolder(parent, 0)
        assertTrue(delegate1.onCreateViewHolderCalled)
        assertFalse(delegate2.onCreateViewHolderCalled)

        // BindViewHolder
        adapter.onBindViewHolder(delegate1.viewHolder, 0)
        assertTrue(delegate1.onBindViewHolderCalled)
        assertFalse(delegate2.onBindViewHolderCalled)

        // bind with payload
        delegate1.onBindViewHolderCalled = false // reset
        adapter.onBindViewHolder(delegate1.viewHolder, 0, mutableListOf())
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

    @Test
    fun `adapter works with empty list`() {
        val adapter = object : AbsDelegationAdapter<List<Any>>() {
            override fun getItemCount() = 0
        }
        adapter.items = emptyList()
        // Should not throw any exception
    }
}
