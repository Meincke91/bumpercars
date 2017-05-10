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
        this.barrierActivated = null;
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

    // Activate barrier
    public void on()  {
        try {rw.P();} catch (InterruptedException e) {System.err.println(e); }

        System.out.println("\ncount: " + count);
        for (int i = 0; i <= threshold; i++) {
            System.out.println("check barrier[" + i + "] = " + barrierArray[i].toString());
        }

        if(barrierActivated == null){
            for(int i = 0; i <= threshold; i++){
                try {
                    barrierArray[i].P();
                } catch (InterruptedException e) { System.err.println(e); }
                System.out.println("turn on barrier[" +i+"] = " + barrierArray[i].toString() );
            }
        }


        barrierActivated = true;
        rw.V();
    }

    // Deactivate barrier
    public void off() {
        try {rw.P();} catch (InterruptedException e) {System.err.println(e); }

        System.out.println("\ncount: " + count);
        for (int i = 0; i <= threshold; i++) {
            System.out.println("check barrier[" + i + "] = " + barrierArray[i].toString());
        }

        for(int i = 0; i < count; i++){
            barrierArray[i].V();
        }
        count = 0;
        barrierActivated = false;
        rw.V();

    }

}