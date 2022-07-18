rootProject.name = "WorldBlocksPrison"

setupPrisonsModule(
            "prisons",
        listOf(
            Pair("mines", listOf("mines-api", "mines-impl")),
            Pair("pickaxes", listOf("enchants-api", "pickaxe-api", "pickaxe-impl")),
            Pair("masks", listOf("masks-api", "masks-impl"))))
           
fun setupPrisonsModule(base: String, setup: List<Pair<String, List<String>>>) =
    setup.forEach { pair
        -> pair.second.forEach { name
            -> setupSubproject(name, file("$base/${pair.first}/$name"))
        }
    }


fun setupSubproject(name: String, projectDirectory: File) = setupSubproject(name) {
    projectDir = projectDirectory
}

inline fun setupSubproject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}