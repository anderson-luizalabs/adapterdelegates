/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4

import org.junit.Assert.*
import org.junit.Test

/**
 * @author Hannes Dorfmann
 */
class ListDelegationAdapterTest {

    @Test(expected = NullPointerException::class)
    fun delegatesManagerNull() {
        object : ListDelegationAdapter<List<Any>>(null) {
            override fun getItemCount() = 0
        }
    }

    @Test
    fun checkDelegatesManagerInstance() {
        val manager = AdapterDelegatesManager<List<Any>>()

        val adapter = object : ListDelegationAdapter<List<Any>>(manager) {
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
        val adapter = object : ListDelegationAdapter<List<Any>>() {
            override fun getItemCount(): Int {
                // Hacky but does the job
                assertNotNull(this.delegatesManager)
                return 0
            }
        }

        adapter.itemCount
    }
}
