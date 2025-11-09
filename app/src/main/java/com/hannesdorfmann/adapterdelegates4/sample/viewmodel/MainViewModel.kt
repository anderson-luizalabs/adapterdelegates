package com.hannesdorfmann.adapterdelegates4.sample.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hannesdorfmann.adapterdelegates4.sample.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.random.Random

/**
 * ViewModel for MainActivity with mock data generation
 */
class MainViewModel : ViewModel() {

    private val _contentList = MutableLiveData<List<ContentItem>>()
    val contentList: LiveData<List<ContentItem>> = _contentList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var currentPage = 0
    private val allItems = mutableListOf<ContentItem>()

    fun loadContent() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Simulate network delay
                delay(1000)

                // Generate initial content
                val items = generateContent()
                allItems.clear()
                allItems.addAll(items)
                _contentList.value = items

            } catch (e: Exception) {
                _errorMessage.value = "Failed to load content: ${e.message}"
                _contentList.value = listOf(
                    ErrorItem(message = "Something went wrong", canRetry = true)
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        currentPage = 0
        loadContent()
    }

    fun loadMore() {
        if (_isLoading.value == true) return

        viewModelScope.launch {
            // Add loading item
            val currentList = _contentList.value?.toMutableList() ?: mutableListOf()
            currentList.add(LoadingItem())
            _contentList.value = currentList

            // Simulate loading delay
            delay(1500)

            // Generate more content
            val moreItems = generatePageContent(++currentPage)

            // Remove loading and add new items
            currentList.removeAll { it is LoadingItem }
            currentList.addAll(moreItems)
            allItems.addAll(moreItems)
            _contentList.value = currentList
        }
    }

    fun retry() {
        loadContent()
    }

    fun sortBy(criteria: String) {
        val sorted = when (criteria) {
            "Latest" -> allItems.sortedByDescending { it.timestamp }
            "Popular" -> allItems.shuffled() // Mock sorting
            "Trending" -> allItems.reversed()
            else -> allItems
        }
        _contentList.value = sorted
    }

    fun toggleBookmark(article: FeaturedArticle) {
        val updated = allItems.map { item ->
            if (item is FeaturedArticle && item.id == article.id) {
                item.copy(isBookmarked = !item.isBookmarked)
            } else item
        }
        allItems.clear()
        allItems.addAll(updated)
        _contentList.value = updated
    }

    fun toggleStar(snippet: CodeSnippet) {
        val updated = allItems.map { item ->
            if (item is CodeSnippet && item.id == snippet.id) {
                item.copy(
                    isStarred = !item.isStarred,
                    stars = if (!item.isStarred) item.stars + 1 else item.stars - 1
                )
            } else item
        }
        allItems.clear()
        allItems.addAll(updated)
        _contentList.value = updated
    }

    private fun generateContent(): List<ContentItem> {
        val items = mutableListOf<ContentItem>()

        // Add featured article
        items.add(createFeaturedArticle())

        // Add section header
        items.add(
            SectionHeader(
                id = "header-latest",
                title = "Latest Articles"
            )
        )

        // Add mix of articles only (types we have delegates for)
        repeat(10) { index ->
            items.add(createArticle(index))
        }

        return items
    }

    private fun generatePageContent(page: Int): List<ContentItem> {
        val items = mutableListOf<ContentItem>()
        val startIndex = page * 10

        // Add section header
        items.add(
            SectionHeader(
                id = "header-page-$page",
                title = "More Articles - Page $page"
            )
        )

        // Generate only articles (type we have delegate for)
        repeat(10) { index ->
            items.add(createArticle(startIndex + index))
        }

        return items
    }

    // Mock data generators

    private fun createFeaturedArticle() = FeaturedArticle(
        id = "featured-1",
        timestamp = Date(),
        title = "Kotlin Multiplatform Mobile Goes Stable",
        subtitle = "Build native apps for iOS and Android with shared business logic",
        author = Author(
            "1",
            "JetBrains Team",
            "https://example.com/avatar1.jpg",
            isVerified = true
        ),
        imageUrl = "https://picsum.photos/800/400?random=1",
        readTimeMinutes = 8,
        category = Category.MOBILE,
        isPremium = true,
        likes = 1234,
        isBookmarked = false
    )

    private fun createArticle(index: Int) = Article(
        id = "article-$index",
        timestamp = Date(System.currentTimeMillis() - index * 3600000),
        title = articleTitles[index % articleTitles.size],
        summary = "Learn about the latest developments and best practices in modern development.",
        author = Author(
            "author-$index",
            authorNames[index % authorNames.size],
            "https://picsum.photos/100/100?random=$index"
        ),
        thumbnailUrl = "https://picsum.photos/400/300?random=$index",
        readTimeMinutes = Random.nextInt(3, 15),
        category = Category.values()[index % Category.values().size],
        tags = listOf("Kotlin", "Android", "Development").shuffled().take(2),
        viewCount = Random.nextInt(100, 10000)
    )

    private fun createVideoTutorial(index: Int) = VideoTutorial(
        id = "video-$index",
        timestamp = Date(),
        title = "Building Your First Compose App",
        instructor = "Google Developers",
        thumbnailUrl = "https://picsum.photos/400/225?random=video$index",
        duration = "${Random.nextInt(5, 45)}:${Random.nextInt(10, 59)}",
        level = SkillLevel.values()[index % SkillLevel.values().size],
        technology = "Jetpack Compose",
        progress = if (index % 3 == 0) Random.nextFloat() else 0f
    )

    private fun createCodeSnippet(index: Int) = CodeSnippet(
        id = "code-$index",
        timestamp = Date(),
        title = "Elegant Kotlin Extension Function",
        language = ProgrammingLanguage.KOTLIN,
        code = """
            |fun View.visible() {
            |    visibility = View.VISIBLE
            |}
            |
            |fun View.gone() {
            |    visibility = View.GONE
            |}
        """.trimMargin(),
        description = "Simplify view visibility changes with extension functions",
        author = Author("dev-$index", "KotlinDev", "https://picsum.photos/50/50?random=$index"),
        stars = Random.nextInt(10, 500)
    )

    private fun createDeveloperTip(index: Int) = DeveloperTip(
        id = "tip-$index",
        timestamp = Date(),
        content = developerTips[index % developerTips.size],
        author = Author(
            "tip-author-$index",
            "ProDev",
            "https://picsum.photos/50/50?random=tip$index"
        ),
        category = "Best Practices"
    )

    private fun createTrendingTopics() = TrendingTopics(
        id = "trending-1",
        timestamp = Date(),
        title = "Trending Technologies",
        topics = listOf(
            Topic("1", "Compose", 2341, TrendDirection.UP),
            Topic("2", "KMM", 1823, TrendDirection.UP),
            Topic("3", "Coroutines", 1567, TrendDirection.STABLE),
            Topic("4", "Flow", 982, TrendDirection.UP),
            Topic("5", "Hilt", 743, TrendDirection.DOWN)
        )
    )

    private fun createNewsletterCard() = NewsletterCard(
        id = "newsletter-1",
        timestamp = Date(),
        title = "Stay Updated!",
        description = "Get weekly Android development tips and news",
        benefits = listOf(
            "Latest Android updates",
            "Code snippets & tutorials",
            "Community highlights"
        )
    )

    private fun createNativeAd(page: Int) = NativeAd(
        id = "ad-$page",
        timestamp = Date(),
        title = "Android Studio Arctic Fox",
        description = "The latest Android Studio with improved performance",
        ctaText = "Download Now",
        imageUrl = "https://picsum.photos/400/200?random=ad$page",
        advertiser = "Google"
    )

    companion object {
        private val articleTitles = listOf(
            "Understanding Kotlin Coroutines",
            "Compose State Management Deep Dive",
            "Building Offline-First Apps",
            "Modern Android Architecture",
            "Testing Strategies for Android",
            "Performance Optimization Tips",
            "Dependency Injection with Hilt",
            "Navigation Component Best Practices",
            "Material Design 3 Implementation",
            "Android 14 New Features"
        )

        private val authorNames = listOf(
            "Sarah Johnson",
            "Mike Chen",
            "Emma Wilson",
            "Carlos Rodriguez",
            "Lisa Park"
        )

        private val developerTips = listOf(
            "💡 Use sealed classes for representing UI states",
            "🚀 Leverage Kotlin's scope functions for cleaner code",
            "🔧 Always test your app on different screen sizes",
            "📱 Implement proper error handling in coroutines",
            "⚡ Use lazy initialization for expensive objects"
        )
    }
}