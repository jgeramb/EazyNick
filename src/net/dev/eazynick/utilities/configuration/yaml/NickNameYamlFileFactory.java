package net.dev.eazynick.utilities.configuration.yaml;

import org.bukkit.configuration.file.YamlConfiguration;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.configuration.BaseFileFactory;

public class NickNameYamlFileFactory implements BaseFileFactory<YamlConfiguration> {

	@Override
	public NickNameYamlFile createConfigurationFile(EazyNick eazyNick) {
		//Initialize 'nickNames.yml' file
		NickNameYamlFile nickNameYamlFile = new NickNameYamlFile(eazyNick);
		nickNameYamlFile.initConfiguration();
		
		return nickNameYamlFile;
	}

}
