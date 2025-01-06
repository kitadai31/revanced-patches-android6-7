import java.lang.Boolean.TRUE

extension {
    name = "extensions/shared.rve"
}

android {
    namespace = "app.revanced.extension"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = TRUE
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    compileOnly(libs.annotation)
    compileOnly(libs.preference)
    implementation(libs.collections4)
    implementation(libs.lang3)

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.2.2")
    compileOnly(project(":extensions:shared:stub"))
}
