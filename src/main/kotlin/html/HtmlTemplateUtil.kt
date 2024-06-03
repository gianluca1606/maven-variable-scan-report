package html

import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import org.apache.maven.plugin.logging.Log
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object HtmlTemplateUtil {

    fun generate(data: Map<String, Any>, targetPath: Path, log: Log): Path {
        val template = Configuration(Configuration.VERSION_2_3_33).apply {
            setClassForTemplateLoading(this::class.java, "/templates")
            defaultEncoding = "UTF-8"
            templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
            logTemplateExceptions = true
            wrapUncheckedExceptions = true
        }.getTemplate("report.ftl")

        val finalData = data.toMutableMap()
        finalData["generatedBy"] = "report-plugin"
        finalData["generatedDate"] = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)

        val stringWriter = StringWriter()
        template.process(finalData, stringWriter)

        if (Files.notExists(targetPath.parent)) {
            Files.createDirectories(targetPath.parent)
        }
        log.info("Writing report to: $targetPath")
        return Files.write(targetPath, stringWriter.toString().toByteArray())
    }
}