package uk.nottsknight.indexprinter

import tornadofx.App
import tornadofx.launch
import uk.nottsknight.indexprinter.view.MainView

class Main: App(MainView::class) {

}

fun main(args: Array<String>) {
    launch<Main>(args)
}