package uk.nottsknight.indexprinter

import tornadofx.App
import tornadofx.launch
import tornadofx.reloadStylesheetsOnFocus
import uk.nottsknight.indexprinter.view.IndexPrinterStyle
import uk.nottsknight.indexprinter.view.MainView

class Main: App(MainView::class, IndexPrinterStyle::class) {
    init {
        reloadStylesheetsOnFocus()
    }
}

fun main(args: Array<String>) {
    launch<Main>(args)
}