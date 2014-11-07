
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

/**
 * <p>Goalkeeper class.</p>
 *
 * @author Atan
 */
public class Goalkeeper extends Player {
    private double        distBall    = 1000;
    private double        minDistLine = 9;
    private double        distanceGoalOwn = 0;

    public Goalkeeper(ArrayList<Player> t, int number)
    {
        super(t, number);
    }
    
    /** {@inheritDoc} */
    @Override
    public void preInfo() {
        super.preInfo();
        distBall    = 1000;
        minDistLine = 1000;
        distanceGoalOwn = 0;

    }

    /** {@inheritDoc} */
    @Override
    public void postInfo() {
        super.postInfo();
        getPlayer().turn((30));
//        if (distBall < 10) {
//            if(canSeeBall)
//            {
//                //Go Towards ball
//            }
//            else
//            {
//                //Turn
//            }
//        } else if (distanceGoalOwn > 10) {
//            //Go towards goal
//        } else {
//            //Wait
//        }
    }
    

    /** {@inheritDoc} */
    @Override
    public ActionsPlayer getPlayer() {
        return player;
    }

    /** {@inheritDoc} */
    @Override
    public void setPlayer(ActionsPlayer p) {
        player = p;
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeLine(Line line, double distance, double direction, double distChange, double dirChange,
                            double bodyFacingDirection, double headFacingDirection) {
        if (distance < minDistLine) {
            minDistLine = distance;
        }
    }

    
    /**
     * This is the action performed when the player can see the ball.
     * It involves running at it and kicking it...
     */
    protected void canSeeBallAction() {  
        if (distanceBall < 2.0) { // Can shoot
            getPlayer().catchBall(directionBall);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void infoHearPlayMode(PlayMode playMode) {
        super.infoHearPlayMode(playMode);
        if (playMode == PlayMode.BEFORE_KICK_OFF) {
            getPlayer().move(-50, 0);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return "Goalkeeper";
    }    
}
