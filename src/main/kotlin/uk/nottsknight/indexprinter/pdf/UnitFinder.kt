package uk.nottsknight.indexprinter.pdf

import org.apache.pdfbox.pdmodel.PDDocument

data class UnitPage(val name: String, val page1: Int, val page2: Int)

class UnitFinder(private val textStripper: UnitTextStripper) {
    fun processDoc(doc: PDDocument): List<UnitPage>? {
        val textModel = textStripper.getModel(doc)
        if (textModel == null) {
            println("No text model generated")
            return null
        }

        return textModel.pages
            .asSequence()
            .filter { p -> p.isLandscape }
            .filter { p -> p.content.isNotEmpty() }
            .map { p -> p.content.sort(); p }
            .map { p -> Pair(p.content[0].text, p) }
            .groupBy { p -> p.first }
            .map { (name, ps) -> UnitPage(name, ps[0].second.pageNo, ps[1].second.pageNo) }
            .sortedBy { p -> p.name }
            .toList()
    }
}
