package com.aws.moviereservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class MovieSchedule {
	private static final float SENIOR_DISCOUNT_PERCENTAGE = 0.20f;
	private static final float PREMIER = 500.00f;
	private static final float REGULAR = 350.00f;
	public static final int ROW = 8;
	public static final int COLUMN = 5;

	private LocalDate showingDateTime;
	private byte cinemaNo;
	private LocalTime timeStart;
	private boolean premierFlag;
	private String movieTitle;
	private String movieLength;
	private boolean[][] seatAvailability;
	private float price;

	public MovieSchedule(LocalDate showingDateTime, byte cinemaNo, LocalTime timeStart, boolean premierFlag,
			String movieTitle, String movieLength) {
		this.showingDateTime = showingDateTime;
		this.cinemaNo = cinemaNo;
		this.timeStart = timeStart;
		this.premierFlag = premierFlag;
		this.movieTitle = movieTitle;
		this.movieLength = movieLength;
		this.seatAvailability = new boolean[ROW][COLUMN];
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COLUMN; j++) {
				seatAvailability[i][j] = true;
			}
		}
		setPrice();
	}

	public LocalDate getShowingDateTime() {
		return showingDateTime;
	}

	public byte getCinemaNo() {
		return cinemaNo;
	}

	public LocalTime getTimeStart() {
		return timeStart;
	}

	public boolean isPremierFlag() {
		return premierFlag;
	}

	public String getMovieTitle() {
		return movieTitle;
	}

	public String getMovieLength() {
		return movieLength;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice() {
		if(this.isPremierFlag()) {
			this.price = PREMIER;
		} else {
			this.price = REGULAR;
		}
	}

	// Method to check if a seat is available
	public boolean isSeatAvailable(String seat) {
		// Extract row and column information from the seat string (e.g., "F1" => row=5, col=0)
		char rowChar = seat.charAt(0);
		int col = Integer.parseInt(seat.substring(1)) - 1; // Subtract 1 to convert to 0-based index

		int row = rowChar - 'A'; // Convert 'A' to 0, 'B' to 1, and so on

		// Check if the seat is within valid range and available
		if(row < 0 || row >= ROW || col < 0 || col >= COLUMN) {
			throw new IllegalArgumentException("Please pick seats from the seat layout above.");
		} else if (row >= 0 && row < ROW && col >= 0 && col < COLUMN && seatAvailability[row][col]) {
			return true;
		} else {
			return false;
		}
	}

	// Method to reserve seats
	public void reserveSeats(ArrayList<String> seatsToReserve) {
		for (String seat : seatsToReserve) {
			// Extract row and column information from the seat string
			char rowChar = seat.charAt(0);
			int col = Integer.parseInt(seat.substring(1)) - 1; // Subtract 1 to convert to 0-based index

			int row = rowChar - 'A'; // Convert 'A' to 0, 'B' to 1, and so on

			// Mark the seat as unavailable
			seatAvailability[row][col] = false;
		}
	}

	// Method to cancel reserved seats
	public void cancelReservedSeats(ArrayList<String> seatsToReserve) {
		for (String seat : seatsToReserve) {
			// Extract row and column information from the seat string
			char rowChar = seat.charAt(0);
			int col = Integer.parseInt(seat.substring(1)) - 1; // Subtract 1 to convert to 0-based index

			int row = rowChar - 'A'; // Convert 'A' to 0, 'B' to 1, and so on

			// Mark the seat as unavailable
			seatAvailability[row][col] = true;
		}
	}

	public int getAvailableSeats() {
		int count = 0;
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COLUMN; j++) {
				if (seatAvailability[i][j]) {
					count++;
				}
			}
		}
		return count;
	}

	public float calculatePrice(int numberOfSeats, int numberOfSenior) {
		float basePrice = numberOfSeats * this.getPrice(); // Calculate the base price
		float seniorDiscount = (numberOfSenior * this.getPrice() * SENIOR_DISCOUNT_PERCENTAGE); // Calculate senior discount
		return basePrice - seniorDiscount; // Apply the senior discount
	}
}
