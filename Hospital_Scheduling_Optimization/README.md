# Hospital Scheduling Optimisation Engine

Project basis:
- Hospital bed / operating room scheduling
- Priority Queue
- Constraint Satisfaction
- Greedy allocation
- Local-search refinement
- Resource utilisation
- Average waiting time
- Makespan
- Automatic scheduling without asking the user to choose an algorithm

## Requirements

- Java JDK 17 or newer
- Any modern browser
- VS Code is recommended but not required

No Maven, Gradle, Node.js, npm or database is required for this version.

## Project structure

Hospital_Scheduling_Optimization/
  backend/
    src/
      Patient.java
      Resource.java
      ScheduleEntry.java
      HospitalDataStore.java
      SchedulingEngine.java
      JsonUtil.java
      HospitalServer.java
    run.bat
    run.sh
  frontend/
    index.html
    style.css
    app.js

## Run on Windows

1. Install JDK 17+.
2. Open the project folder in VS Code.
3. Open a terminal.
4. Run:

   cd backend
   run.bat

5. Keep that terminal running.
6. Open frontend/index.html in Chrome/Edge.
7. The dashboard should show "Backend Online".
8. Click "Generate Optimised Schedule".

## Run on Ubuntu/Linux

Open Terminal:

cd backend
chmod +x run.sh
./run.sh

Then open frontend/index.html in a browser.

## How the DSA part works

1. Patient records are inserted into a PriorityQueue.
2. Priority 5 is considered before priority 4, 3, 2 and 1.
3. Each patient is checked against available resources.
4. The earliest non-conflicting time slot is selected.
5. The generated schedule is refined by a local-search step.
6. The dashboard calculates utilisation, waiting time and makespan.

## API endpoints

GET  /api/dashboard
GET  /api/patients
POST /api/patients
GET  /api/resources
GET  /api/schedule
POST /api/generate
POST /api/reset

## Demo flow

Dashboard -> Patients -> Add Patient -> Generate Optimised Schedule -> Schedule -> Reports
