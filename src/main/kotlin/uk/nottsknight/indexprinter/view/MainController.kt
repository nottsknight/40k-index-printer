package uk.nottsknight.indexprinter.view

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDDocumentInformation
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitDestination
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem
import tornadofx.Controller
import tornadofx.observableListOf
import tornadofx.stringProperty
import uk.nottsknight.indexprinter.pdf.RulesPage
import uk.nottsknight.indexprinter.pdf.UnitFinder
import uk.nottsknight.indexprinter.pdf.UnitPage
import uk.nottsknight.indexprinter.pdf.UnitTextStripper
import java.io.File

class MainController : Controller() {
    private val stripper = UnitTextStripper()
    private val nameFinder = UnitFinder(stripper)

    private var indexFile: File? = null
    val indexFileName = stringProperty("No file selected")
    val units = observableListOf<UnitPage>()
    private val rulesPages = mutableListOf<RulesPage>()

    private var outputFile: File? = null

    private val selectedUnits = mutableListOf<UnitPage>()

    var includeArmyRule = false
    var includeDetachmentRules = false
    var includeStratagems = false
    var includeEnhancements = false
    var includeWargearOptions = false

    fun updateIndexFile(file: File) {
        indexFile = file
        indexFileName.value = file.name

        val newUnits = PDDocument.load(file).use { doc ->
            nameFinder.processDoc(doc)
        } ?: return

        units.clear()
        units.addAll(newUnits.filterIsInstance<UnitPage>())
        rulesPages.clear()
        rulesPages.addAll(newUnits.filterIsInstance<RulesPage>())
    }

    fun updateSelectedUnits(selected: List<UnitPage>) {
        selectedUnits.clear()
        selectedUnits.addAll(selected)
    }

    fun updateOutputFile(file: File) {
        outputFile = file
    }

    fun printSelectedUnits() {
        if (indexFile == null || outputFile == null) {
            return
        }

        val newDoc = PDDocument().apply {
            documentInformation = PDDocumentInformation().apply {
                title = "Datacard reference - IndexPrinter"
                author = "IndexPrinter 0.1.0"
            }
        }

        PDDocument.load(indexFile).use { doc ->
            val outline = PDDocumentOutline()
            var nextBookmark = 0

            for ((kind, page) in rulesPages) {
                val newPage = doc.getPage(page - 1)
                val bookmark = PDOutlineItem().apply {
                    title = kind.toString()
                    destination = PDPageFitDestination().apply {
                        pageNumber = nextBookmark
                        setFitBoundingBox(true)
                    }
                }
                if ((includeArmyRule && kind == RulesPage.Kind.ArmyRule) ||
                    (includeDetachmentRules && kind == RulesPage.Kind.DetachmentRule) ||
                    (includeStratagems && kind == RulesPage.Kind.Stratagems) ||
                    (includeEnhancements && kind == RulesPage.Kind.Enhancements)
                ) {
                    newDoc.addPage(newPage)
                    outline.addLast(bookmark)
                    ++nextBookmark
                }
            }

            for ((name, page1, page2) in selectedUnits) {
                newDoc.addPage(doc.getPage(page1 - 1))
                if (includeWargearOptions) {
                    newDoc.addPage(doc.getPage(page2 - 1))
                }

                val bookmark = PDOutlineItem().apply {
                    title = name
                    destination = PDPageFitDestination().apply {
                        pageNumber = nextBookmark
                        setFitBoundingBox(true)
                    }
                }
                outline.addLast(bookmark)
                nextBookmark += if (includeWargearOptions) 2 else 1
            }

            newDoc.documentCatalog.documentOutline = outline
            newDoc.save(outputFile)
        }
    }
}