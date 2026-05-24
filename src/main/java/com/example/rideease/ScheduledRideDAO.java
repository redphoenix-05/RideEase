package com.example.rideease;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class ScheduledRideDAO {

    private final String DB_URL = "jdbc:mysql://localhost:3306/rideease-1";
    private final String DB_USER = "root";
    private final String DB_PASS = "ariyan123";

    public void addScheduledRide(String userId, String startLocation, String endLocation, String journeyTime, String vehicleType, double fare) {
        String sql = "INSERT INTO scheduledride (user_id, start_location, end_location, journey_time, vehicle_type, fare, payment_status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, startLocation);
            stmt.setString(3, endLocation);
            stmt.setString(4, journeyTime);
            stmt.setString(5, vehicleType);
            stmt.setDouble(6, fare);
            stmt.setString(7, "Paid");

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
