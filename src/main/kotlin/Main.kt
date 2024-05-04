import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import java.io.File


@Mojo(name = "scan")
class ScanMojo : AbstractMojo() {

    @Parameter(property = "packageConfigs")
    private lateinit var packageConfigs: List<PackageConfig>

    @Parameter(property = "outputDirectory", defaultValue = "\${project.build.directory}/reports")
    private lateinit var outputDirectory: File

    override fun execute() {
        val generalVarsReport: MutableMap<String, MutableSet<String>> = mutableMapOf()

        if (outputDirectory.exists()) {
            outputDirectory.deleteRecursively()
        }
        outputDirectory.mkdirs()
        packageConfigs.forEach { config ->
            val setForPackage = mutableSetOf<String>()
            val pkgDir = File("src/main/java", config.packageName.replace('.', '/'))
            if (!pkgDir.exists()) {
                log.warn("Package directory not found: ${config.packageName}")
                return
            }
            log.info("Scanning package: ${config.packageName}")
            pkgDir.walkTopDown().filter { it.isFile && (it.extension == "java" || it.extension == "kt") }
                .forEach { file ->
                    var lineNumber = 0 // Track line number manually
                    config.prefixes.forEach { prefix ->
                        val pattern = Regex("""\b$prefix\w+\s*=\s*"[^"]*"|\b$prefix\w+\s*=\s*\w+""")

                        file.forEachLine { line ->
                            lineNumber++
                            pattern.find(line)?.let { matchResult ->
                                val capturedString = matchResult.groupValues[0]
                                val parts = capturedString.split("=")
                                if (parts.size >= 2) {
                                    val variableName = parts[0].trim() // Extract and trim variable name
                                    setForPackage.add(variableName)
                                    HtmlUtil.generateReport(
                                        config.packageName,
                                        config.heading ?: config.packageName,
                                        file.name,
                                        variableName,
                                        lineNumber,
                                        outputDirectory
                                    )
                                }
                            }
                        }
                    }
                }
            generalVarsReport[config.packageName] = setForPackage
        }
    }

}