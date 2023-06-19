package uk.nottsknight.indexprinter.view

import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ListCell
import javafx.scene.control.SelectionMode
import javafx.stage.FileChooser
import javafx.util.Callback
import tornadofx.*
import uk.nottsknight.indexprinter.pdf.UnitPage

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
        padding = Insets(10.0)
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

            separator(Orientation.HORIZONTAL)

            text(controller.indexFileName) {
                alignment = Pos.CENTER_LEFT
            }
        }

        separator(Orientation.VERTICAL)

        scrollpane(fitToWidth = true, fitToHeight = true) {
            listview(controller.units) {
                cellFactory = Callback { _ -> UnitPageCell() }
                selectionModel.apply {
                    selectionMode = SelectionMode.MULTIPLE
                    selectedItems.onChange { items -> controller.updateSelectedUnits(items.list) }
                }
            }
        }

        separator(Orientation.VERTICAL)

        checkbox("Include unit option cards?") {
            action {
                controller.includeWargearOptions = isSelected
            }
        }

        separator(Orientation.VERTICAL)

        button("Save selected cards") {
            alignment = Pos.CENTER
            action {
                val chooser = FileChooser()
                chooser.title = "Select file to save to"
                chooser.extensionFilters.add(
                    FileChooser.ExtensionFilter("PDF", "*.pdf")
                )
                chooser.showSaveDialog(primaryStage)?.let { f ->
                    controller.updateOutputFile(f)
                    controller.printSelectedUnits()
                }
            }
        }

        prefWidth = 325.0
    }
}