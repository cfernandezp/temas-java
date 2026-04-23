# Generador de Carta - SCI 013-2026
# Usa codigos Unicode [char] para todos los acentuados: inmune a encoding del .ps1
$ErrorActionPreference = 'Stop'

$outPath = 'c:\Proyectos\Preguntas-Java\Postulaciones\Requerimiento-013-2026\Ingrese-Documentos\Documentos-Presentados\CAFP-carta.docx'

# Eliminar archivo previo si existe
if (Test-Path $outPath) { Remove-Item $outPath -Force }

# Atajos de acentos
$a1 = [char]0xE1   # a aguda
$e1 = [char]0xE9   # e aguda
$i1 = [char]0xED   # i aguda
$o1 = [char]0xF3   # o aguda
$u1 = [char]0xFA   # u aguda
$n1 = [char]0xF1   # n tilde
$A1 = [char]0xC1
$E1 = [char]0xC9
$I1 = [char]0xCD
$O1 = [char]0xD3
$U1 = [char]0xDA
$N1 = [char]0xD1
$gr = [char]0xB0   # grado
$co = [char]0x201C # comilla apertura
$cc = [char]0x201D # comilla cierre

$word = New-Object -ComObject Word.Application
$word.Visible = $false
$doc = $word.Documents.Add()

$doc.PageSetup.TopMargin = 71
$doc.PageSetup.BottomMargin = 71
$doc.PageSetup.LeftMargin = 85
$doc.PageSetup.RightMargin = 71

$sel = $word.Selection
$sel.Font.Name = 'Arial'
$sel.Font.Size = 11

# Fecha (derecha)
$sel.ParagraphFormat.Alignment = 2
$sel.TypeText("Lima, 23 de abril de 2026")
$sel.TypeParagraph()
$sel.TypeParagraph()

# Destinatario
$sel.ParagraphFormat.Alignment = 0
$sel.TypeText("Se${n1}ora")
$sel.TypeParagraph()
$sel.Font.Bold = $true
$sel.TypeText("JENNY MILUSKA TARAZONA FRANCO")
$sel.Font.Bold = $false
$sel.TypeParagraph()
$sel.TypeText("Subunidad de Abastecimiento (e)")
$sel.TypeParagraph()
$sel.TypeText("Contralor${i1}a General de la Rep${u1}blica")
$sel.TypeParagraph()
$sel.Font.Underline = 1
$sel.TypeText("Presente.-")
$sel.Font.Underline = 0
$sel.TypeParagraph()
$sel.TypeParagraph()

# Asunto / Referencia
$sel.Font.Bold = $true
$sel.TypeText("Asunto:")
$sel.Font.Bold = $false
$sel.TypeText([char]9 + "Presentaci${o1}n de documentos para perfeccionamiento de contrato")
$sel.TypeParagraph()
$sel.TypeText([char]9 + [char]9 + "SCI N${gr} 013-2026-CG-UE002/BID3")
$sel.TypeParagraph()
$sel.Font.Bold = $true
$sel.TypeText("Referencia:")
$sel.Font.Bold = $false
$sel.TypeText([char]9 + "CARTA N${gr} 000133-2026-CG/GPROY")
$sel.TypeParagraph()
$sel.TypeParagraph()

# Saludo
$sel.TypeText("De mi mayor consideraci${o1}n:")
$sel.TypeParagraph()
$sel.TypeParagraph()

# Cuerpo justificado
$sel.ParagraphFormat.Alignment = 3
$p1 = "Por medio de la presente, en atenci${o1}n a la notificaci${o1}n de adjudicaci${o1}n recibida mediante la CARTA N${gr} 000133-2026-CG/GPROY de fecha 20 de abril de 2026, correspondiente al proceso de Consultor${i1}a Individual SCI N${gr} 013-2026-CG-UE002/BID3 ${co}Contrataci${o1}n de un Consultor Ingeniero de Datos I para el Desarrollo del M${o1}dulo de An${a1}lisis de Datos para dar Soporte a los Auditores durante la Ejecuci${o1}n de los Servicios de Control en el Sistema Integrado de Control del Proyecto Interno 1.8.2${cc}, cumplo con remitir los documentos requeridos para el perfeccionamiento del contrato, en un ${u1}nico archivo PDF debidamente foliado con un total de veintis${e1}is (26) folios, conforme al siguiente detalle:"
$sel.TypeText($p1)
$sel.TypeParagraph()
$sel.TypeParagraph()

# Tabla
$sel.ParagraphFormat.Alignment = 0
$table = $doc.Tables.Add($sel.Range, 11, 3)
$table.Borders.Enable = $true
$table.Range.Font.Size = 10
$table.Range.Font.Name = 'Arial'

$table.Cell(1,1).Range.Text = "N${gr}"
$table.Cell(1,2).Range.Text = "Documento solicitado"
$table.Cell(1,3).Range.Text = "Folios"
for ($c=1; $c -le 3; $c++) {
    $table.Cell(1,$c).Range.Font.Bold = $true
    $table.Cell(1,$c).Range.ParagraphFormat.Alignment = 1
    $table.Cell(1,$c).Shading.BackgroundPatternColor = 14869218
}

$filas = @(
    @("0", "Carta de presentaci${o1}n (el presente documento)", "1"),
    @("1", "Copias simples de grados acad${e1}micos y t${i1}tulos", "2 - 4"),
    @("2", "Copias simples de formaci${o1}n acad${e1}mica complementaria", "5 - 14"),
    @("3", "Copias simples que sustentan experiencia general y espec${i1}fica", "15 - 20"),
    @("4", "Declaraci${o1}n Jurada (inhabilitaci${o1}n / RNSSC-SERVIR / antecedentes penales, policiales y judiciales)", "21"),
    @("5", "Carta de autorizaci${o1}n de pagos - CCI", "22"),
    @("6", "Declaraci${o1}n Jurada (formato adjunto)", "23"),
    @("7", "Copia simple del Documento Nacional de Identidad", "24"),
    @("8", "Declaraci${o1}n Jurada de Compromiso Antisoborno", "25"),
    @("9", "Certificado de Antecedentes Judiciales", "26")
)

for ($i=0; $i -lt $filas.Count; $i++) {
    $row = $i + 2
    $table.Cell($row,1).Range.Text = $filas[$i][0]
    $table.Cell($row,2).Range.Text = $filas[$i][1]
    $table.Cell($row,3).Range.Text = $filas[$i][2]
    $table.Cell($row,1).Range.ParagraphFormat.Alignment = 1
    $table.Cell($row,3).Range.ParagraphFormat.Alignment = 1
}

$table.Columns.Item(1).Width = 30
$table.Columns.Item(2).Width = 340
$table.Columns.Item(3).Width = 70

# Final
$word.Selection.EndKey(6) | Out-Null
$sel = $word.Selection
$sel.Font.Name = 'Arial'
$sel.Font.Size = 11
$sel.TypeParagraph()

$sel.ParagraphFormat.Alignment = 3
$p2 = "Agradeciendo la atenci${o1}n a la presente, quedo a su disposici${o1}n para cualquier coordinaci${o1}n adicional que resulte necesaria para el perfeccionamiento del contrato."
$sel.TypeText($p2)
$sel.TypeParagraph()
$sel.TypeParagraph()

$sel.ParagraphFormat.Alignment = 0
$sel.TypeText("Atentamente,")
$sel.TypeParagraph()
$sel.TypeParagraph()
$sel.TypeParagraph()
$sel.TypeParagraph()

$sel.TypeText("_________________________________________")
$sel.TypeParagraph()
$sel.Font.Bold = $true
$sel.TypeText("Cristian Anthony Fern${a1}ndez P${e1}rez")
$sel.Font.Bold = $false
$sel.TypeParagraph()
$sel.TypeText("DNI N${gr} 44911435")
$sel.TypeParagraph()
$sel.TypeText("RUC N${gr} 10449114359")
$sel.TypeParagraph()
$sel.TypeText("Domicilio: Calle Quipan 185, Independencia - Lima")
$sel.TypeParagraph()
$sel.TypeText("Celular: 939079213")
$sel.TypeParagraph()
$sel.TypeText("Correo: fer.per.cristian@gmail.com")

$doc.SaveAs2([ref]$outPath, 16)
$doc.Close()
$word.Quit()

[System.Runtime.Interopservices.Marshal]::ReleaseComObject($word) | Out-Null
[GC]::Collect()
[GC]::WaitForPendingFinalizers()

Write-Host "OK: $outPath"
