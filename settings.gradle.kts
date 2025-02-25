pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://repo.sendbird.com/public/maven") }
    }
}

rootProject.name = "KURing"
include(
    ":app",
    ":core:util",
    ":core:ui_util",
    ":data:domain",
    ":data:domain:testUtils",
    ":data:notice",
    ":data:notice:test",
    ":core:preferences",
    ":data:ai",
    ":data:push",
    ":data:push:test",
    ":data:user",
    ":data:staff",
    ":data:department",
    ":data:department:test",
    ":data:local",
    ":data:local:test",
    ":data:remote",
    ":data:space",
    ":data:search",
    ":core:thirdparty",
    ":core:work",
    ":core:testUtil",
    ":feature:edit_subscription",
    ":feature:feedback",
    ":feature:notice_detail",
    ":feature:notion",
    ":feature:onboarding",
    ":feature:splash",
    ":feature:main",
    ":core:designsystem",
    ":feature:kuringbot",
    ":feature:edit_departments"
)
