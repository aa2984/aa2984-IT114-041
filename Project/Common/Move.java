package Project.Common;


//aa2984: 2026-July-18
//Summary: Created Move to hold the options for rock paper scissors
//Used method to match what the user input is
//Used toString , override prints the trigger text


public enum Move {
    ROCK("rock"),
    PAPER("paper"),
    SCISSORS("scissors");
    private final String trigger;
    Move(String trigger) {
        this.trigger = trigger;
    }

public static Move fromText(String text) {
    if (text == null) {
        return null;
    }

String lower1 = text.toLowerCase().trim();
for (Move move1: values()) {
    if (lower1.equals(move1.trigger)) {
        return move1;
    }

}
    return null; 

}
@Override 
public String toString() {
    return trigger;
}
}
    

