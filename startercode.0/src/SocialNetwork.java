import java.util.HashSet;
import java.util.Collection;
import java.util.Set;

public class SocialNetwork {
	
	private Collection<Account> accounts = new HashSet<Account>();

	// join SN with a new user name
	public Account join(String userName) {
		if (userName == null || userName.isEmpty()) {
			return null;
		}
		if (findAccountForUserName(userName) != null) {
			return null;
		}
		Account newAccount = new Account(userName);
		accounts.add(newAccount);
		return newAccount;
	}

	// find a member by user name 
	private Account findAccountForUserName(String userName) {
		// find account with user name userName
		// not accessible to outside because that would give a user full access to another member's account
		for (Account each : accounts) {
			if (each.getUserName().equals(userName)) 
					return each;
		}
		return null;
	}
	
	// list user names of all members
	public Collection<String> listMembers() {
		Collection<String> members = new HashSet<String>();
		for (Account each : accounts) {
			members.add(each.getUserName());
		}
		return members;
	}
	
	// from my account, send a friend request to user with userName from my account
	public void sendFriendshipTo(String userName, Account me) {
		Account accountForUserName = findAccountForUserName(userName);
		if (accountForUserName == null) {
			return;
		}
		accountForUserName.requestFriendship(me);
	}

	// from my account, accept a pending friend request from another user with userName
	public void acceptFriendshipFrom(String userName, Account me) {
		Account accountForUserName = findAccountForUserName(userName);
		if (accountForUserName == null) {
			return;
		}
		accountForUserName.friendshipAccepted(me);
	}

	public void rejectFriendshipFrom(String herUserName, Account me) {
		Account accountForUserName = findAccountForUserName(herUserName);
		if (accountForUserName == null) {
			return;
		}
		accountForUserName.friendshipRejected(me);
	}


	public void acceptAllFriendshipsTo(Account me) {
		if (me == null) {
			return;
		}
		Set<String> pending = new HashSet<String>(me.getIncomingRequests());
		for (String requesterName : pending) {
			acceptFriendshipFrom(requesterName, me);
		}
	}


	public void rejectAllFriendshipsTo(Account me) {
		if (me == null) {
			return;
		}
		Set<String> pending = new HashSet<String>(me.getIncomingRequests());
		for (String requesterName : pending) {
			rejectFriendshipFrom(requesterName, me);
		}
	}

	public void autoAcceptFriendshipsTo(Account me) {
		if (me == null) {
			return;
		}
		me.autoAcceptFriendships();
	}

	public void sendFriendshipCancellationTo(String userName, Account me) {
		Account accountForUserName = findAccountForUserName(userName);
		if (accountForUserName == null) {
			return;
		}
		accountForUserName.cancelFriendship(me);
	}

	public void leave(Account me) {
		if (me == null) {
			return;
		}
		for (Account each : accounts) {
			if (each.hasFriend(me.getUserName())) {
				each.cancelFriendship(me);
			}
			each.getIncomingRequests().remove(me.getUserName());
			each.getOutgoingRequests().remove(me.getUserName());
		}
		accounts.remove(me);
	}
}
