package pt.inevo.encontra.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("encontra")
public interface EnContRAService extends RemoteService {
	String index(String descriptors) throws IllegalArgumentException;
}
