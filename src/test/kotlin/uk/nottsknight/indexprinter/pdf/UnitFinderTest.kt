package uk.nottsknight.indexprinter.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import java.io.File

fun main() {
    val stripper = UnitTextStripper()
    val finder = UnitFinder(stripper)

    val f = File("/Users/ianknight/Computing/IdeaProjects/indexprinter/src/test/resources/tau-index.pdf")
    val units = PDDocument.load(f).use { doc ->
        finder.processDoc(doc)
    }

    if (units != null) {
        for ((name, p1, p2) in units) {
            println("$name (pp. $p1, $p2)")
        }
    }
}