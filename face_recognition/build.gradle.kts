plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
}

android {
    namespace = "com.athena.face_recognition"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        targetSdk = 33
        //9. 앱모듈 설정

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.core.ktx)
//    implementation(libs.appcompat)
//    implementation(libs.material)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.test.ext.junit)
//    androidTestImplementation(libs.espresso.core)

    //10. 의존성을 추가한다. camera core 등
    val camerax_version : String = "1.2.1"
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")
    implementation("androidx.camera:camera-extensions:${camerax_version}")

    //11. 이후에 camera class를 하나 만든다.

    //27. ML Kit : 얼굴 인식을 위한 기기 내 머신러닝 Vision API를 제공
    //ML Kit는 얼굴 인식하고 윤곽을 가져오지만, 사람을 인식하진 못한다.
    //그래서 사람인지 아닌지부터 판단해야한다.
    //얼굴 특징 인식 및 위치찾기, 윤곽 가져오기, 표정인식, 동영상 프레임에서 얼굴 추적
    implementation("com.google.mlkit:face-detection:16.1.5")

}













