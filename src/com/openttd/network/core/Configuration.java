package com.openttd.network.core;

public class Configuration {
	public String host = "localhost";
	public Integer adminPort = 3977;
	public Integer clientPort = 3979;
	public String password = "";
	public String name = "openttd";
	public String openttdVersion = "1.3.1";
	/* See rev.cpp.in and .ottdrev */
	private long revision = 25310;
	public long openttdNewgrfVersion = 1 << 28 | 3 << 24 | 1 << 20 | 1 << 19 | (revision & ((1 << 19) - 1));
}
