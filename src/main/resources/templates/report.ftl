<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${projectName} - ${projectVersion}</title>
</head>
<body>
<style>
    body {
        font-family: Arial, sans-serif;
        background-color: #F5F5F5;
    }

    h1, h2 {
        color: #00008F;
    }

    table {
        border-collapse: collapse;
        width: 100%;
        height: 500px;
        overflow: auto;
    }

    tbody {
        display: block;
        width: 100%;
    }

    tr {
        display: table;
        width: 100%;
        table-layout: fixed;
    }

        th, td {
        border: 1px solid #00008F;
        padding: 8px;
        text-align: left;
    }

    th {
        background-color: #00008F;
        color: white;
    }

    tr:nth-child(even) {
        background-color: #D3D3D3;
    }

    tr:hover {
        background-color: #B0C4DE;
    }

    input[type="text"] {
        padding: 6px;
        margin-top: 12px;
        font-size: 17px;
        border: 1px solid #00008F;
        border-radius: 5px;
        color: #00008F;
        background-color: white;
    }

    input[type="text"]:focus {
        background-color: lightblue;
    }

    .hidden {
        visibility: hidden;
    }
</style>
<h1>${projectName} - ${projectVersion}</h1>
<h2>Maven Artifact: ${projectId}</h2>
<h1>Total Variables List</h1>
<#list totalVarsReport?keys as packageName>
    <h2>${packageName}</h2>
    <ul>
        <#list totalVarsReport[packageName] as variable>
            <li>${variable}</li>
        </#list>
    </ul>
</#list>

<h1>Variables with detailed information</h1>

<div>
    <label for="search">Search:</label>
    <input type="text" id="search" name="search" onkeyup="searchFunction()">
</div>
<br>
<br>
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

<table>
    <tr>
        <th>Package</th>
        <th>File</th>
        <th>Variable</th>
        <th>Line Number</th>
    </tr>
    <#list tableRows as row>
        <tr>
            <td>${row.packageName}</td>
            <td>${row.fileName}</td>
            <td>${row.variableName}</td>
            <td>${row.lineNumber}</td>
        </tr>
    </#list>
</table>
</body>
</html>