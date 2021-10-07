/*
 *      Copyright (C) 2021  Sascha Häberling
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * The Elgato-MQTT connector.
 *
 * Allows you to control your Elgato Key Light via MQTT.
 *
 * Arguments: <light ip:port> <mqtt ip:port> <topic-prefix>
 * For example:  192.168.1.100:9123 192.168.1.200:1883 office/light
 *
 * The first parameter is for the Elgato light. Port should always be 9123.
 * The second parameter is for your MQTT broker. Port 1883 is usually the default.
 * The third parameter is the topic prefix, to which actions will be appended.
 * The following topics will be subscribed to:
 *    - <prefix>/brightness  (Value 0.0 - 1.0)
 *    - <prefix>/temperature (Value 0.0 - 1.0)
 *    - <prefix>/onoff       (0=Off, Other=On)
 *
 * Not yet implemented:
 *  - Username and password auth for the MQTT server.
 *  - Multiple Elgato lights.
 *  - Only tested on the Elgato Key Light. Might work for others with the same protocol.
 */
fun main(args: Array<String>) {
  if (args.size != 3) {
    throw Exception("Missing parameters: <light ip:port> <mqtt ip:port> <topic-prefix>")
  }

  // Parse and display the parameters.
  val lightHost = args[0].parseHost()
  val mqttHost = args[1].parseHost()
  val topicPrefix = args[2]
  println("elgato_mqtt. Light: $lightHost  MQTT: $mqttHost  Topic-Prefix: $topicPrefix")

  // Initialize the light controller and MQTT client.
  val light = Elgato(lightHost.host, lightHost.port)
  val mqtt = Mqtt(mqttHost.host, mqttHost.port)
  mqtt.connect()

  // Subscribe to these topics and apply received changes to light itself.
  mqtt.subscribeDoubleValue("$topicPrefix/brightness", light::brightness)
  mqtt.subscribeDoubleValue("$topicPrefix/temperature", light::temperature)
  mqtt.subscribeDoubleValue("$topicPrefix/onoff") { v -> light.turn(v != 0.0) }
}

/**
 * Parses a string like <host>:<port>.
 */
private fun String.parseHost(): Host {
  val parts = this.split(':')
  if (parts.size != 2) {
    throw Exception("Parameter must be of format <ip>:<port>")
  }

  val hostname = parts[0]
  if (hostname.isBlank()) throw Exception("Hostname cannot be blank")
  val port = parsePort(parts[1])
  if (port <= 0) throw Exception("Port must be positive")
  return Host(hostname, port)
}

fun parsePort(portStr: String) =
  try {
    portStr.toInt();
  } catch (e: NumberFormatException) {
    throw Exception("Cannot parse port: '${portStr}'")
  }

private data class Host(
  val host: String,
  val port: Int
)