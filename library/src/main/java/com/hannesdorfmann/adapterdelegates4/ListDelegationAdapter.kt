/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4

/**
 * An adapter implementation designed for items organized in a [List].
 * All you have to do is add [AdapterDelegate]s to the internal [AdapterDelegatesManager].
 *
 * @param T The type of the items. Must extend from List
 * @author Hannes Dorfmann
 */
open class ListDelegationAdapter<T : List<*>> : AbsDelegationAdapter<T> {
    constructor() : super()

    constructor(delegatesManager: AdapterDelegatesManager<T>) : super(delegatesManager)

    /**
     * Adds a list of [AdapterDelegate]s
     *
     * @since 4.1.0
     */
    constructor(vararg delegates: AdapterDelegate<T>) : super(*delegates)

    override fun getItemCount(): Int = items?.size ?: 0
}
