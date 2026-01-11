## Analysis
Modern freight transportation operates as a large-scale, distributed system in which shipments move across interconnected networks under strict timing, reliability, and customer-service constraints. Shipments frequently traverse multiple transportation modes—such as truck, rail, air, or sea—before reaching their destination. Delays introduced at any point in this network, including within a single transportation mode or during intermodal handoffs, can propagate downstream and significantly impact both operational performance and customer delivery commitments.

Intermodal transitions represent a particularly complex source of delay. When freight transfers between modes, it becomes subject to terminal processing times, equipment availability, scheduling alignment, and local operating constraints. A disruption in one mode, such as a rail service delay, can cascade into subsequent trucking legs, missed inbound cut times, extended terminal dwell, and ultimately a later customer delivery date. Traditional tracking systems often treat these legs in isolation, leading to optimistic ETAs that are corrected only after delays occur.

The purpose of TransitFlow is to model this problem space through a software system that continuously simulates shipment movement across multi-leg, multi-modal routes and automatically predicts future outcomes based on both current operational state and forecasted conditions. Rather than requiring users to manually run “what-if” scenarios, TransitFlow is designed to operate as a background prediction engine that continuously updates estimated arrival and delivery times as conditions change.

The primary goal of the system is to demonstrate how engineering decisions can improve predictability, reliability, and trust in freight tracking systems. TransitFlow advances simulated time automatically, incorporates external signals such as weather and congestion, accounts for both intra-modal and intermodal delays, and recalculates two distinct but related estimates: operational arrival time at the destination terminal and customer-facing estimated delivery date. Success is defined not by perfect prediction, but by providing more accurate, explainable, and continuously updated ETAs that reduce surprises for customers and operations teams.

The intended consumers of TransitFlow are existing transportation management and tracking systems rather than end users. The system is designed to be integrated into a real tracking platform, where its predictive outputs can be used to adjust delivery promises, inform customer communications, and support operational decision-making. As such, the system prioritizes clear separation of concerns, deterministic behavior, testability, and transparency over user interfaces or manual interaction.

Several constraints shape the scope of this project. The system must run locally in a reproducible manner, operate automatically without user-driven simulation steps, and remain small enough to be built and reasoned about within a limited timeframe. TransitFlow intentionally avoids real-time geographic visualization, proprietary optimization algorithms, and opaque machine-learning modelsbecause its goal is not to impress users visually or control operations, but to quietly and reliably improve ETA accuracy in real freight tracking systems through explainable, deterministic prediction. External data sources such as weather and congestion are treated as influencing signals rather than authoritative truth, ensuring predictions remain explainable and auditable.

To maintain focus on core engineering concerns, the model makes several simplifying assumptions. Shipments are treated as independent entities progressing along predefined multi-leg routes without explicit contention for shared track capacity, crew availability, or dispatch prioritization. Intermodal transfer times are modeled at a level sufficient to capture downstream impact without reproducing full terminal operations. External conditions such as weather are incorporated using coarse-grained severity and regional indicators rather than fine-grained sensor data. Terminal delivery behavior is governed by configurable business rules, including inbound cut times, business calendars, weekend delivery options, and holidays, allowing customer delivery estimates to reflect real operational policies.
These assumptions and constraints allow TransitFlow to focus on its core objective: demonstrating how continuous simulation, predictive modeling, and business-driven delivery rules can be combined to produce more reliable and trustworthy ETAs for both terminals and customers, while remaining suitable for integration into real-world freight tracking systems.
---
## Design

### Architecture
TransitFlow is implemented as a layered monolithic backend service using Spring Boot. This architecture provides clear separation of concerns while remaining straightforward to understand, test, and extend. The system is designed to operate primarily as a background prediction engine that integrates with existing freight tracking systems, rather than as an interactive simulation tool.
Each layer has a well-defined responsibility, allowing simulation, prediction, delivery estimation, and external integrations to evolve independently without introducing unnecessary coupling or operational complexity.

Logical layers:
- API Layer – REST controllers that expose shipment status, predicted arrival times, estimated delivery dates, and system health
- Simulation Layer – maintains and advances the authoritative operational state of shipments
- Prediction Layer – continuously forecasts future shipment outcomes without mutating live state
- Delivery Layer – applies terminal-specific business rules to convert arrival times into customer delivery dates
- Domain Layer – core entities such as shipments, routes, segments, terminals, and policies
- Integration Layer – external data sources (e.g., weather, congestion signals)
- Data Layer – repositories and database access with versioned schema migrations
- Observability – metrics, health checks, and monitoring hooks

Suggested package structure:
```
transitflow
├── api
├── simulation
├── prediction
├── delivery
├── domain
├── integration
├── repo
└── config
```

### Domain Model
The domain model represents a generalized freight transportation network capable of supporting multi-leg, multi-modal shipments and terminal-specific delivery behavior.

Core domain entities:
- Node (Terminal):
Represents a physical terminal or hub. Each terminal is associated with a delivery policy that governs inbound cut times, business calendars, and weekend delivery behavior.
- Segment:
Represents a single transportation leg between two nodes. Segments are associated with a transportation mode (e.g., truck, rail) and baseline transit characteristics.
- Route:
An ordered collection of segments representing a full shipment journey, potentially spanning multiple transportation modes.
- Shipment:
Represents a unit of freight identified by a tracking number, associated with a route, and maintaining predicted operational arrival and delivery information.
- DelayEvent:
Represents a disruption affecting a shipment, segment, or terminal, including both intra-modal and intermodal delays.
- DeliveryPolicy:
Encapsulates terminal-specific delivery rules such as inbound cut time, weekend delivery options, and holiday calendars.

### Simulation and Prediction Model
TransitFlow maintains a continuously advancing authoritative simulation that reflects the current operational state of all shipments. Time advances automatically via a scheduler, rather than manual API calls, to reflect real-world progression.
At each scheduled interval:
- Simulated time advances
- Shipments progress along their current segments
- Observed delays and operational events are applied
- Operational arrival estimates are updated

In parallel, the system runs a predictive forecasting process that:
- Clones the current simulation state
- Applies forecasted external conditions (e.g., weather, congestion)
- Advances simulated time into the future
- Produces predicted terminal arrival times and confidence indicators

Predictive forecasting is strictly read-only and does not mutate live simulation state. This separation prevents feedback loops and ensures that predictions influence delivery commitments without altering operational truth.

### Delivery Estimation
Customer-facing delivery dates are derived from predicted terminal arrival times using terminal-specific delivery policies. This logic is intentionally separated from simulation and prediction to reflect real-world business practices.
Delivery estimation applies:
- Inbound cut times (default 06:00, configurable per terminal)
- Business calendars (default Monday–Friday)
- Optional weekend delivery rules
- Holiday exclusions

This design ensures that delivery promises reflect how terminals actually operate, rather than relying solely on physical transit completion.
### API Design
TransitFlow exposes REST endpoints that allow external tracking systems to retrieve:
- Current shipment status
- Predicted terminal arrival times
- Estimated customer delivery dates and confidence levels
- System health and metrics

All predictive updates occur automatically in the background. API consumers do not trigger simulation or prediction directly; they simply consume continuously updated results.

### Persistence, Testing, and Observability

- Persistence: PostgreSQL with Flyway-managed schema migrations ensures transparent, versioned data evolution.
- Testing: Unit tests validate simulation, prediction, and delivery logic in isolation, while integration tests using Testcontainers verify behavior against a real database.
- Observability: Spring Boot Actuator and Micrometer expose health checks and metrics such as active shipments, delay accumulation, prediction execution time, and ETA volatility.
---
### Justification
Spring Boot was selected as the primary framework because it aligns naturally with a layered backend architecture and provides mature support for scheduling, external integrations, persistence, and observability. For a system intended to integrate with existing freight tracking platforms, a layered monolith offers the greatest clarity, debuggability, and maintainability. More complex architectures such as microservices or event-driven systems were intentionally avoided, as they would introduce operational overhead without improving the core predictive capabilities being demonstrated.

The transition from a manual, user-driven simulation model to an automatic, continuously running prediction engine reflects real-world freight system requirements. In production environments, users do not interact with simulations directly; instead, systems quietly adjust estimates as conditions change. By separating live simulation from predictive forecasting, TransitFlow preserves deterministic operational state while enabling forward-looking ETA adjustments driven by current and forecasted conditions.

Explicit modeling of multi-leg, multi-modal routes and intermodal delays is essential to accurately represent real freight behavior. Delays frequently occur not only within a single transportation mode but also at the boundaries between modes, where handoffs, scheduling mismatches, and terminal dwell times introduce cascading effects. Incorporating these interactions allows TransitFlow to produce more realistic and trustworthy predictions than systems that treat each leg in isolation.

The introduction of terminal-specific delivery policies addresses a critical gap in many existing tracking systems. Customer delivery promises are governed by business rules such as inbound cut times, business calendars, weekends, and holidays, not just physical arrival times. Separating delivery estimation from transit simulation ensures that customer-facing ETAs align with actual terminal operations and reduces the frequency of broken promises.

External data sources such as weather and congestion are treated as influencing signals rather than authoritative truth. This design choice prioritizes explainability and auditability over opaque prediction models. Predictions can be traced back to specific conditions and rules, which is critical for operational trust and continuous improvement.

Overall, the updated design positions TransitFlow as a continuous predictive ETA engine that enhances existing freight tracking systems rather than replacing them. The system demonstrates how deterministic simulation, background forecasting, intermodal modeling, and business-driven delivery rules can be combined to improve reliability, transparency, and customer confidence. The architecture remains intentionally small and understandable while providing a clear path for future extension into more advanced forecasting, optimization, or analytics capabilities.
