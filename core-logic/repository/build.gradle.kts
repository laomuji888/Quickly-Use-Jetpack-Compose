plugins {
    alias(libs.plugins.laomuji1999.compose.library)
    alias(libs.plugins.laomuji1999.compose.serialization)
    alias(libs.plugins.laomuji1999.compose.hilt)
}

android {
    namespace = "com.laomuji1999.compose.core.logic.repository"
}

dependencies {

    //project
    implementation(project(":core-logic:common"))
    api(project(":core-logic:model"))
    implementation(project(":core-logic:network:http"))
    api(project(":core-logic:database"))
    implementation(project(":core-logic:notification"))

    //firebase
    implementation(platform(libs.firebase.bom))

    //google ai
    implementation(libs.firebase.ai)

    testImplementation(project(":core-logic:common"))
    testImplementation(testFixtures(project(":core-logic:common")))
    testImplementation(libs.junit4)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.ktor.client.mock)
}
