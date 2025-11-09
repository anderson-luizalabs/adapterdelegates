/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test

/**
 * @author Hannes Dorfmann
 */
class AdapterDelegatesManagerTest {

    @Test
    fun addRemove() {
        val d1 = object : AdapterDelegate<Any>() {
            override fun isForViewType(items: Any, position: Int) = false
            override fun onCreateViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder = mockk()
            override fun onBindViewHolder(
                items: Any,
                position: Int,
                holder: RecyclerView.ViewHolder,
                payloads: List<Any>
            ) {
            }
        }

        val d2 = object : AdapterDelegate<Any>() {
            override fun isForViewType(items: Any, position: Int) = false
            override fun onCreateViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder = mockk()
            override fun onBindViewHolder(
                items: Any,
                position: Int,
                holder: RecyclerView.ViewHolder,
                payloads: List<Any>
            ) {
            }
        }

        val manager = AdapterDelegatesManager<Any>()
        manager.addDelegate(d1)

        assertTrue(manager.delegates[0] === d1)
        assertEquals(0, manager.getViewType(d1))

        try {
            // replacing not allowed
            manager.addDelegate(0, d2)
            fail("Replacing delegate should fail")
        } catch (e: IllegalArgumentException) {
            assertTrue(manager.delegates[0] === d1)
            assertEquals(0, manager.getViewType(d1))
            assertEquals(1, manager.delegates.size())
        }

        // replacing allowed
        manager.addDelegate(0, true, d2)
        assertTrue(manager.delegates[0] === d2)
        assertEquals(0, manager.getViewType(d2))
        // Removed delegate

        // Remove a delegate should have no impact, because its already removed
        manager.removeDelegate(d1)
        // Still removed
        assertTrue(manager.delegates[0] === d2)
        assertEquals(1, manager.delegates.size())

        // Should remove d2
        manager.removeDelegate(0)
        assertNull(manager.delegates[0])
        assertEquals(0, manager.delegates.size())
        // Removed delegate
    }

    @Test
    fun isForViewType() {
        // 3 elements and each element has its own viewtype and hence own delegate
        val items = listOf(Any(), Any(), Any())
        val d0 = SpyableAdapterDelegate<List<Any>>(0)
        val d1 = SpyableAdapterDelegate<List<Any>>(1)
        val d2 = SpyableAdapterDelegate<List<Any>>(2)

        val manager = AdapterDelegatesManager<List<Any>>()
        manager.addDelegate(d0)
        manager.addDelegate(d1)
        manager.addDelegate(d2)

        // Test first item
        var viewType = manager.getItemViewType(items, 0)
        assertEquals(0, viewType)
        assertTrue(d0.isForViewTypeReturnedYes)
        assertFalse(d1.isForViewTypeReturnedYes)
        assertFalse(d2.isForViewTypeReturnedYes)
        resetDelegates(d0, d1, d2)

        // Test second item
        viewType = manager.getItemViewType(items, 1)
        assertEquals(1, viewType)
        assertTrue(d1.isForViewTypeReturnedYes)
        assertFalse(d0.isForViewTypeReturnedYes)
        assertFalse(d2.isForViewTypeReturnedYes)
        resetDelegates(d0, d1, d2)

        // Test third item
        viewType = manager.getItemViewType(items, 2)
        assertEquals(2, viewType)
        assertTrue(d2.isForViewTypeReturnedYes)
        assertFalse(d0.isForViewTypeReturnedYes)
        assertFalse(d1.isForViewTypeReturnedYes)
        resetDelegates(d0, d1, d2)
    }

    @Test
    fun onCreateViewHolder() {
        // 3 elements and each element has its own viewtype and hence own delegate
        val items = listOf(Any(), Any(), Any())
        val d0 = SpyableAdapterDelegate<List<Any>>(0)
        val d1 = SpyableAdapterDelegate<List<Any>>(1)
        val d2 = SpyableAdapterDelegate<List<Any>>(2)

        val manager = AdapterDelegatesManager<List<Any>>()
        manager.addDelegate(d0)
        manager.addDelegate(d1)
        manager.addDelegate(d2)

        // Test first item
        var vh = manager.onCreateViewHolder(null as ViewGroup?, 0)
        assertSame(vh, d0.viewHolder)
        assertTrue(d0.onCreateViewHolderCalled)
        assertFalse(d1.onCreateViewHolderCalled)
        assertFalse(d2.onCreateViewHolderCalled)

        resetDelegates(d0, d1, d2)

        // Test second item
        vh = manager.onCreateViewHolder(null as ViewGroup?, 1)
        assertSame(vh, d1.viewHolder)
        assertTrue(d1.onCreateViewHolderCalled)
        assertFalse(d0.onCreateViewHolderCalled)
        assertFalse(d2.onCreateViewHolderCalled)

        resetDelegates(d0, d1, d2)

        // Test third item
        vh = manager.onCreateViewHolder(null as ViewGroup?, 2)
        assertSame(vh, d2.viewHolder)
        assertTrue(d2.onCreateViewHolderCalled)
        assertFalse(d0.onCreateViewHolderCalled)
        assertFalse(d1.onCreateViewHolderCalled)
    }

    @Test
    fun onBindViewHolder() {
        // 3 elements and each element has its own viewtype and hence own delegate
        val items = listOf(Any(), Any(), Any())
        val d0 = SpyableAdapterDelegate<List<Any>>(0)
        val d1 = SpyableAdapterDelegate<List<Any>>(1)
        val d2 = SpyableAdapterDelegate<List<Any>>(2)

        val manager = AdapterDelegatesManager<List<Any>>()
        manager.addDelegate(d0)
        manager.addDelegate(d1)
        manager.addDelegate(d2)

        // Test first item
        manager.onBindViewHolder(items, 0, d0.viewHolder, emptyList())
        assertTrue(d0.onBindViewHolderCalled)
        assertFalse(d1.onBindViewHolderCalled)
        assertFalse(d2.onBindViewHolderCalled)

        resetDelegates(d0, d1, d2)

        // Test second item
        manager.onBindViewHolder(items, 1, d1.viewHolder, emptyList())
        assertTrue(d1.onBindViewHolderCalled)
        assertFalse(d0.onBindViewHolderCalled)
        assertFalse(d2.onBindViewHolderCalled)

        resetDelegates(d0, d1, d2)

        // Test third item
        manager.onBindViewHolder(items, 2, d2.viewHolder, emptyList())
        assertTrue(d2.onBindViewHolderCalled)
        assertFalse(d1.onBindViewHolderCalled)
        assertFalse(d0.onBindViewHolderCalled)

        resetDelegates(d0, d1, d2)
    }

    @Test
    fun onViewDetachedFromWindow() {
        val items = listOf(Any(), Any(), Any())
        val d0 = SpyableAdapterDelegate<List<Any>>(0)
        val d1 = SpyableAdapterDelegate<List<Any>>(1)
        val d2 = SpyableAdapterDelegate<List<Any>>(2)

        val manager = AdapterDelegatesManager<List<Any>>()
        manager.addDelegate(d0)
        manager.addDelegate(d1)
        manager.addDelegate(d2)

        // Test first item
        manager.onViewDetachedFromWindow(d0.viewHolder)
        assertTrue(d0.onViewDetachedFromWindowCalled)
        assertFalse(d1.onViewDetachedFromWindowCalled)
        assertFalse(d2.onViewDetachedFromWindowCalled)

        resetDelegates(d0, d1, d2)

        // Test second item
        manager.onViewDetachedFromWindow(d1.viewHolder)
        assertTrue(d1.onViewDetachedFromWindowCalled)
        assertFalse(d0.onViewDetachedFromWindowCalled)
        assertFalse(d2.onViewDetachedFromWindowCalled)

        resetDelegates(d0, d1, d2)

        // Test third item
        manager.onViewDetachedFromWindow(d2.viewHolder)
        assertTrue(d2.onViewDetachedFromWindowCalled)
        assertFalse(d1.onViewDetachedFromWindowCalled)
        assertFalse(d0.onViewDetachedFromWindowCalled)

        resetDelegates(d0, d1, d2)
    }

    @Test
    fun onViewAttachedToWindow() {
        val items = listOf(Any(), Any(), Any())
        val d0 = SpyableAdapterDelegate<List<Any>>(0)
        val d1 = SpyableAdapterDelegate<List<Any>>(1)
        val d2 = SpyableAdapterDelegate<List<Any>>(2)

        val manager = AdapterDelegatesManager<List<Any>>()
        manager.addDelegate(d0)
        manager.addDelegate(d1)
        manager.addDelegate(d2)

        // Test first item
        manager.onViewAttachedToWindow(d0.viewHolder)
        assertTrue(d0.onViewAtachedToWindowCalled)
        assertFalse(d1.onViewAtachedToWindowCalled)
        assertFalse(d2.onViewAtachedToWindowCalled)

        resetDelegates(d0, d1, d2)

        // Test second item
        manager.onViewAttachedToWindow(d1.viewHolder)
        assertTrue(d1.onViewAtachedToWindowCalled)
        assertFalse(d0.onViewAtachedToWindowCalled)
        assertFalse(d2.onViewAtachedToWindowCalled)

        resetDelegates(d0, d1, d2)

        // Test third item
        manager.onViewAttachedToWindow(d2.viewHolder)
        assertTrue(d2.onViewAtachedToWindowCalled)
        assertFalse(d1.onViewAtachedToWindowCalled)
        assertFalse(d0.onViewAtachedToWindowCalled)

        resetDelegates(d0, d1, d2)
    }

    @Test
    fun onViewRecycled() {
        val items = listOf(Any(), Any(), Any())
        val d0 = SpyableAdapterDelegate<List<Any>>(0)
        val d1 = SpyableAdapterDelegate<List<Any>>(1)
        val d2 = SpyableAdapterDelegate<List<Any>>(2)

        val manager = AdapterDelegatesManager<List<Any>>()
        manager.addDelegate(d0)
        manager.addDelegate(d1)
        manager.addDelegate(d2)

        // Test first item
        manager.onViewRecycled(d0.viewHolder)
        assertTrue(d0.onViewRecycledCalled)
        assertFalse(d1.onViewRecycledCalled)
        assertFalse(d2.onViewRecycledCalled)

        resetDelegates(d0, d1, d2)

        // Test second item
        manager.onViewRecycled(d1.viewHolder)
        assertTrue(d1.onViewRecycledCalled)
        assertFalse(d0.onViewRecycledCalled)
        assertFalse(d2.onViewRecycledCalled)

        resetDelegates(d0, d1, d2)

        // Test third item
        manager.onViewRecycled(d2.viewHolder)
        assertTrue(d2.onViewRecycledCalled)
        assertFalse(d1.onViewRecycledCalled)
        assertFalse(d0.onViewRecycledCalled)

        resetDelegates(d0, d1, d2)
    }

    @Test
    fun onFailedToRecycleViewCalled() {
        val items = listOf(Any(), Any(), Any())
        val d0 = SpyableAdapterDelegate<List<Any>>(0)
        val d1 = SpyableAdapterDelegate<List<Any>>(1)
        val d2 = SpyableAdapterDelegate<List<Any>>(2)

        val manager = AdapterDelegatesManager<List<Any>>()
        manager.addDelegate(d0)
        manager.addDelegate(d1)
        manager.addDelegate(d2)

        // Test first item
        manager.onFailedToRecycleView(d0.viewHolder)
        assertTrue(d0.onFailedToRecycleViewCalled)
        assertFalse(d1.onFailedToRecycleViewCalled)
        assertFalse(d2.onFailedToRecycleViewCalled)

        resetDelegates(d0, d1, d2)

        // Test second item
        manager.onFailedToRecycleView(d1.viewHolder)
        assertTrue(d1.onFailedToRecycleViewCalled)
        assertFalse(d0.onFailedToRecycleViewCalled)
        assertFalse(d2.onFailedToRecycleViewCalled)

        resetDelegates(d0, d1, d2)

        // Test third item
        manager.onFailedToRecycleView(d2.viewHolder)
        assertTrue(d2.onFailedToRecycleViewCalled)
        assertFalse(d1.onFailedToRecycleViewCalled)
        assertFalse(d0.onFailedToRecycleViewCalled)

        resetDelegates(d0, d1, d2)
    }

    @Test
    fun allMethodsTest() {
        val items = listOf(Any(), Any(), Any())
        val d0 = SpyableAdapterDelegate<List<Any>>(0)
        val d1 = SpyableAdapterDelegate<List<Any>>(1)
        val d2 = SpyableAdapterDelegate<List<Any>>(2)

        val manager = AdapterDelegatesManager<List<Any>>()
        manager.addDelegate(d0)
        manager.addDelegate(d1)
        manager.addDelegate(d2)

        val delegates = arrayOf(d0, d1, d2)

        for (i in items.indices) {
            val expectedDelegate = delegates[i]

            val viewType = manager.getItemViewType(items, i)

            // Test view type
            assertEquals(viewType, expectedDelegate.viewType)
            for (d in delegates) {
                if (d === expectedDelegate) {
                    assertTrue(d.isForViewTypeReturnedYes)
                } else {
                    assertFalse(d.isForViewTypeReturnedYes)
                }
            }

            // Test create viewHolder
            val vh = manager.onCreateViewHolder(null as ViewGroup?, viewType)
            assertSame(vh, expectedDelegate.viewHolder)

            for (d in delegates) {
                if (d === expectedDelegate) {
                    assertTrue(d.onCreateViewHolderCalled)
                } else {
                    assertFalse(d.onCreateViewHolderCalled)
                }
            }

            // Test bind viewHolder
            manager.onBindViewHolder(items, i, vh, emptyList())
            for (d in delegates) {
                if (d === expectedDelegate) {
                    assertTrue(d.onBindViewHolderCalled)
                } else {
                    assertFalse(d.onBindViewHolderCalled)
                }
            }

            // Test onViewAtachedToWindow
            manager.onViewAttachedToWindow(vh)
            for (d in delegates) {
                if (d === expectedDelegate) {
                    assertTrue(d.onViewAtachedToWindowCalled)
                } else {
                    assertFalse(d.onViewAtachedToWindowCalled)
                }
            }

            // Test onViewDetachedFromWindow
            manager.onViewDetachedFromWindow(vh)
            for (d in delegates) {
                if (d === expectedDelegate) {
                    assertTrue(d.onViewDetachedFromWindowCalled)
                } else {
                    assertFalse(d.onViewDetachedFromWindowCalled)
                }
            }

            // Test onViewRecycled
            manager.onViewRecycled(vh)
            for (d in delegates) {
                if (d === expectedDelegate) {
                    assertTrue(d.onViewRecycledCalled)
                } else {
                    assertFalse(d.onViewRecycledCalled)
                }
            }

            // Test onFailedToRecycleView
            manager.onFailedToRecycleView(vh)
            for (d in delegates) {
                if (d === expectedDelegate) {
                    assertTrue(d.onFailedToRecycleViewCalled)
                } else {
                    assertFalse(d.onFailedToRecycleViewCalled)
                }
            }

            resetDelegates(*delegates)
        }
    }

    private fun resetDelegates(vararg delegates: SpyableAdapterDelegate<*>) {
        for (d in delegates) {
            d.reset()
        }
    }

    @Test(expected = NullPointerException::class)
    fun testNoDelegates() {
        val delegatesManager = AdapterDelegatesManager<Any>()
        delegatesManager.onCreateViewHolder(null as ViewGroup?, 1) // No Delegate --> throw Exception
    }

    @Test
    fun testUnknownDelegate() {
        val delegatesManager = AdapterDelegatesManager<Any>()
        delegatesManager.addDelegate(SpyableAdapterDelegate(0))

        delegatesManager.onCreateViewHolder(null as ViewGroup?, 0) // NO exception
        try {
            delegatesManager.onCreateViewHolder(
                null as ViewGroup?,
                1
            ) // There is no delegates manager for ViewType == 1
            fail("Exception should be thrown because no Delegate for given ViewType is registered")
        } catch (e: NullPointerException) {
            // No delegate for view type 1 registered --> Nullpointer exception will be thrown
        }
    }

    @Test
    fun fallbackUnknownDelegate() {
        val fallbackViewType = Integer.MAX_VALUE - 1
        val itemPosition = 1
        val items = mutableListOf<Any>()
        items.add(Any())

        val delegatesManager = AdapterDelegatesManager<List<Any>>()
        val fallbackDelegate = SpyableAdapterDelegate<List<Any>>(fallbackViewType)

        val otherDelegate = SpyableAdapterDelegate<List<Any>>(0)
        delegatesManager.fallbackDelegate = fallbackDelegate
        delegatesManager.addDelegate(otherDelegate)

        val vh = delegatesManager.onCreateViewHolder(
            null as ViewGroup?,
            fallbackViewType
        ) // There is no delegates manager for ViewType == 1

        assertSame(vh, fallbackDelegate.viewHolder)
        assertTrue(fallbackDelegate.onCreateViewHolderCalled)
        assertFalse(otherDelegate.onCreateViewHolderCalled)

        // Test bind viewHolder
        delegatesManager.onBindViewHolder(items, itemPosition, vh, emptyList())
        assertTrue(fallbackDelegate.onBindViewHolderCalled)
        assertFalse(otherDelegate.onBindViewHolderCalled)
    }

    @Test
    fun viewTypeInConflictWithFallbackDelegate() {
        try {
            val manager = AdapterDelegatesManager<List<Any>>()
            manager.addDelegate(
                AdapterDelegatesManager.FALLBACK_DELEGATE_VIEW_TYPE,
                SpyableAdapterDelegate(0)
            )
            fail("An exception should be thrown because view type integer is already reserved for fallback delegate")
        } catch (e: IllegalArgumentException) {
            assertEquals(
                "The view type = " +
                    AdapterDelegatesManager.FALLBACK_DELEGATE_VIEW_TYPE +
                    " is reserved for fallback adapter delegate (see setFallbackDelegate() ). Please use another view type.",
                e.message
            )
        }
    }

    @Test
    fun getViewType() {
        val manager = AdapterDelegatesManager<List<Any>>()

        try {
            manager.getViewType(null as AdapterDelegate<List<Any>>?)
            fail("Nullpointer Exception expected")
        } catch (e: NullPointerException) {
        }

        val delegate1 = SpyableAdapterDelegate<List<Any>>(0)
        val delegate2 = SpyableAdapterDelegate<List<Any>>(1)
        assertEquals(-1, manager.getViewType(delegate1))
        assertEquals(-1, manager.getViewType(delegate2))

        manager.addDelegate(delegate1)
        manager.addDelegate(delegate2)

        assertEquals(0, manager.getViewType(delegate1))
        assertEquals(1, manager.getViewType(delegate2))

        val delegate3 = SpyableAdapterDelegate<List<Any>>(2)
        val delegate4 = SpyableAdapterDelegate<List<Any>>(3)

        manager.addDelegate(4, delegate4)
        assertEquals(4, manager.getViewType(delegate4))

        manager.addDelegate(delegate3)
        assertEquals(3, manager.getViewType(delegate3))
    }

    @Test
    fun numberOverflow() {
        val manager = AdapterDelegatesManager<List<Any>>()
        val delegate1 = SpyableAdapterDelegate<List<Any>>(0)

        manager.addDelegate(Integer.MAX_VALUE + 1, delegate1)
        assertEquals(Integer.MAX_VALUE + 1, manager.getViewType(delegate1))
    }

    @Test
    fun delegateForViewType() {
        val manager = AdapterDelegatesManager<List<Any>>()
        val delegate1 = SpyableAdapterDelegate<List<Any>>(0)
        val delegate2 = SpyableAdapterDelegate<List<Any>>(1)

        val fallbackDelegate = SpyableAdapterDelegate<List<Any>>(3)
        manager.fallbackDelegate = fallbackDelegate
        assertEquals(fallbackDelegate, manager.getDelegateForViewType(1))

        manager.addDelegate(delegate1)
        manager.addDelegate(delegate2)
        assertEquals(delegate1, manager.getDelegateForViewType(0))
        assertEquals(delegate2, manager.getDelegateForViewType(1))
    }

    @Test
    fun delegateForViewTypeNoFallback() {
        val manager = AdapterDelegatesManager<List<Any>>()
        val delegate1 = SpyableAdapterDelegate<List<Any>>(0)
        val delegate2 = SpyableAdapterDelegate<List<Any>>(1)

        manager.addDelegate(delegate1)
        manager.addDelegate(delegate2)
        assertEquals(delegate1, manager.getDelegateForViewType(0))
        assertEquals(delegate2, manager.getDelegateForViewType(1))
        assertNull(manager.getDelegateForViewType(2))
    }

    @Test
    fun setGetFallbackDelegate() {
        val manager = AdapterDelegatesManager<List<Any>>()
        assertNull(manager.fallbackDelegate)
        val fallbackDelegate = SpyableAdapterDelegate<List<Any>>(3)
        manager.fallbackDelegate = fallbackDelegate
        assertEquals(fallbackDelegate, manager.fallbackDelegate)
    }
}
