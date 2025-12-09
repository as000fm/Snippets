Voici un script PowerShell complet qui fait exactement ça, en restant efficace pour >1000 fichiers, en lisant les CSV UTF-8 avec BOM et en écrivant un fichier maître en UTF-8 **sans** BOM. Tout est en français (nom suggéré, paramètres, commentaires, messages).

Nom de script suggéré : **Fusionner-Csv.ps1**

```powershell
<#
.SYNOPSIS
Fusionne le contenu de plusieurs fichiers CSV en un seul fichier maître.

.DESCRIPTION
- Analyse un répertoire pour trouver les fichiers *.csv (non récursif).
- Chaque CSV source est encodé en UTF-8 avec marqueur BOM et ne contient pas de ligne d'en-tête.
- Le script lit chaque fichier et ajoute son contenu au fichier maître.
- Le fichier maître est encodé en UTF-8 sans BOM.
- Au démarrage, si le fichier maître existe, il est supprimé.

.PARAMETER Source
Répertoire à analyser.
Par défaut : le répertoire courant.

.PARAMETER Fichier
Nom (ou chemin) du fichier CSV maître de sortie.

.EXAMPLE
.\Fusionner-Csv.ps1

.EXAMPLE
.\Fusionner-Csv.ps1 -Source "C:\Donnees" -Fichier "C:\Sorties\master.csv"
#>

[CmdletBinding()]
param(
    [Parameter(Mandatory = $false)]
    [string]$Source = (Get-Location).Path,

    [Parameter(Mandatory = $false)]
    [string]$Fichier = "master.csv"
)

# Validation du répertoire source
if (-not (Test-Path -LiteralPath $Source -PathType Container)) {
    Write-Host "Erreur : le répertoire source '$Source' n'existe pas ou n'est pas un dossier." -ForegroundColor Red
    exit 1
}

# Résolution du chemin de sortie
# Si l'utilisateur fournit un chemin relatif, on le rend relatif au répertoire courant
$cheminFichier = Resolve-Path -LiteralPath (Join-Path -Path (Get-Location).Path -ChildPath $Fichier) -ErrorAction SilentlyContinue
if (-not $cheminFichier) {
    # Le fichier n'existe pas encore; on construit un chemin absolu propre
    $cheminFichier = [System.IO.Path]::GetFullPath((Join-Path -Path (Get-Location).Path -ChildPath $Fichier))
} else {
    $cheminFichier = $cheminFichier.Path
}

Write-Host "Répertoire source : $Source"
Write-Host "Fichier maître   : $cheminFichier"

# Suppression du fichier maître au démarrage
if (Test-Path -LiteralPath $cheminFichier -PathType Leaf) {
    try {
        Remove-Item -LiteralPath $cheminFichier -Force
        Write-Host "Le fichier maître existant a été supprimé."
    } catch {
        Write-Host "Erreur : impossible de supprimer le fichier maître '$cheminFichier'." -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor Red
        exit 1
    }
}

# Encodage UTF-8 sans BOM pour le fichier maître
$utf8SansBom = New-Object System.Text.UTF8Encoding($false)

# Récupération des fichiers CSV (non récursif)
$fichiersCsv = Get-ChildItem -LiteralPath $Source -Filter "*.csv" -File | Sort-Object Name

if (-not $fichiersCsv -or $fichiersCsv.Count -eq 0) {
    Write-Host "Aucun fichier CSV trouvé dans '$Source'."
    exit 0
}

Write-Host ("Nombre de fichiers CSV à fusionner : {0}" -f $fichiersCsv.Count)

# Ouverture d'un StreamWriter unique pour éviter les coûts d'ouverture/fermeture multiples
$writer = $null

try {
    $writer = New-Object System.IO.StreamWriter($cheminFichier, $true, $utf8SansBom)

    $compteur = 0

    foreach ($f in $fichiersCsv) {
        $compteur++

        Write-Host ("[{0}/{1}] Ajout de : {2}" -f $compteur, $fichiersCsv.Count, $f.Name)

        # StreamReader avec détection automatique du BOM
        $reader = $null
        try {
            $reader = New-Object System.IO.StreamReader($f.FullName, $true)

            while (-not $reader.EndOfStream) {
                $ligne = $reader.ReadLine()
                if ($null -ne $ligne) {
                    $writer.WriteLine($ligne)
                }
            }
        } finally {
            if ($reader) { $reader.Dispose() }
        }
    }

    Write-Host "Fusion terminée avec succès."
    Write-Host ("Fichier généré : {0}" -f $cheminFichier)

} catch {
    Write-Host "Erreur lors de la fusion des fichiers CSV." -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
} finally {
    if ($writer) { $writer.Dispose() }
}
```

### Notes pratiques

* Le tri par `Name` rend l’assemblage déterministe.
* La lecture/écriture est faite en flux (pas de gros chargement en mémoire), donc adaptée à des milliers de fichiers.
* Le `StreamReader(..., $true)` active la détection du BOM automatiquement.
* Le `UTF8Encoding($false)` garantit **aucun BOM** dans le fichier final.

### Exemples d’utilisation

```powershell
# Par défaut : répertoire courant, fichier "master.csv"
.\Fusionner-Csv.ps1

# Spécifier le dossier source et le fichier maître
.\Fusionner-Csv.ps1 -Source "D:\Import" -Fichier "D:\Export\master.csv"
```

Si tu veux une version **récursive** (incluant sous-dossiers) sans ajouter de nouveau paramètre, je peux te donner une petite variante en modifiant une seule ligne.
