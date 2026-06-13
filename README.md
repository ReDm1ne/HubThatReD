# HubThatRed

*A hub for one world, a spawn for each world!*

## Project info

HubThatRed is a Bukkit/SpigotMC/PaperMC plugin that allows players to manage
different spawn points in multiple worlds, and connects all
worlds together through a hub. This is useful to emulate a
small-scale network, where players can play different game modes
in different sub-servers - which in this case, are worlds.

Spawn points can also be grouped and shared through different
worlds. Every message is configurable and the plugin also
includes features such as teleportation cool-downs (useful for
PvP) and Multiverse-Core support.

## Features

- Multi-world spawn point management
- Central hub teleporation system
- Configurable teleport cooldowns
- World-specific chat channels
- Automatic update checking
- Comprehensive permission system
- Multiverse-Core support

## Supported Versions

- Minecraft 1.16.x
- Minecraft 1.17.x
- Minecraft 1.18.x
- Minecraft 1.19.x
- Minecraft 1.20.x
- Minecraft 1.21.x

## Requirements

- Java 21+ (required for 1.20.5+ and 1.21.x servers)
- Spigot/Paper 1.16 or later

## Building from source

- Clone the git repository
- Move into the cloned directory
- Run `mvn clean package`

The compiled JAR file will be located in the `/target` directory.

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/hub` | Teleport to hub | `hubthat.hub` |
| `/hub [player]` | Teleport another player to hub | `hubthat.hub.others` |
| `/sethub` | Set the hub location | `hubthat.sethub` |
| `/spawn` | Teleport to spawn | `hubthat.spawn` |
| `/spawn [world] [player]` | Teleport to another world's spawn | `hubthat.spawn.anotherworld` |
| `/setspawn [world]` | Set spawn location | `hubthat.setspawn` |
| `/worldlist` | List all worlds | `hubthat.listworlds` |
| `/worldtp <world>` | Teleport to a world | `hubthat.gotoworld` |
| `/hubthat reload` | Reload configuration | `hubthat.reloadconfig` |

## Permissions

All permissions use the `hubthat.*` namespace for backward compatibility:

- `hubthat.hub` - Access to hub command (default: true)
- `hubthat.sethub` - Set hub location (default: op)
- `hubthat.spawn` - Access to spawn command (default: true)
- `hubthat.setspawn` - Set spawn location (default: op)
- `hubthat.gotoworld` - Teleport to any world (default: op)
- `hubthat.listworlds` - List all worlds (default: op)
- `hubthat.reloadconfig` - Reload config (default: op)
- `hubthat.nohubdelay` - Skip hub teleport delay (default: op)
- `hubthat.nospawndelay` - Skip spawn teleport delay (default: op)

See `plugin.yml` for the complete permission list.

## License

GNU General Public License v3.0
