package com.aws.moviereservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class MovieReservation {
	private long reservationNo;
	private LocalDate showingDateTime;
	private byte cinemaNo;
	private LocalTime timeStart;
	private ArrayList<String> reservedSeats;
	private float totalPrice;

	public MovieReservation(long reservationNo, LocalDate showingDateTime, byte cinemaNo, LocalTime timeStart,
			ArrayList<String> reservedSeats, float totalPrice) {
		this.reservationNo = reservationNo;
		this.showingDateTime = showingDateTime;
		this.cinemaNo = cinemaNo;
		this.timeStart = timeStart;
		this.reservedSeats = reservedSeats;
		this.totalPrice = totalPrice;
	}

	public long getReservationNo() {
		return reservationNo;
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

	public ArrayList<String> getReservedSeats() {
		return reservedSeats;
	}

	public float getTotalPrice() {
		return totalPrice;
	}
}