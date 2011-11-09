package pt.inevo.encontra.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>EnContRAService</code>.
 */
public interface EnContRAServiceAsync {
	void index(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
}
