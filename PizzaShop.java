package aoodebod.hw6;

import jsl.modeling.elements.EventGenerator;
import jsl.modeling.elements.EventGeneratorActionIfc;
import jsl.modeling.elements.RandomElement;
import jsl.modeling.elements.station.SingleQueueStation;
import jsl.modeling.elements.station.Station;
import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.queue.QObject;
import jsl.modeling.queue.Queue;
import jsl.simulation.JSLEvent;
import jsl.simulation.ModelElement;
import jsl.simulation.Simulation;
import jsl.utilities.random.rvariable.BernoulliRV;
import jsl.utilities.random.rvariable.DEmpiricalRV;
import jsl.utilities.random.rvariable.ExponentialRV;
import jsl.utilities.random.rvariable.TriangularRV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PizzaShop extends ModelElement {

    enum PizzaSize {
        SMALL(115),
        MEDIUM(175),
        LARGE(250);
        private final int space;

        PizzaSize(int space) {
            this.space = space;
        }

        public int getSpace() {
            return space;
        }

    }

    private List<PizzaSize> myPizzaSizeList = List.of(PizzaSize.SMALL,
            PizzaSize.MEDIUM, PizzaSize.LARGE);

    protected RandomVariable myTBOrderArrivals;
    protected EventGenerator myOrderGenerator;
    protected RandomVariable myNumPizzaPerOrderRV;
    protected RandomElement<PizzaSize> myPizzaSizes;
    protected Map<PizzaSize, RandomVariable> myDoughAndSauceRV;
    protected Map<PizzaSize, RandomVariable> myPrimaryIngredientRV;
    protected Map<PizzaSize, RandomVariable> myFinalIngredientRV;
    protected Queue<Order> myOrderWaitingQ;
    private MakeTable myMakeTable;
    protected RandomVariable myCarryOutOrDeliveryRV;
    protected OvenStation myOvenStation;
    //protected SingleQueueStation myAfterOvenStation;
    protected CarryOutStation myCarryOutStation;
    protected BoxAndCutStation myBoxAndCutStation;
    protected DeliveryStation myDeliveryStation;
    protected ResponseVariable myNumOrdersAtEndOfPeak;
    protected Counter myNumOrdersCompleted;
    //protected Integer myCompletedOrders;

    public PizzaShop(ModelElement parent) {
        this(parent, null);
    }

    public PizzaShop(ModelElement parent, String name) {
        super(parent, name);

        myCarryOutOrDeliveryRV = new RandomVariable(this, new BernoulliRV(0.4));
        myTBOrderArrivals = new RandomVariable(this, new ExponentialRV(3.0));
        myOrderGenerator = new EventGenerator(this, new OrderArrival(), myTBOrderArrivals, myTBOrderArrivals);
        double[] values = {1, 2, 3};
        double[] vCDF = {0.64, 0.95, 1.0};
        DEmpiricalRV numPizzas = new DEmpiricalRV(values, vCDF);
        myNumPizzaPerOrderRV = new RandomVariable(this, numPizzas);
        double[] sCDF = {0.12, 0.44, 1.0};
        myPizzaSizes = new RandomElement<>(this, myPizzaSizeList, sCDF);
        myDoughAndSauceRV = new HashMap<>();
        myDoughAndSauceRV.put(PizzaSize.LARGE,
                new RandomVariable(this, new TriangularRV(0.5, 0.7, 0.8)));
        myDoughAndSauceRV.put(PizzaSize.MEDIUM,
                new RandomVariable(this, new TriangularRV(0.4, 0.6, 0.8)));
        myDoughAndSauceRV.put(PizzaSize.SMALL,
                new RandomVariable(this, new TriangularRV(0.3, 0.5, 0.7)));

        myPrimaryIngredientRV = new HashMap<>();
        myPrimaryIngredientRV.put(PizzaSize.LARGE,
                new RandomVariable(this, new TriangularRV(0.6, 0.8, 1.0)));
        myPrimaryIngredientRV.put(PizzaSize.MEDIUM,
                new RandomVariable(this, new TriangularRV(0.5, 0.7, 0.9)));
        myPrimaryIngredientRV.put(PizzaSize.SMALL,
                new RandomVariable(this, new TriangularRV(0.4, 0.5, 0.6)));

        myFinalIngredientRV = new HashMap<>();
        myFinalIngredientRV.put(PizzaSize.LARGE,
                new RandomVariable(this, new TriangularRV(0.5, 0.6, 0.7)));
        myFinalIngredientRV.put(PizzaSize.MEDIUM,
                new RandomVariable(this, new TriangularRV(0.4, 0.5, 0.6)));
        myFinalIngredientRV.put(PizzaSize.SMALL,
                new RandomVariable(this, new TriangularRV(0.3, 0.4, 0.5)));

        myOrderWaitingQ = new Queue<>(this, getName() + ":OrderWQ");
        myMakeTable = new MakeTable(this,1,getName() + ":MakeTable");
        myOvenStation = new OvenStation(this, 435, getName() + ":Oven");
        //myAfterOvenStation = new SingleQueueStation(this, "after oven station");
        //myAfterOvenStation.setNextReceiver();
        myBoxAndCutStation = new BoxAndCutStation(this, 1, getName() + ":BoxAndCut");
        myCarryOutStation = new CarryOutStation(this, getName() + ":CarryOut");
        myDeliveryStation = new DeliveryStation(this, 1, getName() + ":Delivery");
        myNumOrdersAtEndOfPeak = new ResponseVariable(this, getName() + ":NumOrdersAtEndOfPeak");
        myNumOrdersCompleted = new Counter(this, getName() + ":OrdersCompleted");
    }

    public void setOvenLoadingAreaSize(int sqInches) {
        myOvenStation.setLoadingAreaSize(sqInches);
    }

    public void setNumDeliveryDrivers(int drivers) {
        myDeliveryStation.setNumberDeliveryDrivers(drivers);
    }

    public void setNumberBoxAndCutWorkers(int workers) {
        myBoxAndCutStation.setNumberWorkers(workers);
    }

    public void setOrderArrivalRate(double arrivalRate) {
        if (arrivalRate <= 0.0) {
            throw new IllegalArgumentException("The order arrival rate must be > 0");
        }
        double mtba = (1.0 / arrivalRate) * 60.0;
        ExponentialRV rv = new ExponentialRV(mtba, myTBOrderArrivals.getRandomNumberStream());
        myTBOrderArrivals.setInitialRandomSource(rv);
    }

    private class OrderArrival implements EventGeneratorActionIfc {
        @Override
        public void generate(EventGenerator generator, JSLEvent event) {
            // create order here and do something with it
            Order order = new Order();
            myOrderWaitingQ.enqueue(order);
            myMakeTable.processOrder(order);
        }
    }

    void orderReady(Order order) {
        myOrderWaitingQ.remove(order);// remove the order from waiting queue
        if (order.isCarryOut()) {
            myCarryOutStation.processOrder(order);
        } else {
            myDeliveryStation.processOrder(order);
        }
    }

    void afterMakeTable(Order.Pizza pizza) {
        myOvenStation.loadPizza(pizza);
    }

    void afterOvenStation(Order.Pizza pizza) {
        myBoxAndCutStation.boxAndCutPizza(pizza);
    }

    void afterBoxAndCut(Order.Pizza pizza) {
        //System.out.println("pizza.done");
        pizza.done();
    }

    void orderCompleted() {
        myNumOrdersCompleted.increment();
    }

    @Override
    protected void replicationEnded() {
        myNumOrdersAtEndOfPeak.setValue(myOrderWaitingQ.size());
    }



    class Order extends QObject {
        //boolean completed;
        int numPizzas;
        List<Pizza> pizzas;// needed?
        int numCompleted;
        boolean carryOut;

        public Order() {
            super(getTime());
            if (myCarryOutOrDeliveryRV.getValue() == 0.0) {// 0.0 is delivery
                carryOut = false;
            } else {
                carryOut = true;
            }
            pizzas = new ArrayList<>();
            numCompleted = 0;
            //completed = false;
            numPizzas = (int) myNumPizzaPerOrderRV.getValue();
            for (int i = 1; i <= numPizzas; i++) {
                pizzas.add(new Pizza());
            }
        }

        private void pizzaReady() {
            numCompleted++;
            if (isCompleted()) {
                //completed = true;
                orderReady(this);
            }
        }

        public boolean isCarryOut() {
            return carryOut;
        }

        List<Pizza> getPizzas() {
            return pizzas;
        }

        boolean isCompleted() {
            return numCompleted == numPizzas;
        }

        class Pizza extends QObject {
            PizzaSize mySize;
            protected boolean completed;

            public Pizza() {
                super(getTime());
                completed = false;
                mySize = myPizzaSizes.getRandomElement();
            }

            double getDoughAndSauceTime() {
                return myDoughAndSauceRV.get(mySize).getValue();
            }

            double getPrimaryIngredientsTime() {
                return myPrimaryIngredientRV.get(mySize).getValue();
            }

            double getFinalIngredientsTime() {
                return myFinalIngredientRV.get(mySize).getValue();
            }

            PizzaSize getSize() {
                return mySize;
            }

            int getSpace() {
                return mySize.getSpace();
            }

            void done() {
                completed = true;
                Order.this.pizzaReady();
            }

            boolean isCompleted() {
                return completed;
            }
        }
    }


}
