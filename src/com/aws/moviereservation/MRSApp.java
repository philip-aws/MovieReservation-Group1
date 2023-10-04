package com.aws.moviereservation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MRSApp {
	Scanner scanner = new Scanner(System.in);
	private HashMap<Integer, MovieSchedule> movieSchedules = new HashMap<Integer, MovieSchedule>();
	private HashMap<Long, MovieReservation> movieReservation = new HashMap<Long, MovieReservation>();
	private long RESERVATION_NUMBER = 11111;
	private String movieScheduleFilePath = "D://MovieSchedule.csv";
	private String movieTicketFilePath = "D://MovieTicket.csv";

	private int displayMenuAndGetChoice() {
		System.out.println("\n[Press 1] - To Reserve Seats");
		System.out.println("[Press 2] - To Cancel Reservation");
		System.out.println("\nSelect from the options above");
		try {
			return scanner.nextInt();
		} catch (InputMismatchException err) {
			return 0;
		}
	}

	private LocalDate getInputDate() {
		System.out.println("\nMovie Schedule\n");
		while (true) {
			System.out.println("Input the date (yyyy-mm-dd) to reserve or Input c to cancel.");
			String dateInput = scanner.nextLine().trim();
			if (dateInput.equalsIgnoreCase("c")) {
				return null; // User canceled date input
			}

			try {
				return LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			} catch (DateTimeParseException e) {
				System.err.println("Invalid date format. Please use 'yyyy-MM-dd'.");
			}
		}
	}

	private boolean getMovieSchedules(LocalDate date) {
		boolean foundMovies = false; // Flag to indicate if movies were found for the given date

		for (MovieSchedule schedule : movieSchedules.values()) {
			if (date.equals(schedule.getShowingDateTime())) {
				if(foundMovies == false) {
					System.out.printf("%n%15s %n","Movie Showings on "+date);		
					System.out.printf("%-9s %-12s %-13s %-8s %n","Cinema#","Time Start","Show Type","Title");
				}
				System.out.printf("   %-2d   |   %-5s   |   %-6s   |   %-8s   %n", schedule.getCinemaNo(), schedule.getTimeStart(), schedule.isPremierFlag() ? "Premier" : "Regular", schedule.getMovieTitle());
				System.out.println();
				foundMovies = true; // Movies were found for the given date
			}
		}
		return foundMovies; // Return true if movies were found, false otherwise
	}
}
