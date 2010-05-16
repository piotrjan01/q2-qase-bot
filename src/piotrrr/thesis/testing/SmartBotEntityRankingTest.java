package piotrrr.thesis.testing;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import piotrrr.thesis.bots.smartbot.SmartBotEntityRanking;
import piotrrr.thesis.tools.Dbg;

public class SmartBotEntityRankingTest {

	float epsilon = 0.01f;
	
	SmartBotMock bot;
	
	SmartBotEntityRanking rank;
	
	Random rand = new Random();
	
	@Before
	public void setUp() throws Exception {
		bot = new SmartBotMock();
		rank = new SmartBotEntityRanking();
	}
	
	@Test
	public void testGetAspitationReservationValue() {
		float a = bot.nConfig.armorAspiration;
		float r = bot.nConfig.armorReservation;
		float real = -111, exp = -777;
		boolean exc = false;

		try {
			exp = 100f;
			bot.setArmor(r-1);
			real = rank.getNeedFromAspitationReservationValues(bot, "armor");
		} catch (Exception e) {
			exc = true;
			e.printStackTrace();
		}
		
		assertTrue( ! exc );
		assertTrue(real == exp);
		
		try {
			exp = 0f;
			bot.setArmor(a+1);
			real = rank.getNeedFromAspitationReservationValues(bot, "armor");
		} catch (Exception e) {
			exc = true;
			e.printStackTrace();
		}
		
		assertTrue( ! exc );
		assertTrue(real == exp);
		
		for (float e = 1f; e < 99f; e++) {
		
			try {
				exp = e;
				bot.setArmor((bot.nConfig.getParameter("armorAspiration")-bot.nConfig.getParameter("armorReservation"))*exp/100f);
				real = rank.getNeedFromAspitationReservationValues(bot, "armor");
			} catch (Exception ex) {
				exc = true;
				ex.printStackTrace();
			}
			
			assertTrue( ! exc );
			assertTrue(Math.abs(real-exp)<epsilon);
		}
		
		try {
			rank.getNeedFromAspitationReservationValues(bot, "health");
		} catch (Exception e) {
			exc = true;
			e.printStackTrace();
		}
		assertTrue( ! exc );
		
		try {
			rank.getNeedFromAspitationReservationValues(bot, "firepower");
		} catch (Exception e) {
			exc = true;
			e.printStackTrace();
		}
		assertTrue( ! exc );
		
	}
	
	
	@Test
	public void testGetBotParam_health() {
		bot.setHealth(rand.nextFloat()*200f);
		float exp = bot.getBotHealth();
		rank.getBotParam_health(bot);
		assertTrue(exp == bot.getBotHealth());
	}
	
	/*
	@Test
	public void testGetEntityRanking() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFirePowerBenefit() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFitnessBenefit() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetEnemyCost() {
		fail("Not yet implemented");
	}

	

	@Test
	public void testGetEnemiesNeed() {
		fail("Not yet implemented");
	}

	
	*/

}
