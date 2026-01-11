# Software Requirements Specification (SRS) 
## TransitFlow

---

# 1. Purpose
   TransitFlow is a backend service that continuously predicts freight shipment arrival and delivery times across multi-leg, multi-modal transportation routes. This document defines the functional and non-functional requirements of the system in a form intended for engineers building, testing, and integrating the service.
   This SRS derives from the Architectural Design & Justification (ADJ) and translates design intent into concrete system behavior.
---
# 2. Scope
   TransitFlow operates as a background predictive engine integrated into existing freight tracking systems. It maintains an operational simulation of shipment movement, runs predictive forecasting based on current and forecasted conditions, and produces updated ETAs for terminals and customers.
   Out of scope for this version:
   
- Real-time GIS visualization
- Full dispatch optimization or scheduling
- Capacity contention modeling
- Opaque machine learning models
- Direct user interaction or manual “what-if” execution

---

# 3. System Overview
   TransitFlow is a Spring Boot–based layered monolithic service that:
   
- Advances simulated time automatically
- Predicts future shipment outcomes without mutating live state
- Applies terminal-specific delivery rules
- Exposes continuously updated tracking and ETA data via REST APIs

---

# 4. Functional Requirements
## 4.1 Shipment Management
   
- FR-1: The system shall represent shipments identified by a tracking number.
- FR-2: A shipment shall be associated with a multi-leg route that may span multiple transportation modes.
- FR-3: The system shall maintain the current operational state of each shipment.
---
## 4.2 Simulation Execution
- FR-4: The system shall automatically advance simulated time at fixed intervals.
- FR-5: Shipments shall progress along route segments based on simulated time advancement.
- FR-6: Observed operational delays shall be applied to shipment progression.
---
## 4.3 Predictive Forecasting
- FR-7: The system shall periodically generate predictions by simulating future shipment progression.
- FR-8: Predictive simulations shall not mutate the live operational state.
- FR-9: Forecasted external conditions (e.g., weather, congestion) shall influence predicted outcomes.
- FR-10: The system shall produce predicted terminal arrival times and confidence indicators.
---
## 4.4 Inter-Modal Delay Handling
- FR-11: The system shall model delays occurring within transportation modes.
- FR-12: The system shall model delays occurring during inter-modal handoffs.
- FR-13: Delays in one segment or mode shall be allowed to propagate to downstream segments.
---
4.5 Delivery Estimation
- FR-14: The system shall compute customer delivery dates separately from terminal arrival times.
- FR-15: Delivery estimates shall respect terminal-specific inbound cut times.
- FR-16: Delivery estimates shall respect business calendars, weekends, and holidays.
- FR-17: Weekend delivery shall be configurable per terminal.
---
## 4.6 API Access
- FR-18: The system shall expose APIs to retrieve current shipment status.
- FR-19: The system shall expose APIs to retrieve predicted arrival and delivery estimates.
- FR-20: API consumers shall not trigger simulation or prediction directly.
---
# 5. Non-Functional Requirements
## 5.1 Determinism & Explainability
- NFR-1: Simulation and prediction behavior shall be deterministic given identical inputs.
- NFR-2: Predictions shall be explainable through applied rules and conditions.
---
## 5.2 Reliability & Maintainability
- NFR-3: The system shall be testable via unit and integration tests.
- NFR-4: The architecture shall support extension without major restructuring.
---
5.3 Performance
- NFR-5: Prediction execution shall not block live simulation.
- NFR-6: Background processing shall scale independently from API access.
---
## 5.4 Observability
- NFR-7: The system shall expose health checks.
- NFR-8: The system shall expose metrics related to shipment volume, delays, and prediction execution.

# 6. Data Requirements
- The system shall persist shipments, routes, segments, terminals, delay events, and delivery policies.
- Database schema changes shall be versioned and repeatable.
---
# 7. Assumptions & Constraints
- Shipments are modeled as independent entities.
- External signals are treated as influencing inputs, not authoritative truth.
- Terminal operations are modeled at a policy level, not full operational fidelity.
- The system is designed to integrate into existing tracking platforms.
---
# 8. Verification
- Unit tests shall validate simulation, prediction, and delivery logic.
- Integration tests shall validate persistence and service interactions.
- Manual API inspection shall verify continuously updated ETAs.
---
# 9. Future Considerations (Non-Requirements)
- Capacity-aware routing
- Distributed simulation workers
- Optimization and scheduling
- Advanced analytics and forecasting models
