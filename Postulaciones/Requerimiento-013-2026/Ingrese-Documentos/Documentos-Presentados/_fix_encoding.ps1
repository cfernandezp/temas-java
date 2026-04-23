# Fix mojibake in Carta-Presentacion-CAFP.docx
# All strings built from char codes to avoid script-encoding issues.
$ErrorActionPreference = 'Stop'

$path = 'c:\Proyectos\Preguntas-Java\Postulaciones\Requerimiento-013-2026\Ingrese-Documentos\Documentos-Presentados\Carta-Presentacion-CAFP.docx'

# Accent chars (correct Unicode)
$a_acu = [char]0xE1   # a con tilde
$e_acu = [char]0xE9   # e con tilde
$i_acu = [char]0xED   # i con tilde
$o_acu = [char]0xF3   # o con tilde
$u_acu = [char]0xFA   # u con tilde
$n_tld = [char]0xF1   # enie
$A_acu = [char]0xC1
$E_acu = [char]0xC9
$I_acu = [char]0xCD
$O_acu = [char]0xD3
$U_acu = [char]0xDA
$N_tld = [char]0xD1
$deg   = [char]0xB0   # grado

# Mojibake chars (what Word currently has)
$c3 = [char]0xC3
$c2 = [char]0xC2

# Map: mojibake sequence -> correct char
$fixes = [ordered]@{
    ($c3 + [char]0xA1) = $a_acu
    ($c3 + [char]0xA9) = $e_acu
    ($c3 + [char]0xAD) = $i_acu
    ($c3 + [char]0xB3) = $o_acu
    ($c3 + [char]0xBA) = $u_acu
    ($c3 + [char]0xB1) = $n_tld
    ($c3 + [char]0x81) = $A_acu
    ($c3 + [char]0x89) = $E_acu
    ($c3 + [char]0x8D) = $I_acu
    ($c3 + [char]0x93) = $O_acu
    ($c3 + [char]0x9A) = $U_acu
    ($c3 + [char]0x91) = $N_tld
    ($c2 + [char]0xB0) = $deg
}

$word = New-Object -ComObject Word.Application
$word.Visible = $false
$doc = $word.Documents.Open($path)

$totalReplaced = 0
foreach ($pair in $fixes.GetEnumerator()) {
    $find = $doc.Content.Find
    $find.ClearFormatting()
    $find.Replacement.ClearFormatting()
    $find.Text = $pair.Key
    $find.Replacement.Text = $pair.Value
    $find.Forward = $true
    $find.Wrap = 1  # wdFindContinue
    $find.Format = $false
    $find.MatchCase = $true
    $find.MatchWholeWord = $false
    $find.MatchWildcards = $false
    $null = $find.Execute([ref]$pair.Key, [ref]$false, [ref]$true, [ref]$false, [ref]$false, [ref]$false, [ref]$true, [ref]1, [ref]$false, [ref]$pair.Value, [ref]2)  # wdReplaceAll = 2
}

$doc.Save()
$doc.Close()
$word.Quit()

[System.Runtime.Interopservices.Marshal]::ReleaseComObject($word) | Out-Null
[GC]::Collect()
[GC]::WaitForPendingFinalizers()

Write-Host "FIXED: $path"
