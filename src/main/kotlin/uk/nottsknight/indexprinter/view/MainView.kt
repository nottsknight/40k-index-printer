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

class MainView : View("40k Index Printer") {
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

            separator(Orientation.HORIZONTAL) {
                addClass(IndexPrinterStyle.hidden)
            }

            text(controller.indexFileName) {
                alignment = Pos.CENTER_LEFT
            }
        }

        separator(Orientation.VERTICAL) {
            addClass(IndexPrinterStyle.hidden)
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

        separator(Orientation.VERTICAL) {
            addClass(IndexPrinterStyle.hidden)
        }

        checkbox("Include army rule?") {
            action {
                controller.includeArmyRule = isSelected
            }
        }

        separator(Orientation.VERTICAL) {
            addClass(IndexPrinterStyle.hidden)
        }

        checkbox("Include detachment rules?") {
            action {
                controller.includeDetachmentRules = isSelected
            }
        }

        separator(Orientation.VERTICAL) {
            addClass(IndexPrinterStyle.hidden)
        }

        checkbox("Include stratagems?") {
            action {
                controller.includeStratagems = isSelected
            }
        }

        separator(Orientation.VERTICAL) {
            addClass(IndexPrinterStyle.hidden)
        }

        checkbox("Include enhancements?") {
            action {
                controller.includeEnhancements = isSelected
            }
        }

        separator(Orientation.VERTICAL) {
            addClass(IndexPrinterStyle.hidden)
        }

        checkbox("Include wargear options?") {
            action {
                controller.includeWargearOptions = isSelected
            }
        }

        separator(Orientation.VERTICAL) {
            addClass(IndexPrinterStyle.hidden)
        }

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