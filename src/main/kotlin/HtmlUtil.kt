import java.io.File

object HtmlUtil {
    private fun generateHeading(heading: String): String {
        return "<h2>$heading</h2>"
    }

    private fun generateSearchField(): String {
        return """
        <div>
            <label for="search">Search:</label>
            <input type="text" id="search" name="search" onkeyup="searchFunction()">
        </div>
        <script>
            function searchFunction() {
                var input, filter, table, tr, td, i, txtValue;
                input = document.getElementById("search");
                filter = input.value.toUpperCase();
                table = document.getElementsByTagName("table")[0];
                tr = table.getElementsByTagName("tr");
                for (i = 1; i < tr.length; i++) {
                    td = tr[i].getElementsByTagName("td")[2]; // Change 2 to the column index of the variable name
                    if (td) {
                        txtValue = td.textContent || td.innerText;
                        if (txtValue.toUpperCase().indexOf(filter) > -1) {
                            tr[i].style.display = "";
                        } else {
                            tr[i].style.display = "none";
                        }
                    }
                }
            }
        </script>
    """.trimIndent()
    }

    private fun generateTableHeading(): String {
        return """
        <table>
            <tr><th>Package</th><th>File</th><th>Variable</th><th>Line Number</th></tr>
    """.trimIndent()
    }

    private fun generateRow(packageName: String, fileName: String, variableName: String, lineNumber: Int): String {
        return """
        <tr><td>$packageName</td><td>$fileName</td><td>$variableName</td><td>$lineNumber</td></tr>
    """.trimIndent()
    }

    private fun generateClosingTableTag(): String {
        return "</table>"
    }

    private fun generateCssStyles(): String {
        return """
        <style>
            table {
                border-collapse: collapse;
                width: 100%;
            }
            th, td {
                border: 1px solid black;
                padding: 8px;
                text-align: left;
            }
            th {
                background-color: #f2f2f2;
            }
        </style>
    """.trimIndent()
    }

    private fun appendContentToFile(content: String, outputFile: File) {
        outputFile.appendText(content)
    }

    private fun generateDetailedReportContent(
        packageName: String,
        heading: String,
        fileName: String,
        variableName: String,
        lineNumber: Int
    ): String {
        val cssStylesHtml = generateCssStyles()
        val headingHtml = generateHeading(heading)
        val searchFieldHtml = generateSearchField()
        val tableHeadingHtml = generateTableHeading()
        val rowHtml = generateRow(packageName, fileName, variableName, lineNumber)
        //val closingTableTagHtml = generateClosingTableTag()

        return "$cssStylesHtml$headingHtml$searchFieldHtml$tableHeadingHtml$rowHtml"
    }

     fun generateReport(
        packageName: String,
        heading: String,
        fileName: String,
        variableName: String,
        lineNumber: Int,
        outputDirectory: File
    ) {
        val reportFileName = "${packageName.replace('.', '_')}_report.html"
        val reportFile = File(outputDirectory, reportFileName)
        val reportContent = generateDetailedReportContent(packageName, heading, fileName, variableName, lineNumber)
        if (!reportFile.exists()) {
            appendContentToFile(reportContent, reportFile)
        } else {
            // Append to existing report file
            val rowHtml = generateRow(packageName, fileName, variableName, lineNumber)
            appendContentToFile(rowHtml, reportFile)
        }
    }
}