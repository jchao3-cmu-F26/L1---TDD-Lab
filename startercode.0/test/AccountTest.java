import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class AccountTest {
	
	Account me, her, another;
	
	@Before
	public void setUp() throws Exception {
		me = new Account("Hakan");
		her = new Account("Serra");
		another = new Account("Cecile");
	}

	@Test
	public void requestFriendshipAddsRequesterToIncomingRequests() {
		me.requestFriendship(her);
		assertTrue(me.getIncomingRequests().contains(her.getUserName()));
	}
	
	@Test
	public void newAccountHasNoIncomingRequests() {
		assertEquals(0, me.getIncomingRequests().size());
	}
	
	@Test
	public void twoRequestersAppearInIncomingRequests() {
		me.requestFriendship(her);
		me.requestFriendship(another);
		assertEquals(2, me.getIncomingRequests().size());
		assertTrue(me.getIncomingRequests().contains(another.getUserName()));
		assertTrue(me.getIncomingRequests().contains(her.getUserName()));
	}

	@Test
	public void requestFriendshipfromNullDoesNothing() {
		me.requestFriendship(null);
		assertEquals(0, me.getIncomingRequests().size());
	}
	
	@Test
	public void duplicateRequestKeepsIncomingSizeAtOne() {
		me.requestFriendship(her);
		me.requestFriendship(her);
		assertEquals(1, me.getIncomingRequests().size());
	}
	
	@Test
	public void acceptingRequestRemovesItFromIncomingRequests() {
		me.requestFriendship(her);
		her.friendshipAccepted(me);
		assertFalse(me.getIncomingRequests().contains(her.getUserName()));
	}
	
	@Test
	public void acceptedRequestMakesBothAccountsFriends() {
		becomeFriends(her, me);
		assertAreFriends(me, her);
	}

	@Test
	public void accountCanHaveMultipleFriends() {
		becomeFriends(her, me);
		becomeFriends(another, me);
		assertTrue(me.hasFriend(her.getUserName()));
		assertTrue(me.hasFriend(another.getUserName()));
	}
	
	@Test
	public void requestingExistingFriendAddsNoIncomingRequest() {
		becomeFriends(her, me);
		me.requestFriendship(her);
		assertFalse(me.getIncomingRequests().contains(her.getUserName()));
	}

	@Test
	public void newAccountHasNoFriends() {
		assertEquals(0, me.getFriends().size());
	}

	@Test
	public void selfRequestDoesNotAddIncomingRequest() {
		me.requestFriendship(me);
		assertFalse(me.getIncomingRequests().contains(me.getUserName()));
	}

	@Test
	public void acceptWithoutPendingRequestDoesNotAddFriends() {
		her.friendshipAccepted(me);
		assertFalse(me.hasFriend(her.getUserName()));
		assertFalse(her.hasFriend(me.getUserName()));
	}

	@Test
	public void acceptFriendshipWithNullDoesNothing() {
		her.requestFriendship(me);
		her.friendshipAccepted(null);
		assertFalse(me.hasFriend(her.getUserName()));
		assertFalse(her.hasFriend(me.getUserName()));
	}

	@Test
	public void acceptClearsReverseIncomingRequest() {
		me.requestFriendship(her);
		her.requestFriendship(me);
		her.friendshipAccepted(me);
		assertFalse(her.getIncomingRequests().contains(me.getUserName()));
	}

	@Test
	public void newAccountHasNoOutgoingRequests() {
		assertEquals(0, me.getOutgoingRequests().size());
	}

	@Test
	public void requestFriendshipAddsReceiverToSendersOutgoingRequests() {
		me.requestFriendship(her);
		assertTrue(her.getOutgoingRequests().contains(me.getUserName()));
	}

	@Test
	public void acceptedRequestRemovesReceiverFromOutgoingRequests() {
		me.requestFriendship(her);
		her.friendshipAccepted(me);
		assertFalse(her.getOutgoingRequests().contains(me.getUserName()));
	}

	@Test
	public void selfRequestDoesNotAddOutgoingRequest() {
		me.requestFriendship(me);
		assertFalse(me.getOutgoingRequests().contains(me.getUserName()));
	}

	@Test
	public void requestingExistingFriendAddsNoOutgoingRequest() {
		becomeFriends(her, me);
		me.requestFriendship(her);
		assertFalse(her.getOutgoingRequests().contains(me.getUserName()));
	}
	
	@Test
	public void rejectingRequestRemovesItFromOutgoingRequests() {
		her.requestFriendship(me);
		assertTrue(me.getOutgoingRequests().contains(her.getUserName()));
		me.friendshipRejected(her);
		assertFalse(me.getOutgoingRequests().contains(her.getUserName()));
	}

	@Test
	public void rejectingRequestfromNullDoesNothing() {
		her.requestFriendship(me);
		assertTrue(me.getOutgoingRequests().contains(her.getUserName()));
		me.friendshipRejected(null);
		assertTrue(me.getOutgoingRequests().contains(her.getUserName()));
	}

	@Test
	public void rejectingRequestRemovesItFromIncoimingRequests() {
		me.requestFriendship(her);
		assertTrue(me.getIncomingRequests().contains(her.getUserName()));
		her.friendshipRejected(me);
		assertFalse(me.getIncomingRequests().contains(her.getUserName()));
	}

	@Test
	public void rejectingRequestWithoutPendingDoesNothing() {
		assertTrue(me.getIncomingRequests().size() == 0);
		assertTrue(me.getOutgoingRequests().size() == 0);
		her.friendshipRejected(me);
		assertTrue(me.getIncomingRequests().size() == 0);
		assertTrue(me.getOutgoingRequests().size() == 0);
	}

	@Test
	public void selfRejectDoesNothing() {
		assertTrue(me.getIncomingRequests().size() == 0);
		assertTrue(me.getOutgoingRequests().size() == 0);
		me.friendshipRejected(me);
		assertTrue(me.getIncomingRequests().size() == 0);
		assertTrue(me.getOutgoingRequests().size() == 0);
	}

	@Test
	public void cancelFriendshipRemovesBothFromFriendList() {
		becomeFriends(her, me);
		me.cancelFriendship(her);
		assertFalse(me.hasFriend(her.getUserName()));
		assertFalse(her.hasFriend(me.getUserName()));
	}

	@Test
	public void cancelFriendshipWithNullDoesNothing() {
		becomeFriends(her, me);
		me.cancelFriendship(null);
		assertTrue(me.hasFriend(her.getUserName()));
		assertTrue(her.hasFriend(me.getUserName()));
	}

	@Test
	public void cancelFriendshipWhenNotFriendsDoesNothing() {
		me.cancelFriendship(her);
		assertFalse(me.hasFriend(her.getUserName()));
		assertFalse(her.hasFriend(me.getUserName()));
	}

	@Test
	public void selfCancelFriendshipDoesAffectOtherAccounts() {
		becomeFriends(her, me);
		me.cancelFriendship(me);
		assertTrue(me.hasFriend(her.getUserName()));
		assertTrue(her.hasFriend(me.getUserName()));
	}

	@Test
	public void autoAcceptFriendshipWorks() {
		me.autoAcceptFriendships();
		assertFalse(me.hasFriend(her.getUserName()));
		me.requestFriendship(her);
		assertTrue(me.hasFriend(her.getUserName()));
	}

	@Test
	public void autoAcceptFriendshipDoesNotWorkWhenAlreadyFriends() {
		becomeFriends(her, me);
		assertTrue(me.hasFriend(her.getUserName()));
		assertEquals(1, me.getFriends().size());
		assertEquals(1, her.getFriends().size());
		me.autoAcceptFriendships();
		me.requestFriendship(her);
		assertEquals(1, me.getFriends().size());
		assertEquals(1, her.getFriends().size());
	}


	private void becomeFriends(Account requester, Account receiver) {
		receiver.requestFriendship(requester);
		requester.friendshipAccepted(receiver);
	}

	private void assertAreFriends(Account one, Account other) {
		assertTrue(one.hasFriend(other.getUserName()));
		assertTrue(other.hasFriend(one.getUserName()));
	}

}
