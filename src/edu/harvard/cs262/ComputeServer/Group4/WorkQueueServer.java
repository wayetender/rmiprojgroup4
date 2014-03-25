package edu.harvard.cs262.ComputeServer.Group4;

import java.rmi.ConnectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.UUID;

import edu.harvard.cs262.ComputeServer.ComputeServer;
import edu.harvard.cs262.ComputeServer.WorkQueue;
import edu.harvard.cs262.ComputeServer.WorkTask;

/**
 * A WorkQueueServer accepts tasks to perform and delegates them to registered ComputeServer nodes.
 * The WorkQueueServer keeps track of currently active (working on a WorkTask) workers. When a
 * WorkTask arrives, it will delegate it to the first free worker. If none are available, it will
 * wait until one becomes available. If there is a ConnectException while delegating a task, that
 * worker is removed and the WorkTask is retried with another available worker.
 * 
 * @author Group 4: Lucas Waye, Christopher Mueller, George Wu, Kat Zhou
 * 
 */
public class WorkQueueServer implements WorkQueue, ComputeServer {

    private static final String WORKER_SERVER_REGISTRY_NAME = "MasterComptueServer";
    private static final String QUEUED_SERVER_REGISTRY_NAME = "QueuedServer";
    private Hashtable<UUID, ComputeServer> workers;
    private LinkedList<UUID> freeWorkers, busyWorkers;

    private WorkQueueServer() {
        workers = new Hashtable<UUID, ComputeServer>();
        freeWorkers = new LinkedList<UUID>();
        busyWorkers = new LinkedList<UUID>();
    }

    /**
     * Registers a new worker, and notifies the Object monitor of a new free worker.
     */
    @Override
    public synchronized UUID registerWorker(ComputeServer server) throws RemoteException {
        UUID key = UUID.randomUUID();
        workers.put(key, server);
        freeWorkers.add(key);
        notify();
        System.out.println("worker " + key + " registered");
        return key;
    }

    /**
     * Unregisters a worker based on its UUID created from registerWorker.
     */
    @Override
    public synchronized boolean unregisterWorker(UUID workerID) throws RemoteException {
        if (null == workers.get(workerID)) {
            return true;
        }

        workers.remove(workerID);
        freeWorkers.remove(workerID);
        busyWorkers.remove(workerID);
        return true;
    }

    /**
     * Waits until a worker is free to work on a task. Side effect: changes worker status from free
     * to busy. Must call returnFreeWorker when the worker should be considered free for new tasks.
     * 
     * @return the UUID of the first available worker
     */
    private synchronized UUID getFreeWorker() {
        UUID firstFreeID = freeWorkers.poll();
        while (firstFreeID == null) {
            try {
                System.out.println("no worker available, waiting for one to become available");
                wait();
            } catch (InterruptedException e) {
                // continue
            }
            firstFreeID = freeWorkers.poll();
        }
        busyWorkers.add(firstFreeID);
        return firstFreeID;
    }

    /**
     * Returns a worker to the free pool and removes them from being busy. Notifies the Object
     * monitor of the newly free worker
     * 
     * @param freeWorker
     *            the worker to be considered free
     */
    private synchronized void returnFreeWorker(UUID freeWorker) {
        busyWorkers.remove(freeWorker);
        freeWorkers.add(freeWorker);
        notify();
    }

    /**
     * Sends work to the worker to perform
     * 
     * @param worker
     *            the worker to perform the work
     * @param work
     *            the work to perform
     * @return the results of the work
     * @throws RemoteException
     *             problem while sending the work to perform
     */
    private Object delegateWork(UUID worker, WorkTask work) throws RemoteException {
        ComputeServer server = workers.get(worker);
        try {
            System.out.println("delegating task " + work + " to worker " + worker);
            return server.sendWork(work);
        } finally {
            returnFreeWorker(worker);
        }
    }

    /**
     * Sends work to a worker to perform. If no workers are available, it waits until a worker
     * becomes free. If there was a ConnectionException while delegating the work, it is
     * unregistered and the work task is performed on another available worker.
     */
    @Override
    public Object sendWork(WorkTask work) throws RemoteException {
        while (true) {
            UUID firstFreeID = getFreeWorker();
            try {
                return delegateWork(firstFreeID, work);
            } catch (ConnectException ex) {
                System.out.println("problem delegating task to worker " + firstFreeID
                        + "; removing");
                unregisterWorker(firstFreeID);
            }
        }
    }

    /**
     * Creates a WorkQueueServer and exports it as a remote and registers it with the locate
     * registry.
     * 
     * @return the newly created WorkQueueServer.
     * @throws Exception
     *             problem while exporting the object or registering it with the locate registry.
     */
    private static WorkQueueServer createWorkerServer() throws Exception {
        WorkQueueServer server = new WorkQueueServer();
        Registry registry = LocateRegistry.getRegistry();
        Remote stub = UnicastRemoteObject.exportObject(server, 0);
        registry.bind(WORKER_SERVER_REGISTRY_NAME, stub);
        registry.bind(QUEUED_SERVER_REGISTRY_NAME, stub);
        return server;
    }

    /**
     * Driver for the WorkQueueServer
     * 
     * @param args
     *            empty (no parameters required)
     */
    public static void main(String args[]) {
        try {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            createWorkerServer();
            System.out.println("WorkQueueServer ready");
        } catch (Exception e) {
            System.err.println("Server exception");
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.harvard.cs262.ComputeServer.ComputeServer#PingServer()
     */
    @Override
    public boolean PingServer() throws RemoteException {
        return true;
    }

}
