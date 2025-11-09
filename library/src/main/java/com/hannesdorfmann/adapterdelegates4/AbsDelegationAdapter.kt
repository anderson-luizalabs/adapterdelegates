/*
 * Copyright (c) 2015 Hannes Dorfmann.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.hannesdorfmann.adapterdelegates4

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * An implementation of an Adapter that already uses [AdapterDelegatesManager] and calls
 * the corresponding methods from Adapter's methods.
 *
 * @param T The type of the datasource / items
 * @author Hannes Dorfmann
 */
abstract class AbsDelegationAdapter<T>(
    protected val delegatesManager: AdapterDelegatesManager<T> = AdapterDelegatesManager()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: T? = null

    /**
     * Adds a list of [AdapterDelegate]s
     *
     * @param delegates Items to add
     * @since 4.1.0
     */
    constructor(vararg delegates: AdapterDelegate<T>) : this(AdapterDelegatesManager(*delegates))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegatesManager.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegatesManager.onBindViewHolder(items, position, holder, null)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        delegatesManager.onBindViewHolder(items, position, holder, payloads)
    }

    override fun getItemViewType(position: Int): Int {
        return delegatesManager.getItemViewType(items, position)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        delegatesManager.onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return delegatesManager.onFailedToRecycleView(holder)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        delegatesManager.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        delegatesManager.onViewDetachedFromWindow(holder)
    }
}
