group = "app.revanced"

patches {
    about {
        name = "YouTube ReVanced for Android 6-7"
        description = "RVX Patches for YouTube 17.34.36"
        source = "https://github.com/kitadai31/revanced-patches-android6-7"
        author = "kitadai31"
        contact = "https://github.com/kitadai31"
        website = "https://github.com/kitadai31"
        license = "GNU General Public License v3.0"
    }
}

dependencies {
    // Used by JsonGenerator.
    implementation(libs.gson)
}

tasks {
    jar {
        // A classes.dex from CoreLibraryDesugaring
        from("../extensions/shared/build/intermediates/desugar_lib_dex/release/classes1000.dex") {
            into("extensions")
            rename { "desugarlib.rve" }
        }
        exclude("app/revanced/generator")
    }
    register<JavaExec>("generatePatchesFiles") {
        description = "Generate patches files"

        dependsOn(build)

        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("app.revanced.generator.MainKt")
    }
    // Used by gradle-semantic-release-plugin.
    publish {
        dependsOn("generatePatchesFiles")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/inotia00/revanced-patches")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}