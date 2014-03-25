package edu.harvard.cs262.ComputeServer.Group4;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import edu.harvard.cs262.ComputeServer.ComputeServer;
import edu.harvard.cs262.ComputeServer.WorkQueue;
import edu.harvard.cs262.ComputeServer.WorkTask;

public class WorkerServer implements ComputeServer {

	private WorkerServer() { }

	@Override
	public Object sendWork(WorkTask work) throws RemoteException {
		return work.doWork();
	}

	private static ComputeServer createWorkerServer() throws Exception {
		WorkerServer server = new WorkerServer();
		ComputeServer stub = (ComputeServer)UnicastRemoteObject.exportObject(server, 0);
		//Registry registry = LocateRegistry.getRegistry();
		//registry.bind("WorkerServer", stub);
		return stub;
	}
	
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
        	Registry queueRegistry = LocateRegistry.getRegistry(queueServerHost);
            WorkQueue qServer = (WorkQueue) queueRegistry.lookup("QueuedServer");
            qServer.registerWorker(myServer);
			System.out.println("WorkerServer ready");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
		}
	}

	@Override
	public boolean PingServer() throws RemoteException {
		return true;
	}
}