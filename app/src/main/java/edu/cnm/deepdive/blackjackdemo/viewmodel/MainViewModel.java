package edu.cnm.deepdive.blackjackdemo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import edu.cnm.deepdive.blackjackdemo.model.Card;
import edu.cnm.deepdive.blackjackdemo.model.Deck;
import edu.cnm.deepdive.blackjackdemo.model.Draw;
import edu.cnm.deepdive.blackjackdemo.model.Hand;
import edu.cnm.deepdive.blackjackdemo.service.DeckOfCardsService;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class MainViewModel extends ViewModel {

  private MutableLiveData<Deck> deck;
  private MutableLiveData<Hand> hand;
  private MutableLiveData<List<Card>> cards;

  public LiveData<Deck> getDeck() {
    if(deck == null){
      deck = new MutableLiveData<>();
    }
    return deck;
  }

  public LiveData<Hand> getHand() {
    if(hand == null){
      hand = new MutableLiveData<>();
    }
    return hand;
  }

  public LiveData<List<Card>> getCards() {
    if(cards == null){
      cards = new MutableLiveData<>();
    }
    return cards;
  }
  //How many decks are in the shoe!
  public void initDeck(int numDecks){
    DeckOfCardsService.getInstance().newDeck(numDecks)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())        //smart thread scheduler.
        .subscribe(deck::postValue);  //FIXME add this to a disposable container. its ya boy trashcan
  }

  public void shuffle() {
    DeckOfCardsService.getInstance().shuffle(deck.getValue().getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((d) -> initHand()); //FIXME add to a disposable container.
  }

  public void draw(int numCards) {
    DeckOfCardsService.getInstance().draw(deck.getValue().getId(), numCards)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::addToHand); //FIXME add this to ya boy trashcan
  }

  public void initHand(){
    hand.postValue(new Hand());  //postValue is asycronus more flexible then setValue
  }

  private void addToHand(Draw draw){
    Hand hand = getHand().getValue();
    for (Card card : draw.getCards()){
      hand.addCard(card);
    }
    cards.postValue(hand.getCards());
  }

}
