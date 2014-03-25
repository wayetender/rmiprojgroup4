package edu.harvard.cs262.ComputeServer.Group4;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import edu.harvard.cs262.ComputeServer.ComputeServer;

/**
 * A ComputeServer client that performs an AdditionTask. It takes the ComputeServer host as a
 * parameter and continuously sends AdditionTasks as requested by the user through the command
 * prompt.
 * 
 * @author Group 4: Lucas Waye, Christopher Mueller, George Wu, Kat Zhou
 * 
 */
public class WorkClient {

    /**
     * Driver for the client. Runs AdditionTasks in a loop.
     * 
     * @param args
     *            expects the compute server hostname to connect to
     */
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        if (args.length < 2) {
            System.err.println("Usage: WorkClient [ComputeServerHostname] [ComputeServerBindedName]");
        }
        String queueServerHost = args[0];
        String workServerBindedName = args[1];
        ComputeServer qServer = null;
        try {
            Registry queueRegistry = LocateRegistry.getRegistry(queueServerHost);
            qServer = (ComputeServer) queueRegistry.lookup(workServerBindedName);
        } catch (Exception ex) {
            System.out.println("could not connect to Queue Server: " + queueServerHost);
            ex.printStackTrace();
            System.exit(1);
        }
        System.out.println("Connected to " + queueServerHost);
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("Enter addition task integers (separated by a space)> ");
            Integer a = sc.nextInt();
            Integer b = sc.nextInt();
            AdditionTask task = new AdditionTask(a, b);
            try {
                Object result = qServer.sendWork(task);
                System.out.println("result: " + result);
                System.out.println();
            } catch (RemoteException ex) {
                System.out.println("Failed to run task");
                ex.printStackTrace();
            }
        }

    }

}
