package com.hannesdorfmann.adapterdelegates4.sample.model

import androidx.annotation.DrawableRes
import java.util.Date

/**
 * Base sealed class for all content types in our Tech News app
 */
sealed class ContentItem {
    abstract val id: String
    abstract val timestamp: Date
}

/**
 * Featured article with hero image
 */
data class FeaturedArticle(
    override val id: String,
    override val timestamp: Date,
    val title: String,
    val subtitle: String,
    val author: Author,
    val imageUrl: String,
    val readTimeMinutes: Int,
    val category: Category,
    val isPremium: Boolean = false,
    val likes: Int = 0,
    val isBookmarked: Boolean = false
) : ContentItem()

/**
 * Regular article in list
 */
data class Article(
    override val id: String,
    override val timestamp: Date,
    val title: String,
    val summary: String,
    val author: Author,
    val thumbnailUrl: String,
    val readTimeMinutes: Int,
    val category: Category,
    val tags: List<String> = emptyList(),
    val viewCount: Int = 0
) : ContentItem()

/**
 * Video tutorial card
 */
data class VideoTutorial(
    override val id: String,
    override val timestamp: Date,
    val title: String,
    val instructor: String,
    val thumbnailUrl: String,
    val duration: String,
    val level: SkillLevel,
    val technology: String,
    val isCompleted: Boolean = false,
    val progress: Float = 0f
) : ContentItem()

/**
 * Interactive code snippet
 */
data class CodeSnippet(
    override val id: String,
    override val timestamp: Date,
    val title: String,
    val language: ProgrammingLanguage,
    val code: String,
    val description: String,
    val author: Author,
    val stars: Int = 0,
    val isStarred: Boolean = false
) : ContentItem()

/**
 * Developer tip/quote
 */
data class DeveloperTip(
    override val id: String,
    override val timestamp: Date,
    val content: String,
    val author: Author,
    val category: String,
    @DrawableRes val iconRes: Int? = null
) : ContentItem()

/**
 * Horizontal list of trending topics
 */
data class TrendingTopics(
    override val id: String,
    override val timestamp: Date,
    val title: String,
    val topics: List<Topic>
) : ContentItem()

/**
 * Newsletter signup card
 */
data class NewsletterCard(
    override val id: String,
    override val timestamp: Date,
    val title: String,
    val description: String,
    val benefits: List<String>
) : ContentItem()

/**
 * Loading state
 */
data class LoadingItem(
    override val id: String = "loading",
    override val timestamp: Date = Date()
) : ContentItem()

/**
 * Error state
 */
data class ErrorItem(
    override val id: String = "error",
    override val timestamp: Date = Date(),
    val message: String,
    val canRetry: Boolean = true
) : ContentItem()

/**
 * Empty state
 */
data class EmptyItem(
    override val id: String = "empty",
    override val timestamp: Date = Date(),
    val title: String,
    val message: String,
    @DrawableRes val illustrationRes: Int? = null
) : ContentItem()

/**
 * Section header
 */
data class SectionHeader(
    override val id: String,
    override val timestamp: Date = Date(),
    val title: String,
    val actionText: String? = null
) : ContentItem()

// Supporting data classes

data class Author(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val bio: String? = null,
    val isVerified: Boolean = false
)

data class Topic(
    val id: String,
    val name: String,
    val count: Int,
    val trending: TrendDirection = TrendDirection.STABLE,
    @DrawableRes val iconRes: Int? = null
)

enum class Category {
    MOBILE, WEB, AI_ML, CLOUD, DEVOPS, SECURITY, BLOCKCHAIN, IOT, GAMING
}

enum class SkillLevel {
    BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
}

enum class ProgrammingLanguage {
    KOTLIN, JAVA, SWIFT, JAVASCRIPT, TYPESCRIPT, PYTHON, RUST, GO, DART
}

enum class TrendDirection {
    UP, DOWN, STABLE
}

// Advertisement for demonstrating different view types
data class NativeAd(
    override val id: String,
    override val timestamp: Date = Date(),
    val title: String,
    val description: String,
    val ctaText: String,
    val imageUrl: String,
    val advertiser: String
) : ContentItem()