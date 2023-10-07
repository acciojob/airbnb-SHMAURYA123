package com.driver.controllers;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class HotelManagementRepository {
    HashMap<String,Hotel> hoteldb=new HashMap<>();
    HashMap<Integer,User> userdb=new HashMap<>();
    HashMap<String,Booking> bookingdb=new HashMap<>();
    HashMap<Integer,Integer> countofBooking=new HashMap<>(); // p.k -> aadhar card no , value->no of booking
    public String addHotel(Hotel hotel) {
      if(hotel==null||hotel.getHotelName()==null){
          return "FAILURE";
      }
       if(hoteldb.containsKey(hotel.getHotelName())){
           return "FAILURE";
       }
       hoteldb.put(hotel.getHotelName(),hotel);
       return "SUCCESS";
    }

    public Integer addUser(User user) {
        userdb.put(user.getaadharCardNo(),user);
        return user.getaadharCardNo();
    }

    public String getHotelWithMostFacilities() {
        int facility=0;
        String hotelName="";
        for(Hotel h:hoteldb.values()){
            if(h.getFacilities().size()>facility){
                facility=h.getFacilities().size();
                hotelName=h.getHotelName();
            }
            else if(h.getFacilities().size()==facility){
                if(h.getHotelName().compareTo(hotelName)<0){
                    hotelName=h.getHotelName();
                }
            }
        }
        return hotelName;
    }

    public int bookARoom(Booking booking) {
        String key = UUID.randomUUID().toString();
        booking.setBookingId(key);

        String hotelName = booking.getHotelName();
        Hotel h = hoteldb.get(hotelName);
        int availableRoom = h.getAvailableRooms();
        if (booking.getNoOfRooms() > availableRoom) {
            return -1;
        }

        // find amount to be paid
        int amountpaid = booking.getNoOfRooms() * h.getPricePerNight();
        booking.setAmountToBePaid(amountpaid);
        h.setAvailableRooms(h.getAvailableRooms() - booking.getNoOfRooms());
        bookingdb.put(key, booking);
        hoteldb.put(hotelName, h);
        int aadharCard = booking.getBookingAadharCard();
        Integer currBooking = countofBooking.get(aadharCard);
        if (countofBooking.containsKey(currBooking)) {
            countofBooking.put(aadharCard, currBooking + 1);
        } else {
            countofBooking.put(aadharCard, 1);
        }
        return booking.getAmountToBePaid();
    }
    public int getBookings(Integer aadharCard) {
        return countofBooking.get(aadharCard);
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {
    List<Facility> oldfacility=hoteldb.get(hotelName).getFacilities();
    for(Facility f:newFacilities){
        if(oldfacility.contains(f)){
            continue;
        }else{
            oldfacility.add(f);
        }
    }

    Hotel h=hoteldb.get(hotelName);
    h.setFacilities(oldfacility);
    hoteldb.put(hotelName,h);
    return h;
    }
}
