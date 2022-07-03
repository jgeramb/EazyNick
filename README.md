![Logo](https://media.discordapp.net/attachments/845301622632611880/855464034341879838/EazyNick.png?width=96&height=96)
# EazyNick

## Useful links
> [Discord](https://discord.justix-dev.com)

> The documentation is currently unavailable

> [Support my work](https://www.paypal.me/JustixDevelopment)

## Compiling
### Importing the run configuration in IntelliJ IDEA (Mac OS X and Linux only)
1. Make sure you have `sed` and `cat` installed and you have a `Java 8 or later` installation in your bash/zsh environment
2. Open `.run/compile.run.xml` in IntelliJ IDEA
3. Click on `Open Run/Debug Configurations...`
4. Click `OK`

### Compiling to jar using the run configuration
Simply run the configuration, the jar will be saved as `target/EazyNick-vX.X.X-dev.jar`

### Compiling to jar manually (for Windows)
1. Compile the sources using ``mvn clean compile`` (in IntelliJ IDEA: Maven menu --> Lifecycle --> clean/compile)
2. Open `target/classes/plugin.yml` and replace `0.0.0` with the current version (located in `.github/.version`)
3. Run ``mvn -B package --file pom.xml`` (in IntelliJ IDEA: Maven menu --> Lifecycle --> package)
4. The jar will be saved as `target/eazynick-0.0.0.jar`

## Permissions
### eazynick.help
> Allows you to use /eazynick

### eazynick.reload
> Allows you to use /reloadconfig and /eazynick reload

### eazynick.support
> Allows you to use /eazynick support

### eazynick.updatecheck
> Allows you to use /nickupdatecheck

### eazynick.real
> Allows you to use /realname

### eazynick.nick.random
> Allows you to use /nick

### eazynick.nick.custom
> Allows you to use /nick «name» + allows you to enter a name in the bookgui

### eazynick.nick.reset
> Allows you to use /resetname and /unnick

### eazynick.skin.random
> Allows you to use /changeskin

### eazynick.skin.custom
> Allows you to use /changeskin «name»

### eazynick.skin.reset
> Allows you to use  /resetskin

### eazynick.skin.fix
> Allows you to use /fixskin

### eazynick.gui.classic
> Allows you to use /nickgui

### eazynick.gui.list
> Allows you to use /nicklist

### eazynick.gui.book
> Allows you to use /bookgui

### eazynick.other.nick.random
> Allows you to use /nickother «player»

### eazynick.other.nick.custom
> Allows you to use /nickother «player» «name»

### eazynick.other.nick.reset
> Allows you to reset another players identity using /nickother «player»

### eazynick.other.skin.random
> Allows you to use /changeskinother «player»

### eazynick.other.skin.custom
> Allows you to use /changeskinother «player» «name»

### eazynick.other.skin.reset
> Allows you to use /resetskinother «player»

### eazynick.nickedplayers
> Allows you to use /nickedplayers

### eazynick.item
> Lets you receive the nick item (if enabled in setup.yml) and allows you to use /togglebungeenick

### eazynick.actionbar.other
> Shows the alternate actionbar message

### eazynick.bypasslobbymode
> Lets you bypass the LobbyMode

### eazynick.bypass
> See every player undisguised (if enabled in setup.yml)

## License
EazyNick is licensed under the permissive MIT license. Please see [`LICENSE`](https://github.com/JustixDevelopment/EazyNick/blob/master/LICENSE) for more info.