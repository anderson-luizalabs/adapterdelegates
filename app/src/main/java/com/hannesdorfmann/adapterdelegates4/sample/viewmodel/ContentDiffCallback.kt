/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4.sample.viewmodel

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.sample.model.*

/**
 * DiffUtil callback for efficient list updates
 */
class ContentDiffCallback : DiffUtil.ItemCallback<ContentItem>() {
    override fun areItemsTheSame(
        oldItem: ContentItem,
        newItem: ContentItem,
    ): Boolean {
        // Compare by ID
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ContentItem,
        newItem: ContentItem,
    ): Boolean {
        // Compare content based on type
        return when {
            oldItem is FeaturedArticle && newItem is FeaturedArticle -> {
                oldItem == newItem
            }

            oldItem is Article && newItem is Article -> {
                oldItem == newItem
            }

            oldItem is VideoTutorial && newItem is VideoTutorial -> {
                oldItem == newItem
            }

            oldItem is CodeSnippet && newItem is CodeSnippet -> {
                oldItem == newItem
            }

            oldItem is DeveloperTip && newItem is DeveloperTip -> {
                oldItem == newItem
            }

            oldItem is TrendingTopics && newItem is TrendingTopics -> {
                oldItem == newItem
            }

            oldItem is NewsletterCard && newItem is NewsletterCard -> {
                oldItem == newItem
            }

            oldItem is NativeAd && newItem is NativeAd -> {
                oldItem == newItem
            }

            oldItem is SectionHeader && newItem is SectionHeader -> {
                oldItem == newItem
            }

            oldItem is LoadingItem && newItem is LoadingItem -> {
                true // Loading items are always the same
            }

            oldItem is ErrorItem && newItem is ErrorItem -> {
                oldItem == newItem
            }

            oldItem is EmptyItem && newItem is EmptyItem -> {
                oldItem == newItem
            }

            else -> false
        }
    }

    override fun getChangePayload(
        oldItem: ContentItem,
        newItem: ContentItem,
    ): Any? {
        // Return specific change payload for partial updates
        return when {
            oldItem is FeaturedArticle && newItem is FeaturedArticle -> {
                val changes = mutableListOf<String>()
                if (oldItem.isBookmarked != newItem.isBookmarked) changes.add("bookmark")
                if (oldItem.likes != newItem.likes) changes.add("likes")
                if (changes.isNotEmpty()) changes else null
            }

            oldItem is CodeSnippet && newItem is CodeSnippet -> {
                val changes = mutableListOf<String>()
                if (oldItem.isStarred != newItem.isStarred) changes.add("starred")
                if (oldItem.stars != newItem.stars) changes.add("stars")
                if (changes.isNotEmpty()) changes else null
            }

            oldItem is VideoTutorial && newItem is VideoTutorial -> {
                if (oldItem.progress != newItem.progress) "progress" else null
            }

            else -> super.getChangePayload(oldItem, newItem)
        }
    }
}
