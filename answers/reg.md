In a `.cmd/.bat`, you can detect whether **the current CMD process** is 32-bit or 64-bit, then pick the right switch.

### Option A (recommended): detect the script’s process bitness

```bat
@echo off
setlocal

rem If this exists, you're a 32-bit process running on 64-bit Windows (WOW64).
set "REGVIEW=/reg:64"
if defined PROCESSOR_ARCHITEW6432 set "REGVIEW=/reg:32"

rem Example usage:
reg query "HKLM\Software\Microsoft\Windows\CurrentVersion" %REGVIEW% /v ProgramFilesDir

endlocal
```

**Why this works**

* In a **32-bit** CMD running on **64-bit Windows**, `PROCESSOR_ARCHITEW6432` is defined.
* In a **64-bit** CMD, it’s not defined.
* On **32-bit Windows**, it’s not defined either — and `/reg:64` is invalid there. If you need to support 32-bit Windows too, use Option B.

### Option B: handle 32-bit Windows as well

```bat
@echo off
setlocal

set "REGVIEW=/reg:64"

rem If OS is 32-bit, don’t use /reg at all (or use /reg:32 if you prefer).
if /i "%PROCESSOR_ARCHITECTURE%"=="x86" if not defined PROCESSOR_ARCHITEW6432 set "REGVIEW="

rem If 32-bit process on 64-bit OS, use 32-bit view.
if defined PROCESSOR_ARCHITEW6432 set "REGVIEW=/reg:32"

reg query "HKLM\Software\Microsoft\Windows\CurrentVersion" %REGVIEW% /v ProgramFilesDir

endlocal
```

---

if defined PROCESSOR_ARCHITEW6432 (
  set "REGVIEW=/reg:32"
) else (
  set "REGVIEW=/reg:64"
)

