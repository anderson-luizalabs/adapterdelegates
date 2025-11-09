# 🚀 AdapterDelegates Sample App - Tech Hub

## Modern Android App Showcasing AdapterDelegates Library

A beautiful, modern sample application demonstrating the full power of the AdapterDelegates library
with Kotlin.

## ✨ Features

### Architecture & Patterns

- **100% Kotlin** - Modern, concise, and type-safe
- **MVVM Architecture** - ViewModel with LiveData
- **Repository Pattern** - Clean separation of concerns
- **Coroutines** - Async operations with structured concurrency

### UI Components

- **13+ Different View Types** - Showcasing adapter flexibility
- **Material Design 3** - Modern Android UI
- **Dark Mode Support** - Automatic theme switching
- **ViewBinding** - Type-safe view references
- **Kotlin DSL** - Clean delegate creation

### AdapterDelegates Features Demonstrated

#### 1. **Multiple View Types**

- Featured Articles (Hero cards)
- Regular Articles
- Video Tutorials
- Code Snippets with syntax highlighting
- Developer Tips
- Trending Topics (horizontal scroll)
- Newsletter Signup
- Native Ads
- Section Headers
- Loading States
- Error States
- Empty States

#### 2. **Delegate Types**

```kotlin
// ViewBinding Delegate
featuredArticleDelegate(...)

// DSL Delegate
articleDelegate(...)

// Simple Delegate
loadingDelegate()
```

#### 3. **DiffUtil Integration**

- `AsyncListDifferDelegationAdapter` for automatic updates
- Custom `ContentDiffCallback` with payload support
- Smooth animations for list changes

#### 4. **Advanced Features**

- **Pull to Refresh** - SwipeRefreshLayout integration
- **Infinite Scroll** - Pagination support
- **Item Click Handling** - Multiple click zones per item
- **State Management** - Bookmark, like, star toggles
- **Error Handling** - Retry mechanisms
- **Loading States** - Skeleton screens

## 📱 Screenshots

### Light Mode

```
┌───────────────���─────────┐
│      Tech Hub           │
│  Learn • Build • Share  │
├─────────────────────────┤
│ ╔═══════════════════╗   │
│ ║  Featured Article ║   │
│ ║   [Hero Image]    ║   │
│ ║   Kotlin KMM      ║   │
│ ╚═══════════════════╝   │
│                         │
│ Trending Now    See all │
│ [Compose][KMM][Flow]    │
│                         │
│ Latest Articles         │
│ ┌───────────────────┐   │
│ │ Article Title     │   │
│ │ Summary text...   │   │
│ └───────────────────┘   │
│                         │
│ ┌───────────────────┐   │
│ │ 📹 Video Tutorial │   │
│ │ ▶️ Play • 12:34   │   │
│ └───────────────────┘   │
└─────────────────────────┘
```

## 🏗️ Project Structure

```
app/
├── src/main/java/com/hannesdorfmann/adapterdelegates4/sample/
│   ├── model/
│   │   └── ContentItem.kt          # Sealed class hierarchy
│   ├── delegates/
│   │   ├── FeaturedArticleDelegate.kt
│   │   ├── ArticleDelegate.kt
│   │   ├── VideoTutorialDelegate.kt
│   │   ├── CodeSnippetDelegate.kt
│   │   ├── LoadingDelegate.kt
│   │   └── ...
│   ├── viewmodel/
│   │   ├── MainViewModel.kt        # Business logic
│   │   └── ContentDiffCallback.kt  # DiffUtil callback
│   ├── utils/
│   │   ├── SpaceItemDecoration.kt
│   │   ├── InfiniteScrollListener.kt
│   │   └── SlideInUpAnimator.kt
│   └── MainActivity.kt              # Main screen
└── res/
    ├── layout/
    │   ├── activity_main.xml
    │   ├── item_featured_article.xml
    │   ├── item_article.xml
    │   └── ...
    └── values/
        ├── colors.xml               # Material 3 colors
        ├── themes.xml               # Day/Night themes
        └── strings.xml
```

## 💻 Code Examples

### Creating a Delegate with ViewBinding

```kotlin
fun featuredArticleDelegate(
    onItemClick: (FeaturedArticle) -> Unit
) = adapterDelegateViewBinding<FeaturedArticle, ContentItem, ItemFeaturedArticleBinding>(
    { inflater, parent -> ItemFeaturedArticleBinding.inflate(inflater, parent, false) }
) {
    binding.root.setOnClickListener { onItemClick(item) }

    bind {
        binding.titleText.text = item.title
        // ... more bindings
    }
}
```

### Creating a Simple DSL Delegate

```kotlin
fun loadingDelegate() = adapterDelegate<LoadingItem, ContentItem>(
    R.layout.item_loading
) {
    // Static content, no binding needed
}
```

### Setting Up the Adapter

```kotlin
val adapter = AsyncListDifferDelegationAdapter(
    ContentDiffCallback(),
    featuredArticleDelegate(...),
    articleDelegate(...),
    videoTutorialDelegate(...),
    loadingDelegate(),
    errorDelegate(...)
)
```

## 🎨 Customization

### Adding New View Types

1. **Define the Model**

```kotlin
data class PollItem(
    override val id: String,
    override val timestamp: Date,
    val question: String,
    val options: List<String>
) : ContentItem()
```

2. **Create the Delegate**

```kotlin
fun pollDelegate(
    onVote: (PollItem, Int) -> Unit
) = adapterDelegateViewBinding<PollItem, ContentItem, ItemPollBinding>(
    { inflater, parent -> ItemPollBinding.inflate(inflater, parent, false) }
) {
    // Setup and bind
}
```

3. **Add to Adapter**

```kotlin
val adapter = AsyncListDifferDelegationAdapter(
    // ... other delegates
    pollDelegate(...)
)
```

## 🧪 Testing

The sample includes examples of:

- Unit tests for ViewModels
- UI tests for delegates
- Integration tests for the adapter

## 📦 Dependencies

```kotlin
dependencies {
    // AdapterDelegates
    implementation(project(":library"))
    implementation(project(":kotlin-dsl"))
    implementation(project(":kotlin-dsl-viewbinding"))

    // AndroidX
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.material)

    // Architecture
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Image Loading
    implementation(libs.glide)
}
```

## 🚀 Getting Started

1. **Clone the repository**

```bash
git clone https://github.com/sockeqwe/AdapterDelegates.git
```

2. **Open in Android Studio**

```bash
cd AdapterDelegates
studio .
```

3. **Run the app**

```bash
./gradlew :app:installDebug
```

## 📝 Key Learnings

This sample demonstrates:

1. **Scalability** - Easy to add new view types
2. **Maintainability** - Each delegate is isolated
3. **Reusability** - Delegates can be shared across screens
4. **Performance** - DiffUtil for efficient updates
5. **Type Safety** - Kotlin's sealed classes and generics
6. **Clean Code** - Separation of concerns

## 🤝 Contributing

Feel free to submit issues and enhancement requests!

## 📄 License

```
Copyright 2025 Hannes Dorfmann

Licensed under the Apache License, Version 2.0
```

---

**Built with ❤️ using Kotlin and AdapterDelegates**
