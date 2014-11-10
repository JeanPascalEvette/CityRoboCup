
//~--- non-JDK imports --------------------------------------------------------

import com.github.robocup_atan.atan.model.AbstractTeam;
import com.github.robocup_atan.atan.model.ControllerCoach;
import com.github.robocup_atan.atan.model.ControllerPlayer;
import java.util.ArrayList;

/**
 * A class to setup a Simple Silly AbstractTeam.
 *
 * @author Atan
 */
public class Team extends AbstractTeam {

    private ArrayList<Player> team;
    
    /**
     * Constructs a new simple silly team.
     *
     * @param name The team name.
     * @param port The port to connect to SServer.
     * @param hostname The SServer hostname.
     * @param hasCoach True if connecting a coach.
     */
    public Team(String name, int port, String hostname, boolean hasCoach) {
        super(name, port, hostname, hasCoach);
    }

    /**
     * {@inheritDoc}
     *
     * The first controller of the team is silly the others are simple.
     */
    @Override
    public ControllerPlayer getNewControllerPlayer(int number) {
        if(team == null)
            team = new ArrayList<>();
        Player p = null;
        if (number == 0) { // 0 is goal
            p = new Goalkeeper(team, number);
        } else if (number > 0 && number < 5) { // 1, 2, 3, 4 are Defensers
            p = new Defenser(team, number);
        } else if (number > 4 && number < 10) { // 5, 6, 7, 8 are midfielders
            p = new Midfielder(team, number);
        } else { // 9, 10 are forwards
            p = new Forward(team, number);
        }
        team.add(p);
        return p;
    }

    /**
     * {@inheritDoc}
     *
     * Generates a new coach.
     */
    @Override
    public ControllerCoach getNewControllerCoach() {
        return new Coach();
    }
    
    
    
    public ArrayList<Player> getTeam()
    {
        return team;
    }
}
