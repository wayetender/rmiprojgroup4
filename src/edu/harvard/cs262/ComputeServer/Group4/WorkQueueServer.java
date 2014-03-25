package edu.harvard.cs262.ComputeServer.Group4;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteStub;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.UUID;

import edu.harvard.cs262.ComputeServer.ComputeServer;
import edu.harvard.cs262.ComputeServer.WorkQueue;
import edu.harvard.cs262.ComputeServer.WorkTask;

public class WorkQueueServer implements WorkQueue, ComputeServer {

	private Hashtable<UUID, ComputeServer> workers;
	private LinkedList<UUID> freeWorkers, busyWorkers;
	
	private WorkQueueServer() {
		workers = new Hashtable<UUID, ComputeServer>();
		freeWorkers = new LinkedList<UUID>();
		busyWorkers = new LinkedList<UUID>();
	}
	
	@Override
	public synchronized UUID registerWorker(ComputeServer server) throws RemoteException {
		UUID key = UUID.randomUUID();
		workers.put(key, server);
		freeWorkers.add(key);
		notify();
		return key;
	}

	@Override
	public synchronized boolean unregisterWorker(UUID workerID) throws RemoteException {
		if (null == workers.get(workerID)){
			return true;
		}
		
		workers.remove(workerID);
		freeWorkers.remove(workerID);
		busyWorkers.remove(workerID);
		return true;
	}

	@Override
	public synchronized Object sendWork(WorkTask work) throws RemoteException {
		UUID firstFreeID = freeWorkers.poll();
		while (firstFreeID == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				// continue
			}
			firstFreeID = freeWorkers.poll();
		}
		busyWorkers.add(firstFreeID);
		ComputeServer server = workers.get(firstFreeID);
		try {
			return server.sendWork(work);
		} finally {
			busyWorkers.remove(firstFreeID);
			freeWorkers.add(firstFreeID);
			notify();
		}
	}
	
	private static WorkQueueServer createWorkerServer() throws Exception {
		WorkQueueServer server = new WorkQueueServer();
		Registry registry = LocateRegistry.getRegistry();
		Remote stub = UnicastRemoteObject.exportObject(server, 0);
		registry.bind("WorkerServer", stub);
		registry.bind("QueuedServer", stub);
		return server;
	}
	
	public static void main(String args[]) {
		try {
			if (System.getSecurityManager() == null) {
            	System.setSecurityManager(new SecurityManager());
        	}
			createWorkerServer();
        	System.out.println("WorkQueueServer ready");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}


	@Override
	public boolean PingServer() throws RemoteException {
		return true;
	}

}
