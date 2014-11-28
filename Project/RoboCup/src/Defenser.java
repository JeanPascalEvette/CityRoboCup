
//~--- non-JDK imports --------------------------------------------------------

import com.github.robocup_atan.atan.model.ActionsPlayer;
import com.github.robocup_atan.atan.model.ControllerPlayer;
import com.github.robocup_atan.atan.model.enums.Errors;
import com.github.robocup_atan.atan.model.enums.Flag;
import com.github.robocup_atan.atan.model.enums.Line;
import com.github.robocup_atan.atan.model.enums.Ok;
import com.github.robocup_atan.atan.model.enums.PlayMode;
import com.github.robocup_atan.atan.model.enums.RefereeMessage;
import com.github.robocup_atan.atan.model.enums.ServerParams;
import com.github.robocup_atan.atan.model.enums.ViewAngle;
import com.github.robocup_atan.atan.model.enums.ViewQuality;
import com.github.robocup_atan.atan.model.enums.Warning;
import java.util.ArrayList;

import org.apache.log4j.Logger;

//~--- JDK imports ------------------------------------------------------------

import java.util.HashMap;
import java.util.Random;

/**
 * A simple controller. It implements the following simple behaviour. If the
 * client sees nothing (it might be out of the field) it turns 180 degree. If
 * the client sees the own goal and the distance is less than 40 and greater
 * than 10 it turns to his own goal and dashes. If it cannot see the own goal
 * but can see the ball it turns to the ball and dashes. If it sees anything but
 * not the ball or the own goals it dashes a little bit and turns a fixed amount
 * of degree to the right.
 *
 * @author Atan
 */
public class Defenser extends Player {
    
public enum STATE { // FSM Implementation
		REST, // Inactive/Default state
		GOTO_BALL, // Going towards the ball
		BALL_CARRIER, // Currently carrying the ball
		ATTACKING, // Not carrying the ball but supporting the attack
		GO_BACK	//Going back to original position
	}
    protected static int    count         = 0;
    private int             defenserId    = 0;
	private STATE myState;
	private int counter;

    /**
     * Constructs a new simple client.
     */
    public Defenser(ArrayList<Player> t, int number) {   
        super(t, number);
        random = new Random(System.currentTimeMillis() + count);
        defenserId = count++;
                    switch (defenserId) {
                case 0 :
                    startingX = -40;startingY = 15;
                    break;
                case 1 :
                    startingX = -40;startingY = 5;
                    break;
                case 2 :
                    startingX = -40;startingY = -5;
                    break;
                case 3 :
                    startingX = -40;startingY = -15;
                    break;
            }

    }
/** {@inheritDoc} */
    @Override
    public void preInfo() {
        super.preInfo();
    }

    /** {@inheritDoc} */
    @Override
    public void postInfo() {
        super.postInfo();
        if (distanceBall < 0.7) // If you are in shooting range
        {
            if(closestPlayerOtherDistance < 3) // If an opponent is close passs it to a teammate
                shootTowardsClosestPlayer();
            else // else  go towards the opponents goal
            {
                getPlayer().turn(directionOtherGoal);
                getPlayer().dash(50);
            }
        } // else check if you are the closest player to the ball and go for it
        else if (checkIfClosestToBall()) {
            turnTowardBall();
            getPlayer().dash(100);
        }  // Else try to return to your original position
        else if ((m_position != null && m_position.x < startingX - 5)) {
            getPlayer().turn(directionOtherGoal);
            getPlayer().dash(20);
        }
        else if ((m_position != null && m_position.x > startingX + 5)) {
            getPlayer().turn(directionOwnGoal);
            getPlayer().dash(20);
        }
        else if ((m_position != null && m_position.y < startingY - 5)) {
            LineObject up = null;
            for(LineObject l : lineSeen)
            {
                if(l.line == Line.LEFT)
                    up = l;
            }
            if(up == null) getPlayer().turn(90);
            else
            {
            getPlayer().turn(up.direction);
            getPlayer().dash(20);
            }
        }else if ((m_position != null && m_position.y > startingY + 5)) {
            LineObject down = null;
            for(LineObject l : lineSeen)
            {
                if(l.line == Line.RIGHT)
                    down = l;
            }
            if(down == null) getPlayer().turn(90);
            else
            {
            getPlayer().turn(down.direction);
            getPlayer().dash(20);
            }
        } else if(canSeeOwnGoal){// If you are at your original position but looking the wrong way turn around
            getPlayer().turn(180);
        }
        else // If you are at your original position and looking the right way look around for the ball
        {
            getPlayer().turn(90);
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getType() {
        return "Midfielder";
    }

    /**
     * This is the action performed when the player can see the ball.
     * It involves running at it and kicking it...
     */
/*    protected void canSeeBallAction() {
        getPlayer().dash(this.randomDashValueFast());
        turnTowardBall();
        if (distanceBall < 0.7) {
            getPlayer().kick(50, randomKickDirectionValue());
        }
        if (log.isDebugEnabled()) {
            log.debug("b(" + directionBall + "," + distanceBall + ")");
        }
    }
*/
    /**
     * If the player can see anything that is not a ball or a goal, it turns.
     */
    protected void canSeeAnythingAction() {
        getPlayer().dash(this.randomDashValueSlow());
        getPlayer().turn(20);
    }

    /**
     * If the player can see nothing, it turns 180 degrees.
     */
    protected void canSeeNothingAction() {
        getPlayer().turn(180);
    }

    /**
     * If the player can see its own goal, it goes and stands by it...
     */
    protected void canSeeOwnGoalAction() {
        getPlayer().dash(this.randomDashValueFast());
        turnTowardOwnGoal();
    }

}
