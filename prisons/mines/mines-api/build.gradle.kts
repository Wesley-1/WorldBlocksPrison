setupShadowJar()

dependencies {
    compileOnly(Dependencies.LOMBOK)
    compileOnly(Dependencies.SPIGOT)
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.REFLECTIONS)
    compileOnly(Dependencies.LUCKO_HELPER)
    compileOnly(Dependencies.LUCKO_SQL)
    compileOnly(Dependencies._SPIGOT)
}