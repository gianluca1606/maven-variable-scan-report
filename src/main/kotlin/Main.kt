import html.HtmlTemplateUtil
import html.TableRowData
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File
import java.nio.file.Path


@Mojo(name = "scan")
class ScanMojo : AbstractMojo() {

    @Parameter(property = "version")
    private lateinit var version: String

    @Parameter(defaultValue = "\${project}", readonly = true, required = true)
    private lateinit var project: MavenProject

    @Parameter(property = "packageConfigs")
    private lateinit var packageConfigs: List<PackageConfig>

    @Parameter(property = "outputDirectory", defaultValue = "\${project.build.directory}/var-scanner-reports")
    private lateinit var outputDirectory: File

    override fun execute() {
        val templateData = mutableMapOf<String, Any>()
        val totalVarsReport: MutableMap<String, MutableSet<String>> = mutableMapOf()
        val tableRows = mutableListOf<TableRowData>()

        if (outputDirectory.exists()) {
            outputDirectory.deleteRecursively()
        }
        outputDirectory.mkdirs()
        packageConfigs.forEach { config ->
            val allVariablesForPackage = mutableSetOf<String>()
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
                                    allVariablesForPackage.add(variableName)
                                    tableRows.add(
                                        TableRowData(
                                            config.packageName,
                                            file.name,
                                            variableName,
                                            lineNumber
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            totalVarsReport[config.heading ?: config.packageName] = allVariablesForPackage
        }
        templateData.put("projectId", project.id)
        templateData.put("projectName", project.name)
        templateData.put("projectVersion", version ?: project.version)

        templateData.put("tableRows", tableRows)
        templateData.put("totalVarsReport", totalVarsReport)

        HtmlTemplateUtil.generate(templateData, Path.of(outputDirectory.absolutePath, "index.html"), log)
    }

}