package net.dev.eazynick.utilities.configuration.yaml;

import org.bukkit.configuration.file.YamlConfiguration;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.configuration.BaseFileFactory;

public class SavedNickDatasYamlFileFactory implements BaseFileFactory<YamlConfiguration> {

	@Override
	public SavedNickDatasYamlFile createConfigurationFile(EazyNick eazyNick) {
		//Initialize 'savedNickDatas.yml' file
		SavedNickDatasYamlFile savedNickDatasYamlFile = new SavedNickDatasYamlFile(eazyNick);
		savedNickDatasYamlFile.initConfiguration();
		
		return savedNickDatasYamlFile;
	}

}
