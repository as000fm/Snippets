Use PowerShell’s `Get-Content` with `-Wait` (and optionally `-Tail`) — that’s the `tail -f` equivalent.

**Basic follow (like `tail -f`)**

```powershell
Get-Content "C:\path\to\myapp.log" -Wait
```

**Show the last 200 lines and keep following**

```powershell
Get-Content "C:\path\to\myapp.log" -Tail 200 -Wait
```

**UTF-8 logs (avoid mojibake)**

```powershell
Get-Content "C:\path\to\myapp.log" -Tail 200 -Wait -Encoding UTF8
```

**Multiple files at once**

```powershell
Get-Content "C:\logs\app1.log","C:\logs\app2.log" -Tail 50 -Wait
```

**For Tomcat/tcServer logs (today’s catalina file)**

```powershell
Get-Content "$env:CATALINA_BASE\logs\catalina.$(Get-Date -Format yyyy-MM-dd).log" -Tail 100 -Wait
```

> Stop following with **Ctrl+C**.

### (Optional) follow “latest file” for daily-rotated logs

```powershell
$dir = "$env:CATALINA_BASE\logs"
$latest = Get-ChildItem $dir\myapp*.log | Sort-Object LastWriteTime | Select-Object -Last 1
Get-Content $latest.FullName -Tail 100 -Wait
```

(That picks the newest `myapp*.log` and follows it.)
