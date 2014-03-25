CS 262 Group 4 Coding 2 Assignment
==================================

Authors: Lucas Waye, Christopher Mueller, George Wu, Kat Zhou

Node Types
----------

There are two types of servers, a WorkerServer and a WorkQueueServer. The WorkerServer 
performs tasks and the WorkQueueServer delegates tasks to WorkerServers. There is one
type of client, WorkClient, that performs AdditionTasks.

A WorkQueueServer accepts tasks to perform and delegates them to registered
ComputeServer nodes. The WorkQueueServer keeps track of currently active
(working on a WorkTask) workers. When a WorkTask arrives, it will delegate it
to the first free worker. If none are available, it will wait until one
becomes available. If there is a ConnectException while delegating a task,
that worker is removed and the WorkTask is retried with another available
worker.

Scripts
-------

 * `build.sh` -- compiles and jars up the source files
 * `runwebserver.sh` -- runs the RMI webserver for the jar'd up file created from `build.sh`
 * `runregistry.sh` -- runs the RMI registry
 * `runqueue.sh` -- runs the queued compute server
 * `runworker.sh` -- runs a worker compute server
 * `runclient.sh` -- runs an AdditionTask client

Usage
-----

 1. Compile the source: `./build.sh`
 2. Edit the files `myhostname` and `rmicodebases` to reflect your node's hostname and the RMI server codebase(s) to use.
 3. Start the codebase webserver; it runs on port 8000: `./runwebserver.sh &`
 4. Start the RMI registry with the codebase server: `./runregistry.sh &` 
 5. Start the Queued compute server: `./runqueue.sh`
 6. In a separate window, start a worker: `./runworker.sh localhost`. If registering to a remote Queue server, change `localhost` to the hostname of the remote Queue server. If the queued server name registry name is not "QueuedServer", you must edit the "runworker.sh" script.
 7. In a separate window, start the client: `./runclient.sh localhost`. If connecting to a remote compute server, change `localhost` to the hostname of the remote server.


**Note:** For the RMI codebases, don't forget the trailing slash if it is not a JAR.
 
