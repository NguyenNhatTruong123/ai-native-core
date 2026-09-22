[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$repoRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
$buildDirectory = Join-Path $repoRoot 'bin\lab21'
$sourceFile = Join-Path $repoRoot 'scratch\ScratchHandler.java'
$testFile = Join-Path $repoRoot 'tests\unit\ScratchHandlerTest.java'

$null = Get-Command javac -ErrorAction Stop
$null = Get-Command java -ErrorAction Stop
$null = New-Item -ItemType Directory -Path $buildDirectory -Force

& javac --release 17 -encoding UTF-8 -Xlint:all -Werror -d $buildDirectory $sourceFile $testFile
if ($LASTEXITCODE -ne 0) {
    throw 'Lab2.1 compilation failed.'
}

& java -cp $buildDirectory lab21.ScratchHandlerTest
if ($LASTEXITCODE -ne 0) {
    throw 'Lab2.1 contract checks failed.'
}
