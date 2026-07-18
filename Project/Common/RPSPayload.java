package Project.Common;



//aa2984: 2026-July-18
//Summary: Configured Payload so rps can have own fields
//targetUser holds who is being challenged, move holds the submitted choice
//Accept option to accept the challenge from other player



public class RPSPayload extends Payload{
    private long targetUser;
    private Move move;
    private boolean accepted;

    public long getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(long targetUser) {
        this.targetUser = targetUser;
    }

    

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    @Override 
    public String toString() {
        return super.toString() + 
            String.format(" TargetUser: [%s] Move: [%s] Accepted: [%s]",
                        getTargetUser(), getMove(), isAccepted());
    }
}
