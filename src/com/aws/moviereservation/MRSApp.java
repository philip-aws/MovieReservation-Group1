package com.aws.moviereservation;

import java.time.LocalDate;
import java.time.LocalTime;
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

	private void processReservation() {
			while (true) {
				System.out.println("Input (CinemaNumber,TimeStart) to reserve or Input c to cancel.");
				try {
					String input = scanner.nextLine().trim();
					if (input.equalsIgnoreCase("c")) {
						break; // Cancel and go back to the input date
					}

					String[] parts = input.split(",");
					if (parts.length != 2) {
						System.err.println("Invalid input format. Please use 'CinemaNumber,Time' format.");
						continue; // Retry the input
					}

					try {
						byte cinemaNumber = Byte.parseByte(parts[0].trim());
						String timeStart = parts[1].trim();
						LocalTime timeStartFormatted = LocalTime.parse(timeStart, DateTimeFormatter.ofPattern("HH:mm"));


						MovieSchedule selectedSchedule = null;
						for (MovieSchedule schedule : movieSchedules.values()) {
							if (cinemaNumber == schedule.getCinemaNo() && timeStartFormatted.equals(schedule.getTimeStart())) {
								selectedSchedule = schedule;
								break;
							}
						}

						if (selectedSchedule == null) {
							System.err.println("No existing movie.");
						} else {
							if (proceedToSeatLayout(selectedSchedule)) {
								break; // Exit the input loop
							}
						}
					} catch (NumberFormatException e) {
						System.err.println("Invalid cinema number format. Please enter a valid cinema number.");
					}
				} catch (DateTimeParseException err) {
					System.err.println("Invalid time format");
				}
			}
		}

}

