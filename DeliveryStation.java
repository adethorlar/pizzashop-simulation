package aoodebod.hw6;

import jsl.modeling.elements.station.ReceiveQObjectIfc;
import jsl.modeling.elements.station.SResource;
import jsl.modeling.elements.station.SendQObjectIfc;
import jsl.modeling.elements.station.SingleQueueStation;
import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.queue.QObject;
import jsl.modeling.queue.Queue;
import jsl.simulation.EventActionIfc;
import jsl.simulation.JSLEvent;
import jsl.simulation.ModelElement;
import jsl.simulation.SchedulingElement;
import jsl.utilities.random.rvariable.LognormalRV;
import jsl.utilities.random.rvariable.TriangularRV;

public class DeliveryStation extends SchedulingElement {

    protected PizzaShop myPizzaShop;
    protected RandomVariable myDriveTime;
    protected SResource myDriver;
    protected SingleQueueStation myDEStaion;
    protected Queue<PizzaShop.Order> myDeliveryAreaQ;
    protected Depart depart;
    protected ResponseVariable myDelOrderTime;
    protected Counter myDelAmount;
    protected DriveBack myDriveBack;

    public DeliveryStation(PizzaShop pizzashop) {
        this(pizzashop, 1, null);
    }

    public DeliveryStation(PizzaShop pizzashop, int numDrivers, String name) {
        super(pizzashop, name);

        myPizzaShop = pizzashop;
        myDriveTime = new RandomVariable(this, new TriangularRV(3.0, 5.0, 12));
        myDriver = new SResource(this, numDrivers, "Driver");
        //myDEStaion = new SingleQueueStation(this, myDriver, myDriveTime, depart, "DEStation");
        myDeliveryAreaQ = new Queue<>(this, getName() + ":DeliveryAreaQ");
        depart = new Depart();
        myDelOrderTime = new ResponseVariable(this, "Average delivery order time");
        myDelAmount = new Counter(this, "Number of delivery order");
        myDriveBack = new DriveBack();
    }

    public void setNumberDeliveryDrivers(int drivers){
        myDriver.setInitialCapacity(drivers);
    }

    public void processOrder(PizzaShop.Order orderReady) {
        myDeliveryAreaQ.enqueue(orderReady);
        if (myDriver.hasAvailableUnits()){
            myDriver.seize();
            PizzaShop.Order orderEnroute = myDeliveryAreaQ.removeNext();
            scheduleEvent(depart, myDriveTime, orderEnroute);
        }

    }

    protected class Depart implements EventActionIfc<QObject> {


        @Override
        public void action(JSLEvent<QObject> event) {

            PizzaShop.Order orderCompleted = (PizzaShop.Order)event.getMessage();
            myDelOrderTime.setValue(getTime() - orderCompleted.getCreateTime());
            myDelAmount.increment();
            myPizzaShop.orderCompleted();
            scheduleEvent(myDriveBack, myDriveTime);

        }
    }

    protected class DriveBack implements EventActionIfc<QObject> {


        @Override
        public void action(JSLEvent<QObject> event){

            myDriver.release();
            if (myDeliveryAreaQ.isNotEmpty()){
                myDriver.seize();
                PizzaShop.Order orderEnroute = myDeliveryAreaQ.removeNext();
                scheduleEvent(depart, myDriveTime, orderEnroute);
            }

        }
    }
}
