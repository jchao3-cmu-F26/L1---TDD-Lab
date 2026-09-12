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
	public void listMembersAfterOneJoinContainsThatMember() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		sn.login(me);
		Collection<String> members = sn.listMembers();
		assertEquals(1, members.size());
		assertTrue(members.contains("Hakan"));
	}
	
	@Test 
	public void listMembersAfterTwoJoinsContainsBothMembers() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		Collection<String> members = sn.listMembers();
		assertEquals(2, members.size());
		assertTrue(members.contains("Hakan"));
		assertTrue(members.contains("Cecile"));
	}

	@Test
	public void hasMemberReturnsTrueAfterUserJoins() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		sn.login(me);
		assertTrue(sn.hasMember("Hakan"));
	}

	@Test
	public void hasMemberReturnsFalseForUnknownUser() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		sn.login(me);
		assertFalse(sn.hasMember("Cecile"));
	}

	@Test
	public void hasMemberThrowsExceptionWhenNetworkEmpty() throws NoUserLoggedInException {
		try {
			assertFalse(sn.hasMember("Hakan"));
			fail("Expected NoUserLoggedInException to be thrown");
		} catch (NoUserLoggedInException e) {
			// Expected exception, test passes
		}
	}

	@Test
	public void hasMemberReturnsThrowsExceptionWhenLoggedInUserLeaves() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		sn.login(me);
		sn.leave();
		try {
			sn.hasMember("Hakan");
			fail("Expected NoUserLoggedInException to be thrown");
		} catch (NoUserLoggedInException e) {
			// Expected exception, test passes
		}
	}
	
	@Test
	public void sendFriendshipToAddsRequesterToIncomingRequests() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		assertTrue(her.getIncomingRequests().contains("Hakan"));
	}

	@Test
	public void sendFriendshipToAddsTargetToOutgoingRequests() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		assertTrue(me.getOutgoingRequests().contains("Cecile"));
	}

	@Test
	public void acceptFriendshipFromRemovesTargetFromOutgoingRequests() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.acceptFriendshipFrom("Hakan");
		assertFalse(me.getOutgoingRequests().contains("Cecile"));
	}
	
	@Test 
	public void acceptFriendshipFromAddsRequesterToAcceptersFriends() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.acceptFriendshipFrom("Hakan");
		assertTrue(her.hasFriend("Hakan"));
	}

	@Test 
	public void acceptFriendshipFromAddsAccepterToRequestersFriends() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.acceptFriendshipFrom("Hakan");
		assertTrue(me.hasFriend("Cecile"));
	}

	@Test
	public void noUserLoggedInInitially() throws NoUserLoggedInException {
		try {
			sn.getLoggedInUser();
			fail("Expected NoUserLoggedInException to be thrown");
		} catch (NoUserLoggedInException e) {
			// Expected exception, test passes
		}
	}

	@Test
	public void loginReturnsJoinedAccountAndSetsLoggedInUser() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		Account loggedIn = sn.login(me);
		assertSame(me, loggedIn);
		assertSame(me, sn.getLoggedInUser());
	}

	@Test
	public void loginSwitchesAccountWithoutLogout() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		sn.login(me);
		Account loggedIn = sn.login(her);
		assertSame(her, loggedIn);
		assertSame(her, sn.getLoggedInUser());
	}

	@Test
	public void loginNullReturnsNullAndDoesNotChangeLoggedInUser() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		sn.login(me);
		assertNull(sn.login(null));
		assertSame(me, sn.getLoggedInUser());
	}

	@Test
	public void loginUnknownAccountReturnsNull() throws NoUserLoggedInException {
		Account stranger = new Account("Ghost");
		assertNull(sn.login(stranger));
		try {
			sn.getLoggedInUser();
			fail("Expected NoUserLoggedInException to be thrown");
		} catch (NoUserLoggedInException e) {
			// Expected exception, test passes
		}
	}

	@Test
	public void listMembersThrowsExceptionWhenNoUserIsLoggedIn() throws NoUserLoggedInException {
		try {
			sn.listMembers();
			fail("Expected NoUserLoggedInException to be thrown");
		} catch (NoUserLoggedInException e) {
			// Expected exception, test passes
		}
	}

	@Test
	public void joinWithExistingUserNameReturnsNull() {
		sn.join("Hakan");
		assertNull(sn.join("Hakan"));
	}

	@Test
	public void joinWithExistingUserNameLeavesMemberCountUnchanged() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		sn.join("Hakan");
		sn.login(me);
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
	public void sendFriendshipToUnknownUserDoesNotAddIncomingRequest() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		sn.login(me);
		sn.sendFriendshipTo("Ghost");
		assertEquals(0, me.getIncomingRequests().size());
	}

	@Test
	public void sendFriendshipToSelfDoesNotAddIncomingRequest() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		sn.login(me);
		sn.sendFriendshipTo("Hakan");
		assertEquals(0, me.getIncomingRequests().size());
	}

	@Test
	public void acceptFriendshipFromUnknownUserDoesNotAddFriends() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		sn.login(me);
		sn.acceptFriendshipFrom("Ghost");
		assertEquals(0, me.getFriends().size());
	}

	@Test
	public void acceptFriendshipFromWithoutPendingRequestDoesNotAddFriends() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(her);
		sn.acceptFriendshipFrom("Hakan");
		assertFalse(me.hasFriend("Cecile"));
		assertFalse(her.hasFriend("Hakan"));
	}

	@Test
	public void acceptAllFriendshipsToMakesAllRequestersFriends() throws NoUserLoggedInException {
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
	public void acceptAllFriendshipsToNullDoesNothing() throws NoUserLoggedInException {
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
	public void acceptAllFriendshipsToClearsIncomingRequests() throws NoUserLoggedInException {
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
	public void acceptAllFriendshipsToRemovesMeFromRequestersOutgoingRequests() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(her);
		sn.sendFriendshipTo("Hakan");
		sn.login(me);
		sn.acceptAllFriendships();
		assertFalse(her.getOutgoingRequests().contains("Hakan"));
	}

	@Test
	public void acceptAllFriendshipsToWithNoPendingRequestsLeavesFriendsUnchanged() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.acceptAllFriendships();
		assertEquals(0, me.getFriends().size());
	}

	@Test
	public void rejectFriendshipFromRemovesTargetFromOutgoingRequests() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.rejectFriendshipFrom("Hakan");
		assertFalse(me.getOutgoingRequests().contains("Cecile"));
	}

	@Test
	public void rejectFriendshipFromNullDoesNothing() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.rejectFriendshipFrom(null);
		assertTrue(me.getOutgoingRequests().contains("Cecile"));
	}

	@Test
	public void rejectAllFriendshipsToClearsIncomingRequests() throws NoUserLoggedInException {
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
	public void rejectAllFriendshipsToNullDoesNothing() throws NoUserLoggedInException {
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
	public void rejectAllFriendshipsToClearsOutgoingRequests() throws NoUserLoggedInException {
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
	public void autoAcceptFriendshipWhenBecomeFriends() throws NoUserLoggedInException {
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
	public void autoAcceptFriendshipWithoutLoggedInUser() throws NoUserLoggedInException {
		joinHakanAndCecile();
		try {
			sn.autoAcceptFriendships();
			fail("Expected NoUserLoggedInException to be thrown");
		} catch (NoUserLoggedInException e) {
			assertFalse(me.hasFriend("Cecile"));
			assertFalse(me.hasFriend("Hakan"));
			sn.login(her);
			sn.sendFriendshipTo("Hakan");
			assertFalse(me.hasFriend("Cecile"));
			assertFalse(her.hasFriend("Hakan"));
		}
	}
	
	@Test
	public void autoAcceptFriendshipWhenAlreadyFriends() throws NoUserLoggedInException {
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
	public void cancelAutoAcceptRequiresExplicitAcceptanceForFutureRequests() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.autoAcceptFriendships();
		sn.cancelAutoAcceptFriendships();
		sn.login(her);
		sn.sendFriendshipTo("Hakan");
		assertFalse(me.hasFriend("Cecile"));
		assertFalse(her.hasFriend("Hakan"));
		assertTrue(me.getIncomingRequests().contains("Cecile"));
	}

	@Test
	public void cancelAutoAcceptStillAllowsExplicitAccept() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.autoAcceptFriendships();
		sn.cancelAutoAcceptFriendships();
		sn.login(her);
		sn.sendFriendshipTo("Hakan");
		sn.login(me);
		sn.acceptFriendshipFrom("Cecile");
		assertTrue(me.hasFriend("Cecile"));
		assertTrue(her.hasFriend("Hakan"));
	}

	@Test
	public void cancelAutoAcceptAppliesOnlyToLoggedInMember() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.autoAcceptFriendships();
		sn.login(her);
		sn.cancelAutoAcceptFriendships();
		sn.sendFriendshipTo("Hakan");
		assertTrue(me.hasFriend("Cecile"));
		assertTrue(her.hasFriend("Hakan"));
	}

	@Test
	public void cancelFriendshipRemovesEachOtherFromFriends() throws NoUserLoggedInException {
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
	public void cancelFriendshipWhenNotFriends() throws NoUserLoggedInException {
		joinHakanAndCecile();
		assertFalse(me.hasFriend("Cecile"));
		sn.login(me);
		sn.sendFriendshipCancellationTo("Cecile");
		assertFalse(me.hasFriend("Cecile"));
		assertFalse(her.hasFriend("Hakan"));
	}

	@Test
	public void cancelMyselfFromFriends() throws NoUserLoggedInException {
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
	public void cancelFriendshipFromNonExistingAccount() throws NoUserLoggedInException {
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
	public void leaveNetworkRemovesMeFromFriends() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.acceptFriendshipFrom("Hakan");
		assertTrue(me.hasFriend("Cecile"));
		sn.login(me);
		sn.leave();
		assertFalse(her.hasFriend("Hakan"));
		try {
			sn.getLoggedInUser();
			fail("Expected NoUserLoggedInException to be thrown");
		} catch (NoUserLoggedInException e) {
			// Expected exception, test passes
		}
	}

	@Test
	public void leaveNetworkRemovesMeFromIncomingRequest() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		assertEquals(1, her.getIncomingRequests().size());
		sn.leave();
		assertEquals(0, her.getIncomingRequests().size());
	}

	@Test
	public void leaveNetworkRemovesMeFromOutgoingRequest() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(her);
		sn.sendFriendshipTo("Hakan");
		assertEquals(1, her.getOutgoingRequests().size());
		sn.login(me);
		sn.leave();
		assertEquals(0, her.getOutgoingRequests().size());
	}

	@Test
	public void leaveNetworkWithoutLoginThrowsException() throws NoUserLoggedInException {
		joinHakanAndCecile();
		her.requestFriendship(me);
		me.friendshipAccepted(her);
		assertTrue(me.hasFriend("Cecile"));
		assertTrue(her.hasFriend("Hakan"));
		try {
			sn.leave();
			fail("Expected NoUserLoggedInException to be thrown");
		} catch (NoUserLoggedInException e) {
			// Expected exception, test passes
			assertTrue(me.hasFriend("Cecile"));
			assertTrue(her.hasFriend("Hakan"));
		}
	}

	@Test
	public void blockedMemberDoesNotSeeBlockerInListMembers() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.block("Cecile");
		sn.login(her);
		Collection<String> members = sn.listMembers();
		assertFalse(members.contains("Hakan"));
		assertTrue(members.contains("Cecile"));
	}

	@Test
	public void blockedMemberHasMemberReturnsFalseForBlocker() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.block("Cecile");
		sn.login(her);
		assertFalse(sn.hasMember("Hakan"));
		assertTrue(sn.hasMember("Cecile"));
	}

	@Test
	public void blockedMemberCannotSendFriendshipToBlocker() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.block("Cecile");
		sn.login(her);
		sn.sendFriendshipTo("Hakan");
		assertEquals(0, me.getIncomingRequests().size());
		assertEquals(0, her.getOutgoingRequests().size());
		assertFalse(me.hasFriend("Cecile"));
	}

	@Test
	public void blockerStillSeesBlockedMember() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.block("Cecile");
		assertTrue(sn.listMembers().contains("Cecile"));
		assertTrue(sn.hasMember("Cecile"));
	}

	@Test
	public void blockWithoutLoginDoesNothing() throws NoUserLoggedInException {
		joinHakanAndCecile();
		try {
			sn.block("Cecile");
			fail("Expected NoUserLoggedInException to be thrown");
		} catch (NoUserLoggedInException e) {
			// Expected exception, test passes
			sn.login(her);
			assertTrue(sn.listMembers().contains("Hakan"));
			assertTrue(sn.hasMember("Hakan"));
			sn.login(me);
			assertTrue(sn.listMembers().contains("Cecile"));
			assertTrue(sn.hasMember("Cecile"));
		}
	}

	@Test
	public void blockNullUnknownOrSelfLeavesMembersVisible() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.block(null);
		sn.block("Ghost");
		sn.block("Hakan");
		Collection<String> members = sn.listMembers();
		assertEquals(2, members.size());
		assertTrue(members.contains("Hakan"));
		assertTrue(members.contains("Cecile"));
	}

	@Test
	public void blockingSameMemberTwiceBehavesLikeBlockingOnce() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.block("Cecile");
		sn.block("Cecile");
		sn.login(her);
		assertFalse(sn.hasMember("Hakan"));
		assertFalse(sn.listMembers().contains("Hakan"));
		sn.login(me);
		assertTrue(sn.hasMember("Cecile"));
		assertTrue(sn.listMembers().contains("Cecile"));
	}

	@Test
	public void unblockMakesBlockerVisibleInListMembersAgain() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.block("Cecile");
		sn.unblock("Cecile");
		sn.login(her);
		assertTrue(sn.listMembers().contains("Hakan"));
	}

	@Test
	public void unblockMakesHasMemberTrueForPreviouslyBlockedViewer() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.block("Cecile");
		sn.unblock("Cecile");
		sn.login(her);
		assertTrue(sn.hasMember("Hakan"));
	}

	@Test
	public void unblockAllowsFriendshipRequestAndAccept() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.block("Cecile");
		sn.unblock("Cecile");
		sn.login(her);
		sn.sendFriendshipTo("Hakan");
		sn.login(me);
		sn.acceptFriendshipFrom("Cecile");
		assertTrue(me.hasFriend("Cecile"));
		assertTrue(her.hasFriend("Hakan"));
	}

	@Test
	public void unblockWithoutLoginDoesNotLiftExistingBlock() throws NoUserLoggedInException {
		joinHakanAndCecile();
		me.block("Cecile");
		try {
			sn.unblock("Cecile");
			fail("Expected NoUserLoggedInException to be thrown");
		} catch (NoUserLoggedInException e) {
			// Expected exception, test passes
			sn.login(her);
			assertFalse(sn.hasMember("Hakan"));
			assertFalse(sn.listMembers().contains("Hakan"));
		}
	}

	@Test
	public void unblockUnknownNullOrNeverBlockedDoesNotThrow() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.unblock(null);
		sn.unblock("Ghost");
		sn.unblock("Cecile");
		assertTrue(sn.hasMember("Cecile"));
		sn.login(her);
		assertTrue(sn.hasMember("Hakan"));
	}

	@Test
	public void unblockOneMemberLeavesOtherBlockInPlace() throws NoUserLoggedInException {
		Account another = joinHakanCecileAndSerra();
		sn.login(me);
		sn.block("Cecile");
		sn.block("Serra");
		sn.unblock("Cecile");
		sn.login(her);
		assertTrue(sn.hasMember("Hakan"));
		sn.login(another);
		assertFalse(sn.hasMember("Hakan"));
	}

	@Test
	public void recommendFriendsIncludesMemberWithExactlyTwoMutualFriends() throws NoUserLoggedInException {
		Account another = joinHakanCecileAndSerra();
		Account him = sn.join("Rafal");
		sn.login(me);
		sn.sendFriendshipTo("Serra");
		sn.sendFriendshipTo("Rafal");
		sn.login(her);
		sn.sendFriendshipTo("Serra");
		sn.sendFriendshipTo("Rafal");
		sn.login(another);
		sn.acceptFriendshipFrom("Hakan");
		sn.acceptFriendshipFrom("Cecile");
		sn.login(him);
		sn.acceptFriendshipFrom("Hakan");
		sn.acceptFriendshipFrom("Cecile");
		sn.login(me);
		assertTrue(sn.recommendFriends().contains("Cecile"));
	}

	@Test
	public void recommendFriendsWithoutLoginThrowsException() throws NoUserLoggedInException {
		joinHakanAndCecile();
		try {
			sn.recommendFriends();
			fail("Expected NoUserLoggedInException to be thrown");
		} catch (NoUserLoggedInException e) {
			// Expected exception, test passes
		}
	}

	@Test
	public void recommendFriendsWithLoginAccountWithNoFriendsReturnsEmptySet() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		assertTrue(sn.recommendFriends().size() == 0);
	}

	@Test
	public void recommendFriendsWithFriendWhoSharesExactlyOneMutualFriends() throws NoUserLoggedInException {
		Account another = joinHakanCecileAndSerra();
		sn.login(me);
		sn.sendFriendshipTo("Serra");
		sn.login(her);
		sn.sendFriendshipTo("Serra");
		sn.login(another);
		sn.acceptFriendshipFrom("Hakan");
		sn.acceptFriendshipFrom("Cecile");
		sn.login(me);
		assertTrue(sn.recommendFriends().size() == 0);
	}

	@Test
	public void recommendFriendsWithFriendWhoAlreadyHasTwoMutualFriends() throws NoUserLoggedInException {
		Account another = joinHakanCecileAndSerra();
		Account him = sn.join("Rafal");
		sn.login(me);
		sn.sendFriendshipTo("Serra");
		sn.sendFriendshipTo("Rafal");
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.acceptFriendshipFrom("Hakan");
		sn.sendFriendshipTo("Serra");
		sn.sendFriendshipTo("Rafal");
		sn.login(another);
		sn.acceptFriendshipFrom("Hakan");
		sn.acceptFriendshipFrom("Cecile");
		sn.login(him);
		sn.acceptFriendshipFrom("Hakan");
		sn.acceptFriendshipFrom("Cecile");
		sn.login(me);
		assertTrue(sn.recommendFriends().size() == 0);
	}

	@Test
	public void recommendFriendsWithMoreThanTwoMutualFriends() throws NoUserLoggedInException {
		Account another = joinHakanCecileAndSerra();
		Account him = sn.join("Rafal");
		Account student = sn.join("Jeffery");
		sn.login(me);
		sn.sendFriendshipTo("Serra");
		sn.sendFriendshipTo("Rafal");
		sn.sendFriendshipTo("Jeffery");
		sn.login(her);
		sn.sendFriendshipTo("Serra");
		sn.sendFriendshipTo("Rafal");
		sn.sendFriendshipTo("Jeffery");
		sn.login(another);
		sn.acceptFriendshipFrom("Hakan");
		sn.acceptFriendshipFrom("Cecile");
		sn.login(him);
		sn.acceptFriendshipFrom("Hakan");
		sn.acceptFriendshipFrom("Cecile");
		sn.login(student);
		sn.acceptFriendshipFrom("Hakan");
		sn.acceptFriendshipFrom("Cecile");
		sn.login(me);
		assertTrue(sn.recommendFriends().size() == 1);
		assertTrue(sn.recommendFriends().contains("Cecile"));
	}

	@Test
	public void recommendFriendsDoesNotContainLoggedInUser() throws NoUserLoggedInException {
		Account another = joinHakanCecileAndSerra();
		Account him = sn.join("Rafal");
		Account student = sn.join("Jeffery");
		sn.login(me);
		sn.sendFriendshipTo("Serra");
		sn.sendFriendshipTo("Rafal");
		sn.sendFriendshipTo("Jeffery");
		sn.login(her);
		sn.sendFriendshipTo("Serra");
		sn.sendFriendshipTo("Rafal");
		sn.sendFriendshipTo("Jeffery");
		sn.login(another);
		sn.acceptFriendshipFrom("Hakan");
		sn.acceptFriendshipFrom("Cecile");
		sn.login(him);
		sn.acceptFriendshipFrom("Hakan");
		sn.acceptFriendshipFrom("Cecile");
		sn.login(student);
		sn.acceptFriendshipFrom("Hakan");
		sn.acceptFriendshipFrom("Cecile");
		sn.login(me);
		assertFalse(sn.recommendFriends().contains("Hakan"));
	}

	@Test
	public void recommendFriendsWithNoOtherUsersReturnEmptyList() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		sn.login(me);
		assertTrue(sn.recommendFriends().size() == 0);
	}

	@Test
	public void blockFriendRemovesFriendship() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		sn.acceptFriendshipFrom("Hakan");
		assertTrue(me.hasFriend("Cecile"));
		sn.block("Hakan");
		assertFalse(me.hasFriend("Hakan"));
		assertFalse(her.hasFriend("Cecile"));
	}

	@Test
	public void blockAccountWithPendingRemovesRequest() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		assertTrue(me.getOutgoingRequests().contains("Cecile"));
		assertTrue(her.getIncomingRequests().contains("Hakan"));
		sn.block("Hakan");
		assertFalse(me.getOutgoingRequests().contains("Cecile"));
		assertFalse(her.getIncomingRequests().contains("Hakan"));
		assertFalse(me.hasFriend("Cecile"));
		assertFalse(her.hasFriend("Hakan"));
	}

	@Test
	public void blockAccountAfterSendingRequestRemovesRequest() throws NoUserLoggedInException {
		joinHakanAndCecile();
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
		sn.login(her);
		assertTrue(me.getOutgoingRequests().contains("Cecile"));
		assertTrue(her.getIncomingRequests().contains("Hakan"));
		sn.block("Hakan");
		assertFalse(me.getOutgoingRequests().contains("Cecile"));
		assertFalse(her.getIncomingRequests().contains("Hakan"));
		assertFalse(me.hasFriend("Cecile"));
		assertFalse(her.hasFriend("Hakan"));
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
