# TransitFlow

TransitFlow is a backend freight prediction engine that continuously simulates shipment movement across multi-leg, multi-modal transportation networks and automatically produces more accurate estimated arrival and delivery times.

Rather than reacting only after delays occur, TransitFlow runs in the background as a predictive service that incorporates current operational state, inter-modal dependencies, and forecasted conditions (such as weather and congestion) to adjust ETAs in real time. The system is designed to integrate into existing freight tracking and transportation management systems, improving delivery reliability and customer trust without replacing core operational platforms.

---

## What Problem Does TransitFlow Solve?

Traditional freight tracking systems often:
- Treat each transportation leg in isolation
- Provide optimistic ETAs that are corrected only after delays occur
- Fail to account for inter-modal handoffs, terminal cut times, and delivery calendars

TransitFlow addresses these gaps by:
- Modeling shipments across multi-leg, multi-modal routes
- Predicting how delays in one mode propagate downstream
- Distinguishing between operational arrival at terminals and customer delivery dates
- Applying terminal-specific business rules (cut times, weekends, holidays)
- Continuously updating ETAs as conditions change

---

## Key Capabilities

- Continuous background simulation of shipment movement
- Predictive ETA modeling using current and forecasted conditions
- Explicit modeling of inter-modal delays and terminal handoffs
- Terminal-specific delivery policies (inbound cut times, weekends, holidays)
- Separation of operational arrival estimates and customer-facing delivery dates
- Explainable, deterministic prediction logic
- Designed for integration into real-world tracking systems

---

## Architecture Overview

TransitFlow is implemented as a layered monolithic Spring Boot service:

- Simulation layer maintains authoritative operational state
- Prediction layer forecasts future outcomes without mutating live state
- Delivery layer applies business rules to calculate customer delivery dates
- Integration layer incorporates external signals (e.g., weather)
- API layer exposes continuously updated tracking and ETA data
- Observability built in via metrics and health endpoints

The system runs automatically in the background; users and external systems consume updated results via APIs.

---

## Intended Use

TransitFlow is not a UI-driven simulation tool.  
It is intended to run as a backend service that feeds predictive ETAs into:

- Freight tracking portals
- Transportation management systems (TMS)
- Customer service and operations dashboards

---

## Current Implementation Status

TransitFlow currently implements a complete, end-to-end foundation for freight arrival and delivery prediction, spanning domain modeling, simulation, prediction, and API exposure.

Implemented features include:
- Domain-driven modeling of shipments, routes, segments, terminals, and transport modes
- Multi-leg, multi-modal shipment routing (truck, rail, air, sea)
- Deterministic, time-based simulation engine with strict segment completion semantics
- Immutable prediction snapshots enabling safe, read-only forecasting
- Prediction engine capable of computing terminal arrival times via simulated progression
- Terminal-specific delivery policies with inbound cut-off times and business calendar awareness
- Arrival estimate domain model separating terminal arrival from customer delivery time
- Delivery estimation service applying policy logic to predicted arrivals
- REST API exposing shipment arrival and delivery estimates via Spring Boot
- In-memory shipment repository for API prototyping and development
- End-to-end HTTP → JSON prediction flow validated via live API calls
- Comprehensive unit tests covering simulation behavior, arrival prediction, and delivery policy enforcement

At this stage, TransitFlow represents a stable, test-backed predictive simulation core with a working API surface suitable for iteration, experimentation, and external integration.

## Planned Next Steps

Upcoming work will focus on expanding predictive realism and production readiness, including:
- Delay event modeling (weather, congestion, intermodal dwell)
- Probabilistic ETA confidence and risk scoring
- External signal integration (stubbed → real APIs)
- Persistence layer for shipments and historical predictions
- Expanded REST API surface (batch queries, scenario forecasting)
- Observability enhancements (metrics, logging, tracing)
- Lightweight UI or client for interactive exploration of prediction behavior


Development is intentionally iterative, with each feature implemented on isolated branches and merged only after validation.
---

## Documentation

- **ADJ** – Architectural Design & Justification
- **SRS** – Software Requirements Specification
- more to come 

