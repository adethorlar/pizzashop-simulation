package aoodebod.hw6;

import jsl.modeling.elements.station.NWayByChanceStationSender;
import jsl.modeling.elements.station.SResource;
import jsl.modeling.elements.station.SendQObjectIfc;
import jsl.modeling.elements.station.SingleQueueStation;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.queue.QObject;
import jsl.simulation.ModelElement;
import jsl.simulation.SchedulingElement;
import jsl.utilities.random.rvariable.MixtureRV;
import jsl.utilities.random.rvariable.RVariableIfc;
import jsl.utilities.random.rvariable.TriangularRV;

import java.util.List;

public class BoxAndCutStation extends SchedulingElement {

    protected PizzaShop myPizzaShop;
    protected RVariableIfc myBnCTime1;
    protected RVariableIfc myBnCTime2;
    protected RVariableIfc mySelection;
    protected RandomVariable myBnCRV;
    protected SResource boxCutter;
    protected SingleQueueStation myBnCStaion;
    protected Depart depart;

    public BoxAndCutStation (PizzaShop pizzashop) {
        this(pizzashop, 1, null);
    }

    public BoxAndCutStation (PizzaShop pizzashop, int numWorkers, String name) {
        super(pizzashop, name);

        myPizzaShop = pizzashop;
        myBnCTime1 = new TriangularRV(0.0167, 0.0565, 0.0821);
        myBnCTime2 = new TriangularRV(0.0833, 0.132, 0.15);
        double[] cdf = {0.75, 1.0};
        mySelection = new MixtureRV(List.of(myBnCTime1, myBnCTime2), cdf);
        myBnCRV = new RandomVariable(this, mySelection);
        boxCutter = new SResource(this, numWorkers, "BoxCutter");
        depart = new Depart();
        myBnCStaion = new SingleQueueStation(this, boxCutter, myBnCRV, depart, "BnCStation");

    }

    public void setNumberWorkers(int workers){
        boxCutter.setInitialCapacity(workers);
    }

    public void boxAndCutPizza(PizzaShop.Order.Pizza cookedPizza){
        myBnCStaion.receive(cookedPizza);
    }

    protected class Depart implements SendQObjectIfc {

        @Override
        public void send(QObject qObj) {
            //System.out.println("End of Box n Cut");
            myPizzaShop.afterBoxAndCut((PizzaShop.Order.Pizza)qObj);
        }
    }
}
