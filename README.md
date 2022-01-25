# DirtMultiversion
[![Discord](https://img.shields.io/discord/684429844947271767.svg?label=Discord)](https://discord.gg/v6xsRdc)
[![Build Status](https://ci.mzga.dev/job/DirtMultiversion/badge/icon)](https://ci.mzga.dev/job/DirtMultiversion/)
[![License: MIT](https://img.shields.io/badge/license-MIT-red.svg)](LICENSE)

Proxy that allows connecting older servers using newer client. Work in progress

#### Join DMV discord server to get latest news & support
https://discord.gg/v6xsRdc

### Supported versions
All versions between beta 1.3 - release 1.18.1 (with <a href="https://github.com/ViaVersion/ViaVersion">ViaVersion</a> support enabled in config)
Example: You can connect to beta 1.7.3 server using every version up to release 1.18.1

### Bugs
- [x] All listed above protocols are unfinished, but almost ready for normal usage
- [X] Fence bounding boxes (beta 1.3-1.8 server)
- [X] Problems with joining to beta 1.3-1.4 servers using beta 1.3-1.4 client
- [X] Online mode is not supported yet
- [X] Boat controlling (1.6+ client)
- [X] 1.6 encryption (doesn't work with servers behind BungeeCord)
- [x] and few others

### Releases
**Jenkins**: https://ci.mzga.dev

### Compiling from source
Using maven:
```
git clone https://github.com/DirtPowered/DirtMultiversion.git
cd ./DirtMultiversion
mvn clean package
```

### How to run
```
1. Compile or download latest release from github releases
2. Create start script or use terminal / command prompt and type java -jar <file>.jar
3. Open config.yml and set remote server version and address
4. Restart DMV
```