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

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPut
import com.google.gson.Gson

private const val VALUE_OFF = 0
private const val VALUE_ON = 1
private const val TEMP_MIN = 143
private const val TEMP_MAX = 344
private const val BRIGHT_MIN = 3
private const val BRIGHT_MAX = 100

private typealias StateMutator = (ElgatoStateResponse) -> ElgatoStateResponse

/**
 * Elgato Key Light controller.
 *
 * @param host hostname or IP address of the light.
 * @param port port of the key light (9123 by default).
 */
class Elgato(host: String, port: Int) {
  private val url: String = "http://$host:$port/elgato/lights"

  /**
   * Set color temperature 0.0 - 1.0
   *
   * @param amount A range of 0.0 - 1.0 for temperature.
   */
  fun temperature(amount: Double) =
    action { state ->
      state.copy(
        lights = listOf(state.lights[0].copy(temperature = lerp(TEMP_MIN, TEMP_MAX, amount)))
      )
    }

  /**
   * Set brightness 0.0 - 1.0
   *
   * @param amount A range of 0.0 - 1.0 for brightness.
   */
  fun brightness(amount: Double) =
    action { state ->
      state.copy(
        lights = listOf(state.lights[0].copy(brightness = lerp(BRIGHT_MIN, BRIGHT_MAX, amount)))
      )
    }

  /** Turn the light on or off. */
  fun turn(turnOn: Boolean) =
    action { state ->
      state.copy(
        lights = listOf(state.lights[0].copy(on = if (turnOn) VALUE_ON else VALUE_OFF))
      )
    }

  /** Linear interpretation. Clamps 'amount' if needed. */
  private fun lerp(min: Int, max: Int, amount: Double) =
    (((max - min) * amount.coerceIn(0.0, 1.0)) + min).toInt()

  /** Creates a PUT action by taking the current state and applying the mutator. */
  private fun action(mutator: StateMutator): ElgatoStateResponse {
    val state = getState()
    if (state.numberOfLights != 1 || state.lights.size != 1) {
      throw Exception("Aborting, not exactly one light: $state")
    }
    return requestPut(Gson().toJson(mutator(state)))
  }

  /** Request current state from the light. */
  private fun getState(): ElgatoStateResponse = requestGet()

  /** Send a GET request to the given URL. */
  private fun requestGet() = request(url.httpGet())

  /** Send a PUT request with the given body to the given URL). */
  private fun requestPut(bodyStr: String) = request(url.httpPut().body(bodyStr))

  /** Make the request and parse an Elgato response, then return it. */
  private fun request(request: Request): ElgatoStateResponse {
    val (_, _, result) = request.responseObject(ElgatoStateResponse.Deserializer())
    val (payload, error) = result
    if (error != null) {
      throw Exception("Request failed")
    }
    if (payload == null) {
      throw Exception("Cannot parse response");
    }
    return payload
  }
}

/** The data returned from requests to the Elgato light as JSON. */
data class ElgatoStateResponse(
  val numberOfLights: Int,
  val lights: List<ElgatoLight>
) {

  /** Use GSON to deserialize the JSON data. */
  class Deserializer : ResponseDeserializable<ElgatoStateResponse> {
    override fun deserialize(content: String): ElgatoStateResponse =
      Gson().fromJson(content, ElgatoStateResponse::class.java)
  }
}

/** Data returned from the Elgato light for a single device. */
data class ElgatoLight(
  val on: Int,
  val brightness: Int,
  val temperature: Int
)