# Concurrent Data Mover

A **Java concurrent programming assignment** focused on multi-threading, thread pools, and inter-thread communication. Developed following strict coding conventions with full command-line configurability.  

## Project Overview

This assignment consists of two versions: **DataMover** and **DataMover2**.

### Base Version (DataMover)

- Moves elements in a shared integer array using manually managed threads.  
- Each thread repeatedly subtracts and adds its index to specific array elements with synchronized access.  
- Thread activity is logged to standard output.  
- Supports command-line arguments for move and sleep times; defaults to `123 111 256 404`.  
- Prints final array values after all threads finish.  

### Extended Version (DataMover2)

- Uses **ExecutorService** thread pool and **Callable** tasks.  
- Each task communicates via **BlockingQueues** to pass integers to the next task.  
- Tracks results with `DataMover2Result` objects and **AtomicInteger** counters.  
- Threads repeatedly send, forward, or consume integers, with pseudo-random timeouts.  
- Logs activity in a detailed turn-based format and calculates totals for verification.  
- Handles discarded items and ensures total sent equals sum of received and discarded values.  

## Features

- Manual and pool-based thread management.  
- Inter-thread communication with queues.  
- Logging of thread operations and results.  
- Command-line configurability and default parameters.  
- Proper synchronization and safe concurrent access to shared data.  

## Technologies

- **Language:** Java  
- **Paradigm:** Concurrent programming with threads, thread pools, and synchronization.  

## Usage

1. Clone the repository:

```bash
git clone https://github.com/MinooMousavi/java-ConcurrentProgramming.git
