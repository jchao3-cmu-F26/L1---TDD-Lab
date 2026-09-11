import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SocialNetworkTest {

	SocialNetwork sn;
	Account me;
	Account her;

	@Before
	public void setUp() throws Exception {
		sn = new SocialNetwork();
	}

	@After
	public void tearDown() throws Exception {
	
	}

	@Test 
	public void joinReturnsAccountWithGivenUserName() {
		me = sn.join("Hakan");
		assertEquals("Hakan", me.getUserName());
	}
	
	@Test 
	public void listMembersAfterOneJoinContainsThatMember() {
		sn.join("Hakan");
		Collection<String> members = sn.listMembers();
		assertEquals(1, members.size());
		assertTrue(members.contains("Hakan"));
	}
	
	@Test 
	public void listMembersAfterTwoJoinsContainsBothMembers() {
		joinHakanAndCecile();
		Collection<String> members = sn.listMembers();
		assertEquals(2, members.size());
		assertTrue(members.contains("Hakan"));
		assertTrue(members.contains("Cecile"));
	}

	@Test
	public void hasMemberReturnsTrueAfterUserJoins() {
		sn.join("Hakan");
		assertTrue(sn.hasMember("Hakan"));
	}

	@Test
	public void hasMemberReturnsFalseForUnknownUser() {
		sn.join("Hakan");
		assertFalse(sn.hasMember("Cecile"));
	}

	@Test
	public void hasMemberReturnsFalseWhenNetworkEmpty() {
		assertFalse(sn.hasMember("Hakan"));
	}

	@Test
	public void hasMemberReturnsFalseAfterMemberLeaves() {
		me = sn.join("Hakan");
		sn.login(me);
		sn.leave();
		assertFalse(sn.hasMember("Hakan"));
	}
	
	@Test
	public void sendFriendshipToAddsRequesterToIncomingRequests() {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		assertTrue(her.getIncomingRequests().contains("Hakan"));
	}

	@Test
	public void sendFriendshipToAddsTargetToOutgoingRequests() {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		assertTrue(me.getOutgoingRequests().contains("Cecile"));
	}

	@Test
	public void acceptFriendshipFromRemovesTargetFromOutgoingRequests() {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.acceptFriendshipFrom("Hakan");
		assertFalse(me.getOutgoingRequests().contains("Cecile"));
	}
	
	@Test 
	public void acceptFriendshipFromAddsRequesterToAcceptersFriends() {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.acceptFriendshipFrom("Hakan");
		assertTrue(her.hasFriend("Hakan"));
	}

	@Test 
	public void acceptFriendshipFromAddsAccepterToRequestersFriends() {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.acceptFriendshipFrom("Hakan");
		assertTrue(me.hasFriend("Cecile"));
	}

	@Test
	public void noUserLoggedInInitially() {
		assertNull(sn.getLoggedInUser());
	}

	@Test
	public void loginReturnsJoinedAccountAndSetsLoggedInUser() {
		me = sn.join("Hakan");
		Account loggedIn = sn.login(me);
		assertSame(me, loggedIn);
		assertSame(me, sn.getLoggedInUser());
	}

	@Test
	public void loginSwitchesAccountWithoutLogout() {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		sn.login(me);
		Account loggedIn = sn.login(her);
		assertSame(her, loggedIn);
		assertSame(her, sn.getLoggedInUser());
	}

	@Test
	public void loginNullReturnsNullAndDoesNotChangeLoggedInUser() {
		me = sn.join("Hakan");
		sn.login(me);
		assertNull(sn.login(null));
		assertSame(me, sn.getLoggedInUser());
	}

	@Test
	public void loginUnknownAccountReturnsNull() {
		Account stranger = new Account("Ghost");
		assertNull(sn.login(stranger));
		assertNull(sn.getLoggedInUser());
	}

	@Test
	public void emptyNetworkHasNoMembers() {
		assertEquals(0, sn.listMembers().size());
	}

	@Test
	public void joinWithExistingUserNameReturnsNull() {
		sn.join("Hakan");
		assertNull(sn.join("Hakan"));
	}

	@Test
	public void joinWithExistingUserNameLeavesMemberCountUnchanged() {
		sn.join("Hakan");
		sn.join("Hakan");
		assertEquals(1, sn.listMembers().size());
	}

	@Test
	public void joinWithNullUserNameReturnsNull() {
		assertNull(sn.join(null));
	}

	@Test
	public void joinWithEmptyUserNameReturnsNull() {
		assertNull(sn.join(""));
	}

	@Test
	public void sendFriendshipToUnknownUserDoesNotAddIncomingRequest() {
		me = sn.join("Hakan");
		sn.login(me);
		sn.sendFriendshipTo("Ghost");
		assertEquals(0, me.getIncomingRequests().size());
	}

	@Test
	public void sendFriendshipToSelfDoesNotAddIncomingRequest() {
		me = sn.join("Hakan");
		sn.login(me);
		sn.sendFriendshipTo("Hakan");
		assertEquals(0, me.getIncomingRequests().size());
	}

	@Test
	public void acceptFriendshipFromUnknownUserDoesNotAddFriends() {
		me = sn.join("Hakan");
		sn.login(me);
		sn.acceptFriendshipFrom("Ghost");
		assertEquals(0, me.getFriends().size());
	}

	@Test
	public void acceptFriendshipFromWithoutPendingRequestDoesNotAddFriends() {
		joinHakanAndCecile();
		sn.login(her);
		sn.acceptFriendshipFrom("Hakan");
		assertFalse(me.hasFriend("Cecile"));
		assertFalse(her.hasFriend("Hakan"));
	}

	@Test
	public void acceptAllFriendshipsToMakesAllRequestersFriends() {
		Account another = joinHakanCecileAndSerra();
		sn.login(her);
		sn.sendFriendshipTo("Hakan");
		sn.login(another);
		sn.sendFriendshipTo("Hakan");
		sn.login(me);
		sn.acceptAllFriendships();
		assertTrue(me.hasFriend("Cecile"));
		assertTrue(me.hasFriend("Serra"));
	}

	@Test
	public void acceptAllFriendshipsToNullDoesNothing() {
		Account another = joinHakanCecileAndSerra();
		sn.login(her);
		sn.sendFriendshipTo("Hakan");
		sn.login(another);
		sn.sendFriendshipTo("Hakan");
		sn.acceptAllFriendships();
		assertFalse(me.hasFriend("Cecile"));
		assertFalse(me.hasFriend("Serra"));
	}


	@Test
	public void acceptAllFriendshipsToClearsIncomingRequests() {
		Account another = joinHakanCecileAndSerra();
		sn.login(her);
		sn.sendFriendshipTo("Hakan");
		sn.login(another);
		sn.sendFriendshipTo("Hakan");
		sn.login(me);
		sn.acceptAllFriendships();
		assertEquals(0, me.getIncomingRequests().size());
	}

	@Test
	public void acceptAllFriendshipsToRemovesMeFromRequestersOutgoingRequests() {
		joinHakanAndCecile();
		sn.login(her);
		sn.sendFriendshipTo("Hakan");
		sn.login(me);
		sn.acceptAllFriendships();
		assertFalse(her.getOutgoingRequests().contains("Hakan"));
	}

	@Test
	public void acceptAllFriendshipsToWithNoPendingRequestsLeavesFriendsUnchanged() {
		joinHakanAndCecile();
		sn.login(me);
		sn.acceptAllFriendships();
		assertEquals(0, me.getFriends().size());
	}

	@Test
	public void rejectFriendshipFromRemovesTargetFromOutgoingRequests() {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.rejectFriendshipFrom("Hakan");
		assertFalse(me.getOutgoingRequests().contains("Cecile"));
	}

	@Test
	public void rejectFriendshipFromNullDoesNothing() {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.rejectFriendshipFrom(null);
		assertTrue(me.getOutgoingRequests().contains("Cecile"));
	}

	@Test
	public void rejectAllFriendshipsToClearsIncomingRequests() {
		Account another = joinHakanCecileAndSerra();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(another);
		sn.sendFriendshipTo("Cecile");
		assertEquals(2, her.getIncomingRequests().size());
		sn.login(her);
		sn.rejectAllFriendships();
		assertEquals(0, her.getIncomingRequests().size());
	}

	@Test
	public void rejectAllFriendshipsToNullDoesNothing() {
		Account another = joinHakanCecileAndSerra();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(another);
		sn.sendFriendshipTo("Cecile");
		assertEquals(2, her.getIncomingRequests().size());
		sn.rejectAllFriendships();
		assertEquals(2, her.getIncomingRequests().size());
	}

	@Test
	public void rejectAllFriendshipsToClearsOutgoingRequests() {
		Account another = joinHakanCecileAndSerra();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(another);
		sn.sendFriendshipTo("Cecile");
		assertEquals(1, me.getOutgoingRequests().size());
		assertEquals(1, another.getOutgoingRequests().size());
		sn.login(her);
		sn.rejectAllFriendships();
		assertEquals(0, me.getOutgoingRequests().size());
		assertEquals(0, another.getOutgoingRequests().size());
	}

	@Test
	public void autoAcceptFriendshipWhenBecomeFriends() {
		joinHakanAndCecile();
		sn.login(me);
		sn.autoAcceptFriendships();
		assertFalse(me.hasFriend("Cecile"));
		assertFalse(me.hasFriend("Hakan"));
		sn.login(her);
		sn.sendFriendshipTo("Hakan");
		assertTrue(me.hasFriend("Cecile"));
		assertTrue(her.hasFriend("Hakan"));
	}

	@Test
	public void autoAcceptFriendshipWithNullDoesNothing() {
		joinHakanAndCecile();
		sn.autoAcceptFriendships();
		assertFalse(me.hasFriend("Cecile"));
		assertFalse(me.hasFriend("Hakan"));
		sn.login(her);
		sn.sendFriendshipTo("Hakan");
		assertFalse(me.hasFriend("Cecile"));
		assertFalse(her.hasFriend("Hakan"));
	}
	
	@Test
	public void autoAcceptFriendshipWhenAlreadyFriends() {
		joinHakanAndCecile();
		sn.login(me);
		sn.autoAcceptFriendships();
		sn.login(her);
		sn.sendFriendshipTo("Hakan");
		assertTrue(me.hasFriend("Cecile"));
		assertTrue(her.hasFriend("Hakan"));
		sn.sendFriendshipTo("Hakan");
		assertTrue(me.hasFriend("Cecile"));
		assertTrue(her.hasFriend("Hakan"));
		assertEquals(0, me.getOutgoingRequests().size());
		assertEquals(0, her.getOutgoingRequests().size());
	}

	@Test
	public void cancelFriendshipRemovesEachOtherFromFriends() {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.acceptFriendshipFrom("Hakan");
		assertTrue(me.hasFriend("Cecile"));
		sn.login(me);
		sn.sendFriendshipCancellationTo("Cecile");
		assertFalse(me.hasFriend("Cecile"));
		assertFalse(her.hasFriend("Hakan"));
	}

	@Test
	public void cancelFriendshipWhenNotFriends() {
		joinHakanAndCecile();
		assertFalse(me.hasFriend("Cecile"));
		sn.login(me);
		sn.sendFriendshipCancellationTo("Cecile");
		assertFalse(me.hasFriend("Cecile"));
		assertFalse(her.hasFriend("Hakan"));
	}

	@Test
	public void cancelMyselfFromFriends() {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.acceptFriendshipFrom("Hakan");
		assertTrue(me.hasFriend("Cecile"));
		assertFalse(me.hasFriend("Hakan"));
		sn.login(me);
		sn.sendFriendshipCancellationTo("Hakan");
		assertTrue(me.hasFriend("Cecile"));
		assertFalse(me.hasFriend("Hakan"));
	}

	@Test
	public void cancelFriendshipFromNonExistingAccount() {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.acceptFriendshipFrom("Hakan");
		assertTrue(me.hasFriend("Cecile"));
		sn.login(me);
		sn.sendFriendshipCancellationTo("Ghost");
		assertTrue(me.hasFriend("Cecile"));
		assertFalse(me.hasFriend("Ghost"));
	}
	
	@Test 
	public void leaveNetworkRemovesMeFromFriends() {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.acceptFriendshipFrom("Hakan");
		assertTrue(me.hasFriend("Cecile"));
		sn.login(me);
		sn.leave();
		assertFalse(her.hasFriend("Hakan"));
		assertNull(sn.getLoggedInUser());
	}

	@Test
	public void leaveNetworkRemovesMeFromIncomingRequest() {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		assertEquals(1, her.getIncomingRequests().size());
		sn.leave();
		assertEquals(0, her.getIncomingRequests().size());
	}

	@Test
	public void leaveNetworkRemovesMeFromOutgoingRequest() {
		joinHakanAndCecile();
		sn.login(her);
		sn.sendFriendshipTo("Hakan");
		assertEquals(1, her.getOutgoingRequests().size());
		sn.login(me);
		sn.leave();
		assertEquals(0, her.getOutgoingRequests().size());
	}

	@Test
	public void leaveNetworkWithNullDoesNothing() {
		joinHakanAndCecile();
		her.requestFriendship(me);
		me.friendshipAccepted(her);
		assertTrue(me.hasFriend("Cecile"));
		assertTrue(her.hasFriend("Hakan"));
		sn.leave();
		assertTrue(me.hasFriend("Cecile"));
		assertTrue(her.hasFriend("Hakan"));
	}

	private void joinHakanAndCecile() {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
	}

	private Account joinHakanCecileAndSerra() {
		joinHakanAndCecile();
		return sn.join("Serra");
	}

}
