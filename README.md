# Shop List

A native Android app for managing shopping lists: organize products by category, track recurring shopping lists that regenerate themselves on a schedule, quickly re-add items you buy often, and clone an existing list to start a new trip.

## Features

- **Shopping lists** — create, rename, delete; each list tracks item count and checked-off progress.
- **Categories & products** — group products into categories (e.g. "Produce", "Dairy"); browse products by category.
- **List items** — add products to a list with quantity/unit, grouped and collapsible by category, check items off as you shop.
- **Frequent items** — one-tap "quick add" chips for products bought often (computed from the last 90 days of purchase history), so you don't have to search for staples every time.
- **Clone list** — duplicate an existing list (including its items) into a new one.
- **Recurring lists** — mark a list as a template with an interval (daily/weekly/monthly). A background job checks for due templates and automatically generates a fresh copy of the list, so a weekly grocery run doesn't need to be rebuilt by hand every week.
- **Priority flag** — each item has a Low/Normal/High priority (tap the flag icon to cycle it); items are shown highest-priority first, and same-priority items keep the order you added them in — useful for sequencing a routine (e.g. a habit tracker) rather than always alphabetizing.
- **A-Z fast-scroll index** — a Contacts-app-style letter rail on the Categories and Category-Products screens for jumping straight to a section.
- **Light/dark mode** — follows the system setting automatically, no in-app toggle needed.
- **Multi-language** — English, Spanish, French, and Portuguese, with an in-app language picker (Settings, via the gear icon on "My Lists") that overrides the system locale for this app only and persists across launches.
- **Starter data** — a first launch comes pre-loaded with 9 categories, their products, and 8 ready-to-use shopping lists (see [Starter data](#starter-data) below) so the app isn't empty out of the box.

## Tech stack

| Concern | Choice |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Persistence | Room (SQLite) |
| Dependency injection | Hilt |
| Background work | WorkManager |
| Navigation | Navigation Compose |
| Async | Kotlin Coroutines + Flow |
| Build | Gradle Kotlin DSL, version catalog (`gradle/libs.versions.toml`) |

## Architecture

Clean Architecture in a single Gradle module, organized by layer rather than by feature, so `domain` stays free of any Android/Room/Compose imports:

```
app/src/main/java/com/shoplist/app/
  ShopListApp.kt           # @HiltAndroidApp, schedules the recurring-list worker on startup
  MainActivity.kt          # single Activity hosting the Compose NavHost

  di/                      # Hilt modules (Database, Repository, Dispatcher)

  data/
    local/                 # Room entities, DAOs, AppDatabase, TypeConverters
    repository/            # Repository implementations
    mapper/                # Entity <-> domain model mapping

  domain/
    model/                 # Plain Kotlin domain models
    repository/            # Repository interfaces (implemented by data/)
    usecase/                # One class per action, grouped by feature: list, item, category, product, recurrence

  presentation/
    navigation/            # Nav routes and the NavHost graph
    theme/                 # Material 3 theme, color, typography
    common/                # Shared composables (EmptyState, ConfirmDeleteDialog, QuantityStepper, LoadingIndicator)
    shoplists/              # Active shopping lists screen
    listdetail/             # Items within a list (+ AddItemSheet, FrequentChipsRow, CategoryHeader, ItemRow)
    categories/              # Category CRUD
    categoryproducts/        # Products filtered by category
    recurring/                # Manage recurring templates
    settings/                 # Language picker

  worker/                  # WorkManager: RecurringListCheckWorker + WorkScheduler
```

Multi-module was considered and intentionally rejected: for an app this size, module boundaries add Gradle overhead without a real payoff, while keeping `domain` free of framework imports already gives the separation-of-concerns benefit clean architecture is for.

### Data model

- `Category` — name.
- `Product` — belongs to a `Category`, has an optional default unit.
- `ShoppingList` — a list is either a normal active list or a **recurring template** (`isRecurringTemplate`). Templates carry `recurrenceInterval` and `nextDueAt` and are hidden from the main "My Lists" screen. Every list produced by cloning (manual or automatic) sets `clonedFromListId` back to its source, so generated lists can be traced to their template.
- `ShoppingListItem` — a product on a list, with quantity, unit, note, checked state, and a `priority` (Low/Normal/High).

### Key design decisions

- **Cloning is centralized.** `ShoppingListRepositoryImpl` has one internal `copyList()` routine used by both the manual "Clone list" action (`cloneList()`) and the recurring-regeneration path (`regenerateFromTemplate()`), so the copy logic exists exactly once.
- **Recurring regeneration is atomic.** `regenerateFromTemplate()` clones the list *and* advances the template's `nextDueAt` inside a single Room transaction (`AppDatabase.withTransaction`), so a worker run that's killed mid-way can't leave a template "due but already generated" — which would otherwise produce a duplicate list on the next retry.
- **`nextDueAt` advances from the due timestamp, not from "now"**, to avoid drift across repeated runs.
- **Frequent items** are derived from purchase history (`ShoppingListItemDao.getFrequentProducts`) rather than a separate favorites table — grouped by product, ordered by purchase count then recency, bounded to the last 90 days, and excluding template lists (a template isn't purchase history).
- **UDF in the presentation layer.** Each screen has a `HiltViewModel` exposing a single immutable `UiState` via `StateFlow`; the UI calls plain ViewModel methods for events. ViewModels hold no `Context`/`Resources`.
- **Flat SQL joins over `@Relation`.** DAO queries that need related data (e.g. an item's product/category name) use a JOIN returning a flat row class, avoiding Room's N+1-prone `@Relation` graphs.
- **Every user-facing string lives in `res/values*/strings.xml`** (no hardcoded `Text("...")` in Compose code), which is what makes the language picker possible — see [Localization](#localization) below.
- **Item ordering is priority-first, then insertion sequence — not alphabetical.** `getItemsForList` sorts by `priority`, then by the row's auto-increment `id` (which is already a perfect proxy for "the order you added things in", so no separate `sortOrder` column was needed). Categories/products elsewhere (Categories screen, product catalog) still sort alphabetically — that distinction is deliberate: a product *catalog* is browsed alphabetically, but the *items on a given list* (especially a routine like a habit tracker) usually need to stay in the sequence you built them in.
- **Schema changes go through real `Migration` objects, not `fallbackToDestructiveMigration()`.** Adding the `priority` column bumped the DB version and added an `ALTER TABLE` migration (`Migrations.kt`) so existing installs upgrade in place instead of losing data.

## Localization

All UI strings are externalized to `strings.xml` and translated into English (default), Spanish (`values-es`), French (`values-fr`), and Portuguese (`values-pt`). Language can be changed two ways:

- **System locale** (default): the app just follows whatever language the phone is set to, falling back to English if unsupported.
- **In-app override**: Settings (gear icon on "My Lists") → Language. Selecting a language calls `AppCompatDelegate.setApplicationLocales()`, which overrides the locale for this app only. This requires `MainActivity` to extend `AppCompatActivity` (not a plain `ComponentActivity`) — that's what makes the automatic locale-change activity recreation work reliably pre-API 33 as well as on it. The choice is persisted automatically across app restarts via appcompat's `autoStoreLocales` (see the `AppLocalesMetadataHolderService` entry in `AndroidManifest.xml`), no custom storage code needed.

Adding a new language: create `res/values-<lang>/strings.xml` with the same keys as `res/values/strings.xml`, add the locale to `res/xml/locales_config.xml`, and add it to the option list in `presentation/settings/SettingsScreen.kt`.

## Starter data

`data/local/DatabaseSeeder.kt` populates the database the first time it's created, via a `RoomDatabase.Callback.onCreate` wired up in `di/DatabaseModule.kt`. It only ever runs once against a brand-new database file — it never touches an existing install, and a schema migration (like the `priority` column) does **not** re-trigger it. The callback receives the `AppDatabase` through a Dagger `Provider<AppDatabase>` rather than a direct injection, specifically so it can be constructed *inside* the same `@Provides` method that builds the database, without Dagger seeing a circular dependency — the provider is only resolved later, once the singleton already exists.

Seeded content:

- **9 categories**: Supermarket, Stationery, DIY and tools, Work tasks, Homework, DIY, Upper Body Workout routine, Lower Body Workout routine, Habit tracker.
- **Their products** — including the full Supermarket/Stationery/workout catalogs. Two things worth calling out: `Homework` has both "Homework 1" and "Homework 2" as distinct products, and the Habit tracker's "Work" task is a single catalog product that gets added to a day's list twice (there's no uniqueness constraint on how many times a product appears within one list — only on a product's name within its category), which is how a task can legitimately occur more than once in a day.
- **8 shopping lists**: "Frequent supermarket" plus a Monday–Sunday habit-tracker routine, each with its items in the exact sequence given — which is exactly what the priority-then-insertion-order sort in the previous section is designed to preserve.

## Requirements

- Android Studio (or the command-line SDK tools) with:
  - JDK 17
  - Android SDK Platform 35 (compileSdk/targetSdk)
  - Android SDK Build-Tools
- **Minimum OS version:** Android 8.0 (API 26)
- No external services or API keys — the app is fully local/offline, backed by an on-device Room database.

## Building & running

```bash
./gradlew assembleDebug
```

Open the project in Android Studio and run the `app` configuration, or install the built APK directly:

```bash
./gradlew installDebug
```

## Testing

```bash
./gradlew test                    # unit tests (use cases, pure Kotlin, fake repositories)
./gradlew connectedAndroidTest    # instrumented tests (Room DAOs, in-memory database) - requires a device/emulator
```

Coverage is intentionally light and representative rather than exhaustive: a couple of use-case unit tests (`CloneShoppingListUseCaseTest`, `ComputeNextDueDateUseCaseTest`) and a Room DAO test (`ShoppingListItemDaoTest`) covering item ordering and the frequent-products query.

## App icon

The launcher icon (`app/src/main/res/drawable/ic_launcher_*.xml`) is an adaptive icon: a green gradient background with a white shopping-cart glyph, chosen to make the app's purpose recognizable at a glance in the launcher and app drawer.
