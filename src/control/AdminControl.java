package control;

import entity.Admin;
import entity.Report;
import entity.User;
import entity.events.Event;
import database.EventRepository;
import database.ShipmentRepository;
import database.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminControl {
    private final UserRepository userRepository;
    private final ShipmentRepository shipmentRepository;
    private final EventRepository eventRepository;

    public AdminControl(UserRepository userRepository, ShipmentRepository shipmentRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.shipmentRepository = shipmentRepository;
        this.eventRepository = eventRepository;
    }

    public List<User> viewAllUsers() {
        return userRepository.findAllUsers();
    }

    public List<entity.Shipment> viewAllShipments() {
        return shipmentRepository.findAllShipments();
    }

    public Report generateSystemReport() {
        List<Event> allEvents = new ArrayList<>();
        for (entity.Shipment shipment : shipmentRepository.findAllShipments()) {
            allEvents.addAll(eventRepository.getEventsByShipment(shipment.getShipmentId()));
        }
        return new Report(UUID.randomUUID().toString(), allEvents);
    }

    public void addUser(User user) {
        userRepository.addUser(user);
    }

    public void deleteUser(String userId) {
        userRepository.deleteUser(userId);
    }
}
