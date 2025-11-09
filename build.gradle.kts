// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.kover) apply false
    id("com.diffplug.spotless") version "6.25.0" apply false
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**/*.kt")
            ktlint("1.0.1")
                .editorConfigOverride(
                    mapOf(
                        "indent_size" to "4",
                        "max_line_length" to "120",
                        "ktlint_standard_no-wildcard-imports" to "disabled",
                        "ktlint_standard_filename" to "disabled"
                    )
                )
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
        }

        format("kts") {
            target("**/*.kts")
            targetExclude("**/build/**/*.kts")
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"), "(^(?![\\/ ]\\**).*\$)")
        }

        format("xml") {
            target("**/*.xml")
            targetExclude("**/build/**/*.xml")
            licenseHeaderFile(rootProject.file("spotless/copyright.xml"), "(<[^!?])")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
