/**
 * Created by martinmeincke on 10/05/2017.
 */
public class Barrier {
    Semaphore rw;

    Boolean barrierActivated;

    int count;
    int threshold;
    Semaphore[] barrierArray;

    public Barrier(){
        this.barrierActivated = false;
        this.count = 0;

        this.threshold = 9;
        this.barrierArray = new Semaphore[threshold+1];

        for(int i = 0; i <= threshold; i++){
            this.barrierArray[i] = new Semaphore(1);
        }
        this.rw = new Semaphore(1);
    }

    // Wait for others to arrive (if barrier active)
    public void sync() {
        if (barrierActivated) {

            try {rw.P();} catch (InterruptedException e) {System.err.println(e); }
            count++;
            if (count >= threshold) {
                System.out.println("\ncount: " + count);
                for (int i = 0; i <= threshold; i++) {
                    System.out.println("check barrier[" + i + "] = " + barrierArray[i].toString());
                }
                for (int i = 0; i <= threshold; i++) {
                    //open barrier
                    barrierArray[i].V();

                }
                count = 0;
                rw.V();
            } else {
                System.out.println("\ncount: " + count);
                for (int i = 0; i <= threshold; i++) {
                    System.out.println("check barrier[" + i + "] = " + barrierArray[i].toString());
                }
                try {
                    rw.V();
                    barrierArray[count - 1].P();
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }

        }
    }
                /*
                rw.P();
                System.out.println("check1" + rw.toString());
                if(count >= threshold){
                    for(int i = 0; i <= threshold; i++){
                        System.out.println("check barrier[" + i + "] = " + barrierArray[i].toString() );
                    }
                    System.out.println("\nreset at count: " + count);
                    count = 0;

                    for(int i = 0; i <= threshold; i++){


                        barrierArray[i].V();
                        System.out.println("barrier[" + i + "] = " + barrierArray[i].toString() );
                    }
                    System.out.println();

                }
                System.out.println("check1 stop"+ rw.toString());
                rw.V();

                rw.P();

                System.out.println("check2 " + rw.toString());
                if(count == 0){
                    count = 1;
                    for(int i = 0; i <= threshold; i++){
                        System.out.println("check barrier[" + i + "] = " + barrierArray[i].toString() );
                    }
                    for(int i = 0; i <= threshold; i++){
                        System.out.println("lock barrier[" + i + "] = " + barrierArray[i].toString() );

                        barrierArray[i].P();
                    }
                    System.out.println("check2 stop");
                    rw.V();
                    barrierArray[0].P();
                    //System.out.print("lock 0");
                } else {
                    System.out.println("check2 stop" + rw.toString());
                    rw.V();
                }



                rw.P();
                System.out.println("check3" + rw.toString());
                if (count < threshold && count > 0)  {
                    for(int i = 0; i <= threshold; i++){
                        System.out.println("check barrier[" + i + "] = " + barrierArray[i].toString() );
                    }
                    count++;
                    System.out.println("\ncount up to: " + count);
                    System.out.println("barrier at count: " + barrierArray[count].toString());
                    System.out.println("check3 stop" + rw.toString());
                    rw.V();

                    System.out.println("blocker?");
                    barrierArray[count-1].P();*/
                    /*

                    System.out.println("\ncount up on: " + (count));

                    System.out.println("up on: " + barrierArray[count].toString());
                    int tmp = count - 1;
                    if(count == 0){
                        tmp = threshold;
                    }
                    System.out.println("down on: " + barrierArray[tmp].toString());

                    barrierArray[count].V();
                    rw.V();
                    barrierArray[tmp].P();

                }



            }
            */

        /*
        if(this.barrierActivated){
            try {
                System.out.println("cars waiting " + barrierWaiting);
                System.out.println("car at barrier");
                rw.P();


                if(barrierWaiting == 8){
                    System.out.println("Release cars");
                    this.barrierWaiting = 0;
                    barrierOn.V();

                    rw.V();
                } else if(barrierWaiting == 0){
                    barrierOn.P();
                    barrierWaiting++;

                    System.out.println("first car");
                    rw.V();
                    barrierOn.P();
                } else {
                    barrierWaiting++;
                    rw.V();
                    System.out.println("barriorOn: " + barrierOn.toString());
                    barrierOn.P();
                    barrierOn.V();
                }
            } catch (InterruptedException e) {
                System.err.println(e);
            }
            System.out.println();
        }
        */

    // Activate barrier
    public void on()  {
        try {rw.P();} catch (InterruptedException e) {System.err.println(e); }

        for(int i = 0; i <= threshold; i++){
            try {
                barrierArray[i].P();
            } catch (InterruptedException e) { System.err.println(e); }
            System.out.println("turn on barrier[" +i+"] = " + barrierArray[i].toString() );
        }

        barrierActivated = true;
        rw.V();


        /*
        try {
            rw.P();
            this.barrierActivated = true;

            System.out.println("barrier on");

            //barrierOn.P();
            rw.V();
        } catch (InterruptedException e) {
            System.err.println(e);
        }*/
    }

    // Deactivate barrier
    public void off() {
        try {rw.P();} catch (InterruptedException e) {System.err.println(e); }

        for(int i = 0; i < count; i++){
            try {
                barrierArray[i].P();
            } catch (InterruptedException e) { System.err.println(e); }

        }
        count = 0;
        barrierActivated = false;
        rw.V();

        /*
        try {
            rw.P();
            this.barrierActivated = false;
            this.barrierWaiting = 0;
            this.barrierOn.V();
            System.out.println("barrier off");
            rw.V();
        } catch (InterruptedException e) {
            System.err.println(e);
        }
        */

    }

}