# The Elgato-MQTT connector.
Control your Elgato Key Light using MQTT

[![Java CI with Gradle](https://github.com/shaeberling/elgato-mqtt/actions/workflows/gradle.yml/badge.svg)](https://github.com/shaeberling/elgato-mqtt/actions/workflows/gradle.yml)

- Arguments: `<light ip:port>` `<mqtt ip:port>` `<topic-prefix>`
- For example: `192.168.1.100:9123` `192.168.1.200:1883` `office/light`


1. Parameter is for the Elgato light. Port should always be 9123.
2. Parameter is for your MQTT broker. Port 1883 is usually the default.
3. Parameter is the topic prefix, to which actions will be appended.

The following topics will be subscribed to:
- `<prefix>/brightness`  (Value 0.0 - 1.0)
- `<prefix>/temperature` (Value 0.0 - 1.0)
- `<prefix>/onoff`       (0=Off, Other=On)

Not yet implemented:
- Username and password auth for the MQTT server.
- Multiple Elgato lights.
- Only tested on the Elgato Key Light. Might work for others with the same protocol.
