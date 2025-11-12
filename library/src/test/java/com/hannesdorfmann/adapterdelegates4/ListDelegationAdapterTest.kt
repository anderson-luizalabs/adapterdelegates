/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * @author Hannes Dorfmann
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ListDelegationAdapterTest {

    @Test
    fun `constructor with AdapterDelegatesManager works as expected`() {
        val delegate = SpyableAdapterDelegate<List<Any>>(0)
        val manager = AdapterDelegatesManager<List<Any>>().addDelegate(0, delegate)
        val adapter = ListDelegationAdapter(manager)
        adapter.items = listOf("a")
        assertThat(adapter.getItemViewType(0)).isEqualTo(0)
    }

    @Test
    fun `constructor with delegates vararg works as expected`() {
        val delegate1 = SpyableAdapterDelegate<List<Any>>(0)
        val delegate2 = SpyableAdapterDelegate<List<Any>>(1)
        val adapter = ListDelegationAdapter(delegate1, delegate2)
        adapter.items = listOf("a", "b")
        assertThat(adapter.getItemViewType(0)).isEqualTo(0)
        assertThat(adapter.getItemViewType(1)).isEqualTo(1)
    }

    @Test
    fun `heterogeneous items list works correctly`() {
        val catDelegate = CatAdapterDelegate()
        val dogDelegate = DogAdapterDelegate()
        val manager = AdapterDelegatesManager<List<Any>>().addDelegate(0, catDelegate).addDelegate(1, dogDelegate)
        val adapter = ListDelegationAdapter(manager)
        adapter.items = listOf(Cat(), Dog(), Cat())

        assertThat(adapter.getItemViewType(0)).isEqualTo(0)
        assertThat(adapter.getItemViewType(1)).isEqualTo(1)
        assertThat(adapter.getItemViewType(2)).isEqualTo(0)
    }

    @Test(expected = NullPointerException::class)
    fun `throws exception if no delegate found`() {
        val adapter = ListDelegationAdapter<List<Any>>()
        adapter.items = listOf("a")
        adapter.getItemViewType(0)
    }

    @Test
    fun `getItemCount returns correct size`() {
        val adapter = ListDelegationAdapter<List<Any>>()
        adapter.items = listOf("a", "b", "c")
        assertThat(adapter.itemCount).isEqualTo(3)
    }

    @Test
    fun `getItemCount returns 0 for empty list`() {
        val adapter = ListDelegationAdapter<List<Any>>()
        adapter.items = emptyList()
        assertThat(adapter.itemCount).isEqualTo(0)
    }

    @Test
    fun `getItemCount returns 0 for null list`() {
        val adapter = ListDelegationAdapter<List<Any>>()
        adapter.items = null
        assertThat(adapter.itemCount).isEqualTo(0)
    }

    @Test
    fun `getItemViewType delegates to manager`() {
        val delegate1 = SpyableAdapterDelegate<List<Any>>(0)
        val delegate2 = SpyableAdapterDelegate<List<Any>>(1)
        val adapter = ListDelegationAdapter(delegate1, delegate2)
        adapter.items = listOf("a", "b")

        assertThat(adapter.getItemViewType(0)).isEqualTo(0)
        assertThat(adapter.getItemViewType(1)).isEqualTo(1)
    }

    @Test
    fun `setItems notifies about change`() {
        val adapter = ListDelegationAdapter<List<Any>>()
        val mockObserver = MockAdapterDataObserver()
        adapter.registerAdapterDataObserver(mockObserver)

        adapter.items = listOf("a", "b")
        adapter.notifyDataSetChanged()
        assertThat(mockObserver.onChangedCalled).isTrue()
    }

    private class MockAdapterDataObserver : androidx.recyclerview.widget.RecyclerView.AdapterDataObserver() {
        var onChangedCalled = false
        override fun onChanged() {
            onChangedCalled = true
        }
    }

    private open class Animal
    private class Cat : Animal()
    private class Dog : Animal()

    private class CatAdapterDelegate : AdapterDelegate<List<Any>>() {
        override fun isForViewType(items: List<Any>, position: Int) = items[position] is Cat
        override fun onCreateViewHolder(parent: android.view.ViewGroup?) = TODO()
        override fun onBindViewHolder(items: List<Any>, position: Int, holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, payloads: List<Any>) = TODO()
    }

    private class DogAdapterDelegate : AdapterDelegate<List<Any>>() {
        override fun isForViewType(items: List<Any>, position: Int) = items[position] is Dog
        override fun onCreateViewHolder(parent: android.view.ViewGroup?) = TODO()
        override fun onBindViewHolder(items: List<Any>, position: Int, holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, payloads: List<Any>) = TODO()
    }
}
