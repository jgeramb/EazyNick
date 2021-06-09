package net.dev.eazynick.utilities.configuration.yaml;

import org.bukkit.configuration.file.YamlConfiguration;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.configuration.BaseFileFactory;

public class GUIYamlFileFactory implements BaseFileFactory<YamlConfiguration> {

	@Override
	public GUIYamlFile createConfigurationFile(EazyNick eazyNick) {
		//Initialize 'guis.yml' file
		GUIYamlFile guiYamlFile = new GUIYamlFile(eazyNick, "guis.yml");
		guiYamlFile.initConfiguration();
		
		return guiYamlFile;
	}

}
