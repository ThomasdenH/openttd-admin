package com.openttd.demo;

import com.openttd.admin.OpenttdAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openttd.admin.event.ChatEvent;
import com.openttd.admin.event.ChatEventListener;
import com.openttd.admin.event.ClientEvent;
import com.openttd.admin.event.ClientEventListener;
import com.openttd.admin.event.CompanyEvent;
import com.openttd.admin.event.CompanyEventListener;
import com.openttd.admin.event.DateEvent;
import com.openttd.admin.event.DateEventListener;
import com.openttd.network.core.Configuration;

public class OpenttdAdminConsole extends OpenttdAdmin implements DateEventListener, ClientEventListener, ChatEventListener, CompanyEventListener {

	private static final Logger log = LoggerFactory.getLogger(OpenttdAdminConsole.class);

	public OpenttdAdminConsole(Configuration configuration) {
		super(configuration);
	}

	@Override
	public void onDateEvent(DateEvent dateEvent) {
		log.info("Date event " + dateEvent);
	}

	@Override
	public void onClientEvent(ClientEvent clientEvent) {
		log.info("Client event " + clientEvent);
	}

	@Override
	public void onChatEvent(ChatEvent chatEvent) {
		log.info("Chat event : " + chatEvent);
	}

	@Override
	public void onCompanyEvent(CompanyEvent companyEvent) {
		log.info("Company event : " + companyEvent);
	}

	/**
	 * Console output demo
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		
		try {
			String url = args[0];
			configuration.host = url.split(":")[0];
			configuration.adminPort = new Integer(url.split(":")[1]);
			configuration.password = args[1];
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.error("Usage: java -jar openttd-admin.jar localhost:3977 admin_password");
			log.error("See openttd.cfg to set your server's admin_password first !");
			System.exit(0);
		}

		OpenttdAdminConsole simpleAdmin = new OpenttdAdminConsole(configuration);
		simpleAdmin.addListener(DateEvent.class, simpleAdmin);
		simpleAdmin.addListener(ChatEvent.class, simpleAdmin);
		simpleAdmin.addListener(ClientEvent.class, simpleAdmin);
		simpleAdmin.addListener(CompanyEvent.class, simpleAdmin);
		simpleAdmin.startup();
		log.info("Openttd admin started");
	}
}
