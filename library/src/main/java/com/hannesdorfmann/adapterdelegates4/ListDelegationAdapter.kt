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
