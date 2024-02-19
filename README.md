# Pizza Shop Simulation: Discrete Event Simulation Model

### Overview
This project involves creating a discrete event simulation model for a pizza shop to enhance operational efficiency and profitability. The simulation model aims to address peak demand periods, staffing levels at various stations within the shop, oven sizes, and the number of delivery personnel required to meet customer satisfaction goals.

### Project Structure
The project consists of several Java classes that simulate different components of the pizza shop, including:

- PizzaShop.java: Central class that orchestrates the simulation, managing orders, pizza sizes, and interactions between different stations.
- MakeTable.java: Simulates the pizza preparation process, where dough is prepared and ingredients are added.
- OvenStation.java: Represents the oven where pizzas are baked, managing oven space and cooking times.
- BoxAndCutStation.java: Simulates the boxing and cutting station, where pizzas are prepared for delivery or carryout.
- DeliveryStation.java: Manages the delivery process, including the allocation of delivery drivers.
- CarryOutStation.java: Handles carryout orders, ensuring pizzas are ready for customer pickup.
- ModelRunner.java: Entry point for running the simulation, setting up the simulation environment and executing the model.

### Key Features
- Customer Satisfaction: The model focuses on achieving a high level of customer satisfaction, aiming for at least 95% of carryout and delivery orders to be completed within 35 and 45 minutes, respectively.
- Staffing and Equipment Decisions: Offers insights on the optimal number of staff for the make table, oven size, and the number of delivery personnel needed during peak hours.
Performance Measures: Evaluates the cost of different operational designs, waiting times, queue lengths, and resource utilization to guide decision-making.

### Simulation Results
Simulation outcomes help in identifying the best make table configuration (parallel or series), the ideal oven size, and the necessary staffing levels to meet predefined customer satisfaction levels while maintaining profitability.

### Validation and Verification
The model was rigorously tested and validated against expected outputs and real-world scenarios to ensure accuracy. The simulation results were compared with historical data and theoretical outcomes to verify the model's effectiveness in optimizing pizza shop operations.

### Conclusion
This simulation project provides a comprehensive tool for analyzing and optimizing the operations of a pizza shop. By adjusting various parameters, shop owners can identify strategies to improve efficiency, reduce wait times, and increase customer satisfaction, ultimately leading to higher profitability.
