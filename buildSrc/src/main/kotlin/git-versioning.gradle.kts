val gitVersion = providers
    .exec { commandLine("git", "describe", "--tags", "--always") }
    .standardOutput.asText.map { it.trim().removePrefix("v") }

extensions.add(typeOf<Provider<String>>(), "gitVersion", gitVersion)

allprojects {

    version = gitVersion.get()

}
