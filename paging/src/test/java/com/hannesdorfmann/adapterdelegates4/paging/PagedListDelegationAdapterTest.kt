/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4.paging

import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import com.google.common.truth.Truth.assertThat
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.Executor

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PagedListDelegationAdapterTest {

    private val diffCallback = object : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any) = false
        override fun areContentsTheSame(oldItem: Any, newItem: Any) = false
    }

    @Test
    fun `adapterDelegateManagerIsNull throws exception`() {
        try {
            PagedListDelegationAdapter(
                null as AdapterDelegatesManager<List<Any>>?,
                diffCallback
            )
        } catch (e: NullPointerException) {
            assertThat(e).hasMessageThat().isEqualTo("AdapterDelegatesManager is null")
        }
    }

    @Test
    fun `checkDelegatesManagerInstance should be the same of constructor`() {
        val manager = AdapterDelegatesManager<List<Any>>()
        val config: AsyncDifferConfig<Any> = mockk(relaxed = true)

        val adapter = PagedListDelegationAdapter<Any>(manager, config)

        assertThat(manager).isEqualTo(adapter.delegatesManager)
    }

    @Test
    fun `callAllMethods should be called in every delegate`() {
        val delegate1 = SpyableAdapterDelegate<List<Any>>(0)
        val delegate2 = SpyableAdapterDelegate<List<Any>>(1)

        val manager = AdapterDelegatesManager<List<Any>>().apply {
            addDelegate(delegate1)
            addDelegate(delegate2)
        }

        val config: AsyncDifferConfig<Any> = mockk(relaxed = true)
        val adapter = PagedListDelegationAdapter(manager, config)
        adapter.submitList(createPagedList(listOf("A", "B")))

        val parent: ViewGroup = mockk(relaxed = true)

        // CreateViewHolder
        adapter.onCreateViewHolder(parent, 0)
        assertThat(delegate1.onCreateViewHolderCalled).isTrue()
        assertThat(delegate2.onCreateViewHolderCalled).isFalse()

        // BindViewHolder
        adapter.onBindViewHolder(delegate1.viewHolder, 0)
        assertThat(delegate1.onBindViewHolderCalled).isTrue()
        assertThat(delegate2.onBindViewHolderCalled).isFalse()

        // bind with payload
        delegate1.onBindViewHolderCalled = false // reset
        adapter.onBindViewHolder(delegate1.viewHolder, 0, mutableListOf())
        assertThat(delegate1.onBindViewHolderCalled).isTrue()
        assertThat(delegate2.onBindViewHolderCalled).isFalse()

        // On view AttachedToWindow
        adapter.onViewAttachedToWindow(delegate1.viewHolder)
        assertThat(delegate1.onViewAtachedToWindowCalled).isTrue()
        assertThat(delegate2.onViewAtachedToWindowCalled).isFalse()

        // On view Detached from window
        adapter.onViewDetachedFromWindow(delegate1.viewHolder)
        assertThat(delegate1.onViewDetachedFromWindowCalled).isTrue()
        assertThat(delegate2.onViewDetachedFromWindowCalled).isFalse()

        // failed to recycle view holder
        assertThat(adapter.onFailedToRecycleView(delegate1.viewHolder)).isFalse()
        assertThat(delegate1.onFailedToRecycleViewCalled).isTrue()
        assertThat(delegate2.onFailedToRecycleViewCalled).isFalse()

        // view recycle view holder
        adapter.onViewRecycled(delegate1.viewHolder)
        assertThat(delegate1.onViewRecycledCalled).isTrue()
        assertThat(delegate2.onViewRecycledCalled).isFalse()
    }

    @Test
    fun `constructor with vararg delegates should add all delegates to manager`() {
        // Given
        val delegate1 = SpyableAdapterDelegate<List<Any>>(0)
        val delegate2 = SpyableAdapterDelegate<List<Any>>(1)

        // When
        val adapter = PagedListDelegationAdapter(diffCallback, delegate1, delegate2)

        // Then
        assertThat(adapter.delegatesManager.delegatesCount).isEqualTo(2)
    }

    @Test
    fun `constructor with diffCallback should create an empty delegates manager`() {
        // When
        val adapter = PagedListDelegationAdapter<Any>(diffCallback)

        // Then
        assertThat(adapter.delegatesManager.delegatesCount).isEqualTo(0)
    }

    @Test
    fun `constructor with delegatesManager and diffCallback should use the provided manager`() {
        // Given
        val delegatesManager = AdapterDelegatesManager<List<Any>>()
        val delegate1 = SpyableAdapterDelegate<List<Any>>(0)
        delegatesManager.addDelegate(delegate1)

        // When
        val adapter = PagedListDelegationAdapter(delegatesManager, diffCallback)

        // Then
        assertThat(adapter.delegatesManager).isEqualTo(delegatesManager)
        assertThat(adapter.delegatesManager.delegatesCount).isEqualTo(1)
    }

    @Test
    fun `constructor with config should create an empty delegates manager`() {
        // Given
        val config: AsyncDifferConfig<Any> = mockk(relaxed = true)

        // When
        val adapter = PagedListDelegationAdapter<Any>(config)

        // Then
        assertThat(adapter.delegatesManager.delegatesCount).isEqualTo(0)
    }

    @Test
    fun `constructor with delegatesManager and config should use the provided manager`() {
        // Given
        val delegatesManager = AdapterDelegatesManager<List<Any>>()
        val delegate1 = SpyableAdapterDelegate<List<Any>>(0)
        delegatesManager.addDelegate(delegate1)
        val config: AsyncDifferConfig<Any> = mockk(relaxed = true)

        // When
        val adapter = PagedListDelegationAdapter(delegatesManager, config)

        // Then
        assertThat(adapter.delegatesManager).isEqualTo(delegatesManager)
        assertThat(adapter.delegatesManager.delegatesCount).isEqualTo(1)
    }

    @Test
    fun `submitList should update the adapter's data`() {
        // Given
        val items = listOf("A", "B", "C")
        val pagedList = createPagedList(items)
        val delegate = SpyableAdapterDelegate<List<Any>>(0)
        val adapter = PagedListDelegationAdapter(diffCallback, delegate)

        // When
        adapter.submitList(pagedList)

        // Then
        assertThat(adapter.itemCount).isEqualTo(3)
    }

    @Test
    fun `getItem should retrieve items from the PagedList`() {
        // Given
        val items = listOf("A", "B", "C")
        val pagedList = createPagedList(items)
        val delegate = SpyableAdapterDelegate<List<Any>>(0)
        val adapter = PagedListDelegationAdapter(diffCallback, delegate)
        adapter.submitList(pagedList)

        // When
        val item = adapter.getItem(1)

        // Then
        assertThat(item).isEqualTo("B")
    }

    @Test
    fun `getItemViewType should return correct view type`() {
        // Given
        val delegate1 = SpyableAdapterDelegate<List<Any>>(0)
        val delegate2 = SpyableAdapterDelegate<List<Any>>(1)
        val adapter = PagedListDelegationAdapter(diffCallback, delegate1, delegate2)
        adapter.submitList(createPagedList(listOf("A", "B")))

        // When
        val viewType1 = adapter.getItemViewType(0)
        val viewType2 = adapter.getItemViewType(1)

        // Then
        assertThat(viewType1).isEqualTo(0)
        assertThat(viewType2).isEqualTo(1)
    }

    @Test
    fun `adapter should handle empty list`() {
        // Given
        val items = emptyList<String>()
        val pagedList = createPagedList(items)
        val delegate = SpyableAdapterDelegate<List<Any>>(0)
        val adapter = PagedListDelegationAdapter(diffCallback, delegate)

        // When
        adapter.submitList(pagedList)

        // Then
        assertThat(adapter.itemCount).isEqualTo(0)
    }


    @Test
    fun `onBindViewHolder with payload should be called`() {
        // Given
        val delegate = SpyableAdapterDelegate<List<Any>>(0)
        val adapter = PagedListDelegationAdapter(diffCallback, delegate)
        adapter.submitList(createPagedList(listOf("A", "B")))
        val holder = adapter.onCreateViewHolder(mockk(relaxed = true), 0)
        val payload = "payload"

        // When
        adapter.onBindViewHolder(holder, 0, mutableListOf(payload))

        // Then
        assertThat(delegate.onBindViewHolderCalled).isTrue()
    }

    private fun createPagedList(items: List<String>): PagedList<Any> {
        val config = PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(false)
            .build()

        return PagedList.Builder(TestPositionalDataSource(items), config)
            .setNotifyExecutor(Executor { it.run() })
            .setFetchExecutor(Executor { it.run() })
            .build() as PagedList<Any>
    }

    private class TestPositionalDataSource(
        private val items: List<String>
    ) : androidx.paging.PositionalDataSource<String>() {

        override fun loadInitial(
            params: LoadInitialParams,
            callback: LoadInitialCallback<String>
        ) {
            callback.onResult(items, 0, items.size)
        }

        override fun loadRange(
            params: LoadRangeParams,
            callback: LoadRangeCallback<String>
        ) {
            val end = minOf(params.startPosition + params.loadSize, items.size)
            callback.onResult(items.subList(params.startPosition, end))
        }
    }
}
