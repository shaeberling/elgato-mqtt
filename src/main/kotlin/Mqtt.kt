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

import org.eclipse.paho.client.mqttv3.IMqttClient
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.UUID

/**
 * A simple MQTT server.
 *
 * @param host hostname or IP address of the MQTT broker.
 * @param port port of the MQTT broker (1883 by default).
 */
class Mqtt(host: String, port: Int) {
  private val url: String = "tcp://$host:$port"
  private val client: IMqttClient

  init {
    this.client = MqttClient(this.url, "mqtt-elgato_${UUID.randomUUID()}", MemoryPersistence())
  }

  /** Connect to the broker. Will throw an exception if it fails. */
  fun connect() {
    val options = MqttConnectOptions()
    options.isAutomaticReconnect = true
    options.isCleanSession = true
    options.connectionTimeout = 10
    client.connect(options)
    println("MQTT connection success")
  }

  /**
   * Subscribes to a given topic
   *
   * @param topic the topic to subscribe to.
   * @param apply function to be called on update with the parsed double value.
   */
  fun subscribeDoubleValue(topic: String, apply: (value: Double) -> Unit) {
    client.subscribe(topic, 2) { _, msg ->
      apply(msg.toString().toDouble())
    }
    println("Subscribed to topic '${topic}'")
  }

}