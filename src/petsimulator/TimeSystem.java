/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author AJ Franchuk
 */
//The game runs on real world time - there is no separate in-game clock.
//
//The system clock is the only clock. This class remembers the real date and
//time of the last update, and every time the game checks in it works out how
//many real minutes have gone by and applies them:
//
//  - the pet's needs drop by one point each per MINUTES_PER_NEED_POINT
//  - the apple tree grows on the same real minutes
//  - the pet's age is the number of whole real days since you adopted it
//
//The exact same method runs whether the game has been open the whole time or
//closed for three days, so leaving the game running and leaving it shut work
//out the same. The only special case is the seven day rule from our design:
//leave the pet alone for a week and it goes to a new owner.
//
//To make the game move faster for a demo, turn MINUTES_PER_NEED_POINT and
//MINUTES_PER_APPLE down - everything else follows from those two numbers.
public class TimeSystem {

    //Each need loses one point every this many real minutes
    //(100 points, so a full bar empties in about 17 real hours)
    public static final int MINUTES_PER_NEED_POINT = 10;

    //The apple tree grows one apple every this many real minutes
    public static final int MINUTES_PER_APPLE = 10;

    //Leave the pet this many real days and it finds a new owner
    public static final int ABANDON_DAYS = 7;

    //Time away never takes a need all the way to zero, so the seven day rule
    //is the only thing that can lose the pet while the game is closed
    private static final int MINIMUM = 1;

    private static final DateTimeFormatter STAMP
            = DateTimeFormatter.ofPattern("EEE d MMM, HH:mm");

    //The real time the pet was adopted - the pet's age is measured from here
    private LocalDateTime firstPlayed;
    //The real time the needs were last brought up to date
    private LocalDateTime lastUpdate;

    //A brand new game starts the clock now
    public TimeSystem() {
        this.firstPlayed = LocalDateTime.now();
        this.lastUpdate = LocalDateTime.now();
    }

    //Used when loading a save, so time carries on from where it left off
    public TimeSystem(LocalDateTime firstPlayed, LocalDateTime lastUpdate) {
        this.firstPlayed = (firstPlayed == null) ? LocalDateTime.now() : firstPlayed;
        this.lastUpdate = (lastUpdate == null) ? LocalDateTime.now() : lastUpdate;
    }

    public LocalDateTime getFirstPlayed() {
        return firstPlayed;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    //The real time right now
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    //Today's real date and time, shown at the top of the screen
    public String getRealTimeText() {
        return now().format(STAMP);
    }

    //Real minutes since the needs were last updated
    public long minutesSinceUpdate() {
        return Math.max(0, Duration.between(lastUpdate, now()).toMinutes());
    }

    //Real days since the needs were last updated
    public long daysSinceUpdate() {
        return Math.max(0, Duration.between(lastUpdate, now()).toDays());
    }

    //Whole real days since the pet was adopted - this is the pet's age, and
    //the score the player is really playing for
    public int daysAlive() {
        return (int) Math.max(0, Duration.between(firstPlayed, now()).toDays());
    }

    //Real minutes until the next need point is lost, for the status display
    public long minutesUntilNextDrop() {
        return MINUTES_PER_NEED_POINT - (minutesSinceUpdate() % MINUTES_PER_NEED_POINT);
    }

    //Brings the game up to date with the real world clock.
    //Called at the start of every turn and straight after loading a save, so
    //there is only one place in the whole program where time is applied.
    //Returns a message for the player, or an empty string if nothing happened.
    public String update(Pet pet, AppleTree tree) {
        //The seven day rule comes first - nothing else matters after that
        if (daysSinceUpdate() >= ABANDON_DAYS) {
            long days = daysSinceUpdate();
            lastUpdate = now();
            pet.leave();
            return pet.getName() + " waited " + days
                    + " days for you and has left you for a new owner.";
        }

        long minutes = minutesSinceUpdate();
        int points = (int) (minutes / MINUTES_PER_NEED_POINT);

        //Not enough time has passed to lose a whole point yet
        if (points <= 0) {
            updateAge(pet);
            return "";
        }

        //Only count the minutes actually used, so the leftovers carry over to
        //the next check instead of being thrown away
        int minutesUsed = points * MINUTES_PER_NEED_POINT;
        lastUpdate = lastUpdate.plusMinutes(minutesUsed);

        pet.getNeeds().changeHunger(-points);
        pet.getNeeds().changeEnergy(-points);
        pet.getNeeds().changeHappiness(-points);
        keepAboveZero(pet.getNeeds());

        int grown = tree.grow(minutesUsed);
        updateAge(pet);

        //A short gap is not worth mentioning
        if (minutes < MINUTES_PER_NEED_POINT * 3) {
            return "";
        }

        String away = describe(minutes);
        String fruit = (grown > 0)
                ? " " + grown + " apple(s) grew on the tree while you were gone." : "";

        return "It has been " + away + " since you last checked on "
                + pet.getName() + "." + fruit;
    }

    //The pet's age is simply how many whole real days it has been alive
    private void updateAge(Pet pet) {
        pet.setAge(daysAlive());
    }

    //Turns a number of minutes into something readable
    private String describe(long minutes) {
        if (minutes < 60) {
            return minutes + " minute(s)";
        }
        if (minutes < 60 * 24) {
            return (minutes / 60) + " hour(s)";
        }
        return (minutes / (60 * 24)) + " day(s)";
    }

    //Lifts any need that hit zero back to the minimum
    private void keepAboveZero(PetNeeds needs) {
        if (needs.getEnergy() < MINIMUM) {
            needs.changeEnergy(MINIMUM - needs.getEnergy());
        }
        if (needs.getHunger() < MINIMUM) {
            needs.changeHunger(MINIMUM - needs.getHunger());
        }
        if (needs.getHappiness() < MINIMUM) {
            needs.changeHappiness(MINIMUM - needs.getHappiness());
        }
    }
}
