# AGENTS.md - AdapterDelegates Project Guide

## Project Overview

**AdapterDelegates** is a modern Android library written in 100% Kotlin that enables building RecyclerView adapters
through composition rather than inheritance. The library provides a clean, type-safe way to handle multiple view types
in RecyclerView by composing reusable adapter delegates.

### Core Principle

> **Favor composition over inheritance**

Each view type gets its own `AdapterDelegate` responsible for creating and binding ViewHolders. These delegates are then
composed together via an `AdapterDelegatesManager` to build complete adapters.

---

## Project Structure

```
AdapterDelegates/
├── library/                          # Core library module
│   └── src/main/java/.../adapterdelegates4/
│       ├── AdapterDelegate.kt        # Base delegate interface
│       ├── AdapterDelegatesManager.kt # Manages multiple delegates
│       ├── AbsListItemAdapterDelegate.kt
│       ├── ListDelegationAdapter.kt  # List-based adapter
│       └── AsyncListDifferDelegationAdapter.kt # DiffUtil support
│
├── kotlin-dsl/                       # Kotlin DSL for delegates
│   └── src/main/java/.../dsl/
│       └── ListAdapterDelegateDsl.kt # adapterDelegate() DSL
│
├── kotlin-dsl-viewbinding/           # ViewBinding DSL support
│   └── src/main/java/.../dsl/
│       └── ViewBindingListAdapterDelegateDsl.kt
│
├── paging/                           # Paging library support
│   └── src/main/java/.../paging/
│       └── PagedListDelegationAdapter.kt
│
└── app/                              # Sample application
    └── src/main/java/.../sample/
        ├── model/                    # Data models
        ├── delegates/                # Adapter delegates
        └── viewmodel/                # ViewModels & DiffCallbacks
```

---

## Key Concepts

### 1. AdapterDelegate

The core abstraction that handles a specific view type:

- `isForViewType()`: Determines if this delegate handles the item
- `onCreateViewHolder()`: Creates the ViewHolder
- `onBindViewHolder()`: Binds data to the ViewHolder
- Optional lifecycle hooks: `onViewRecycled()`, `onViewAttachedToWindow()`, etc.

### 2. AdapterDelegatesManager

Manages multiple delegates and routes calls to the appropriate delegate based on view type.

### 3. Delegation Adapters

Pre-built adapter implementations:

- `ListDelegationAdapter`: For `List<T>` data sources
- `AsyncListDifferDelegationAdapter`: Integrates DiffUtil with AsyncListDiffer
- `PagedListDelegationAdapter`: For Paging library support

---

## Development Guidelines

### Code Style & Conventions

1. **Language**: 100% Kotlin
2. **Async Operations**: Use Kotlin coroutines, **never** `runBlocking`
3. **Commit Messages**: Semantic commits
    - `feat:` - New features
    - `fix:` - Bug fixes
    - `refactor:` - Code restructuring
    - `docs:` - Documentation changes
4. **Documentation**:
    - KDoc for all public APIs
    - `@JvmField` for Java interoperability where needed
5. **Testing**: Unit tests for all public APIs using JUnit, MockK, and Truth

### Build Configuration

- **Gradle**: Kotlin DSL (`.kts`)
- **Version Catalog**: `gradle/libs.versions.toml` for dependency management
- **Plugins**:
    - Spotless: Code formatting (ktlint 1.0.1)
    - Kover: Code coverage
    - Maven Publish: Library publishing

### Code Formatting

The project uses Spotless with ktlint. All code must:

- Follow `.editorconfig` rules
- Include license headers (see `spotless/copyright.kt`)
- Disabled rules: `no-consecutive-comments`, `max-line-length`, `property-naming`

Run formatting:

```bash
./gradlew spotlessApply
```

Check formatting:

```bash
./gradlew spotlessCheck
```

---

## Common Use Cases

### Creating a Simple Adapter Delegate (Kotlin DSL)

```kotlin
fun catAdapterDelegate(onClick: (Cat) -> Unit) = adapterDelegate<Cat, Animal>(
    R.layout.item_cat
) {
    // Initialization block (called in onCreateViewHolder)
    val name: TextView = findViewById(R.id.name)
    name.setOnClickListener { onClick(item) }

    // Binding block (called in onBindViewHolder)
    bind { payloads ->
        name.text = item.name
    }
}
```

### Creating a ViewBinding Adapter Delegate

```kotlin
fun catAdapterDelegate(onClick: (Cat) -> Unit) = adapterDelegateViewBinding<Cat, Animal, ItemCatBinding>(
    { inflater, parent -> ItemCatBinding.inflate(inflater, parent, false) }
) {
    binding.root.setOnClickListener { onClick(item) }

    bind {
        binding.name.text = item.name
    }
}
```

### Custom View Type Check

```kotlin
adapterDelegate<Cat, Animal>(
    layout = R.layout.item_cat,
    on = { item, items, position ->
        item is Cat && position == 0 // Custom logic
    }
) {
    bind { /* ... */ }
}
```

### Composing the Adapter

```kotlin
val adapter = AsyncListDifferDelegationAdapter(
    diffCallback,
    catAdapterDelegate(::onCatClick),
    dogAdapterDelegate(::onDogClick),
    loadingDelegate()
)

recyclerView.adapter = adapter
adapter.items = listOfAnimals
```

---

## Module Responsibilities

### `library`

Core functionality:

- Base `AdapterDelegate` interface
- `AdapterDelegatesManager` implementation
- `ListDelegationAdapter` and `AsyncListDifferDelegationAdapter`
- Helper classes like `AbsListItemAdapterDelegate`

### `kotlin-dsl`

Provides the `adapterDelegate()` DSL function for creating delegates with a clean Kotlin syntax.

### `kotlin-dsl-viewbinding`

Provides the `adapterDelegateViewBinding()` DSL for delegates using ViewBinding/DataBinding.

### `paging`

Integration with AndroidX Paging library via `PagedListDelegationAdapter`.

### `app`

Comprehensive sample application demonstrating:

- Multiple view types (13+ different types)
- MVVM architecture with ViewModels
- DiffUtil integration
- Kotlin coroutines
- Material Design 3
- Dark mode support

---

## Testing Strategy

### Unit Tests

- Test delegate selection logic (`isForViewType`)
- Test manager routing
- Test adapter state management
- Use MockK for mocking, Truth for assertions

### Integration Tests

- Test full adapter behavior
- Test DiffUtil calculations
- Test pagination scenarios

### Test Location

- `src/test/` - Unit tests (JVM)
- `src/androidTest/` - Instrumented tests (requires device/emulator)

---

## Publishing & Versioning

### Version Configuration

Located in `gradle.properties`:

```properties
VERSION_NAME=4.3.3-SNAPSHOT
VERSION_CODE=433-SNAPSHOT
GROUP=com.hannesdorfmann
```

### Maven Coordinates

```
com.hannesdorfmann:adapterdelegates4:4.3.3-SNAPSHOT
com.hannesdorfmann:adapterdelegates4-kotlin-dsl:4.3.3-SNAPSHOT
com.hannesdorfmann:adapterdelegates4-kotlin-dsl-viewbinding:4.3.3-SNAPSHOT
com.hannesdorfmann:adapterdelegates4-pagination:4.3.3-SNAPSHOT
```

---

## Important Notes for AI Agents

### When Making Changes

1. **Never break public API**: This is a library used by external projects
2. **Always add tests**: New features require unit tests
3. **Update documentation**: Keep README.md and KDocs current
4. **Run checks**: Execute `./gradlew check` before committing
5. **Format code**: Run `./gradlew spotlessApply`
6. **Maintain Kotlin 2.x compatibility**: Project uses Kotlin 2.1.0
7. **Support minSdk 24**: Don't use APIs below Android 7.0

### Sync Gradle After Changes

**CRITICAL**: Always run Gradle sync after modifying:

- `build.gradle.kts` files
- `gradle/libs.versions.toml`
- Any dependency or plugin changes

Use the `sync_gradle` tool immediately after configuration changes.

### Common Patterns

#### Pattern 1: Function vs Val for Delegates

✅ **Prefer functions** (allows garbage collection):

```kotlin
fun catDelegate() = adapterDelegate<Cat, Animal> { /* ... */ }
```

❌ **Avoid top-level vals** (kept in memory forever):

```kotlin
val catDelegate = adapterDelegate<Cat, Animal> { /* ... */ }
```

#### Pattern 2: Type-Safe Delegates

Always use typed delegates with generics:

```kotlin
// I: Item type, T: Dataset type
adapterDelegate<I, T>(layout) { /* ... */ }
```

#### Pattern 3: DiffUtil Integration

Use `AsyncListDifferDelegationAdapter` with proper DiffUtil callbacks:

```kotlin
class MyDiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(old: Item, new: Item) = old.id == new.id
    override fun areContentsTheSame(old: Item, new: Item) = old == new
    override fun getChangePayload(old: Item, new: Item): Any? = /* partial updates */
}
```

### Debugging Tips

1. **ViewType conflicts**: Check `isForViewType()` logic doesn't overlap
2. **ClassCastException**: Verify generic types match in delegates
3. **Items not updating**: Ensure DiffUtil callback is correct
4. **Memory leaks**: Check lifecycle callbacks are balanced
5. **Performance issues**: Use payloads in `getChangePayload()` for partial updates

---

## Build Commands

### Essential Commands

```bash
# Clean build
./gradlew clean

# Build all modules
./gradlew build

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Check code style
./gradlew spotlessCheck

# Format code
./gradlew spotlessApply

# Generate coverage report
./gradlew koverHtmlReport

# Publish to Maven Local
./gradlew publishToMavenLocal
```

### Module-Specific Commands

```bash
# Build specific module
./gradlew :library:build
./gradlew :kotlin-dsl:build

# Test specific module
./gradlew :library:test
./gradlew :paging:test

# Run sample app
./gradlew :app:installDebug
```

---

## Resources & References

- **Main Repository**: https://github.com/sockeqwe/AdapterDelegates
- **Blog Post**: http://hannesdorfmann.com/android/adapter-delegates
- **License**: Apache 2.0
- **Author**: Hannes Dorfmann

### Key Dependencies

- AndroidX RecyclerView 1.3.2
- AndroidX Paging 3.3.5
- Kotlin 2.1.0
- Coroutines 1.9.0

---

## Migration Notes

### From Version 3.x to 4.x

- Group ID changed: `adapterdelegates3` → `adapterdelegates4`
- AndroidX migration: Use `androidx.recyclerview` instead of `android.support.v7`
- Use find & replace for package names

### Kotlin 2.x Compatibility

- `kotlin-dsl-layoutcontainer` module temporarily disabled
- Kotlin Android Extensions removed (deprecated)
- Use ViewBinding instead

---

## FAQ for Agents

**Q: How do I add a new view type?**
A: Create a new adapter delegate function and add it to your adapter composition.

**Q: Can delegates share ViewHolder logic?**
A: Yes, use `AbsListItemAdapterDelegate` as a base class for common logic.

**Q: How do I handle click events?**
A: Pass click listeners as parameters to delegate functions and set them in the initialization block.

**Q: What if no delegate matches an item?**
A: Use `setFallbackDelegate()` on the manager, or the app will throw an exception.

**Q: How do I optimize for performance?**
A: Use `AsyncListDifferDelegationAdapter` with proper DiffUtil callbacks and implement `getChangePayload()`.

**Q: Can I use this with Jetpack Compose?**
A: No, this library is for RecyclerView. Consider LazyColumn for Compose.

---

## Contact & Support

For issues, questions, or contributions:

- **Issues**: https://github.com/sockeqwe/AdapterDelegates/issues
- **Releases**: https://github.com/sockeqwe/AdapterDelegates/releases

---

*Last Updated: 2025 - Based on version 4.3.3-SNAPSHOT*
