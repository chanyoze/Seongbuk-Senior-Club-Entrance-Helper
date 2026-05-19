# Build script - ASCII only to dodge PowerShell 5.1 encoding traps.
# Korean app name is read from appname.txt (UTF-8) at runtime.
# Usage: powershell -ExecutionPolicy Bypass -File .\build.ps1

$ErrorActionPreference = "Stop"

# Force UTF-8 on console + native command argument passing.
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding           = [System.Text.Encoding]::UTF8

# JDK 25 install path (adjust if you installed it elsewhere)
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-25.0.3.9-hotspot"
$env:PATH      = "$env:JAVA_HOME\bin;$env:PATH"

# Read Korean app name from external UTF-8 file (no Korean in this script).
if (Test-Path "appname.txt") {
    $AppName = (Get-Content -Path "appname.txt" -Encoding UTF8 -Raw).Trim()
} else {
    $AppName = "EntranceHelper"
}

Write-Host "App name : $AppName"

Write-Host "=== [1/6] Clean previous artifacts ==="
Remove-Item -Recurse -Force bin, staging, dist, manifest.txt -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path bin, staging | Out-Null

Write-Host "=== [2/6] javac compile ==="
$sources = (Get-ChildItem src/app/*.java).FullName
javac -d bin -encoding UTF-8 -cp "lib/jnativehook-2.2.2.jar" $sources
if ($LASTEXITCODE -ne 0) { throw "javac failed" }

Write-Host "=== [3/6] Build manifest ==="
$manifestText = "Manifest-Version: 1.0`r`nMain-Class: app.App`r`nClass-Path: jnativehook-2.2.2.jar`r`n`r`n"
[System.IO.File]::WriteAllText("$PWD\manifest.txt", $manifestText, [System.Text.UTF8Encoding]::new($false))

Write-Host "=== [4/6] jar packaging ==="
# Use ASCII jar name internally; the user-facing exe gets the Korean name via jpackage --name.
jar --create --file staging/app.jar --manifest manifest.txt -C bin .
if ($LASTEXITCODE -ne 0) { throw "jar failed" }
Copy-Item lib/jnativehook-2.2.2.jar staging/

Write-Host "=== [5/6] jpackage (takes a few minutes) ==="
jpackage `
    --name $AppName `
    --input staging `
    --main-jar app.jar `
    --type app-image `
    --dest dist `
    --app-version 1.1.0 `
    --vendor "Refactored" `
    --add-modules java.base,java.desktop,java.logging
if ($LASTEXITCODE -ne 0) { throw "jpackage failed" }

Write-Host "=== [6/6] Place config.properties next to launcher ==="
$destDir = Join-Path "dist" $AppName
Copy-Item config.properties $destDir

$size = "{0:N1} MB" -f ((Get-ChildItem -Recurse $destDir | Measure-Object Length -Sum).Sum / 1MB)
Write-Host ""
Write-Host "=== Done. Total size: $size ==="
Write-Host "Run: $destDir\$AppName.exe"
