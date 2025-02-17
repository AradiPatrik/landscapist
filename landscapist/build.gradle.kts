/*
 * Designed and developed by 2020-2023 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.github.skydoves.landscapist.Configuration

plugins {
  id("landscapist.library.compose")
  id("landscapist.spotless")
}

rootProject.extra.apply {
  set("PUBLISH_GROUP_ID", Configuration.artifactGroup)
  set("PUBLISH_ARTIFACT_ID", "landscapist")
  set("PUBLISH_VERSION", rootProject.extra.get("rootVersionName"))
}

apply(from = "${rootDir}/scripts/publish-module.gradle")

android {
  namespace = "com.skydoves.landscapist"
  compileSdk = Configuration.compileSdk
  defaultConfig {
    minSdk = Configuration.minSdk
  }
  publishing {
    singleVariant("release")
  }
}

baselineProfile {
  filter {
    include("com.skydoves.landscapist.**")
    exclude("com.skydoves.landscapist.glide.**")
    exclude("com.skydoves.landscapist.fresco.**")
    exclude("com.skydoves.landscapist.coil.**")
    exclude("com.skydoves.landscapist.animation.**")
    exclude("com.skydoves.landscapist.palette.**")
    exclude("com.skydoves.landscapist.placeholder.**")
    exclude("com.skydoves.landscapist.transformation.**")
    exclude("com.skydoves.benchmark.**")
  }
}


dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.kotlinx.coroutines.android)
}
