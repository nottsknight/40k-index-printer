package uk.nottsknight.indexprinter.view

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDDocumentInformation
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitDestination
import tornadofx.*
import uk.nottsknight.indexprinter.pdf.UnitFinder
import uk.nottsknight.indexprinter.pdf.UnitPage
import uk.nottsknight.indexprinter.pdf.UnitTextStripper
import java.io.File

class MainController : Controller() {
    private val stripper = UnitTextStripper()
    private val nameFinder = UnitFinder(stripper)

    private val indexFile = objectProperty<File?>()
    val indexFileName = stringProperty("No file selected")
    val units = observableListOf<UnitPage>()

    private val selectedUnits = mutableListOf<UnitPage>()

    init {
        indexFile.onChange { f ->
            if (f == null) {
                return@onChange
            }

            val newUnits = PDDocument.load(f).use { doc ->
                nameFinder.processDoc(doc)
            } ?: return@onChange

            units.clear()
            units.addAll(newUnits)
        }
    }

    fun updateIndexFile(file: File) {
        indexFile.value = file
        indexFileName.value = file.name
    }

    fun updateSelectedUnits(selected: List<UnitPage>) {
        selectedUnits.clear()
        selectedUnits.addAll(selected)
    }

    fun printSelectedUnits() {
        if (indexFile.value == null) {
            return
        }

        val newDoc = PDDocument().apply {
            documentInformation = PDDocumentInformation().apply {
                title = "Index Printer doc"
                author = "IndexPrinter 1.0.0"
            }
        }
        PDDocument.load(indexFile.value).use { doc ->
            for ((_, page1, page2) in selectedUnits) {
                newDoc.addPage(doc.getPage(page1 - 1))
                newDoc.addPage(doc.getPage(page2 - 1))
                val bookmark = PDPageFitDestination().apply {
                    pageNumber = page1
                    setFitBoundingBox(true)
                }
            }

            newDoc.save("reference-sheet.pdf")
        }
    }
}