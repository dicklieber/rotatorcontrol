/*
 *   Copyright (C) 2022  Dick Lieber, WA9NNN
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.wa9nnn.rotator.ui

import scalafx.scene.control.{TextField, TextFormatter, TextInputControl}
import scalafx.scene.input.KeyEvent
import scalafx.util.converter.FormatStringConverter

import java.text.NumberFormat
import scala.util.matching.Regex
import _root_.scalafx.Includes._

object InputHelper {
  /**
   *
   * @param textFields that will ensure uppercase
   */
  def forceCaps(textFields: TextInputControl*): Unit = {
    textFields.foreach {
      _.setTextFormatter(new TextFormatter[AnyRef]((change: TextFormatter.Change) => {
        def foo(change: TextFormatter.Change) = {
          change.setText(change.getText.toUpperCase)
          change
        }

        foo(change)
      }))
    }
  }

  /**
   *
   * @param textField to work with.
   * @param regex     discard Chars not passing this.
   */
  def forceAllowed(textField: TextInputControl, regex: Regex): Unit = {
    textField.onKeyPressed = { (event: KeyEvent) =>
      Option(event.text).foreach { (ch: String) =>
        val matches = regex.matches(ch)
        if (!matches) {
          event.consume()
        }
      }
    }
  }

  /**
   *
   * @param textFields that will ensure integer only
   */
  def forceInt(textFields: TextField*): Unit = {
    val nf: NumberFormat = NumberFormat.getIntegerInstance()
    val converter: FormatStringConverter[Number] = new FormatStringConverter[Number](nf)

    textFields.foreach { tf =>
      tf.setTextFormatter(new TextFormatter(converter))
    }
  }


}
