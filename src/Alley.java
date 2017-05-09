/**
 * Created by martinmeincke on 08/05/2017.
 */
class Alley {
    Semaphore gate;
    Semaphore rw;

    int carsNorth;
    int carsSouth;

    public Alley(){
        this.gate = new Semaphore(1);
        this.carsNorth = 0;
        this.carsSouth = 0;
        this.rw = new Semaphore(1);
    }

    public void enter(int no) throws InterruptedException{

        if(no == 1 || no == 2 || no == 3 || no == 4){
            rw.P();

            if(carsNorth == 0 && carsSouth == 0){
                gate.P();
                carsSouth++;
                rw.V();
            } else if(carsSouth > 0) {
                carsSouth++;
                rw.V();
            } else {
                rw.V();
                gate.P();
                carsSouth++;
            }
        } else {
            rw.P();

            if(carsNorth == 0 && carsSouth == 0){
                gate.P();
                carsNorth++;
                rw.V();
            } else if(carsNorth > 0) {
                carsNorth++;
                rw.V();
            } else {
                rw.V();
                gate.P();
                carsNorth++;
            }
        }

    }

    public void leave(int no) throws InterruptedException{
        rw.P();
        if(no == 1 || no == 2 || no == 3 || no == 4){
            carsSouth--;
        } else {
            carsNorth--;
        }

        if(carsSouth == 0 && carsNorth == 0){
            gate.V();
        }
        rw.V();

    }

}