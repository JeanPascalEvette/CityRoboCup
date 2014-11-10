
//~--- non-JDK imports --------------------------------------------------------

import com.github.robocup_atan.atan.model.ActionsPlayer;
import com.github.robocup_atan.atan.model.enums.Line;
import com.github.robocup_atan.atan.model.enums.PlayMode;
import java.util.ArrayList;

import org.apache.log4j.Logger;

//~--- JDK imports ------------------------------------------------------------

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
public class Forward extends Player {
    protected static int    count         = 0;
    private int             forwardId     = 0;
    private double          directionCenterLine = 0;
    /**
     * Constructs a new simple client.
     */
    public Forward(ArrayList<Player> t, int number) {
        super(t, number);
        directionCenterLine = Double.MAX_VALUE;
        random = new Random(System.currentTimeMillis() + count);
        forwardId = count++;
        switch (forwardId) {
                case 0 :
                    startingX = -10;startingY = 10;
                    break;
                case 1 :
                    startingX = -10;startingY = -10;
                    break;
            }
    }

   /** {@inheritDoc} */
    @Override
    public void preInfo() {
        super.preInfo();
        directionCenterLine = Double.MAX_VALUE;
    }

    /** {@inheritDoc} */
    @Override
    public void postInfo() {
        super.postInfo();
        if (canSeeBall) {
            canSeeBallAction();
        } else {
            canSeeNothingAction();
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getType() {
        return "Forward";
    }

    
    /**
     * This is the action performed when the player can see the ball.
     * It involves running at it and kicking it...
     */
    protected void canSeeBallAction() {  
        if(checkIfClosestToBall()) // Trying to figure out if he can get the ball
        {
            if (distanceBall < 0.7) { // Can shoot
                if (currentPlayMode == PlayMode.FREE_KICK_FAULT_OWN || 
                        currentPlayMode == PlayMode.FREE_KICK_OWN || 
                        currentPlayMode == PlayMode.KICK_OFF_OWN) {
                    shootTowardsClosestPlayer();
                }
                else if(closestPlayerOtherDistance < 1.5)
                {
                    shootTowardsClosestPlayer();
                }
                else
                {
                    checksBothSides = -1;
                    if(canSeeOtherGoal) // Shoot towards goal
                    {
                        if(distanceOtherGoal > 20)
                            getPlayer().kick(10, directionOtherGoal);
                        else
                            getPlayer().kick(100, directionOtherGoal);
                    }
                    else
                    {
                        getPlayer().dash(60);
                    }
                }
            }
            else
            {
            turnTowardBall();
            getPlayer().dash(100);
            }
        }
        else // Not allowed to get the ball, do something else
        {
            if(canSeeOtherGoal)
            {
                getPlayer().turn(directionOtherGoal);
                getPlayer().dash(100);
            }
            else
            {
                getPlayer().dash(20);
            }
        }
    }

  

    /**
     * If the player can see nothing, it turns 180 degrees.
     */
    protected void canSeeNothingAction() {
        goDefaultPos();
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
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeLine(Line line, double distance, double direction, double distChange, double dirChange,
                            double bodyFacingDirection, double headFacingDirection) {
        super.infoSeeLine(line, distance, direction, distChange, dirChange, bodyFacingDirection, headFacingDirection);
        if(line == Line.CENTER) {
            directionCenterLine = direction;
        }
    }

    
    private void goDefaultPos() {
        if ((m_position != null && m_position.x < startingX - 5)) {
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
        } else if(canSeeOwnGoal){
            getPlayer().turn(180);
        }
        else if(leftRight)
        {
            getPlayer().turn(50);
            leftRight = !leftRight; 
        }
        else
        {
            getPlayer().turn(-50);
            leftRight = !leftRight;
        }
    }
}
