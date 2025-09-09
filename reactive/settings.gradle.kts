@Suppress("EnumEntryName", "unused")
enum class ImplId {
    push_pull, priority_queue,
}

rootProject.name = "reactive"

includeBuild("../foundation")

val defaultImpl = ImplId.push_pull

val implId = System.getProperty("impl") ?: defaultImpl.name

includeBuild("reactive_impl_$implId")
