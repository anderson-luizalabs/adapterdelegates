package com.hannesdorfmann.adapterdelegates4

/**
 * Base class for a fallback delegate [AdapterDelegatesManager.setFallbackDelegate].
 *
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
abstract class AbsFallbackAdapterDelegate<T> : AdapterDelegate<T>() {

    /**
     * Not needed, because never called for fallback adapter delegates.
     */
    final override fun isForViewType(items: T, position: Int): Boolean = true
}
