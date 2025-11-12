/*
 * Copyright (c) 2017 Hannes Dorfmann.
 */
package com.hannesdorfmann.adapterdelegates4

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AbsFallbackAdapterDelegateTest {

    private class ConcreteFallbackAdapterDelegate : AbsFallbackAdapterDelegate<List<Any>>() {
        var onCreateViewHolderCalled = false
        var onBindViewHolderCalled = false

        override fun onCreateViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
            onCreateViewHolderCalled = true
            return object : RecyclerView.ViewHolder(FrameLayout(parent!!.context)) {}
        }

        override fun onBindViewHolder(
            items: List<Any>,
            position: Int,
            holder: RecyclerView.ViewHolder,
            payloads: List<Any>
        ) {
            onBindViewHolderCalled = true
        }
    }

    @Test
    fun `fallback delegate is used when no other delegate matches`() {
        val fallbackDelegate = ConcreteFallbackAdapterDelegate()
        val manager = AdapterDelegatesManager<List<Any>>().setFallbackDelegate(fallbackDelegate)
        val adapter = ListDelegationAdapter(manager)
        adapter.items = listOf("a")

        val parent = FrameLayout(RuntimeEnvironment.getApplication())
        val holder = adapter.onCreateViewHolder(parent, adapter.getItemViewType(0))
        adapter.onBindViewHolder(holder, 0)

        assertThat(fallbackDelegate.onCreateViewHolderCalled).isTrue()
        assertThat(fallbackDelegate.onBindViewHolderCalled).isTrue()
    }
}
