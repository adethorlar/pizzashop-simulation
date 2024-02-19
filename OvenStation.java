package aoodebod.hw6;

import jsl.modeling.elements.station.DelayStation;
import jsl.modeling.elements.station.ReceiveQObjectIfc;
import jsl.modeling.elements.station.SResource;
import jsl.modeling.elements.station.SendQObjectIfc;
import jsl.modeling.queue.QObject;
import jsl.modeling.queue.Queue;
import jsl.simulation.ModelElement;
import jsl.simulation.SchedulingElement;
import jsl.utilities.random.rvariable.ConstantRV;

public class OvenStation extends SchedulingElement {

    protected PizzaShop myPizzaShop;
    protected Integer myLoadingAreaSize;
    protected Queue<PizzaShop.Order.Pizza> myLoadAreaQ;
    protected SResource loadArea;
    protected Integer availableCapacity;
    protected DelayStation myOvenArea;
    protected ConstantRV myCookingTime;
    protected Depart depart;
    protected Queue<PizzaShop.Order.Pizza> myArrivalAreaQ;

    public OvenStation(PizzaShop pizzashop) {
        this(pizzashop, 435, null);
    }

    public OvenStation(PizzaShop pizzashop, int loadingAreaSize) {
        this(pizzashop,  loadingAreaSize, null);
    }

    public OvenStation(PizzaShop pizzashop, int loadingAreaSize, String name) {
        super(pizzashop, name);
        //loadingAreaSize = 0;
        myPizzaShop = pizzashop;
        myLoadAreaQ = new Queue<>(this, getName() + ":LoadAreaQ");
        loadArea = new SResource(this,loadingAreaSize);
        myLoadingAreaSize = loadingAreaSize;
        availableCapacity = loadingAreaSize;
        myCookingTime = new ConstantRV(1.9 + 7.5);
        depart = new Depart();
        myOvenArea = new DelayStation(this, myCookingTime, depart, "oven area");
        //myOvenArea.setNextReceiver(myPizzaShop.myAfterOvenStation);
        //myOvenArea.setSender(loadPizza());
        myArrivalAreaQ = new Queue<>(this, getName() + ":ArrivalLoadAreaQ");

    }

    public void setLoadingAreaSize(int sqInches){
        loadArea.setInitialCapacity(sqInches);
        availableCapacity = sqInches;
    }

    public void loadPizza(PizzaShop.Order.Pizza pizza){
        //Still need to work on this to clarify the process logic

       /* System.out.println("initial available capacity: " + availableCapacity);
        System.out.println("initial size of queue: " + myLoadAreaQ.size());
        int totalSpace = 0;
        if (availableCapacity >= pizza.getSpace()){
            myLoadAreaQ.enqueue(pizza);
            availableCapacity = availableCapacity - pizza.getSpace();

            System.out.println(" available capacity 1: " + availableCapacity);
            System.out.println("size of queue 1: " + myLoadAreaQ.size());
        } else {
            totalSpace = 0;
            for (int i = 0; i < myLoadAreaQ.size(); i++){
                PizzaShop.Order.Pizza loadedPizza = myLoadAreaQ.removeNext();
                //availableCapacity = availableCapacity + loadedPizza.getSpace();
                //availableCapacity = myLoadingAreaSize;
                //totalSpace = totalSpace + loadedPizza.getSpace();
                myOvenArea.receive(loadedPizza);
            }
            System.out.println(" available capacity 2: " + availableCapacity);
            System.out.println("size of queue 2: " + myLoadAreaQ.size());
            //availableCapacity = availableCapacity + totalSpace;
            availableCapacity = myLoadingAreaSize;
            //loadPizza(pizza);
        }*/

        myArrivalAreaQ.enqueue(pizza);
        while (myArrivalAreaQ.isNotEmpty()){
            if (availableCapacity >= myArrivalAreaQ.peekNext().getSpace()){
                PizzaShop.Order.Pizza loadedPizza = myArrivalAreaQ.removeNext();
                availableCapacity = availableCapacity - loadedPizza.getSpace();
                myLoadAreaQ.enqueue(loadedPizza);
            }
            else {
                for (int i = 0; i < myLoadAreaQ.size(); i++) {
                    PizzaShop.Order.Pizza leavingPizza = myLoadAreaQ.removeNext();

                    myOvenArea.receive(leavingPizza);
                }
                availableCapacity = myLoadingAreaSize;
            }
        }



       /* myArrivalAreaQ.enqueue(pizza);
        while (myArrivalAreaQ.isNotEmpty()){
            if (availableCapacity >= myArrivalAreaQ.peekNext().getSpace()){
                PizzaShop.Order.Pizza loadedPizza = myArrivalAreaQ.removeNext();
                availableCapacity = availableCapacity - loadedPizza.getSpace();
                myLoadAreaQ.enqueue(loadedPizza);
                if (availableCapacity < myArrivalAreaQ.peekNext().getSpace()){
                    for (int i = 0; i < myLoadAreaQ.size(); i++) {
                        PizzaShop.Order.Pizza leavingPizza = myLoadAreaQ.removeNext();

                        myOvenArea.receive(leavingPizza);
                    }
                    availableCapacity =myLoadingAreaSize;
                }
            }
        }*/

        /*int totalSpace = 0;
        myArrivalAreaQ.enqueue(pizza);
        while (myArrivalAreaQ.isNotEmpty()){
            if (loadArea.getCapacity() >= myArrivalAreaQ.peekNext().getSpace()){
                PizzaShop.Order.Pizza loadedPizza = myArrivalAreaQ.removeNext();
                loadArea.seize(loadedPizza.getSpace());
                myLoadAreaQ.enqueue(loadedPizza);
                totalSpace = 0;
                if (loadArea.getCapacity() < myArrivalAreaQ.peekNext().getSpace()){
                    for (int i = 0; i < myLoadAreaQ.size(); i++) {
                        PizzaShop.Order.Pizza leavingPizza = myLoadAreaQ.removeNext();
                        //availableCapacity = availableCapacity + loadedPizza.getSpace();
                        //availableCapacity = myLoadingAreaSize;
                        //totalSpace = totalSpace + loadedPizza.getSpace();
                        totalSpace = totalSpace + leavingPizza.getSpace();
                        myOvenArea.receive(leavingPizza);
                    }
                    loadArea.release(totalSpace);
                }
            }
        }*/
       /*if (loadArea.getCapacity() >= myArrivalAreaQ.peekNext().getSpace()) {
           PizzaShop.Order.Pizza loadedPizza = myArrivalAreaQ.removeNext();
           loadArea.seize(loadedPizza.getSpace());
           myLoadAreaQ.enqueue(loadedPizza);
           if (loadArea.getCapacity() < myArrivalAreaQ.peekNext().getSpace()){
               for (int i = 0; i < myLoadAreaQ.size(); i++) {
                   PizzaShop.Order.Pizza leavingPizza = myLoadAreaQ.removeNext();
                   //availableCapacity = availableCapacity + loadedPizza.getSpace();
                   //availableCapacity = myLoadingAreaSize;
                   //totalSpace = totalSpace + loadedPizza.getSpace();
                   myOvenArea.receive(leavingPizza);
               }
               loadArea.release();
           }

       }*/

        /*if (loadArea.getCapacity() >= pizza.getSpace()){
            myLoadAreaQ.enqueue(pizza);
            //myLoadAreaQ.peekNext().getSpace();
            loadArea.seize(pizza.getSpace());
        }
        else {
            loadArea.release();
            for (int i = 0; i < myLoadAreaQ.size(); i++) {
                PizzaShop.Order.Pizza loadedPizza = myLoadAreaQ.removeNext();
                //availableCapacity = availableCapacity + loadedPizza.getSpace();
                //availableCapacity = myLoadingAreaSize;
                //totalSpace = totalSpace + loadedPizza.getSpace();
                myOvenArea.receive(loadedPizza);
            }
        }*/
        //myLoadAreaQ.enqueue(pizza);
        //loadArea.seize(pizza.getSpace());
        //if (loadArea.getCapacity())

    }

    protected class Depart implements SendQObjectIfc {

        @Override
        public void send(QObject qObj) {
            myPizzaShop.afterOvenStation((PizzaShop.Order.Pizza)qObj);
        }
    }
}
