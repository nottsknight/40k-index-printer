package uk.nottsknight.indexprinter.view

import tornadofx.View
import tornadofx.label
import tornadofx.vbox

class ArmySelectionView : View() {
    private val controller: MainController by inject()

    override val root = vbox {
        label("Here is the army selection view")
    }
}