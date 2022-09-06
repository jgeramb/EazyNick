![Logo](https://media.discordapp.net/attachments/845301622632611880/855464034341879838/EazyNick.png?width=96&height=96)
# EazyNick

## Useful links
> [Discord](https://discord.justix-dev.com)

> [Documentation](https://www.justix-dev.com/docs/eazynick)

> [Support my work](https://www.paypal.me/JustixDevelopment)

## Compiling
### Requirements
#### Mac OS
> [brew](https://brew.sh): Execute the command on the homepage

> sed: `brew install gnu-sed`

#### Debian/Ubuntu
> sed: `sudo apt-get install sed`

#### Windows
> [sed](http://gnuwin32.sourceforge.net/packages/sed.htm): Read the instructions on the download page

### Importing the run configuration in IntelliJ IDEA
1. Open `.run/compile-unix.run.xml` (Mac OS & Linux) or `.run/compile-win.run.xml` (Windows) in IntelliJ IDEA
2. Click on `Open Run/Debug Configurations...`
3. Click `OK`

### Compiling to jar using the run configuration
Simply run the configuration, the jar will be saved at `target/EazyNick-vX.X.X-dev.jar`

### Compiling to jar manually
1. Compile the sources using ``mvn clean compile`` (in IntelliJ IDEA: Maven menu --> Lifecycle --> clean/compile)
2. Open `target/classes/plugin.yml` and replace `0.0.0` with the current version (located in `.github/.version`)
3. Run ``mvn -B package --file pom.xml`` (in IntelliJ IDEA: Maven menu --> Lifecycle --> package)
4. The jar will be saved at `target/eazynick-0.0.0.jar`

## License
EazyNick is licensed under the permissive MIT license. Please see [`LICENSE`](https://github.com/JustixDevelopment/EazyNick/blob/master/LICENSE) for more info.