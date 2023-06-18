package uk.nottsknight.indexprinter.view

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ListCell
import javafx.scene.control.SelectionMode
import javafx.stage.FileChooser
import javafx.util.Callback
import tornadofx.*
import uk.nottsknight.indexprinter.pdf.UnitPage
import javax.swing.GroupLayout.Alignment

private class UnitPageCell : ListCell<UnitPage>() {
    override fun updateItem(item: UnitPage?, empty: Boolean) {
        super.updateItem(item, empty)
        if (empty || item == null) {
            return
        }

        text = item.name
    }
}

class MainView : View() {
    private val controller: MainController by inject()

    override val root = vbox {
        hbox {
            button("Select index file") {
                alignment = Pos.TOP_CENTER
                action {
                    val chooser = FileChooser()
                    chooser.title = "Select index file"
                    chooser.extensionFilters.add(
                        FileChooser.ExtensionFilter("PDF", "*.pdf")
                    )
                    chooser.showOpenDialog(primaryStage)?.let { f ->
                        controller.updateIndexFile(f)
                    }
                }
            }
            text(controller.indexFileName) {
                alignment = Pos.CENTER_LEFT
            }
        }

        scrollpane(fitToWidth = true, fitToHeight = true) {
            listview(controller.units) {
                cellFactory = Callback { _ -> UnitPageCell() }
                selectionModel.apply {
                    selectionMode = SelectionMode.MULTIPLE
                    selectedItems.onChange { items -> controller.updateSelectedUnits(items.list) }
                }
            }
        }

        separator {
            orientation = Orientation.VERTICAL
        }

        button("Print") {
            alignment = Pos.CENTER
            action { controller.printSelectedUnits() }
        }

        separator {
            orientation = Orientation.VERTICAL
        }

        prefWidth = 300.0
    }
}