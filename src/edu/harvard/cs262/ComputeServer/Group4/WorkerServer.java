package edu.harvard.cs262.ComputeServer.Group4;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import edu.harvard.cs262.ComputeServer.ComputeServer;
import edu.harvard.cs262.ComputeServer.WorkQueue;
import edu.harvard.cs262.ComputeServer.WorkTask;

/**
 * A WorkerServer locally performs WorkTasks.
 * 
 * @author Group 4: Lucas Waye, Christopher Mueller, George Wu, Kat Zhou
 * 
 */
public class WorkerServer implements ComputeServer {

	private WorkerServer() {
	}

	/**
	 * Locally performs a work task.
	 * 
	 * @see edu.harvard.cs262.ComputeServer.ComputeServer#sendWork(edu.harvard.cs262.ComputeServer.WorkTask)
	 */
	@Override
	public Object sendWork(WorkTask work) throws RemoteException {
		System.out.println("performing WorkTask " + work);
		return work.doWork();
	}

	/**
	 * Creates a ComputeServer stub but does not register it with a
	 * LocateRegistry. Clients should not connect to it directly, but instead
	 * through a WorkQueueServer.
	 * 
	 * @return the newly created ComputeServer remote
	 * @throws Exception
	 *             problem exporting the ComputeServer remote
	 */
	private static ComputeServer createWorkerServer() throws Exception {
		WorkerServer server = new WorkerServer();
		ComputeServer stub = (ComputeServer) UnicastRemoteObject.exportObject(
				server, 0);
		return stub;
	}

	/**
	 * Driver for the WorkerServer. Starts the compute server, registers it with
	 * the given QueueServer and waits for tasks to run.
	 * 
	 * @param args
	 *            expects the QueueServer hostname
	 */
	public static void main(String args[]) {
		try {
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
			if (args.length < 1) {
				System.err.println("Usage: WorkerServer [QueueServerHostname]");
			}
			String queueServerHost = args[0];
			ComputeServer myServer = createWorkerServer();
			Registry queueRegistry = LocateRegistry
					.getRegistry(queueServerHost);
			WorkQueue qServer = (WorkQueue) queueRegistry
					.lookup("QueuedServer");
			qServer.registerWorker(myServer);
			System.out.println("WorkerServer ready");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
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