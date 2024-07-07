/*
 * Copyright (C) 2022 Dick Lieber, WA9NNN
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
 */

package com.wa9nnn.util

import play.api.libs.json.{Format, JsError, JsResult, JsString, JsSuccess, JsValue}

import java.net.{InetAddress, InetSocketAddress}

case class HostAndPort(host: String, port: Int) extends Ordered[HostAndPort] {

  override val toString: String =
    f"$host:$port%d"

  def toSocketAddress: InetSocketAddress = {
    new InetSocketAddress(toInetAddress, port)
  }

  def toInetAddress:InetAddress = InetAddress.getByName(host)


  override def compare(that: HostAndPort): Int = {
    var ret = this.host.compareTo(that.host)
    if (ret == 0) {
      ret = this.port.compareTo(that.port)
    }
    ret
  }
}

object HostAndPort {

  import scala.language.implicitConversions

  implicit def hostAndPortToString(hostAndPort: HostAndPort): String = hostAndPort.toString

  private val regex = """([^:]*)(?::(\d+))?""".r


  /**
   *
   * @param in          host:1234 or host
   * @param defaultPort if no :port in the string.
   */
  def apply(in: String, defaultPort: Int): HostAndPort = {
    try {
      val regex(host, port) = in
      val p = if (port == null) {
        defaultPort
      } else {
        port.toInt
      }
      if (host.isEmpty) throw new IllegalArgumentException("Can't have empty host!")

      new HostAndPort(host, p)
    } catch {
      case _: Throwable =>
        throw new IllegalArgumentException(s"""Expecting "host:port" or "host", for default port ($defaultPort) """)
    }
  }

  implicit val hostAndPortFormat: Format[HostAndPort] = new Format[HostAndPort] {
    override def reads(json: JsValue): JsResult[HostAndPort] = {

      try {
        val host = json.as[String]
        JsSuccess( HostAndPort(host, 80))
      }
      catch {
        case e: IllegalArgumentException => JsError(e.getMessage)
      }
    }

    override def writes(hostAndPort: HostAndPort): JsValue = {
      JsString(hostAndPort.toString)
    }
  }

}
