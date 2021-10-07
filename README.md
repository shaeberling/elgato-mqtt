# The Elgato-MQTT connector.
Control your Elgato Key Light using MQTT

Arguments: `<light ip:port>` `<mqtt ip:port>` `<topic-prefix>`
For example: `192.168.1.100:9123` `192.168.1.200:1883` `office/light`

- The first parameter is for the Elgato light. Port should always be 9123.
- The second parameter is for your MQTT broker. Port 1883 is usually the default.
- The third parameter is the topic prefix, to which actions will be appended.

The following topics will be subscribed to:
- `<prefix>/brightness`  (Value 0.0 - 1.0)
- `<prefix>/temperature` (Value 0.0 - 1.0)
- `<prefix>/onoff`       (0=Off, Other=On)

Not yet implemented:
- Username and password auth for the MQTT server.
- Multiple Elgato lights.
- Only tested on the Elgato Key Light. Might work for others with the same protocol.
