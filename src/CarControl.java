//Prototype implementation of Car Control
//Mandatory assignment
//Course 02158 Concurrent Programming, DTU, Fall 2016

//Hans Henrik Lovengreen    Oct 3, 2016


import java.awt.Color;

class Grid {
    Semaphore[][] grid;

    public Grid(){
        this.grid = new Semaphore[Layout.ROWS][Layout.COLS];
        //Fill grid
        for(int i = 0; i < Layout.ROWS; i++){
            for(int j = 0; j < Layout.COLS; j++){
                this.grid[i][j] = new Semaphore(1);
            }
        }
    }

    public void enter(Pos p) throws InterruptedException {
        this.grid[p.row][p.col].P();
    }

    public void leave(Pos p){
        this.grid[p.row][p.col].V();
    }

}

class Gate {

    Semaphore g = new Semaphore(0);
    Semaphore e = new Semaphore(1);
    boolean isopen = false;

    public void pass() throws InterruptedException {
        g.P(); 
        g.V();
    }

    public void open() {
        try { e.P(); } catch (InterruptedException e) {}
        if (!isopen) { g.V();  isopen = true; }
        e.V();
    }

    public void close() {
        try { e.P(); } catch (InterruptedException e) {}
        if (isopen) { 
            try { g.P(); } catch (InterruptedException e) {}
            isopen = false;
        }
        e.V();
    }

}

class Car extends Thread {

    int basespeed = 50;             // Rather: degree of slowness
    int variation =  20;             // Percentage of base speed


    CarDisplayI cd;                  // GUI part

    int no;                          // Car number
    Pos startpos;                    // Startpositon (provided by GUI)
    Pos barpos;                      // Barrierpositon (provided by GUI)
    Color col;                       // Car  color
    Gate mygate;                     // Gate at startposition

    Alley alley;
    Barrier barrier;

    int speed;                       // Current car speed
    Pos curpos;                      // Current position 
    Pos newpos;                      // New position to go to
    Grid grid;

    public Car(int no, CarDisplayI cd, Gate g, Grid grid, Alley alley, Barrier barrier) {
        this.grid = grid;
        this.no = no;
        this.cd = cd;
        this.alley = alley;
        this.barrier = barrier;

        mygate = g;
        startpos = cd.getStartPos(no);
        barpos = cd.getBarrierPos(no);  // For later use
        col = chooseColor();

        // do not change the special settings for car no. 0
        if (no==0) {
            basespeed = 0;  
            variation = 0; 
            setPriority(Thread.MAX_PRIORITY); 
        }
    }

    public synchronized void setSpeed(int speed) { 
        if (no != 0 && speed >= 0) {
            basespeed = speed;
        }
        else
            cd.println("Illegal speed settings");
    }

    public synchronized void setVariation(int var) { 
        if (no != 0 && 0 <= var && var <= 100) {
            variation = var;
        }
        else
            cd.println("Illegal variation settings");
    }

    synchronized int chooseSpeed() { 
        double factor = (1.0D+(Math.random()-0.5D)*2*variation/100);
        return (int) Math.round(factor*basespeed);
    }

    private int speed() {
        // Slow down if requested
        final int slowfactor = 3;  
        return speed * (cd.isSlow(curpos)? slowfactor : 1);
    }

    Color chooseColor() { 
        return Color.blue; // You can get any color, as longs as it's blue 
    }

    Pos nextPos(Pos pos) {
        // Get my track from display
        return cd.nextPos(no,pos);
    }

    boolean atGate(Pos pos) {
        return pos.equals(startpos);
    }

    boolean atBarrier(Pos pos, int carNo){
        if (pos.col >= 3){
            if(carNo <= 4 && pos.row == 6){
                return true;
            } else if(carNo > 4 && pos.row == 5) {
                return true;
            }
        }
        return false;

    }

   public void run() {


       try {

            speed = chooseSpeed();
            curpos = startpos;
            cd.mark(curpos,col,no);

            while (true) {
                sleep(speed());
  
                if (atGate(curpos)) { 
                    mygate.pass(); 
                    speed = chooseSpeed();

                }

                if(atBarrier(curpos, no)){
                    barrier.sync();
                }

                if(curpos.equals(new Pos(2,1)) || curpos.equals(new Pos(1,3)) || curpos.equals(new Pos(10,0))){
                    alley.enter(no);

                }
                if(curpos.equals(new Pos(9,1)) || curpos.equals(new Pos(0,2))){
                    alley.leave(no);
                }


                newpos = nextPos(curpos);
                
                //  Move to new position
                grid.enter(newpos);

                cd.clear(curpos);
                cd.mark(curpos, newpos, col, no);
                sleep(speed());
                cd.clear(curpos, newpos);
                cd.mark(newpos, col, no);


                grid.leave(curpos);

                curpos = newpos;
            }

        } catch (Exception e) {
            cd.println("Exception in Car no. " + no);
            System.err.println("Exception in Car no. " + no + ":" + e);
            e.printStackTrace();
        }
    }

}

public class CarControl implements CarControlI{

    CarDisplayI cd;             // Reference to GUI
    Car[]  car;                 // Cars
    Gate[] gate;                // Gates
    Alley alley;                // alley
    Barrier barrier;
    Grid grid;

    public CarControl(CarDisplayI cd) {
        this.cd = cd;
        car  = new  Car[9];
        gate = new Gate[9];
        this.grid = new Grid();
        this.alley = new Alley();
        this.barrier = new Barrier();
        for (int no = 0; no < 9; no++) {
            gate[no] = new Gate();
            car[no] = new Car(no,cd,gate[no], grid, alley, barrier);
            car[no].start();
        }
    }


   public void startCar(int no) {
        gate[no].open();
    }

    public void stopCar(int no) {
        gate[no].close();
    }

    public void barrierOn() {
        barrier.on();
    }

    public void barrierOff() { 
        barrier.off();
    }

    public void barrierSet(int k) { 
        cd.println("Barrier threshold setting not implemented in this version");
         // This sleep is for illustrating how blocking affects the GUI
        // Remove when feature is properly implemented.
        try { Thread.sleep(3000); } catch (InterruptedException e) { }
     }

    public void removeCar(int no) { 
        cd.println("Remove Car not implemented in this version");
    }

    public void restoreCar(int no) { 
        cd.println("Restore Car not implemented in this version");
    }

    /* Speed settings for testing purposes */

    public void setSpeed(int no, int speed) { 
        car[no].setSpeed(speed);
    }

    public void setVariation(int no, int var) { 
        car[no].setVariation(var);
    }

}






