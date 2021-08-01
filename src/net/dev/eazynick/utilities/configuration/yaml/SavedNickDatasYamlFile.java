package net.dev.eazynick.utilities.configuration.yaml;

import net.dev.eazynick.EazyNick;

public class SavedNickDatasYamlFile extends YamlFile {

	public SavedNickDatasYamlFile(EazyNick eazyNick) {
		super(eazyNick, "", "savedNickDatas.yml");
	}

	@Override
	public void setDefaults() {
	}
	
}