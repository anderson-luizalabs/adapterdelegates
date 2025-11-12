// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.spotless) apply false
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

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

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
