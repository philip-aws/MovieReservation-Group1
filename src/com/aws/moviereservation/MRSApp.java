package com.aws.moviereservation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
			String userInput = scanner.nextLine();
			if (userInput.isEmpty()) {
				throw new NumberFormatException();
			} else {
				int intUserInput = Integer.parseInt(userInput);
				return intUserInput;
			}
		} catch (NumberFormatException err) {
			return 0;
		}
	}

	private LocalDate getInputDate() {
		System.out.println("\nMovie Schedule\n");
		while (true) {
			System.out.println("Input the date (yyyy-mm-dd) to reserve or Input c to cancel :");
			String dateInput = scanner.nextLine().trim();
			if (dateInput.equalsIgnoreCase("C")) {
				return null; // User canceled date input
			}

			try {
				return LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			} catch (DateTimeParseException e) {
				System.err.println("Invalid date format. Please use 'yyyy-mm-dd'.");
			}
		}
	}

	private boolean getMovieSchedules(LocalDate date) {
		boolean foundMovies = false; // Flag to indicate if movies were found for the given date

		for (MovieSchedule schedule : movieSchedules.values()) {
			if (date.equals(schedule.getShowingDateTime())) {
				if(foundMovies == false) {
					System.out.printf("%n%15s %n", "Movie Showings on " + date + "\n");		
					System.out.printf("%-9s %-12s %-17s %-8s %n", "Cinema#", "Time Start", "Show Type", "Title");
				}
				System.out.printf("   %-5d|   %-8s|   %-9s|   %-11s%n", schedule.getCinemaNo(), schedule.getTimeStart(), schedule.isPremierFlag() ? "Premier" : "Regular", schedule.getMovieTitle());
				foundMovies = true; // Movies were found for the given date
			}
		}
		return foundMovies; // Return true if movies were found, false otherwise
	}

	private void processReservation(LocalDate date) {
		while (true) {
			System.out.println("Input (CinemaNumber,TimeStart) to reserve or Input c to cancel :");
			try {
				String input = scanner.nextLine().trim();
				if (input.equalsIgnoreCase("C")) {
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

					if(cinemaNumber < 1 || cinemaNumber > 4) {
						throw new NumberFormatException();
					}

					for (MovieSchedule schedule : movieSchedules.values()) {
						if (date.equals(schedule.getShowingDateTime()) && cinemaNumber == schedule.getCinemaNo() && timeStartFormatted.equals(schedule.getTimeStart())) {
							selectedSchedule = schedule;
							break;
						}
					}

					if (selectedSchedule == null) {
						System.err.println("No existing movie.");
					} else {
						long minutesUntilShow = ChronoUnit.MINUTES.between(LocalTime.now(), selectedSchedule.getTimeStart());
						if ((minutesUntilShow <= 120 && LocalDate.now().equals(selectedSchedule.getShowingDateTime())) || LocalDate.now().isAfter(selectedSchedule.getShowingDateTime())) {
							System.err.println("You can't book this movie now.");
							continue;
						}
						if (proceedToSeatLayout(selectedSchedule)) {
							break; // Exit the input loop
						}
					}
				} catch (NumberFormatException e) {
					System.err.println("Invalid cinema number. Please enter a valid cinema number.");
				}
			} catch (DateTimeParseException err) {
				System.err.println("Invalid time format.");
			}
		}
	}

	private boolean proceedToSeatLayout(MovieSchedule schedule) {
		System.out.println("\nSeat Layout for " + schedule.getMovieTitle() + " @ " + schedule.getTimeStart() + "\n");
		System.out.printf("%38s %n %n","******** Screen ********");

		String[] letter = {"A","B","C","D","E","F","G","H"};

		for (int i = 0; i < 8; i++) {

			if(i == 7) {
				System.out.printf("%-8s ","Exit |");
			} else {
				System.out.printf("    %-4s "," |");
			}
			for (int j = 1; j <=5; j++) {
				if(schedule.isSeatAvailable(letter[i] + Integer.toString(j))) {
					System.out.printf("  %-4s","[" + letter[i] + Integer.toString(j) + "]");
				} else {
					System.out.printf("  %-4s","[**]");
				}
			}
			System.out.printf("%n");
		}
		System.out.println("\nLegend: [Xn] = available seat, [**] = reserved seat\n");

		while (true) {
			if (schedule.getAvailableSeats() > 0) {
				try {
					System.out.println("Input seats (Seat1,Seat2,...) to be reserved or Input c to cancel :");
					ArrayList<String> seatInput = new ArrayList<>(); // seats reserved will be put here
					String seatNumbers = scanner.nextLine().trim();

					if (seatNumbers.equalsIgnoreCase("C")) {
						return true; // Cancel and go back to menu
					}

					String[] seatArray = seatNumbers.split(",");
					for (String seat : seatArray) {
						seatInput.add(seat.trim().toUpperCase());
					}

					boolean seatsAvailable = true;
					for (String seat : seatInput) {
						if (!schedule.isSeatAvailable(seat)) {
							System.err.println("Seat " + seat + " is not available.");
							seatsAvailable = false;
						}
					}

					if (seatsAvailable) {                     
						int numberOfSenior = 0;
						if(!schedule.isPremierFlag()) {
							while(true) {
								System.out.println("Input the number of senior citizens or type c to cancel :");
								String seniorInput = scanner.nextLine().trim();

								if (seniorInput.equalsIgnoreCase("C")) {
									return true;
								}

								try {
									numberOfSenior = Integer.parseInt(seniorInput);
									if(numberOfSenior > seatInput.size()) {
										System.err.println("Input exceeds number of seats.");
										continue;
									}
									break; // Exit the loop when a valid integer is entered
								} catch (NumberFormatException e) {
									System.err.println("Invalid input. Please enter a valid number.");
								}
							}
						}

						while(true) {
							System.out.println("Total price for Ticket is Php " + schedule.calculatePrice(seatArray.length, numberOfSenior) + " Confirm? (Y/N) :");
							String confirmReservation = scanner.nextLine();
							if (confirmReservation.equalsIgnoreCase("Y")) {
								// Reserve the selected seats
								schedule.reserveSeats(seatInput);
								// Create a MovieReservation Ticket
								MovieReservation reservation = new MovieReservation(RESERVATION_NUMBER, schedule.getShowingDateTime(), schedule.getCinemaNo(), schedule.getTimeStart(), seatInput, schedule.calculatePrice(seatArray.length, numberOfSenior));
								movieReservation.put(RESERVATION_NUMBER++, reservation);

								// CREATE CSV FILE FOR TICKET HERE
								createMovieTicketCSV(reservation);
								return true;

							} else if (confirmReservation.equalsIgnoreCase("N")) {
								break;
							} else {
								System.err.println("Please input 'Y' or 'N'");
							}
						}
					}
				} catch (NumberFormatException err) {
					System.err.println("Please input the correct seat number format.");
				} catch (IllegalArgumentException err) {
					System.err.println(err.getMessage());
				}
			} else {
				System.err.println("Seats are all reserved.");
				return false;
			}
		}
	}

	private void createMovieTicketCSV(MovieReservation reservation) {
		try {
			// Create a FileWriter object for the CSV file
			FileWriter writer = new FileWriter(movieTicketFilePath, true);

			writer.write("\"" + String.valueOf(reservation.getReservationNo() + "\"," +
					"\"" + reservation.getShowingDateTime().toString()) + "\"," +
					"\"" + String.valueOf(reservation.getCinemaNo()) + "\"," +
					"\"" + reservation.getTimeStart().toString() + "\"," +
					"\"" + String.join(",", reservation.getReservedSeats()) + "\"," +
					"\"" + String.valueOf(reservation.getTotalPrice()) + "\"\n"
					);
			// Close the FileWriter
			writer.close();
			System.out.println("Your ticket number is \"" + reservation.getReservationNo() + "\"");

		} catch (IOException e) {
			System.err.println("Error writing to CSV file: " + e.getMessage());
		}
	}

	private void cancelMovieReservation() {
		System.out.println("\nCancel Reservation\n");
		while(true) {
			System.out.println("Input Reservation Number or Input c to cancel :");
			try {
				String reservationNumberInput = scanner.nextLine();

				if(reservationNumberInput.equalsIgnoreCase("C")) {
					break;
				}

				long reservationNumber = Long.valueOf(reservationNumberInput);

				MovieReservation reservation = movieReservation.get(reservationNumber);

				if (reservation != null && reservationNumber == reservation.getReservationNo()) {
					while(true) {
						System.out.println("Are you sure you want to cancel Ticket # \"" + reservation.getReservationNo() + "\" (Y/N) ? :");
						String cancelReservation = scanner.nextLine();
						if(cancelReservation.equalsIgnoreCase("Y")) {
							for(MovieSchedule schedule : movieSchedules.values()) {
								if (reservation.getCinemaNo() == schedule.getCinemaNo()
										&& reservation.getShowingDateTime().equals(schedule.getShowingDateTime())
										&& reservation.getTimeStart().equals(schedule.getTimeStart())) {
									schedule.cancelReservedSeats(reservation.getReservedSeats());
									break;
								}
							}
							deleteMovieReservation(reservation.getReservationNo()); // delete reservation from the csv
							movieReservation.remove(reservation.getReservationNo()); // remove reservation from the hashmap
							System.err.println("Ticket \"" + reservationNumber + "\" Has Been Canceled.\n");
							return;
						} else if (cancelReservation.equalsIgnoreCase("N")) {
							break;
						} else {
							System.err.println("Please input 'Y' or 'N'.");
						}
					}
				} else {
					System.err.println("Reservation Number does not exist.");
				}
			} catch (NumberFormatException err) {
				System.err.println("Invalid input. Please input a valid Reservation Number.");
			}
		}
	}

	private void deleteMovieReservation(long reservationNumberToDelete) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(movieTicketFilePath));
			String line = "";
			String reservationNumber = Long.toString(reservationNumberToDelete);
			//This is your buffer, where you are writing all your lines to
			ArrayList<String> fileContents = new ArrayList<String>();

			//loop through each line
			while ((line = br.readLine()) != null) {
				//if the line we're on contains the text we don't want to add, skip it
				if (line.contains(reservationNumber)) {
					//skip
					continue;
				}
				//if we get here, we assume that we want the text, so add it
				fileContents.add(line);
			}

			br.close();

			//create a writer
			BufferedWriter bw = new BufferedWriter(new FileWriter(movieTicketFilePath));

			//loop through our buffer
			for (String s : fileContents) {
				//write the line to our file
				bw.write(s);
				bw.newLine();
			}

			//close the writer
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void initializeMovieSchedulesFromCSV() {
		try (BufferedReader br = new BufferedReader(new FileReader(movieScheduleFilePath))) {
			int key = 1; // Key to identify each movie schedule
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split("\",\"");

				if (parts.length == 6) {
					try {
						LocalDate date = LocalDate.parse(parts[0].trim().replace("\"", ""), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
						byte cinemaNo = Byte.parseByte(parts[1].trim());
						LocalTime timeStart = LocalTime.parse(parts[2].trim());
						boolean isPremierFlag = Boolean.parseBoolean(parts[3].trim());
						String movieTitle = parts[4].trim();
						String movieLength = parts[5].trim().replace("\"", "");

						MovieSchedule schedule = new MovieSchedule(date, cinemaNo, timeStart, isPremierFlag, movieTitle, movieLength);
						movieSchedules.put(key++, schedule);
					} catch (DateTimeParseException | NumberFormatException e) {
						System.out.println("Error parsing movie schedule data from CSV : " + e.getMessage());
					}
				} else {
					System.err.println("Invalid CSV line : " + line);
				}
			}
		} catch (IOException e) {
			System.err.println("Error reading CSV file : " + e.getMessage());
		}
	}

	private void initializeMovieReservationFromCSV() {
		long maxReservationNumber = RESERVATION_NUMBER;

		try (BufferedReader br = new BufferedReader(new FileReader(movieTicketFilePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split("\",\"");

				if (parts.length == 6) {
					try {
						long reservationNo = Long.parseLong(parts[0].trim().replace("\"", ""));
						LocalDate showingDateTime = LocalDate.parse(parts[1].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
						byte cinemaNo = Byte.parseByte(parts[2].trim());
						LocalTime timeStart = LocalTime.parse(parts[3].trim());
						ArrayList<String> reservedSeats = new ArrayList<>(Arrays.asList(parts[4].trim().split(",")));
						float totalPrice = Float.parseFloat(parts[5].trim().replace("\"", ""));

						MovieReservation reservation = new MovieReservation(reservationNo, showingDateTime, cinemaNo, timeStart, reservedSeats, totalPrice);
						movieReservation.put(reservationNo, reservation);

						if (reservationNo > maxReservationNumber) {
							maxReservationNumber = reservationNo;
						}

					} catch (DateTimeParseException | NumberFormatException e) {
						System.err.println("Error parsing movie reservation data from CSV : " + e.getMessage());
					}
				} else {
					System.err.println("Invalid CSV line : " + line);
				}
				// Initializing reservation Number +1 from the last reservation Number
				RESERVATION_NUMBER = maxReservationNumber + 1;
			}

			// Reserved seats when running the system
			for(MovieReservation movieReservation : movieReservation.values()) {
				for(MovieSchedule schedule : movieSchedules.values()) {
					if (movieReservation.getCinemaNo() == schedule.getCinemaNo()
							&& movieReservation.getShowingDateTime().equals(schedule.getShowingDateTime())
							&& movieReservation.getTimeStart().equals(schedule.getTimeStart())) {
						schedule.reserveSeats(movieReservation.getReservedSeats());
						break;
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Error reading CSV file : " + e.getMessage());
		}
	}

	public void run() {
		System.out.println("Welcome to CinemaWorld Cebu Branch \n\n");
		initializeMovieSchedulesFromCSV(); // add schedules from csv to hashmap

		File f1 = new File(movieTicketFilePath);
		try {
			if(!f1.createNewFile()) {
				initializeMovieReservationFromCSV(); // add reservations from csv to hashmap
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		while(true) {
			int menu = displayMenuAndGetChoice();

			switch (menu) {
			case 1:
				while(true) {
					LocalDate date = getInputDate();
					if (date == null) {
						break; // Back to menu
					}

					if (getMovieSchedules(date)) {
						processReservation(date);
						break; // Back to menu
					} else {
						System.err.println("No movies scheduled for this date. Please try another date.");
						continue; // Back to getInputDate()
					}
				}
				break;
			case 2:
				cancelMovieReservation();
				break;
			default:
				System.err.println("Invalid input. Please try again.");
				break;
			}
		}
	}
}