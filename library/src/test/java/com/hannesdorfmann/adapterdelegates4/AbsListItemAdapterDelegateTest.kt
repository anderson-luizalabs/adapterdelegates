/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import org.junit.Assert.assertTrue
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
class AbsListItemAdapterDelegateTest {

    @Test
    fun invokeMethods() {
        val items: List<Animal> = listOf(Cat())

        val delegate = CatAbsListItemAdapterDelegate()

        delegate.isForViewType(items, 0)
        assertTrue(delegate.isForViewTypeCalled)

        val parent = FrameLayout(RuntimeEnvironment.getApplication())
        val vh = delegate.onCreateViewHolder(parent)
        assertTrue(delegate.onCreateViewHolderCalled)

        delegate.onBindViewHolder(items, 0, vh, emptyList())
        assertTrue(delegate.onBindViewHolderCalled)
    }

    interface Animal

    class Cat : Animal

    class CatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class CatAbsListItemAdapterDelegate :
        AbsListItemAdapterDelegate<Cat, Animal, CatViewHolder>() {
        var isForViewTypeCalled = false
        var onCreateViewHolderCalled = false
        var onBindViewHolderCalled = false
        var onViewDetachedFromWindow = false

        override fun isForViewType(
            item: Animal,
            items: List<Animal>,
            position: Int,
        ): Boolean {
            isForViewTypeCalled = true
            return false
        }

        override fun onCreateViewHolder(parent: ViewGroup?): CatViewHolder {
            onCreateViewHolderCalled = true
            return CatViewHolder(View(RuntimeEnvironment.getApplication()))
        }

        override fun onBindViewHolder(
            item: Cat,
            holder: CatViewHolder,
            payloads: List<Any>,
        ) {
            onBindViewHolderCalled = true
        }

        override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
            super.onViewDetachedFromWindow(holder)
            onViewDetachedFromWindow = true
        }
    }
}
