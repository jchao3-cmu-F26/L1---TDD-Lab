import java.util.HashSet;
import java.util.Set;

public class SocialNetwork implements ISocialNetwork {
	
	private Set<Account> accounts = new HashSet<Account>();
	private Account loggedInUser = null;

	public Account getLoggedInUser()  throws NoUserLoggedInException {
		if (loggedInUser == null) {
			throw new NoUserLoggedInException();
		}
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
	public Set<String> listMembers() throws NoUserLoggedInException {
		Account me = getLoggedInUser();
		Set<String> members = new HashSet<String>();
		for (Account each : accounts) {
			if (me != null && each.hasBlocked(me.getUserName())) {
				continue;
			}
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
	public boolean hasMember(String userName) throws NoUserLoggedInException {
		Account me = getLoggedInUser();
		Account found = findAccountForUserName(userName);
		if (found == null) {
			return false;
		}
		if (me != null && found.hasBlocked(me.getUserName())) {
			return false;
		}
		return true;
	}

	@Override
	public void sendFriendshipTo(String userName) throws NoUserLoggedInException {
		Account me = getLoggedInUser();
		Account accountForUserName = findAccountForUserName(userName);
		if (accountForUserName == null) {
			return;
		}
		if (accountForUserName.hasBlocked(me.getUserName())) {
			return;
		}
		accountForUserName.requestFriendship(me);
	}

	@Override
	public void block(String userName) throws NoUserLoggedInException {
		Account me = getLoggedInUser();
		if (findAccountForUserName(userName) == null) {
			return;
		}
		Account blockedAccount = findAccountForUserName(userName);
		me.block(userName);
		me.cancelFriendship(blockedAccount);
		if (me.getIncomingRequests().contains(userName)) {
			me.getIncomingRequests().remove(userName);
			blockedAccount.getOutgoingRequests().remove(me.getUserName());
		}
		if (me.getOutgoingRequests().contains(userName)) {
			me.getOutgoingRequests().remove(userName);
			blockedAccount.getIncomingRequests().remove(me.getUserName());
		}
	}

	@Override
	public void unblock(String userName) throws NoUserLoggedInException {
		Account me = getLoggedInUser();
		me.unblock(userName);
	}

	@Override
	public void sendFriendshipCancellationTo(String userName) throws NoUserLoggedInException {
		Account me = getLoggedInUser();
		Account accountForUserName = findAccountForUserName(userName);
		if (accountForUserName == null) {
			return;
		}
		accountForUserName.cancelFriendship(me);
	}

	@Override
	public void acceptFriendshipFrom(String userName) throws NoUserLoggedInException {
		Account me = getLoggedInUser();
		Account accountForUserName = findAccountForUserName(userName);
		if (accountForUserName == null) {
			return;
		}
		accountForUserName.friendshipAccepted(me);
	}

	@Override
	public void acceptAllFriendships() throws NoUserLoggedInException {
		Account me = getLoggedInUser();
		Set<String> pending = new HashSet<String>(me.getIncomingRequests());
		for (String requesterName : pending) {
			acceptFriendshipFrom(requesterName);
		}
	}

	@Override
	public void rejectFriendshipFrom(String userName) throws NoUserLoggedInException {
		Account me = getLoggedInUser();
		Account accountForUserName = findAccountForUserName(userName);
		if (accountForUserName == null) {
			return;
		}
		accountForUserName.friendshipRejected(me);
	}

	@Override
	public void rejectAllFriendships() throws NoUserLoggedInException {
		Account me = getLoggedInUser();
		Set<String> pending = new HashSet<String>(me.getIncomingRequests());
		for (String requesterName : pending) {
			rejectFriendshipFrom(requesterName);
		}
	}

	@Override
	public void autoAcceptFriendships() throws NoUserLoggedInException {
		Account me = getLoggedInUser();
		me.autoAcceptFriendships();
	}

	@Override
	public void cancelAutoAcceptFriendships() throws NoUserLoggedInException {
		Account me = getLoggedInUser();
		me.cancelAutoAcceptFriendships();
	}

	@Override
	public Set<String> recommendFriends() throws NoUserLoggedInException {
		Account me = getLoggedInUser();
		Set<String> friends = me.getFriends();
		Set<String> recommendations = new HashSet<String>();
		for (Account each : accounts) {
			String fname = each.getUserName();
			if (each == me) {
				continue;
			}
			if (friends.contains(fname)) {
				continue;
			}
			int mutual = 0;
			for (String friend : friends) {
				if (each.hasFriend(friend)) {
					mutual++;
				}
			}
			if (mutual >= 2) {
				recommendations.add(fname);
			}
		}
		return recommendations;
	}

	@Override
	public void leave() throws NoUserLoggedInException {
		Account me = getLoggedInUser();
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
