package aoodebod.hw6;

import jsl.modeling.elements.station.SResource;
import jsl.modeling.elements.station.SingleQueueStation;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.queue.QObject;
import jsl.modeling.queue.Queue;
import jsl.simulation.EventActionIfc;
import jsl.simulation.JSLEvent;
import jsl.simulation.ModelElement;
import jsl.simulation.SchedulingElement;

import java.util.List;

public class MakeTable extends SchedulingElement {

    protected PizzaShop myPizzaShop;
    protected SResource myWorker;
    protected SingleQueueStation myMTStation;
    protected RandomVariable myMTTime;
    protected PizzaMaking myPizzaMaker;
    protected Queue<PizzaShop.Order.Pizza> myMTAreaQ;
    protected Depart depart;
    //protected SeriesMakeTable mySeriesMakeTable;

    protected SResource myWorker1;
    protected SResource myWorker2;
    protected SResource myWorker3;
    //protected Integer AmtOfWorkers;
    protected Queue<PizzaShop.Order.Pizza> mySMTAreaQ1;
    protected Queue<PizzaShop.Order.Pizza> mySMTAreaQ2;
    protected Queue<PizzaShop.Order.Pizza> mySMTAreaQ3;
    protected Maker1 myMaker1;
    protected Maker2 myMaker2;
    protected Maker3 myMaker3;


    public MakeTable(PizzaShop pizzashop) {
        this(pizzashop, 1, null);
    }

    public MakeTable(PizzaShop pizzashop, int numWorkers, String name) {
        super(pizzashop, name);

        myPizzaShop = pizzashop;
        //myMTTime = new RandomVariable(this, new )
        myWorker = new SResource(this, numWorkers, "MT Worker");
        //myMTStation = new SingleQueueStation(this, myWorker, null,"Make Table Station");
        //myMTStation.setUseQObjectServiceTimeOption(true);
        myPizzaMaker = new PizzaMaking();
        myMTAreaQ = new Queue<>(this, getName() + ":MTAreaQ");
        depart = new Depart();
        //mySeriesMakeTable = new SeriesMakeTable(pizzashop);


        myWorker1 = new SResource(this, 1, "SMT Worker 1");
        myWorker2 = new SResource(this, 1, "SMT Worker 2");
        myWorker3 = new SResource(this, 1, "SMT Worker 3");
        //AmtOfWorkers = numWorkers;
        mySMTAreaQ1 = new Queue<>(this, ":SMTAreaQ1");
        mySMTAreaQ2 = new Queue<>(this, ":SMTAreaQ2");
        mySMTAreaQ3 = new Queue<>(this, ":SMTAreaQ3");
        myMaker1 = new Maker1();
        myMaker2 = new Maker2();
        myMaker3 = new Maker3();
        //myPizzaShop = pizzashop;

    }

    public void setNumberWorkers(int workers){
        myWorker.setInitialCapacity(workers);

    }

    public void processOrder(PizzaShop.Order order){
        List<PizzaShop.Order.Pizza> pizzas = order.getPizzas();

        for (PizzaShop.Order.Pizza pizza : pizzas) {
            //myMTStation.receive(pizza);
            scheduleEvent(myPizzaMaker, 0, pizza);  //remove comment to use parallel make table
            //myMTStation.setServiceTime((pizza.getDoughAndSauceTime() + pizza.getPrimaryIngredientsTime() + pizza.getFinalIngredientsTime()));

            //processSeriesPizza(pizza); //remove comment to use series make table
        }

    }

    protected class PizzaMaking implements EventActionIfc<QObject> {

        @Override
        public void action(JSLEvent<QObject> event) {
            PizzaShop.Order.Pizza pizza = (PizzaShop.Order.Pizza)event.getMessage();
            myMTAreaQ.enqueue(pizza);
            if (myWorker.hasAvailableUnits()){
                myWorker.seize();
                myMTAreaQ.remove(pizza);
                double makeTime = pizza.getDoughAndSauceTime() + pizza.getPrimaryIngredientsTime() + pizza.getFinalIngredientsTime();
                scheduleEvent(depart, makeTime, pizza);
            }

        }
    }

    protected class Depart implements EventActionIfc<QObject> {

        @Override
        public void action(JSLEvent<QObject> event) {
            PizzaShop.Order.Pizza pizzaCreated = (PizzaShop.Order.Pizza)event.getMessage();
            myPizzaShop.afterMakeTable(pizzaCreated);
            myWorker.release();

            if (myMTAreaQ.isNotEmpty()){
                myWorker.seize();
                PizzaShop.Order.Pizza pizza = myMTAreaQ.removeNext();
                double makeTime = pizza.getDoughAndSauceTime() + pizza.getPrimaryIngredientsTime() + pizza.getFinalIngredientsTime();
                scheduleEvent(depart, makeTime, pizza);
            }
        }
    }


                //3-man seriesMakeTable




    public void processSeriesPizza(PizzaShop.Order.Pizza pizza){
        mySMTAreaQ1.enqueue(pizza);
        /*while ((mySMTAreaQ1.isNotEmpty()) && (myWorker1.isIdle())){
            PizzaShop.Order.Pizza waitingPizza = mySMTAreaQ1.removeNext();
            myWorker1.seize();
            scheduleEvent(myMaker1, waitingPizza.getDoughAndSauceTime(), waitingPizza);
        }*/

        while ((mySMTAreaQ2.size() < 2) && (myWorker1.isIdle())){
            PizzaShop.Order.Pizza waitingPizza = mySMTAreaQ1.removeNext();
            myWorker1.seize();
            scheduleEvent(myMaker1, waitingPizza.getDoughAndSauceTime(), waitingPizza);
        }
    }

    protected class Maker1 implements EventActionIfc<QObject>{

        @Override
        public void action(JSLEvent<QObject> event) {
            PizzaShop.Order.Pizza pizza = (PizzaShop.Order.Pizza)event.getMessage();
            /*if (mySMTAreaQ2.isEmpty()){
                mySMTAreaQ2.enqueue(pizza);
                myWorker1.release();
                //processSeriesPizza(null);
                if ((mySMTAreaQ1.isNotEmpty()) && (myWorker1.isIdle())){
                    PizzaShop.Order.Pizza waitingPizza = mySMTAreaQ1.removeNext();
                    myWorker1.seize();
                    scheduleEvent(myMaker1, waitingPizza.getDoughAndSauceTime(), waitingPizza);
                }
            }*/
            mySMTAreaQ2.enqueue(pizza);
            myWorker1.release();
            while ((mySMTAreaQ3.size() < 2) && (myWorker2.isIdle())){
                PizzaShop.Order.Pizza waitingPizza = mySMTAreaQ2.removeNext();
                myWorker2.seize();
                scheduleEvent(myMaker2, waitingPizza.getPrimaryIngredientsTime(), waitingPizza);
            }

            /*while ((mySMTAreaQ2.isNotEmpty()) && (myWorker2.isIdle())){
                PizzaShop.Order.Pizza waitingPizza = mySMTAreaQ2.removeNext();
                myWorker2.seize();
                scheduleEvent(myMaker2, waitingPizza.getPrimaryIngredientsTime(), waitingPizza);
            }*/
        }
    }

    protected class Maker2 implements EventActionIfc<QObject> {

        @Override
        public void action(JSLEvent<QObject> event) {

            PizzaShop.Order.Pizza pizza = (PizzaShop.Order.Pizza)event.getMessage();
            /*if (mySMTAreaQ3.isEmpty()) {
                mySMTAreaQ3.enqueue(pizza);
                myWorker2.release();
                //scheduleEvent(myMaker1, 0);
                if ((mySMTAreaQ2.isNotEmpty()) && (myWorker2.isIdle())){
                    PizzaShop.Order.Pizza waitingPizza = mySMTAreaQ2.removeNext();
                    myWorker2.seize();
                    scheduleEvent(myMaker2, waitingPizza.getPrimaryIngredientsTime(), waitingPizza);
                }
            }*/

            mySMTAreaQ3.enqueue(pizza);
            myWorker2.release();

            while ((mySMTAreaQ3.isNotEmpty()) && (myWorker3.isIdle())){
                PizzaShop.Order.Pizza waitingPizza = mySMTAreaQ3.removeNext();
                myWorker3.seize();
                scheduleEvent(myMaker3, waitingPizza.getFinalIngredientsTime(), waitingPizza);
            }

        }
    }

    protected class Maker3 implements EventActionIfc<QObject>{

        @Override
        public void action(JSLEvent<QObject> event) {
            PizzaShop.Order.Pizza pizza = (PizzaShop.Order.Pizza)event.getMessage();
            myPizzaShop.afterMakeTable(pizza);
            myWorker3.release();
            //scheduleEvent(myMaker2, 0);
            /*if ((mySMTAreaQ3.isNotEmpty()) && (myWorker3.isIdle())){
                PizzaShop.Order.Pizza waitingPizza = mySMTAreaQ3.removeNext();
                myWorker3.seize();
                scheduleEvent(myMaker3, waitingPizza.getFinalIngredientsTime(), waitingPizza);
            }*/
        }
    }

}
