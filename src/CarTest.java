//Prototype implementation of Car Test class
//Mandatory assignment
//Course 02158 Concurrent Programming, DTU, Fall 2016

//Hans Henrik Lovengreen    Oct 3, 2016
//Martin Meincke            Maj 10, 2017

public class CarTest extends Thread {

    CarTestingI cars;
    int testno;

    public CarTest(CarTestingI ct, int no) {
        cars = ct;
        testno = no;
    }

    public void run() {
        try {
            switch (testno) { 
            case 0:
                // Demonstration of startAll/stopAll.
                // Should let the cars go one round (unless very fast)
                cars.startAll();
                sleep(3000);
                cars.stopAll();
                break;
            case 1:
                // Demonstration basic use of barrier.
                // Should let the cars go one round (unless very fast)
                cars.barrierOn();
                cars.startAll();
                sleep(2000);
                cars.barrierOff();
                cars.stopAll();
                break;
            case 2:
                // Demonstration barrier turned off while cars waiting
                // Should let the cars go one round (unless very fast)
                cars.startAll();
                cars.startCar(0);
                sleep(1000);
                cars.barrierOn();
                sleep(3000);
                cars.barrierOff();
                cars.stopAll();
                break;
            case 3:
                // Basic demonstration of fast cars
                for (int i = 1; i < 9; i++) {
                    cars.setSpeed(i,5);
                };
                cars.startAll();
                sleep(1000);
                cars.stopAll();
                break;
            case 4:
                // Demonstration of fast cars and barrier
                for (int i = 1; i < 9; i++) {
                    cars.setSpeed(i,10);
                }
                cars.startAll();
                cars.startCar(0);
                sleep(1000);
                cars.barrierOn();
                sleep(3000);
                cars.barrierOff();
                cars.stopAll();
                break;
            case 5:
                // demonstrate instant reset of barrier
                for (int i = 1; i < 9; i++) {
                    cars.setSpeed(i,30);
                }
                cars.startAll();
                cars.startCar(0);


                cars.barrierOn();
                sleep(1500);
                cars.barrierOff();
                cars.barrierOn();
                sleep(1000);
                cars.barrierOff();
                cars.stopAll();
                break;
            case 19:
                // Demonstration of speed setting.
                // Change speed to double of default values
                cars.println("Doubling speeds");
                for (int i = 1; i < 9; i++) {
                    cars.setSpeed(i,50);
                };
                break;

            default:
                cars.println("Test " + testno + " not available");
            }

            cars.println("Test ended");

        } catch (Exception e) {
            System.err.println("Exception in test: "+e);
        }
    }

}



