package aoodebod.hw6;

import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.simulation.ModelElement;
import jsl.simulation.SchedulingElement;

public class CarryOutStation extends SchedulingElement {

    protected PizzaShop myPizzaShop;
    protected ResponseVariable myCOrderTime;
    protected Counter myCOAmount;

    public CarryOutStation(PizzaShop pizzashop) {
        this(pizzashop, null);
    }

    public CarryOutStation(PizzaShop pizzashop, String name) {
        super(pizzashop, name);

        myPizzaShop = pizzashop;
        myCOrderTime = new ResponseVariable(this, "Average carryout order time");
        myCOAmount = new Counter(this, "Number of carryout order");
    }


    public void processOrder(PizzaShop.Order orderReady) {
        myCOrderTime.setValue(getTime() - orderReady.getCreateTime());
        myCOAmount.increment();
        myPizzaShop.orderCompleted();

    }
}
