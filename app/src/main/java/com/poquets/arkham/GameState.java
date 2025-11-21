/*
 * This file is part of Arkham Companion.
 *
 *  Arkham Companion is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Arkham Companion is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Arkham Companion.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.poquets.arkham;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class GameState 
{
	private final static GameState INSTANCE = new GameState();
	private static boolean loaded = false;
	//Sorted arrays for each neighborhood representing the deck of cards
	private HashMap<Long,ArrayList<NeighborhoodCard>> neighborhoodCardsList;
	private ArrayList<OtherWorldCard> otherWorldCards = null;
	private Random rand;
	private ArrayList<EncounterHx> encounterHx = null;
	private HashMap<Long,OtherWorldColor> currentColors = null;
	
	private long gameID;
	
	//The expansions selected for this play session
	private HashMap<Long,Long> currentExpansions;
	
	public static GameState getInstance()
	{
		if(!loaded)
		{
			INSTANCE.Load();
			loaded = true;
		}
		
		return INSTANCE;
	}
	
	private void Load()
	{
		rand = new Random(System.currentTimeMillis());

		Long currGameID = AHFlyweightFactory.INSTANCE.getMostRecentGameID();
		if(currGameID == null)
		{			
			gameID = AHFlyweightFactory.INSTANCE.createNewGame();
			// Apply base game by default
			applyExpansion(1, true);
			android.util.Log.i("GameState", "New game created with base expansion (ID 1) applied by default");
		}
		else
		{
			gameID = currGameID;
			currentExpansions = AHFlyweightFactory.INSTANCE.getGameExps(gameID);
			encounterHx = AHFlyweightFactory.INSTANCE.getGameEncHx(gameID);
			otherWorldCards = arrayListConvert(AHFlyweightFactory.INSTANCE.getDeck(-1, gameID));
			
			// If no expansions are loaded, apply base game
			if (currentExpansions.isEmpty()) {
				applyExpansion(1, true);
				android.util.Log.i("GameState", "No expansions loaded, applying base game (ID 1) by default");
			}
		}
	}
	
	private GameState()
	{
		currentExpansions = new HashMap<Long,Long>();
		neighborhoodCardsList = new HashMap<Long,ArrayList<NeighborhoodCard>>();
		encounterHx = new ArrayList<EncounterHx>();
		currentColors = new HashMap<Long,OtherWorldColor>();
	}
	
//	public void onSaveInstanceState(Bundle savedInstanceState) 
//	{
//	  // Save UI state changes to the savedInstanceState.
//	  // This bundle will be passed to onCreate if the process is
//	  // killed and restarted.
//		for (Long expID : currentExpansions.keySet())
//		{
//			savedInstanceState.putBoolean("Exp"+expID.toString(), true);	
//		}
//	  //savedInstanceState.putBoolean("MyBoolean", true);
//	  //savedInstanceState.putDouble("myDouble", 1.9);
//	  //savedInstanceState.putInt("MyInt", 1);
//	  //savedInstanceState.putString("MyString", "Welcome back to Android");
//	  // etc.
//	}
	
//	public void onRestoreInstanceState(Bundle savedInstanceState) {
//		if(savedInstanceState != null)
//		{
//	  // Restore UI state from the savedInstanceState.
//	  // This bundle has also been passed to onCreate.
//			for( Expansion exp : AHFlyweightFactory.INSTANCE.getExpansions())
//			{
//				if (savedInstanceState.getBoolean("Exp"+Long.toString(exp.getID())))
//				{
//					applyExpansion(exp.getID(), true);
//				}
//					
//			}
//		}
//	}
	
	public ArrayList<NeighborhoodCard> getDeckByNeighborhood(long neiID)
	{
		android.util.Log.d("GameState", "getDeckByNeighborhood called with neiID: " + neiID);
		
		if(neighborhoodCardsList.containsKey(neiID))
		{
			ArrayList<NeighborhoodCard> cached = neighborhoodCardsList.get(neiID);
			android.util.Log.d("GameState", "Returning cached deck with " + (cached != null ? cached.size() : 0) + " cards");
			return cached;
		}
		else
		{
			ArrayList<NeighborhoodCard> cards = AHFlyweightFactory.INSTANCE.getCurrentNeighborhoodsCards(neiID);
			android.util.Log.d("GameState", "Fetched new deck with " + (cards != null ? cards.size() : 0) + " cards");
			randomize(cards);
			
			neighborhoodCardsList.put(neiID, cards);
			return cards;
		}
	}
	
	public void randomizeNeighborhood(long neiID)
	{
		if(neighborhoodCardsList.containsKey(neiID))
		{
			randomize(neighborhoodCardsList.get(neiID));
		}
	}
	
	private <T> void randomize(ArrayList<T> cards) {
		if (cards != null && !cards.isEmpty()) {
			Collections.shuffle(cards, rand); // Use the GameState's rand instance
		}

		int randIdx;
		T swap;
		// i is the index the random card is insert
		for(int i = 0; i < cards.size()-1; i++)
		{
			//randIdx is the card to be inserted into i
			randIdx = rand.nextInt(cards.size()-i)+i;
			swap = cards.remove(randIdx);
			cards.add(i, swap);
		}
	}

	public Set<Long> getAppliedExpansions()
	{
		return currentExpansions.keySet();
	}
	
	public void applyExpansion(long expID, boolean isChecked) 
	{
		// Base game (ID 1) is mandatory and cannot be removed
		if (expID == 1 && !isChecked) {
			android.util.Log.w("GameState", "Attempted to remove base game (ID 1) - ignoring request");
			return;
		}
		
		if(!currentExpansions.containsKey(expID) && isChecked)
		{
			currentExpansions.put(expID,expID);
			AHFlyweightFactory.INSTANCE.addGameEx(expID, gameID);
			neighborhoodCardsList = new HashMap<Long,ArrayList<NeighborhoodCard>>();
			otherWorldCards = null;
			android.util.Log.i("GameState", "Applied expansion ID " + expID);
		}
		else if (currentExpansions.containsKey(expID) && !isChecked)
		{
			currentExpansions.remove(expID);
			AHFlyweightFactory.INSTANCE.removeGameEx(expID, gameID);
			neighborhoodCardsList = new HashMap<Long,ArrayList<NeighborhoodCard>>();
			otherWorldCards = null;
			android.util.Log.i("GameState", "Removed expansion ID " + expID);
		}
	}
	
	public void AddHistory(Encounter enc) 
	{
		long hxID = AHFlyweightFactory.INSTANCE.addEncounterHx(enc, gameID);
		
		encounterHx.add(0, new EncounterHx(enc.getID(), enc.getLocID(), hxID));
	}
	
	public ArrayList<EncounterHx> getEncounterHx()
	{
		return encounterHx;
	}
	
	public ArrayList<OtherWorldCard> getAllOtherWorldDeck() {
		if (otherWorldCards == null)
		{
			prepOtherWorldDeck();
		}
		
		return otherWorldCards;
	}
	
	public void addSelectedOtherWorldColor(OtherWorldColor color)
	{
		if(!currentColors.containsKey(color.getID()))
		{
				currentColors.put(color.getID(), color);
		}
	}
	
	public void removeSelectedOtherWorldColor(OtherWorldColor color)
	{
		if(currentColors.containsKey(color.getID()))
		{
				currentColors.remove(color.getID());
		}
	}
	
	public void clearSelectedOtherWorldColor()
	{
		currentColors.clear();
	}
	
	public boolean isSelectedOtherWorldColor(OtherWorldColor owc)
	{
		if( currentColors == null )
		{
			return false;
		}
		
		return currentColors.containsKey(owc.getID());
	}
	
	public ArrayList<OtherWorldCard> getFilteredOtherWorldDeck() {
		Collection<OtherWorldColor> colorIDs = currentColors.values();
		if( colorIDs == null || colorIDs.size() == 0 )
		{
			return getAllOtherWorldDeck();
		}
		else
		{
			ArrayList<OtherWorldCard> cards = getAllOtherWorldDeck();
			ArrayList<OtherWorldCard> filteredCards = new ArrayList<OtherWorldCard>();

			for(int i = 0; i < cards.size(); i++)
			{
				//If is stars are right or card has at least one matching color
				if(cards.get(i).getID() == 4242 || hasMatchingColor(cards.get(i).getOtherWorldColors(), colorIDs))
				{
					filteredCards.add(cards.get(i));
				}
			}
			
			return filteredCards;
		}
			
	}
	
	
	public boolean otherWorldCardSelected(long cardId) {
		
		//Shuffle if they selected Stars are right
		if(cardId == 4242)
		{
			prepOtherWorldDeck();
			return true;
		}
		//Keep removing cards until we hit the end of the deck or we find the card selected
		int i = 0;
		while( otherWorldCards.size() != i && otherWorldCards.get(i).getID() != cardId)
		{
			//Skip Stars Are right
			if(otherWorldCards.get(i).getID() != 4242)
			{
				ICard card = otherWorldCards.remove(i);
				AHFlyweightFactory.INSTANCE.removeFromDeck(-1, gameID, card.getID());
			}
			else
			{
				i++;
			}
		}
		//remove the selected card
		if(otherWorldCards.size() > i)
		{
			ICard card = otherWorldCards.remove(i);
			AHFlyweightFactory.INSTANCE.removeFromDeck(-1, gameID, card.getID());
		}
		//Shuffle if we're at the end of the deck
		if(otherWorldCards.size() == 0)
		{
			prepOtherWorldDeck();
			return true;
		}
		return false;
	}
	
	public void prepOtherWorldDeck()
	{
		otherWorldCards = AHFlyweightFactory.INSTANCE.getCurrentOtherWorldCards();
		randomize(otherWorldCards);
		ArrayList<ICard> deck = arrayListConvert(otherWorldCards);
		AHFlyweightFactory.INSTANCE.setDeck(-1, gameID, deck);
	}
	
	//TODO Must be a better way than this
	@SuppressWarnings("unchecked")
	private <T, E> ArrayList<T> arrayListConvert(ArrayList<E> orig)
	{
		if(orig == null)
		{
			return null;
		}
		ArrayList<T> ret = new ArrayList<T>();
		
		for(int i = 0; i < orig.size(); i++)
		{
			ret.add((T)orig.get(i));
		}
		
		return ret;
	}

	public void newGame() {
		//Clear the deck lists

		rand = new Random(System.currentTimeMillis());
		neighborhoodCardsList = new HashMap<Long,ArrayList<NeighborhoodCard>>();
		encounterHx = new ArrayList<EncounterHx>();
		currentExpansions = new HashMap<Long,Long>();
		
		rand = new Random(System.currentTimeMillis());
		gameID = AHFlyweightFactory.INSTANCE.createNewGame();
		otherWorldCards = null;
		
		// Apply base game by default
		applyExpansion(1, true);
		android.util.Log.i("GameState", "New game - base expansion (ID 1) applied by default");
	}

	public void removeHx(int position) {
		AHFlyweightFactory.INSTANCE.removeEncounterHx(encounterHx.get(position));
		encounterHx = AHFlyweightFactory.INSTANCE.getGameEncHx(gameID);
	}
	
	/**
	 * Check if there's any overlap between card colors and selected colors
	 */
	private boolean hasMatchingColor(ArrayList<OtherWorldColor> cardColors, Collection<OtherWorldColor> selectedColors) {
		if (cardColors == null || cardColors.isEmpty()) {
			return false;
		}
		
		for (OtherWorldColor cardColor : cardColors) {
			for (OtherWorldColor selectedColor : selectedColors) {
				if (cardColor.getID() == selectedColor.getID()) {
					return true;
				}
			}
		}
		return false;
	}
}
