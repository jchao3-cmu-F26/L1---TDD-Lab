import java.util.HashSet;
import java.util.Set;


public class Account  {
	
	// the unique user name of account owner
	private String userName;
	
	// list of members who are awaiting an acceptance response from this account's owner 
	private Set<String> incomingRequests = new HashSet<String>();

	// list of members to whom this account's owner has sent a request with no response yet
	private Set<String> outgoingRequests = new HashSet<String>();
	
	// list of members who are friends of this account's owner
	private Set<String> friends = new HashSet<String>();

	private Boolean Auto = false;
	
	public Account(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	// return list of members who had sent a friend request to this account's owner 
	// and are still waiting for a response
	public Set<String> getIncomingRequests() {
		return incomingRequests; 
	}

	public Set<String> getOutgoingRequests() {
		return outgoingRequests;
	}

	// an incoming friend request to this account's owner from another member account
	public void requestFriendship(Account fromAccount) {
		if (fromAccount == null || fromAccount.getUserName().equals(userName)) {
			return;
		}
		if (!friends.contains(fromAccount.getUserName())) {
			if (Auto) {
				fromAccount.incomingRequests.add(this.userName);
				friendshipAccepted(fromAccount);
			}
			else {
				incomingRequests.add(fromAccount.getUserName());
				fromAccount.outgoingRequests.add(this.userName);
			}
		}
	}

	public void friendshipRejected(Account fromAccount) {
		if (fromAccount == null || fromAccount.getUserName().equals(userName)) {
			return;
		}
		outgoingRequests.remove(fromAccount.getUserName());
		fromAccount.incomingRequests.remove(this.userName);
	}

	// check if account owner has a member with user name userName as a friend
	public boolean hasFriend(String userName) {
		return friends.contains(userName);
	}

	// receive an acceptance from a member to whom a friend request has been sent and from whom no response has been received
	public void friendshipAccepted(Account toAccount) {
		if (toAccount == null || !toAccount.incomingRequests.contains(this.getUserName())) {
			return;
		}
		friends.add(toAccount.getUserName());
		toAccount.friends.add(this.getUserName());
		toAccount.incomingRequests.remove(this.getUserName());
		incomingRequests.remove(toAccount.getUserName());
		outgoingRequests.remove(toAccount.getUserName());
		toAccount.outgoingRequests.remove(this.getUserName());
	}

	public void cancelFriendship(Account toAccount) {
		if (toAccount == null || !friends.contains(toAccount.getUserName())) {
			return;
		}
		friends.remove(toAccount.getUserName());
		toAccount.friends.remove(this.getUserName());
	}
	
	public void autoAcceptFriendships() {
		Auto = true;
	}

	public Set<String> getFriends() {
		return friends;
	}

}
