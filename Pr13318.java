
package pr13318;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Pr13318 {//Starts Pr13318
private static DecimalFormat df2 = new DecimalFormat(".##");
     
    //System in, EndTime... Landing List Size.... Rates(arrival,departure)...
 public static void main(String[] args){//Starts main
     int endTime, listSize;
     double arrivalRate,departureRate;
    
     Scanner kb = new Scanner(System.in);
     System.out.print("Enter the end time for this simulaiton(in intervals): ");
     endTime =  kb.nextInt();
     System.out.print("\nEnter the landing list size(keep small): ");
     listSize= kb.nextInt();
     System.out.print("\nPlease enter arrival and departure Rates(Seperate them with a space)"
             + "\nNOTE: the sum of these two should should be less than 1.0: ");
     arrivalRate = kb.nextDouble();
     departureRate = kb.nextDouble();
     
     while(validate(arrivalRate, departureRate) == false){
     System.out.print("\nThe sum of arrival rate and departure rate should not be greater than 1.0"
             + "\nTry Again (Seperate your numbers with a space): ");
     arrivalRate = kb.nextDouble();
     departureRate = kb.nextDouble();
     }

             
     
     Runway runway = new Runway(listSize);
     //Keep Track of numbers
    ArrayList<Integer> lPlanes = new ArrayList<>();
    ArrayList<Integer> tPlanes = new ArrayList<>();
    ArrayList<Integer> lPlanes2 = new ArrayList<>();
    ArrayList<Integer> tPlanes2 = new ArrayList<>();
    ArrayList<Plane> aP = new ArrayList<>();
     int idleTime = 0;
     int pCounter = 0;
     int rcounter = 0;
     
     //Principle For loop
     for(int i = 0; i <= endTime; i++){
        for(int j = 0; j < poisson(departureRate); j++){
          Plane a = new Plane(++pCounter,i,2);
          runway.addTakeoff(a);
          }
        for(int k = 0; k < poisson(arrivalRate); k++){
           Plane b = new Plane(++pCounter,i,1);
           if(runway.canLand(b)) runway.addLanding(b);
           else {b.setStat(0); b.refuse(); rcounter++;}
        }



         if(!runway.landing.isEmpty()) {
            lPlanes.add(i - runway.landing.get(0).getCS());
             runway.activityLand(i);

             }
         else if(!runway.takeOff.isEmpty()){
           tPlanes.add(i - runway.takeOff.get(0).getCS());
             runway.activityTakeoff(i);
         }
         else {idleTime++;}
      
         lPlanes2.add(runway.landing.size());
         tPlanes2.add(runway.takeOff.size());
     }

  
     
      kb.close();
       shutdown( pCounter,rcounter, endTime,tPlanes,lPlanes,idleTime,tPlanes2,lPlanes2);  
     
     
 }//Ends main
  public static void shutdown(int pCounter,int rCounter, int endTime, ArrayList<Integer> tPlanes, ArrayList<Integer> lPlanes, int idleTime,ArrayList<Integer> tPlanes2, ArrayList<Integer> lPlanes2){
    System.out.println("\n\nPlanes Served: "+ (pCounter - rCounter));
    System.out.println("Planes Refused: " + rCounter);

    double sum = 0;
      for(double i : tPlanes){
        sum += i;
      }
      System.out.println("\nAverage Wait in the DEPARTING line " + df2.format(sum/tPlanes.size()) );
      double sum3 = 0;
      for(double i : tPlanes2){
        sum3 += i;
      }
      System.out.println("Average rate of planes Wanting to DEPART per interval: " + df2.format(sum3/endTime));

          double sum2 = 0;
      for(double i : lPlanes){
        sum2 += i;
      }
      System.out.println("\nAverage Wait in the LANDING line " + df2.format(sum2/lPlanes.size()) );

      double sum4 = 0;
      for(double i : lPlanes2){
        sum4 += i;
      }
      System.out.println("Average rate of planes Wanting to LAND per interval: " + df2.format(sum4/endTime));


      System.out.println("\nPercentage of time the runway was Idle: " + (idleTime*100)/endTime + "%");

  }
  
  public static int poisson(double rate){
 double limit = Math.exp(-rate);
 double product = Math.random();
 int count = 0;
    while (product > limit){
    count++;
    product *= Math.random();
    }
        return count;
 }
 
  public static boolean validate(double arrivalRate, double departureRate){
      return (arrivalRate + departureRate) < 1;
  }

 
 static class Runway extends Plane{ //Starts Runway Class
    ArrayList<Plane> takeOff;
    ArrayList<Plane> landing;
    int listLimit;
  
    Runway(){
        takeOff = new ArrayList<>();
        landing=  new ArrayList<>();
        listLimit = 1;
    };
    Runway(int limit){
        takeOff = new ArrayList<>();
        landing=  new ArrayList<>();
        listLimit = limit;
    } 
    //getter
    int getLimit(){return listLimit;}
    //Getters for current plane
    int getFN(Plane current){return current.flightNumber;}
    int getCS(Plane current){return current.clockStart;}
    int getStat(Plane current){return current.status;}
    //setter
    void setLimit(int limit){listLimit = limit;}
    //Setters for current plane
    void setFN(Plane current,int fn){current.flightNumber = fn;}
    void setCS(Plane current,int cs){current.clockStart = cs;} 
    void setStat(Plane current,int stat){current.status = stat;}
    
    boolean canLand(Plane current){return (landing.size() != listLimit);}
    //Add planes to ArrayLists
    void addLanding(Plane current){
      landing.add(current);
      }
    void addTakeoff(Plane current){
        takeOff.add(current);}
    
    //These Methods deal with time
    void activityLand(int time){
        land(time);
        System.out.println("Time waited for landing: " + (time - landing.get(0).clockStart) + "\n");
        
        landing.remove(0);
        
    }
    void activityTakeoff(int time){
        fly(time);
        System.out.println("Time waited for take off " + (time  - takeOff.get(0).clockStart) +"\n");
       
        takeOff.remove(0);} 

    void runIdle(int time){
           time++; }
   
//Overridden Methods  
    //These methods deal with time
    @Override
    void refuse(){
     if(landing.size() >= listLimit){
                System.out.println("Flight number "+ flightNumber+ " been redirected");
            }
    }
    
    @Override
    void land(int time){System.out.println("Flight number " + landing.get(0).flightNumber+ " has landed");}
    @Override
    void fly(int time){ System.out.println("Flight number " + takeOff.get(0).flightNumber+ " has departed");}
    @Override    
    int started(){
            //Return the planes Start Time
            return clockStart;
        }
        }//Ends Runway Class
 
static class Plane{//Start Planes Class
    int flightNumber;
    int clockStart;
    int status;
        
        Plane(){
            flightNumber = -1;
            clockStart = -1;
            status = -1;
        }
        
        Plane(int fltNumber, int time, int stat){
            flightNumber = fltNumber;
            clockStart = time;
            status = stat;
        }
        //Setters
        void setFN(int fn){flightNumber = fn;}
        void setCS(int cs){clockStart = cs;}
        void setStat(int stat){status = stat;}
        //Getters
        int getFN(){return flightNumber;}
        int getCS(){return clockStart;}
        int getStat(){return status;}
        
        
        void refuse(){                System.out.println("Flight number "+ flightNumber+ " been redirected");
            }
        void land(int time){}
        void fly(int time){}
        int started(){return clockStart;}
        
        
        }//End Planes Class

}//End Pr13318
 



