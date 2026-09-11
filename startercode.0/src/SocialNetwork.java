import java.util.HashSet;
import java.util.Set;

public class SocialNetwork implements ISocialNetwork {
	
	private Set<Account> accounts = new HashSet<Account>();
	private Account loggedInUser = null;

	public Account getLoggedInUser() {
		return loggedInUser;
	}

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
	public Set<String> listMembers() {
		Set<String> members = new HashSet<String>();
		for (Account each : accounts) {
			members.add(each.getUserName());
		}
		return members;
	}

	@Override
	public Account login(Account me) {
		if (me == null) {
			return null;
		}
		if (findAccountForUserName(me.getUserName()) == null) {
			return null;
		}
		loggedInUser = me;
		return loggedInUser;
	}

	@Override
	public boolean hasMember(String userName) {
		return findAccountForUserName(userName) != null;
	}

	@Override
	public void sendFriendshipTo(String userName) {
		Account me = getLoggedInUser();
		if (me == null) {
			return;
		}
		Account accountForUserName = findAccountForUserName(userName);
		if (accountForUserName == null) {
			return;
		}
		accountForUserName.requestFriendship(me);
	}

	@Override
	public void block(String userName) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'block'");
	}

	@Override
	public void unblock(String userName) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'unblock'");
	}

	@Override
	public void sendFriendshipCancellationTo(String userName) {
		Account me = getLoggedInUser();
		if (me == null) {
			return;
		}
		Account accountForUserName = findAccountForUserName(userName);
		if (accountForUserName == null) {
			return;
		}
		accountForUserName.cancelFriendship(me);
	}

	@Override
	public void acceptFriendshipFrom(String userName) {
		Account me = getLoggedInUser();
		if (me == null) {
			return;
		}
		Account accountForUserName = findAccountForUserName(userName);
		if (accountForUserName == null) {
			return;
		}
		accountForUserName.friendshipAccepted(me);
	}

	@Override
	public void acceptAllFriendships() {
		Account me = getLoggedInUser();
		if (me == null) {
			return;
		}
		Set<String> pending = new HashSet<String>(me.getIncomingRequests());
		for (String requesterName : pending) {
			acceptFriendshipFrom(requesterName);
		}
	}

	@Override
	public void rejectFriendshipFrom(String userName) {
		Account me = getLoggedInUser();
		if (me == null) {
			return;
		}
		Account accountForUserName = findAccountForUserName(userName);
		if (accountForUserName == null) {
			return;
		}
		accountForUserName.friendshipRejected(me);
	}

	@Override
	public void rejectAllFriendships() {
		Account me = getLoggedInUser();
		if (me == null) {
			return;
		}
		Set<String> pending = new HashSet<String>(me.getIncomingRequests());
		for (String requesterName : pending) {
			rejectFriendshipFrom(requesterName);
		}
	}

	@Override
	public void autoAcceptFriendships() {
		Account me = getLoggedInUser();
		if (me == null) {
			return;
		}
		me.autoAcceptFriendships();
	}

	@Override
	public void cancelAutoAcceptFriendships() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'cancelAutoAcceptFriendships'");
	}

	@Override
	public Set<String> recommendFriends() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'recommendFriends'");
	}

	@Override
	public void leave() {
		Account me = getLoggedInUser();
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
		if (loggedInUser == me) {
			loggedInUser = null;
		}
		accounts.remove(me);
	}
}
