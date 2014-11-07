
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
    protected static int    count         = 0;
    private int             defenserId    = 0;

    /**
     * Constructs a new simple client.
     */
    public Defenser(ArrayList<Player> t, int number) {   
        super(t, number);
        random = new Random(System.currentTimeMillis() + count);
        defenserId = count++;
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
        getPlayer().turn((30));
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
    
        /** {@inheritDoc} */
    @Override
    public void infoHearPlayMode(PlayMode playMode) {
        super.infoHearPlayMode(playMode);
        if (playMode == PlayMode.BEFORE_KICK_OFF) {
            this.pause(1000);
            switch (defenserId) {
                case 0 :
                    this.getPlayer().move(-40, 15);
                    break;
                case 1 :
                    this.getPlayer().move(-40, 5);
                    break;
                case 2 :
                    this.getPlayer().move(-40, -5);
                    break;
                case 3 :
                    this.getPlayer().move(-40, -15);
                    break;
                default :
                    throw new Error("number must be initialized before move");
            }
        }
    }

}
