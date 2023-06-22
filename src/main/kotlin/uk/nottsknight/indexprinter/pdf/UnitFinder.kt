package uk.nottsknight.indexprinter.pdf

import org.apache.pdfbox.pdmodel.PDDocument

sealed class PageData
data class UnitPage(val name: String, val page1: Int, val page2: Int) : PageData()

data class RulesPage(val kind: Kind, val page: Int) : PageData() {
    enum class Kind {
        ArmyRule, DetachmentRule, Stratagems, Enhancements;

        override fun toString() = when (this) {
            ArmyRule -> "ARMY RULE"
            DetachmentRule -> "DETACHMENT RULE"
            Stratagems -> "STRATAGEMS"
            Enhancements -> "ENHANCEMENTS"
        }
    }
}


class UnitFinder(private val textStripper: UnitTextStripper) {
    fun processDoc(doc: PDDocument): List<PageData>? {
        val textModel = textStripper.getModel(doc)
        if (textModel == null) {
            println("No text model generated")
            return null
        }

        val pages = mutableListOf<PageData>()
        textModel.pages
            .asSequence()
            .filter { p -> p.isPortrait }
            .filter { p -> p.content.isNotEmpty() }
            .map { p ->
                if (p.content.any { it.text == "ARMY RULE" }) {
                    RulesPage(RulesPage.Kind.ArmyRule, p.pageNo)
                } else if (p.content.any { it.text == "DETACHMENT RULE" }) {
                    RulesPage(RulesPage.Kind.DetachmentRule, p.pageNo)
                } else if (p.content.any { it.text == "STRATAGEMS" }) {
                    RulesPage(RulesPage.Kind.Stratagems, p.pageNo)
                } else if (p.content.any { it.text == "ENHANCEMENTS" }) {
                    RulesPage(RulesPage.Kind.Enhancements, p.pageNo)
                } else {
                    null
                }
            }
            .filterNotNull()
            .sortedBy { p -> p.page }
            .forEach { p -> pages.add(p) }

        textModel.pages
            .asSequence()
            .filter { p -> p.isLandscape }
            .filter { p -> p.content.isNotEmpty() }
            .map { p -> p.content.sort(); p }
            .map { p -> Pair(p.content[0].text, p) }
            .groupBy { p -> p.first }
            .map { (name, ps) -> UnitPage(name, ps[0].second.pageNo, ps[1].second.pageNo) }
            .sortedBy { p -> p.name }
            .forEach { p -> pages.add(p) }

        return pages
    }
}
