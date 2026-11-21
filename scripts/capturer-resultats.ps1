$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$captureDirectory = Join-Path $root "docs\captures"
New-Item -ItemType Directory -Force -Path $captureDirectory | Out-Null

$edgeCandidates = @(
    (Join-Path ${env:ProgramFiles(x86)} "Microsoft\Edge\Application\msedge.exe"),
    (Join-Path $env:ProgramFiles "Microsoft\Edge\Application\msedge.exe")
)
$edgeCommand = Get-Command msedge.exe -ErrorAction SilentlyContinue
$edge = if ($edgeCommand) { $edgeCommand.Source } else { $edgeCandidates | Where-Object { Test-Path $_ } | Select-Object -First 1 }
if (-not $edge) {
    throw "Microsoft Edge est introuvable. Installez Edge ou ajoutez msedge.exe au PATH."
}

$clients = @(Invoke-RestMethod "http://localhost:8888/api/clients")
$voitures = @(Invoke-RestMethod "http://localhost:8888/api/voitures")
if ($clients.Count -eq 0 -or $voitures.Count -eq 0) {
    throw "Les API répondent mais ne contiennent aucune donnée à capturer."
}

$serviceNames = @("gateway", "service-client", "service-voiture")
foreach ($serviceName in $serviceNames) {
    $instances = @(Invoke-RestMethod "http://localhost:8500/v1/health/service/$serviceName`?passing=true")
    if ($instances.Count -eq 0) {
        throw "Le service '$serviceName' n'est pas enregistré et sain dans Consul."
    }
}

$pages = @(
    @{ Url = "http://localhost:8888/api/clients"; File = "api-clients.png" },
    @{ Url = "http://localhost:8888/api/voitures"; File = "api-voitures.png" },
    @{ Url = "http://localhost:8500/ui/"; File = "consul-services.png" }
)

foreach ($page in $pages) {
    $imagePath = Join-Path $captureDirectory $page.File
    $profilePath = Join-Path $env:TEMP ("tp25-edge-" + [guid]::NewGuid().ToString("N"))
    try {
        & $edge "--headless=new" "--disable-gpu" "--no-first-run" "--user-data-dir=$profilePath" "--window-size=1440,1000" "--run-all-compositor-stages-before-draw" "--virtual-time-budget=2500" "--screenshot=$imagePath" $page.Url
        if ($LASTEXITCODE -ne 0 -or -not (Test-Path $imagePath)) {
            throw "La capture de '$($page.Url)' a échoué."
        }
        Write-Output "Capture créée : docs/captures/$($page.File)"
    } finally {
        if (Test-Path $profilePath) {
            Remove-Item -Recurse -Force -LiteralPath $profilePath
        }
    }
}
