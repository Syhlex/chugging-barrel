package com.chuggingbarrel;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ChuggingBarrelPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ChuggingBarrelPlugin.class);
		RuneLite.main(args);
	}
}