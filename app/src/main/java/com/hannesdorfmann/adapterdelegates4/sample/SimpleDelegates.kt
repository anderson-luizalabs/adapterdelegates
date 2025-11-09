/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4.sample

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.hannesdorfmann.adapterdelegates4.sample.model.*

/**
 * Simple delegates without custom layouts - creates views programmatically
 */

/**
 * Featured Article Delegate
 */
class FeaturedArticleDelegate(
    private val onItemClick: (FeaturedArticle) -> Unit,
) : AbsListItemAdapterDelegate<FeaturedArticle, ContentItem, FeaturedArticleDelegate.ViewHolder>() {

    override fun isForViewType(item: ContentItem, items: List<ContentItem>, position: Int): Boolean {
        return item is FeaturedArticle
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val layout = LinearLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }

        val title = TextView(parent.context).apply {
            textSize = 20f
            setTextColor(Color.BLACK)
            setPadding(0, 0, 0, 16)
        }

        val subtitle = TextView(parent.context).apply {
            textSize = 14f
            setTextColor(Color.GRAY)
        }

        layout.addView(title)
        layout.addView(subtitle)

        return ViewHolder(layout, title, subtitle)
    }

    override fun onBindViewHolder(item: FeaturedArticle, holder: ViewHolder, payloads: List<Any>) {
        holder.title.text = "⭐ ${item.title}"
        holder.subtitle.text =
            "${item.subtitle} • ${item.readTimeMinutes} min • ${item.category.name}"
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    class ViewHolder(
        view: ViewGroup,
        val title: TextView,
        val subtitle: TextView,
    ) : RecyclerView.ViewHolder(view)
}

/**
 * Article Delegate
 */
class ArticleDelegate(
    private val onItemClick: (Article) -> Unit,
) : AbsListItemAdapterDelegate<Article, ContentItem, ArticleDelegate.ViewHolder>() {

    override fun isForViewType(item: ContentItem, items: List<ContentItem>, position: Int): Boolean {
        return item is Article
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val layout = LinearLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
        }

        val title = TextView(parent.context).apply {
            textSize = 16f
            setTextColor(Color.BLACK)
            setPadding(0, 0, 0, 8)
        }

        val summary = TextView(parent.context).apply {
            textSize = 12f
            setTextColor(Color.GRAY)
        }

        layout.addView(title)
        layout.addView(summary)

        return ViewHolder(layout, title, summary)
    }

    override fun onBindViewHolder(item: Article, holder: ViewHolder, payloads: List<Any>) {
        holder.title.text = item.title
        holder.summary.text =
            "${item.summary} • ${item.readTimeMinutes} min • ${item.viewCount} views"
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    class ViewHolder(
        view: ViewGroup,
        val title: TextView,
        val summary: TextView,
    ) : RecyclerView.ViewHolder(view)
}

// Section Header Delegate
class SectionHeaderDelegate :
    AbsListItemAdapterDelegate<SectionHeader, ContentItem, SectionHeaderDelegate.ViewHolder>() {

    override fun isForViewType(item: ContentItem, items: List<ContentItem>, position: Int): Boolean {
        return item is SectionHeader
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val textView = TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            textSize = 18f
            setTextColor(Color.BLACK)
            setPadding(32, 32, 32, 16)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(item: SectionHeader, holder: ViewHolder, payloads: List<Any>) {
        holder.textView.text = "📚 ${item.title}"
    }

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
}

// Loading Delegate
class LoadingDelegate :
    AbsListItemAdapterDelegate<LoadingItem, ContentItem, LoadingDelegate.ViewHolder>() {

    override fun isForViewType(item: ContentItem, items: List<ContentItem>, position: Int): Boolean {
        return item is LoadingItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val textView = TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                200,
            )
            text = "Loading..."
            gravity = Gravity.CENTER
            textSize = 16f
            setTextColor(Color.GRAY)
        }
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(item: LoadingItem, holder: ViewHolder, payloads: List<Any>) {
        // Static content
    }

    class ViewHolder(view: TextView) : RecyclerView.ViewHolder(view)
}

// Error Delegate
class ErrorDelegate(
    private val onRetryClick: () -> Unit,
) : AbsListItemAdapterDelegate<ErrorItem, ContentItem, ErrorDelegate.ViewHolder>() {

    override fun isForViewType(item: ContentItem, items: List<ContentItem>, position: Int): Boolean {
        return item is ErrorItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val layout = LinearLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 64, 32, 64)
        }

        val message = TextView(parent.context).apply {
            text = "Error occurred"
            textSize = 16f
            setTextColor(Color.RED)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 16)
        }

        val retryButton = TextView(parent.context).apply {
            text = "🔄 Retry"
            textSize = 14f
            setTextColor(Color.BLUE)
            gravity = Gravity.CENTER
            setPadding(32, 16, 32, 16)
        }

        layout.addView(message)
        layout.addView(retryButton)

        return ViewHolder(layout, message, retryButton)
    }

    override fun onBindViewHolder(item: ErrorItem, holder: ViewHolder, payloads: List<Any>) {
        holder.message.text = "❌ ${item.message}"
        holder.retryButton.setOnClickListener { onRetryClick() }
    }

    class ViewHolder(
        view: ViewGroup,
        val message: TextView,
        val retryButton: TextView,
    ) : RecyclerView.ViewHolder(view)
}
