pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Starlight"
include(":app")
include(":core:presentation")
include(":core:data")
include(":core:domain")
include(":core:navigation")
include(":core:network")
include(":core:util")
include(":feature:splash")
include(":feature:authorization")
include(":feature:profile")
include(":feature:search")
include(":feature:details")
