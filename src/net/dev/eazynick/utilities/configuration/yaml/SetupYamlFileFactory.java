package net.dev.eazynick.utilities.configuration.yaml;

import org.bukkit.configuration.file.YamlConfiguration;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.configuration.BaseFileFactory;

public class SetupYamlFileFactory implements BaseFileFactory<YamlConfiguration> {

	@Override
	public SetupYamlFile createConfigurationFile(EazyNick eazyNick) {
		SetupYamlFile setupYamlFile = new SetupYamlFile(eazyNick);
		setupYamlFile.initConfiguration();
		
		return setupYamlFile;
	}

}
