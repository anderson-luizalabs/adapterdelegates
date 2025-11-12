/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4

import android.view.ViewGroup
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * @author Hannes Dorfmann
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AsyncListDifferDelegationAdapterTest {

    @Before
    fun setup() {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean {
                return true
            }
        })
    }

    private val callback = object : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Any, newItem: Any) = oldItem == newItem
        override fun getChangePayload(oldItem: Any, newItem: Any) = "payload"
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
            .addDelegate(0, delegate1)
            .addDelegate(1, delegate2)

        val adapter = AsyncListDifferDelegationAdapter(callback, manager)
        adapter.submitList(listOf("a", "b"))

        val parent: ViewGroup = mockk(relaxed = true)

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
        adapter.onBindViewHolder(delegate1.viewHolder, 0, mutableListOf("payload"))
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
    fun `submitList triggers correct notifications`() {
        val adapter = AsyncListDifferDelegationAdapter(callback)
        val mockObserver = MockAdapterDataObserver()
        adapter.registerAdapterDataObserver(mockObserver)

        adapter.submitList(listOf("a", "b"))
        assertThat(mockObserver.onItemRangeInsertedCalled).isTrue()
        assertThat(mockObserver.insertPosition).isEqualTo(0)
        assertThat(mockObserver.insertItemCount).isEqualTo(2)
    }

    @Test
    fun `payloads are passed to onBindViewHolder`() {
        val delegate = SpyableAdapterDelegate<List<Any>>(0)
        val adapter = AsyncListDifferDelegationAdapter(callback, delegate)
        adapter.submitList(listOf("a"))

        val parent: ViewGroup = mockk(relaxed = true)
        val holder = adapter.onCreateViewHolder(parent, 0)
        adapter.onBindViewHolder(holder, 0, mutableListOf("payload"))

        assertTrue(delegate.onBindViewHolderCalled)
    }

    private class MockAdapterDataObserver : androidx.recyclerview.widget.RecyclerView.AdapterDataObserver() {
        var onItemRangeInsertedCalled = false
        var insertPosition = -1
        var insertItemCount = -1

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            onItemRangeInsertedCalled = true
            insertPosition = positionStart
            insertItemCount = itemCount
        }
    }
}
