package uk.nottsknight.indexprinter.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition

class UnitTextModel(val title: String?) {
    val pages = mutableListOf<UnitTextPage>()
}

class UnitTextPage(val pageNo: Int, val isPortrait: Boolean, val isLandscape: Boolean) {
    val content = mutableListOf<UnitTextEntry>()
}

data class UnitTextEntry(val x: Float, val y: Float, val font: String, val text: String) : Comparable<UnitTextEntry> {
    override fun compareTo(other: UnitTextEntry) =
        if (this.y == other.y) {
            this.x.compareTo(other.x)
        } else {
            this.y.compareTo(other.y)
        }
}

class UnitTextStripper : PDFTextStripper() {
    private var currentDoc: UnitTextModel? = null
    private var currentPage: UnitTextPage? = null

    override fun startDocument(document: PDDocument) {
        currentDoc = UnitTextModel(document.documentInformation?.title)
    }

    override fun startPage(page: PDPage) {
        val w = page.mediaBox.width
        val h = page.mediaBox.height
        currentPage = UnitTextPage(currentPageNo, h > w, w > h)
    }

    override fun writeString(text: String?, textPositions: MutableList<TextPosition>?) {
        if (text == null || textPositions.isNullOrEmpty()) {
            return
        }

        val tp = textPositions[0]
        val font = "${tp.font.name}/${tp.fontSizeInPt}"
        val entry = UnitTextEntry(tp.x, tp.y, font, text)
        currentPage?.content?.add(entry)
    }

    override fun endPage(page: PDPage?) {
        currentPage?.let {
            currentDoc?.pages?.add(it)
            currentPage = null
        }
    }

    fun getModel(doc: PDDocument): UnitTextModel? {
        getText(doc)
        return currentDoc
    }
}