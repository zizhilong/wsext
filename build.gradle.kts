plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.24"
  id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.example"
version = "1.1-SNAPSHOT"

repositories {
  mavenCentral()
  flatDir {
    dirs("libs")
  }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set("2023.3.7")
  type.set("IU") // Target IDE Platform

  plugins.set(listOf("JavaScript","DatabaseTools"))
  //ideaDependencyCachePath.set(file("D:\\Program Files\\JetBrains\\WebStorm 2023.3.7"))
}
dependencies {

  //compileOnly(files("D:\\Program Files\\JetBrains\\WebStorm 2023.3.7\\plugins\\javascript-intentions\\lib\\javascript-intentions.jar"))
  compileOnly(files("E:\\WebStorm 2023.3.7\\plugins\\javascript-intentions\\lib\\javascript-intentions.jar"))
  implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")
  implementation("com.fasterxml.jackson.core:jackson-core:2.12.3")
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.3")
  implementation("io.javalin:javalin:6.1.6")

  implementation("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:1.8.21")
}
tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
    options.encoding = "UTF-8"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
  }
  runIde {
    // 指定 WebStorm 的安装目录
    //ideDir.set(file("D:\\Program Files\\JetBrains\\WebStorm 2023.3.7"))
    ideDir.set(file("E:\\WebStorm 2023.3.7"))
  }
  patchPluginXml {
    sinceBuild.set("233")
    untilBuild.set("242.*")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
  withType<ProcessResources> {
    from(sourceSets.main.get().resources.srcDirs) {
      include("**/*.properties")
      filteringCharset = "UTF-8"
    }
  }
}
