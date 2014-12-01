
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
import java.util.Collections;

import org.apache.log4j.Logger;

//~--- JDK imports ------------------------------------------------------------

import java.util.HashMap;
import java.util.Random;
import jdk.nashorn.internal.runtime.PropertyMap;

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

    

    

public abstract class Player implements ControllerPlayer {
    protected static int        playerCount         = 0;
    protected static Logger     log           = Logger.getLogger(Forward.class);
    protected Random            random        = null;
    protected boolean           canSeeOwnGoal = false;
    protected boolean           canSeeNothing = true;
    protected boolean           canSeeBall    = false;
    protected double            directionBall;
    protected double            directionOwnGoal;
    protected double            distanceBall;
    protected double            distanceOwnGoal;
    protected ActionsPlayer     player;
    protected double            directionOtherGoal;
    protected double            distanceOtherGoal;
    protected double[]          lastDistanceOtherGoal;
    protected boolean           canSeeOtherGoal;
    protected ArrayList<Player> myTeam = null;
    protected Player            closestPlayer = null;
    protected double            closestPlayerDistance = Double.MAX_VALUE;
    protected double            closestPlayerDirection = 0;
    protected double            closestPlayerOtherDistance = Double.MAX_VALUE;
    protected double            closestPlayerOtherDirection = 0;
    protected int               myNumber = -1;
    protected ArrayList<FlagObject>   flagsSeen = null;
    protected ArrayList<LineObject>   lineSeen = null;
    protected Flag              closestFlag = null;
    protected double            closestFlagDistance = Double.MAX_VALUE;
    protected double            closestFlagDirection = 0;
    protected PlayMode          currentPlayMode = null;
    protected double            blackListTimer = Double.MIN_VALUE;
    protected double            playerDirection = 0;
    protected int               checksBothSides = -1; // -1 = NO - 0 = Checking Left - 1 = Checking Right
    protected Coords            m_position;
    protected double            positionLifetime;
    protected boolean           leftRight;
    protected int               startingX, startingY;
    protected boolean              canSeeFlagPenaltyOwn;
    
    /**
     * Constructs a new simple client.
     */
    public Player(ArrayList<Player> t, int number) {
        myTeam = t;//My Team controls the list of players
        leftRight = false;
        myNumber = number;
        random = new Random(System.currentTimeMillis() + playerCount);
        playerCount++;
        flagsSeen = new ArrayList<>(); //list of flags currently in players vision
        closestFlag = null; 
        closestFlagDirection = 0;
        lineSeen = new ArrayList<>();// list of lines currently in players vision 
        closestFlagDistance = 0;
        lastDistanceOtherGoal = new double[2];// This records the last distance of the opponents goal
        lastDistanceOtherGoal[0] = Double.MAX_VALUE;
        lastDistanceOtherGoal[1] = Double.MAX_VALUE;
        
        closestPlayer = null;
        closestPlayerDirection = 0;
        closestPlayerDistance = Double.MAX_VALUE;
        closestPlayerOtherDirection = 0;
        closestPlayerOtherDistance = Double.MAX_VALUE;
        
        distanceOwnGoal= Double.MAX_VALUE;
        directionOwnGoal = Double.MAX_VALUE;
        
        m_position = new Coords(0,0);//calculated based on the position of the flags
        positionLifetime = 0;
     }
    
    protected void startBlackList(int time)
    {
        //blackListTimer = System.nanoTime() + (time*1000);
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
    public void preInfo() {
        //reset every sensor data
        canSeeOwnGoal = false;
        canSeeFlagPenaltyOwn = false;
        canSeeBall    = false;
        distanceBall = Double.MAX_VALUE;
        canSeeOtherGoal = false;
        canSeeNothing = true;
        flagsSeen.clear();
        lineSeen.clear();
        playerDirection = 0;
        closestPlayer = null;
        closestPlayerDistance = Double.MAX_VALUE;
        closestPlayerDirection = 0;
        closestPlayerOtherDistance = Double.MAX_VALUE;
        closestPlayerOtherDirection = 0;
        
        if(positionLifetime < 5)
            positionLifetime++;
        else
        {
            m_position = null;
        }
            
    }

    /** {@inheritDoc} */
    @Override
    public void postInfo() {
callExpensiveStuff();
    }

    public double getDistanceFromBall()
    {
        //return the distance of ball. If unknown or player has shot recently return max value.
        if(blackListTimer > System.nanoTime())
            return Double.MAX_VALUE;
        else if(canSeeBall)
            return distanceBall;
        else
            return Double.MAX_VALUE;
    }
    
    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagRight(Flag flag, double distance, double direction, double distChange, double dirChange,
                                 double bodyFacingDirection, double headFacingDirection) {
        
        //Record the flag in the list
        canSeeNothing = false;
        flagsSeen.add(new FlagObject(flag, distance, direction));
        if(distance < closestFlagDistance)
        {
            closestFlag = flag;
            closestFlagDistance = distance;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagLeft(Flag flag, double distance, double direction, double distChange, double dirChange,
                                double bodyFacingDirection, double headFacingDirection) {
        //Record the flag in the list
        canSeeNothing = false;
        flagsSeen.add(new FlagObject(flag, distance, direction));
        if(distance < closestFlagDistance)
        {
            closestFlag = flag;
            closestFlagDistance = distance;
            closestFlagDirection = direction;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagOwn(Flag flag, double distance, double direction, double distChange, double dirChange,
                               double bodyFacingDirection, double headFacingDirection) {
        //Record the flag in the list
        canSeeNothing = false;
        flagsSeen.add(new FlagObject(flag, distance, direction));
        if(distance < closestFlagDistance)
        {
            closestFlag = flag;
            closestFlagDistance = distance;
            closestFlagDirection = direction;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagOther(Flag flag, double distance, double direction, double distChange, double dirChange,
                                 double bodyFacingDirection, double headFacingDirection) {
        //Record the flag in the list
        canSeeNothing = false;
        flagsSeen.add(new FlagObject(flag, distance, direction));
        if(distance < closestFlagDistance)
        {
            closestFlag = flag;
            closestFlagDistance = distance;
            closestFlagDirection = direction;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagCenter(Flag flag, double distance, double direction, double distChange, double dirChange,
                                  double bodyFacingDirection, double headFacingDirection) {
        //Record the flag in the list
        canSeeNothing = false;
        flagsSeen.add(new FlagObject(flag, distance, direction));
        if(distance < closestFlagDistance)
        {
            closestFlag = flag;
            closestFlagDistance = distance;
            closestFlagDirection = direction;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagCornerOwn(Flag flag, double distance, double direction, double distChange, double dirChange,
                                     double bodyFacingDirection, double headFacingDirection) {
        //Record the flag in the list
        canSeeNothing = false;
        flagsSeen.add(new FlagObject(flag, distance, direction));
        if(distance < closestFlagDistance)
        {
            closestFlag = flag;
            closestFlagDistance = distance;
            closestFlagDirection = direction;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagCornerOther(Flag flag, double distance, double direction, double distChange,
                                       double dirChange, double bodyFacingDirection, double headFacingDirection) {
        //Record the flag in the list
        canSeeNothing = false;
        flagsSeen.add(new FlagObject(flag, distance, direction));
        if(distance < closestFlagDistance)
        {
            closestFlag = flag;
            closestFlagDistance = distance;
            closestFlagDirection = direction;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagPenaltyOwn(Flag flag, double distance, double direction, double distChange,
                                      double dirChange, double bodyFacingDirection, double headFacingDirection) {
        //Record the flag in the list
        canSeeNothing = false;
        canSeeFlagPenaltyOwn = true;
        flagsSeen.add(new FlagObject(flag, distance, direction));
        if(distance < closestFlagDistance)
        {
            closestFlag = flag;
            closestFlagDistance = distance;
            closestFlagDirection = direction;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagPenaltyOther(Flag flag, double distance, double direction, double distChange,
            double dirChange, double bodyFacingDirection, double headFacingDirection) {
        //Record the flag in the list
        canSeeNothing = false;
        flagsSeen.add(new FlagObject(flag, distance, direction));
        if(distance < closestFlagDistance)
        {
            closestFlag = flag;
            closestFlagDistance = distance;
            closestFlagDirection = direction;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagGoalOwn(Flag flag, double distance, double direction, double distChange, double dirChange,
                                   double bodyFacingDirection, double headFacingDirection) {
        //Record the flag in the list
        canSeeNothing = false;
        flagsSeen.add(new FlagObject(flag, distance, direction));
        if(distance < closestFlagDistance)
        {
            closestFlag = flag;
            closestFlagDistance = distance;
            closestFlagDirection = direction;
        }
        if (flag == Flag.CENTER) {
            this.canSeeOwnGoal    = true;
            this.distanceOwnGoal  = distance;
            this.directionOwnGoal = direction;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagGoalOther(Flag flag, double distance, double direction, double distChange, double dirChange,
                                     double bodyFacingDirection, double headFacingDirection) {
        //Record the flag in the list
        canSeeNothing = false;
        flagsSeen.add(new FlagObject(flag, distance, direction));
        if(distance < closestFlagDistance)
        {
            closestFlag = flag;
            closestFlagDistance = distance;
            closestFlagDirection = direction;
        }
        lastDistanceOtherGoal[0] = lastDistanceOtherGoal[1];
        lastDistanceOtherGoal[1] = distance;
        if (flag == Flag.CENTER) {
            this.canSeeOtherGoal    = true;
            this.distanceOtherGoal  = distance;
            this.directionOtherGoal = direction;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeLine(Line line, double distance, double direction, double distChange, double dirChange,
                            double bodyFacingDirection, double headFacingDirection) {
        //Record the line in the list
        canSeeNothing = false;
        lineSeen.add(new LineObject(line,distance,direction));
        
        if(line == Line.RIGHT) {
            if(direction > 0)
                playerDirection = -direction;
            else
                playerDirection = -direction - 180;
        }
        else if(line == Line.LEFT) {
            if(direction < 0)
                playerDirection = -direction;
            else
                playerDirection = -direction + 180;
        }	
        else if(line == Line.OWN) {
            if(Math.abs(direction) == 90.0)
                playerDirection = 180.0;
            else if(direction > 0)
                playerDirection =  -direction - 90;
            else if(direction < 0)
                playerDirection = -direction + 90;
        }
        else if(line == Line.OTHER){
            if(Math.abs(direction) == 90.0)
                playerDirection = 0.0;
            else if(direction > 0)
                playerDirection = -direction + 90;
            else if(direction < 0)
                playerDirection = -direction - 90;
        }
    }

    
    /** {@inheritDoc} */
    @Override
    public void infoSeePlayerOther(int number, boolean goalie, double distance, double direction, double distChange,
                                   double dirChange, double bodyFacingDirection, double headFacingDirection) 
    {
        //Record closest players distance
        if(closestPlayerOtherDistance > distance)
        {
            closestPlayerOtherDistance = distance;
            closestPlayerOtherDirection = direction;
        }
    }

    public boolean isGoingForward()
    {
        //check that the player is going towards the right goal
        return lastDistanceOtherGoal[0] - lastDistanceOtherGoal[1] < 0;
    }
    
    public int getNumber() { return myNumber; }
        private void callExpensiveStuff()
    {
        Collections.sort(flagsSeen);// this sorts the flags by distance to player
        Collections.sort(lineSeen); // this sorts the lines by distance to player
        Coords p1 = Tools.doCircleThingy(flagsSeen, true);//This uses trigonometry calculation
        if(p1 == null)
        {
            if(positionLifetime < 10)
                    positionLifetime++;
                else
                {
                    m_position = null;
                    positionLifetime = 0;
                }
        }
        else if(Math.abs(p1.x) > 54.0 || Math.abs(p1.y) > 32.0) //If the point is out of the field then find other points
        {
            p1 = Tools.doCircleThingy(flagsSeen, false);
            if(Math.abs(p1.x) < 54.0 || Math.abs(p1.y) < 32.0)
                m_position = p1;
            else
            {
                if(positionLifetime < 10)
                    positionLifetime++;
                else
                {
                    m_position = null;
                    positionLifetime = 0;
                }
            }
        }
        else
            m_position = p1;
            
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeePlayerOwn(int number, boolean goalie, double distance, double direction, double distChange,
                                 double dirChange, double bodyFacingDirection, double headFacingDirection) 
    {
        //Calculates closest team mate
        if(closestPlayerDistance > distance)
        {
            closestPlayerDistance = distance;
            closestPlayerDirection = direction;
            for(Player p : myTeam) 
            {
                if(p.getNumber() == number)
                {
                    closestPlayer = p;
                }
            }
        }
    }
    
    protected boolean checkIfClosestToBall()
    {
        //Checks if another player from my team is closer to the ball than me
        for(Player p : myTeam)
            if(p != this && p.getDistanceFromBall() < getDistanceFromBall())
                return false;
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeBall(double distance, double direction, double distChange, double dirChange,
                            double bodyFacingDirection, double headFacingDirection) {
        canSeeNothing      = false;
        canSeeBall    = true;
        distanceBall  = distance;
        directionBall = direction;
    }

    /** {@inheritDoc} */
    @Override
    public void infoHearReferee(RefereeMessage refereeMessage) {}

    /** {@inheritDoc} */
    @Override
    public void infoHearPlayMode(PlayMode playMode) {
        currentPlayMode = playMode;
        if (playMode == PlayMode.BEFORE_KICK_OFF || 
                playMode == PlayMode.GOAL_L || 
                playMode == PlayMode.GOAL_R || 
                playMode == PlayMode.GOAL_OTHER ||
                playMode == PlayMode.GOAL_OWN) {
            getPlayer().move(startingX, startingY);
        }
    }
    

    protected void shootTowardsClosestPlayer()
    {
     //If the player has seen a team mate shoot towards him    
        if(closestPlayer != null)
        {
            checksBothSides = -1;
            getPlayer().kick(70, closestPlayerDirection);
            startBlackList(2);
        }
        else if (checksBothSides == -1)
        {
            //else look left and right for a team mate 
            checksBothSides = 0;
            getPlayer().dash(-100);
            getPlayer().turn(90);
        }
        else if(checksBothSides == 0)
        {
            checksBothSides = 1;
            getPlayer().dash(-100);
            getPlayer().turn(-180);

        }
        else
        {
            //else shoot upwards
            checksBothSides = -1;
            getPlayer().kick(50,-90);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void infoHearPlayer(double direction, String message) {
    }

    /** {@inheritDoc} */
    @Override
    public void infoSenseBody(ViewQuality viewQuality, ViewAngle viewAngle, double stamina, double unknown,
                              double effort, double speedAmount, double speedDirection, double headAngle,
                              int kickCount, int dashCount, int turnCount, int sayCount, int turnNeckCount,
                              int catchCount, int moveCount, int changeViewCount) {}

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return "Simple";
    }

    /** {@inheritDoc} */
    @Override
    public void setType(String newType) {}

    /** {@inheritDoc} */
    @Override
    public void infoHearError(Errors error) {}

    /** {@inheritDoc} */
    @Override
    public void infoHearOk(Ok ok) {}

    /** {@inheritDoc} */
    @Override
    public void infoHearWarning(Warning warning) {}

    /** {@inheritDoc} */
    @Override
    public void infoPlayerParam(double allowMultDefaultType, double dashPowerRateDeltaMax,
                                double dashPowerRateDeltaMin, double effortMaxDeltaFactor, double effortMinDeltaFactor,
                                double extraStaminaDeltaMax, double extraStaminaDeltaMin,
                                double inertiaMomentDeltaFactor, double kickRandDeltaFactor,
                                double kickableMarginDeltaMax, double kickableMarginDeltaMin,
                                double newDashPowerRateDeltaMax, double newDashPowerRateDeltaMin,
                                double newStaminaIncMaxDeltaFactor, double playerDecayDeltaMax,
                                double playerDecayDeltaMin, double playerTypes, double ptMax, double randomSeed,
                                double staminaIncMaxDeltaFactor, double subsMax) {}

    /** {@inheritDoc} */
    @Override
    public void infoPlayerType(int id, double playerSpeedMax, double staminaIncMax, double playerDecay,
                               double inertiaMoment, double dashPowerRate, double playerSize, double kickableMargin,
                               double kickRand, double extraStamina, double effortMax, double effortMin) {}

    /** {@inheritDoc} */
    @Override
    public void infoCPTOther(int unum) {}

    /** {@inheritDoc} */
    @Override
    public void infoCPTOwn(int unum, int type) {}

    /** {@inheritDoc} */
    @Override
    public void infoServerParam(HashMap<ServerParams, Object> info) {}

    /**
     * This is the action performed when the player can see the ball.
     * It involves running at it and kicking it...
     */
    protected void canSeeBallAction() {
        // NYI
        
    }

    /**
     * If the player can see anything that is not a ball or a goal, it turns.
     */
    protected void canSeeAnythingAction() {
        //NYI
    }

    /**
     * If the player can see nothing, it turns 180 degrees.
     */
    protected void canSeeNothingAction() {
        //NYI
        
    }

    /**
     * If the player can see its own goal, it goes and stands by it...
     */
    protected void canSeeOwnGoalAction() {
        //NYI
    }

    /**
     * Randomly choose a fast dash value.
     * @return
     */
    protected int randomDashValueFast() {
        return 30 + random.nextInt(100);
    }

    /**
     * Randomly choose a slow dash value.
     * @return
     */
    protected int randomDashValueSlow() {
        return -10 + random.nextInt(50);
    }

    /**
     * Turn towards the ball.
     */
    protected void turnTowardBall() {
        getPlayer().turn(directionBall);
    }

    /**
     * Turn towards our goal.
     */
    protected void turnTowardOwnGoal() {
        getPlayer().turn(directionOwnGoal);
    }

    /**
     * Randomly choose a kick direction.
     * @return
     */
    protected int randomKickDirectionValue() {
        return -45 + random.nextInt(90);
    }

    /**
     * Pause the thread.
     * @param ms How long to pause the thread for (in ms).
     */
    protected synchronized void pause(int ms) {
        try {
            this.wait(ms);
        } catch (InterruptedException ex) {
            log.warn("Interrupted Exception ", ex);
        }
    }
    
    
    protected boolean hasToPass()
    {
        return (currentPlayMode == PlayMode.FREE_KICK_FAULT_OWN || 
                currentPlayMode == PlayMode.FREE_KICK_OWN || 
                currentPlayMode == PlayMode.CORNER_KICK_L || 
                currentPlayMode == PlayMode.CORNER_KICK_R || 
                currentPlayMode == PlayMode.CORNER_KICK_OTHER || 
                currentPlayMode == PlayMode.CORNER_KICK_OWN || 
                currentPlayMode == PlayMode.FREE_KICK_FAULT_L || 
                currentPlayMode == PlayMode.FREE_KICK_FAULT_R || 
                currentPlayMode == PlayMode.FREE_KICK_FAULT_OTHER || 
                currentPlayMode == PlayMode.FREE_KICK_L || 
                currentPlayMode == PlayMode.FREE_KICK_R || 
                currentPlayMode == PlayMode.GOAL_KICK_L || 
                currentPlayMode == PlayMode.GOAL_KICK_R || 
                currentPlayMode == PlayMode.KICK_IN_OTHER || 
                currentPlayMode == PlayMode.KICK_IN_OWN || 
                currentPlayMode == PlayMode.KICK_IN_L || 
                currentPlayMode == PlayMode.KICK_IN_R || 
                currentPlayMode == PlayMode.KICK_OFF_OWN);
    }
}
