
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
 2. Start the codebase webserver; it runs on port 8000: `./runwebserver.sh &`
 3. Start the RMI registry with the codebase server: `./runregistry.sh http://localhost:8000/ &` -- don't forget the trailing slash!
 4. Start the Queued compute server: `./runqueue.sh localhost http://localhost:8000/`
 5. In a separate window, start a worker: `./runworker.sh localhost http://localhost:8000/ localhost`
 6. In a separate window, start the client: `./runclient.sh localhost`
 

