
/*
 * Copyright (C) 2022  Dick Lieber, WA9NNN
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.wa9nnn.rotorgenius

import _root_.scalafx.Includes._
import _root_.scalafx.beans.property.{IntegerProperty, ObjectProperty, StringProperty}
import _root_.scalafx.collections.ObservableBuffer
import _root_.scalafx.geometry.Insets
import _root_.scalafx.scene.control._
import _root_.scalafx.scene.layout.{GridPane, Pane, Region}
import _root_.scalafx.util.StringConverter

import java.text.NumberFormat
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import scala.util.matching.Regex

/**
 * Help to build a GridPane of one column of labeled controls.
 */
class GridOfControls(gaps: (Int, Int) = 10 -> 10, insets: Insets = Insets(20, 100, 10, 10)) extends GridPane {
  hgap = gaps._1
  vgap = gaps._2
  padding = insets
  implicit val row: AtomicInteger = new AtomicInteger()

  private def label(label: String): Int = {
    style = "-fx-alignment:top-left"
    val r = row.getAndIncrement()
    add(new Label(label + ":"), 0, r)
    r
  }

  def nextRow: Int = row.get()

  def addText(labelText: String, defValue: String = "",
              regx: Option[Regex] = None,
              tooltip: Option[String] = None): StringProperty = {
    val row = label(labelText)
    val control = new TextField()

    tooltip.foreach(control.tooltip = _)
    control.text = defValue
    add(control, 1, row)
    control.text
  }

  def addInt(labelText: String, defValue: Int = 0, tooltip: Option[String] = None): IntegerProperty = {
    val row = label(labelText)

    val control = new TextField()

    tooltip.foreach(control.tooltip = _)
    control.text = NumberFormat.getNumberInstance.format(defValue)

    add(control, 1, row)
    val integerProperty = IntegerProperty(defValue)
    control.text.onChange { (_, _, nv) =>
      integerProperty.value = NumberFormat.getNumberInstance.parse(nv)
    }
    integerProperty
  }

  def addDuration(labelText: String, defValue: Duration, tooltip: Option[String] = None): ObjectProperty[Duration] = {
    val row = label(labelText)
    val control = new TextField()
    tooltip.foreach(control.tooltip = _)
    control.text = defValue.toString
    add(control, 1, row)
    val durProperty = ObjectProperty(defValue)
    control.text.onChange { (_, _, nv) =>
      durProperty.value = Duration.parse(nv)
    }
    durProperty
  }


  def addTextArea(labelText: String, defValue: String = "", nRows: Int = 3, tooltip: Option[String] = None): StringProperty = {
    val row = label(labelText)
    val control = new TextArea(defValue) {
      prefRowCount = nRows
    }
    tooltip.foreach(control.tooltip = _)
    add(control, 1, row)
    control.text
  }

  def addCombo[T](labelText: String,
                  choices: ObservableBuffer[T],
                  defValue: Option[T] = None,
                  tooltip: Option[String] = None,
                  converter: Option[StringConverter[T]] = None): ObjectProperty[T] = {
    val row = label(labelText)
    val control: ComboBox[T] = new ComboBox[T](ObservableBuffer.from(choices.toSeq))
    converter.foreach(control.converter = _)
    tooltip.foreach(control.tooltip = _)

    val selectionModel: SingleSelectionModel[T] = control.selectionModel.value
    defValue.foreach { d: T =>
      selectionModel.select(d)
    }
    add(control, 1, row)
    control.value
  }

  /**
   *
   * @param labelText     column 1
   * @param control       column 2
   * @param extraControls zero or more
   */
  def addControl(labelText: String, control: Region, extraControls: Control*): Region = {
    val row = label(labelText)
    add(control, 1, row)
    extraControls.zipWithIndex.foreach { case (extra, index) =>
      add(extra, index + 2, row)
    }
    control
  }

  def add(labelText: String, value: Any): StringProperty = {
    val row = label(labelText)
    val cell = com.wa9nnn.util.tableui.Cell(value)
    val control = Label(cell.value)
    control.styleClass = Seq("fieldLabel")
    add(control, 1, row)
    control.styleClass = Seq("statCell")
    control.text
  }

  def add(labelText: String, pane: Pane): Unit = {
    val row = label(labelText)
    add(pane, 1, row)
  }
  def set(p: StringProperty, value: Any): Unit = {
    val cell = com.wa9nnn.util.tableui.Cell(value)
    p.value = cell.value
  }

}
