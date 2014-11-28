
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

//~--- JDK imports ------------------------------------------------------------

import java.util.HashMap;
import java.util.Random;


/**
 * <p>Silly class.</p>
 *
 * @author Atan
 */
public class GoalieManager extends Player {
    private double        distBall    = 1000;
    private double        minDistLine = 9;
    private double        dirBall     = 0; 
    private double        dirOwnGoal = 0;
    private double        distGoal = -1.0;
    private boolean 	  canSeeGoal = false;
    private boolean 	  canSeeGoalLeft = false;
    private boolean 	  canSeeGoalRight = false;
    private boolean 	  canSeeFieldEnd = false;
    private boolean 	  alreadySeeingGoal = false;
    private boolean 	  canSeePenalty = false;
    private ActionsPlayer player;
    private Random        random        = null;
    private static int    count         = 0;
    private double        dirMultiplier = 1.0;
    private double        goalTurn;
    private boolean 	  needsToRetreat = false;
    

    public GoalieManager(ArrayList<Player> t, int number) {
        super(t, number);
        startingX = -50;startingY = 0;
        random = new Random(System.currentTimeMillis() + count);
        count++;
    }
    
    /** {@inheritDoc} */
    @Override
    public void preInfo() {
        distBall    = 1000;
        distGoal    = 1000;
        canSeeGoal = false;
        canSeeGoalLeft = false;
        canSeeGoalRight = false;
        canSeePenalty = false;
        canSeeFieldEnd = false;
        goalTurn = 0.0;
    }

    /** {@inheritDoc} */
    @Override
    public void postInfo() 
    {
        if (distBall < 15) 
        {
                if (distBall < 5)
                {
                     if (distBall < 0.7) 
                         {
        		 if(canSeeGoal || canSeePenalty) 
        		 	this.getPlayer().catchBall(dirBall);
                                this.getPlayer().kick(50, closestPlayerDirection);
        		 
        		 if(canSeeGoal)
        			 this.getPlayer().kick(60, 135);
        		 else
        			 this.getPlayer().kick(60, 0);
                             }
          
        	 else if(canSeeGoal || canSeePenalty)
        	 {
		    	 if(distBall < 2)
		    	 {
		    		needsToRetreat = true;
		    		 
                                turnTowardBall();
                                getPlayer().dash(10);
		    	 }
                         else if (!canSeeFlagPenaltyOwn || (m_position != null && m_position.x > -40))
                         {
                             if (canSeeBall)
                             getPlayer().turn(180);
                             getPlayer().dash(20);
                         }
                         else if (canSeeOwnGoal && distanceOwnGoal < 2)
                         {
                             getPlayer().turn(180);
                         }
                         
                         else        
		    	 {
                            needsToRetreat = true;
		    		 
		            getPlayer().turn(dirBall);
		            getPlayer().dash(randomDashValueVeryFast());
		    	 }
        	 
                 }
             } 
        }       
        else 
        {
                if (canSeeBall)
                {
                    getPlayer().turn(dirBall);
                    //getPlayer().dash(15);
                                       
                }
                
                else if(!canSeeGoal && !needsToRetreat)
        	{
        		if(!canSeePenalty)
        		{
        			getPlayer().turn(90);
    				getPlayer().dash(randomDashValueFast());
        		}
        		else if((canSeeGoalLeft || canSeeGoalRight) && !canSeeFieldEnd)
        		{
        			getPlayer().turn(-1.0 * goalTurn);
        			getPlayer().dash(randomDashValueSlow());
        		}
        		else
        			getPlayer().turn(25 * dirMultiplier);
        	}
      
                else
	        {   
        		if(!canSeeGoal)
        		{
        			getPlayer().turn(120);
    				//getPlayer().dash(randomDashValueSlow());
        		}
        		else if(distGoal > 3.5)
        		{
        			if(!alreadySeeingGoal)
        			{
	        			getPlayer().turn(dirOwnGoal);
	        			alreadySeeingGoal = true;
        			}
        		       
        			//getPlayer().dash(randomDashValueVeryFast());
        		}
        		else
        		{
        			needsToRetreat = false;
        			
        			if(alreadySeeingGoal)
        			{
        				getPlayer().turn(goalTurn);
        				alreadySeeingGoal = false;
        			}
        			else
        				alreadySeeingGoal = true;
        		}
    		}		
        }
        
        }
    /** {@inheritDoc} */
    @Override
    public ActionsPlayer getPlayer() 
    {
        return player;
    }

    /** {@inheritDoc} */
    @Override
    public void setPlayer(ActionsPlayer p) 
    {
        player = p;
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeLine(Line line, double distance, double direction, double distChange, double dirChange,
                            double bodyFacingDirection, double headFacingDirection) {}

    /** {@inheritDoc} */
    @Override
    public void infoSeeBall(double distance, double direction, double distChange, double dirChange,
                            double bodyFacingDirection, double headFacingDirection) 
    {
        distBall = distance;
        dirBall = direction;
    }

    /** {@inheritDoc} */
    @Override
    public void infoHearReferee(RefereeMessage refereeMessage) {}

    /** {@inheritDoc} */
    @Override
    public void infoHearPlayMode(PlayMode playMode) 
    {
        if (playMode == PlayMode.BEFORE_KICK_OFF) 
            getPlayer().move(-50, 0);
    }

    /** {@inheritDoc} */
    @Override
    public void infoHearPlayer(double direction, String string) {}

    /** {@inheritDoc} */
    @Override
    public void infoSenseBody(ViewQuality viewQuality, ViewAngle viewAngle, double stamina, double unknown,
                              double effort, double speedAmount, double speedDirection, double headAngle,
                              int kickCount, int dashCount, int turnCount, int sayCount, int turnNeckCount,
                              int catchCount, int moveCount, int changeViewCount) {}

    /** {@inheritDoc} */
    @Override
    public String getType() 
    {
        return "Goalie";
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
    public void infoSeeFlagRight(Flag flag, double distance, double direction, double distChange, double dirChange,
                                 double bodyFacingDirection, double headFacingDirection) {}

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagLeft(Flag flag, double distance, double direction, double distChange, double dirChange,
                                double bodyFacingDirection, double headFacingDirection) {}

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagOwn(Flag flag, double distance, double direction, double distChange, double dirChange,
                               double bodyFacingDirection, double headFacingDirection) 
    {
    	canSeeFieldEnd = true;
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagOther(Flag flag, double distance, double direction, double distChange, double dirChange,
                                 double bodyFacingDirection, double headFacingDirection) {}

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagCenter(Flag flag, double distance, double direction, double distChange, double dirChange,
                                  double bodyFacingDirection, double headFacingDirection) {}

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagCornerOwn(Flag flag, double distance, double direction, double distChange, double dirChange,
                                     double bodyFacingDirection, double headFacingDirection) {}

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagCornerOther(Flag flag, double distance, double direction, double distChange,
                                       double dirChange, double bodyFacingDirection, double headFacingDirection) {}

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagPenaltyOwn(Flag flag, double distance, double direction, double distChange,
                                      double dirChange, double bodyFacingDirection, double headFacingDirection) 
    {   
    	canSeePenalty = true;
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagPenaltyOther(Flag flag, double distance, double direction, double distChange,
            double dirChange, double bodyFacingDirection, double headFacingDirection) {}

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagGoalOwn(Flag flag, double distance, double direction, double distChange, double dirChange,
                                   double bodyFacingDirection, double headFacingDirection) 
    {
    	if(!alreadySeeingGoal)
    		dirMultiplier *= -1.0;	
    	
    	if(flag.compareTo(Flag.CENTER) == 0)
    	{
        	distGoal = distance;
        	dirOwnGoal = direction;

        	canSeeGoal = true;
        	
	    	goalTurn = 180;
    	}
    	if(flag.compareTo(Flag.LEFT) == 0)
    	{
    		canSeeGoalLeft = true;
    		goalTurn = 90;
    	}
    	if(flag.compareTo(Flag.RIGHT) == 0)
    	{
    		canSeeGoalRight = true;
    		goalTurn = -90;
    	}
    }

    /** {@inheritDoc} */
    @Override
    public void infoSeeFlagGoalOther(Flag flag, double distance, double direction, double distChange, double dirChange,
                                     double bodyFacingDirection, double headFacingDirection) {}

    /** {@inheritDoc} */
    @Override
    public void infoSeePlayerOther(int number, boolean goalie, double distance, double direction, double distChange,
                                   double dirChange, double bodyFacingDirection, double headFacingDirection) {}

    /** {@inheritDoc} */
    @Override
    public void infoSeePlayerOwn(int number, boolean goalie, double distance, double direction, double distChange,
                                 double dirChange, double bodyFacingDirection, double headFacingDirection) {}

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
    
    
    private int randomDashValueVeryFast() 
    {
        return 100 + random.nextInt(30);
    }
    

    
    @Override
    public void infoCPTOther(int unum) {}

    /** {@inheritDoc} */
    @Override
    public void infoCPTOwn(int unum, int type) {}

    /** {@inheritDoc} */
    @Override
    public void infoServerParam(HashMap<ServerParams, Object> info) {}
}
