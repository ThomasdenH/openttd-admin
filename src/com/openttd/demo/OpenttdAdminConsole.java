package com.openttd.demo;

import com.openttd.admin.OpenttdAdmin;
import com.openttd.admin.model.Company;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openttd.admin.event.DateEvent;
import com.openttd.admin.event.DateEventListener;
import com.openttd.network.core.Configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Calendar;

public class OpenttdAdminConsole extends OpenttdAdmin implements DateEventListener {

	private static final Logger log = LoggerFactory.getLogger(OpenttdAdminConsole.class);
	private static final Path gameNumberPath = FileSystems.getDefault().getPath("saves", "game_number.txt");
	private static final int COUNTER_AUTOSAVE = 100;
	private int gameNumber = -1;
	private int counter = 0;

	private ArrayList<Calendar> dates = new ArrayList<>();
	private ArrayList<Company> companies = new ArrayList<>();
	private ArrayList<long[]> companyValues = new ArrayList<>();

	public OpenttdAdminConsole(Configuration configuration) {
		super(configuration);
	}

	@Override
	public void onDateEvent(DateEvent dateEvent) {
		Calendar calendar = dateEvent.getOpenttd().getDate();
		if (!dates.isEmpty() && calendar.before(dates.get(dates.size() - 1))) {
			// Game reset
			try {
				saveGame();
				gameNumber++;
				saveGameNumber();
			} catch (IOException e) {
				e.printStackTrace();
			}
			dates = new ArrayList<>();
			companies = new ArrayList<>();
			companyValues = new ArrayList<>();
		}

		if (counter > COUNTER_AUTOSAVE) {
			counter = 0;
			try {
				saveGame();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		counter++;

		// Save date
		dates.add(calendar);

		// Update list
		for (Company company : dateEvent.getOpenttd().getCompanies()) {
			boolean found = false;
			for (Company current : companies)
				if (current.getId() == company.getId()) {
					found = true;
					break;
				}
			if (!found)
				companies.add(company);
		}

		// Save value
		long[] values = new long[companies.size()];
		for (int i = 0; i < values.length; i++) {
			values[i] = dateEvent.getOpenttd().getCompany(companies.get(i).getId()).getValue();
		}
		companyValues.add(values);
	}

	private void saveGame() throws IOException {
		Path gamePath = FileSystems.getDefault().getPath("saves", "save_" + gameNumber + ".txt");
		Files.deleteIfExists(gamePath);
		BufferedWriter writer = Files.newBufferedWriter(gamePath, StandardOpenOption.CREATE);
		for (Company company : this.companies)
			writer.write(company.getName() + " (" + company.getId() + ")\t");
		writer.write("\n");
		for (int i = 0; i < dates.size(); i++) {
			writer.write(dates.get(i).getTimeInMillis() + "\t");
			for (long value : companyValues.get(i))
				writer.write(value + "\t");
			writer.write("\n");
		}
		writer.close();
	}

	private void saveGameNumber() throws IOException {
		Files.deleteIfExists(gameNumberPath);
		BufferedWriter writer = Files.newBufferedWriter(gameNumberPath);
		writer.write(this.gameNumber + "\n");
		writer.close();
	}

	private void readGameNumber() throws IOException {
		BufferedReader reader = Files.newBufferedReader(gameNumberPath);
		this.gameNumber = Integer.parseInt(reader.readLine().trim());
		reader.close();
	}

	/**
	 * Console output demo
	 */
	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		configuration.password = "banaan";

		OpenttdAdminConsole simpleAdmin = new OpenttdAdminConsole(configuration);
		try {
			simpleAdmin.readGameNumber();
		} catch (IOException e) {
			simpleAdmin.gameNumber = 0;
		}
		simpleAdmin.addListener(DateEvent.class, simpleAdmin);
		simpleAdmin.startup();
		log.info("Openttd admin started");
	}
}
