package uk.nottsknight.indexprinter.view

import tornadofx.FXVisibility
import tornadofx.Stylesheet
import tornadofx.cssclass

class IndexPrinterStyle : Stylesheet() {
    init {
        hidden {
            visibility = FXVisibility.HIDDEN
        }
    }

    companion object {
        val hidden by cssclass()
    }
}