powershell.exe -Command "Get-CimInstance -ClassName Win32_OperatingSystem | ForEach-Object { $_.LastBootUpTime.ToString('yyyy-MM-dd HH:mm:ss') }"
