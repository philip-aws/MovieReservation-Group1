package com.aws.moviereservation;

import java.time.LocalDate;
import java.time.LocalTime;

public class MovieSchedule {
	private final float SENIOR_DISCOUNT_PERCENTAGE = 0.20f;

	private LocalDate showingDateTime;
	private byte cinemaNo;
	private LocalTime timeStart;
	private boolean premierFlag;
	private String movieTitle;
	private String movieLength;
	private int availableSeats;
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
		this.availableSeats = 40;
		this.seatAvailability = new boolean[8][5];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 5; j++) {
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
			this.price = 500.00f;
		} else {
			this.price = 350.00f;
		}
	}
}
