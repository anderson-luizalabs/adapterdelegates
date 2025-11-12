// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.dokka)
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "org.jetbrains.dokka")

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**/*.kt", "**/test/**/*.kt", "**/*Test.kt")
            ktlint("1.0.1")
                .setEditorConfigPath("$rootDir/.editorconfig")
                .editorConfigOverride(
                    mapOf(
                        "ktlint_disabled_rules" to "no-consecutive-comments,max-line-length,property-naming"
                    )
                )
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
        }

        kotlinGradle {
            target("**/*.kts")
            targetExclude("**/build/**/*.kts")
            ktlint("1.0.1")
                .setEditorConfigPath("$rootDir/.editorconfig")
        }

        format("xml") {
            target("**/*.xml")
            targetExclude("**/build/**/*.xml", "**/res/**/*.xml")
        }
    }
}

// Aggregate documentation from all subprojects
dependencies {
    dokka(project(":library"))
    dokka(project(":paging"))
    dokka(project(":kotlin-dsl"))
    dokka(project(":kotlin-dsl-viewbinding"))
}

// Configure the output directory for the aggregated documentation
dokka {
    dokkaPublications.html {
        outputDirectory.set(rootDir.resolve("docs/api"))
    }
}
